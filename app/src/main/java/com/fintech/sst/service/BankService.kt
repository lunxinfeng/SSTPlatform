package com.fintech.sst.service

import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.*
import com.fintech.sst.net.ApiProducerModule
import com.fintech.sst.net.ApiService
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants.KEY_ACCOUNT_ID_BANK
import com.fintech.sst.net.SignRequestBody
import com.fintech.sst.net.bean.Sms
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class BankService : BaseService() {
    private var checkDisposable: Disposable? = null


    override fun onCreate() {
        super.onCreate()
        if (Configuration.isLogin(METHOD_BANK) || Configuration.isLogin(METHOD_YUN)) {
            try {
                subscribeSms()
                SmsObserverUtil.registerSmsDatabaseChangeObserver(this)
                check()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun subscribeSms() {
        RxBus.getDefault().toObservable(Sms::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Sms> {
                    var d: Disposable? = null
                    override fun onSubscribe(d: Disposable) {
                        this.d = d
                    }

                    override fun onNext(t: Sms) {
                        addToNoticeList(t, false)
                    }

                    override fun onComplete() {
                        debug("subscribeNotice", "onComplete")
                        d?.dispose()
                        subscribeSms()
                    }

                    override fun onError(e: Throwable) {
                        debug("subscribeNotice", "onError")
                        d?.dispose()
                        subscribeSms()
                    }
                })
    }

    private fun addToNoticeList(t: Sms, reSend: Boolean) {
        val notice = Notice()
        notice.content = t.content
        notice.saveTime = t.time.toLong()
        notice.status = 2
        notice.tag = ""
        notice.noticeId = 0
        notice.title = "银行通知"

        notice.packageName = t.sendName
        notice.type = METHOD_BANK.toInt()
        notice.orderNo = ""
        notice.amount = t.amount
        notice.mark = if (reSend) "补单" else "正常"
        if (notice.amount.toFloatOrNull() != 0f)
            notices.offer(notice)
    }

    private fun check() {
        checkDisposable = Observable.interval(4 * 60 * 1000, TimeUnit.MILLISECONDS)
                .map {
                    val listSql = SmsObserverUtil.mSmsDBChangeObserver.query50()
                    listSql
                }
                .delay(5000, TimeUnit.MILLISECONDS)
                .subscribe { listSql ->
                    val request = HashMap<String, String>()
                    request["accountId"] = Configuration.getUserInfoByKey(KEY_ACCOUNT_ID_BANK)
                    ApiProducerModule.create(ApiService::class.java).smsList(SignRequestBody(request).sign(METHOD_BANK))
                            .subscribeOn(Schedulers.io())
                            .map { result ->
                                val listNet = mutableListOf<Sms>()
                                result.result.forEach {
                                    val time = it.split(",")[0]
                                    val amount = it.split(",")[1]

                                    listNet.add(Sms().apply {
                                        this.time = time
                                        this.amount = amount
                                    })
                                }
                                listNet
                            }
                            .subscribe { it ->

                                listSql.removeAll(it)

                                listSql.forEach {
                                    println("自动补单$it")
                                    addToNoticeList(it, true)
                                }
                            }
                }
    }

    override fun onDestroy() {
        checkDisposable?.dispose()
        checkDisposable = null
        SmsObserverUtil.unregisterSmsDatabaseChangeObserver(this)
        super.onDestroy()
    }
}
