package com.fintech.sst.service

import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.METHOD_BANK
import com.fintech.sst.helper.RxBus
import com.fintech.sst.helper.SmsObserverUtil
import com.fintech.sst.helper.debug
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.bean.Sms
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class BankService : BaseService() {

    override fun onCreate() {
        super.onCreate()
        if (Configuration.isLogin(METHOD_BANK)) {
            try {
                subscribeSms()
                SmsObserverUtil.registerSmsDatabaseChangeObserver(this)
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
                        val notice = Notice()
                        notice.content = t.content
//                notice.saveTime = notification.`when`
                        notice.status = 2
                        notice.tag = ""
                        notice.noticeId = 0
                        notice.title = "银行通知"

                        notice.packageName = t.sendName
                        notice.type = METHOD_BANK.toInt()
                        notice.orderNo = ""
                        notice.amount = t.amount
                        notice.mark = ""
                        if (notice.amount.toFloatOrNull() != 0f)
                            notices.offer(notice)
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

    override fun onDestroy() {
        SmsObserverUtil.unregisterSmsDatabaseChangeObserver(this)
        super.onDestroy()
    }
}
