package com.fintech.sst.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.fintech.sst.data.db.DB
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.RxBus
import com.fintech.sst.helper.debug
import com.fintech.sst.net.*
import com.fintech.sst.other.xposed.WechatHook.BILLRECEIVED_ACTION
import com.fintech.sst.other.xposed.Message
import com.fintech.sst.other.xposed.PayHelperUtils
import com.fintech.sst.other.xposed.WechatHook
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

class WechatService : Service() {
    private val TAG = "WechatService"
    private val notices = LinkedList<Notice>()
    private var disposable: Disposable? = null
    private val billReceiver = BillReceiver()

    inner class BillReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!!.contentEquals(PayHelperUtils.MSGRECEIVED_ACTION)) {
                val msg = intent.getStringExtra("msg")
                RxBus.getDefault().send(Message().apply {
                    content = "==================\n$msg"
                })
//                RxBus.getDefault().send(Notice().apply {
//                    content = "=========hook反馈=========\n$msg"
//                })
            }else if(intent.action!!.contentEquals(BILLRECEIVED_ACTION)){

                val order_no = intent.getStringExtra("bill_no")
                val order_money = intent.getStringExtra("bill_money")
                val order_mark = intent.getStringExtra("bill_mark")
                val order_type = intent.getStringExtra("bill_type")

                val notice = Notice()
                notice.content = "微信到账$order_money"
//                notice.saveTime = notification.`when`
                notice.status = 2
                notice.tag = ""
                notice.noticeId = 0
                notice.title = "微信通知"

                notice.packageName = "com.tencent.mm"
                notice.type = order_type.toInt()
                notice.orderNo = order_no
                notice.amount = order_money
                notice.mark = order_mark
                if (order_money.toFloat() != 0f)
                    notices.offer(notice)
            }
        }
    }

    private fun registerReceiver(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(BILLRECEIVED_ACTION)
        intentFilter.addAction(PayHelperUtils.MSGRECEIVED_ACTION)
        intentFilter.addAction(WechatHook.QRCODERECEIVED_ACTION)
        intentFilter.addAction(PayHelperUtils.TRADENORECEIVED_ACTION)
        intentFilter.addAction(PayHelperUtils.LOGINIDRECEIVED_ACTION)
//        intentFilter.addAction(WechatHook.SAVEALIPAYCOOKIE_ACTION)
        intentFilter.addAction(PayHelperUtils.GETTRADEINFO_ACTION)
        registerReceiver(billReceiver, intentFilter)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand")
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        registerReceiver()
        db()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        disposable?.dispose()
        unregisterReceiver(billReceiver)
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
                            DB.updateAll(this@WechatService, notice)
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
}
