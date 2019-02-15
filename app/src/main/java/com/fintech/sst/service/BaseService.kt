package com.fintech.sst.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.fintech.sst.data.db.DB
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.DEBUG
import com.fintech.sst.helper.RxBus
import com.fintech.sst.helper.debug
import com.fintech.sst.net.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit


abstract class BaseService : Service() {
    val TAG = javaClass.simpleName
    val notices = LinkedList<Notice>()
    var disposable: Disposable? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        db()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    private fun db() {
        if (disposable != null)
            disposable?.dispose()
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .filter {
                    notices.size > 0 && Configuration.isLogin(notices[0].type.toString())
                }
                .flatMap {
                    val notice = notices.poll()
                    debug(TAG, "=========DB========: 发起请求$notice")
                    val body = MessageRequestBody()
                    body.put("uuid", notice.uuid)
                    body.put("amount", notice.amount)
                    body.put("title", notice.title)
                    body.put("content", notice.content)
                    body.put("time", notice.saveTime)
                    body.put("type", notice.type)
                    body.put("packageName", notice.packageName)
                    body.put("id", notice.noticeId)
                    body.put("tag", notice.tag)
                    body.put("memo", notice.mark)
                    body.sign(notice.type.toString())
                    println("++++insert$notice")
                    DB.insert(this, notice)
                    RxBus.getDefault().send(notice)

                    ApiProducerModule
                            .create(ApiService::class.java)
                            .notifyLog(body)
                }
                .subscribe(object : Observer<ResultEntity<Notice>> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(resultEntity: ResultEntity<Notice>) {
                        val notice = resultEntity.result
                        if (resultEntity.msg == "success" && notice != null) {
                            notice.status = 1
                            DB.updateAll(this@BaseService, notice)
                        }
                        debug(TAG, "=========DB========: $resultEntity")
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "db onError")
                        e.printStackTrace()
                        disposable?.dispose()
                        db()
                    }

                    override fun onComplete() {
                        Log.d(TAG, "db onComplete")
                        disposable?.dispose()
                        db()
                    }
                })
    }

    fun log(msg:String){
        if (DEBUG)
            println("${javaClass.simpleName}：$msg")
    }
}