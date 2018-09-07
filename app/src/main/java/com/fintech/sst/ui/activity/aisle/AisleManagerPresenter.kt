package com.fintech.sst.ui.activity.aisle

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.PermissionUtil
import com.fintech.sst.helper.RxBus
import com.fintech.sst.helper.debug
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.bean.AisleInfo
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AisleManagerPresenter(val view: AisleManagerContract.View, private val model: AisleManagerModel = AisleManagerModel()) : AisleManagerContract.Presenter, LifecycleObserver {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var clickNum = 0

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (Configuration.noLogin()) {
            view.toLogin()
            return
        } else {
            Constants.baseUrl = Configuration.getUserInfoByKey(Constants.KEY_ADDRESS)
        }
//        JobServiceCompact.startJob(1000)
        subscribeNotice()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (!PermissionUtil.isNotificationListenerEnabled()) {
            view.toNotifactionSetting()
            view.showToast("请打开随身听通知监听权限")
        }
        clickNum = 0
        userInfo()
    }

    override fun userInfo() {
        model.userInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<AisleInfo>, AisleManagerContract.View>(view) {
                    override fun onNext_(t: ResultEntity<AisleInfo>?) {
                        model.aisleInfo = t?.result
                        view.updateUserInfo(t?.result)
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun aisleStatus(open: Boolean) {
        model.aisleStatus(open)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<String>, AisleManagerContract.View>(view) {
                    override fun onNext_(t: ResultEntity<String>?) {
                        view.aisleStatusResult(t?.msg.toString().contains("success"))
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun aisleRefresh() {
        model.aisleRefresh()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<String>, AisleManagerContract.View>(view) {
                    override fun onNext_(t: ResultEntity<String>?) {
                        view.aisleRefreshResult(t?.msg.toString().contains("success"))
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun aisleDelete() {
        model.aisleDelete()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<String>, AisleManagerContract.View>(view) {
                    override fun onNext_(t: ResultEntity<String>?) {
                        view.aisleDeleteResult(t?.msg.toString().contains("success"))
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun toOrder() {
        view.toOrderList()
    }

    override fun toSetting() {
        view.toSetting()
    }

    override fun toNoticeList() {
        if (++clickNum >= 7) {
            view.toNoticeList()
            return
        } else {
            compositeDisposable.clear()
            if (clickNum > 2)
                view.showToast("再点${7 - clickNum}次进入通知详情页")
        }
        val d = Single.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe { _ -> clickNum = 0 }
        compositeDisposable.add(d)
    }

    override fun toAisleManager() {
        if (++clickNum == 7) {
            view.toAisleManager()
            return
        } else {
            compositeDisposable.clear()
            if (clickNum in 3..6)
                view.showToast("再点${7 - clickNum}次进入通道管理")
        }
        val d = Single.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe { _ -> clickNum = 0 }
        compositeDisposable.add(d)
    }

    private fun subscribeNotice() {
        RxBus.getDefault().toObservable(Notice::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Notice> {
                    var d: Disposable? = null
                    override fun onSubscribe(d: Disposable) {
                        this.d = d
                    }

                    override fun onNext(t: Notice) {
                        if (t.type == 1) {
                            userInfo()
                        } else {
                            view.updateNoticeList(t)
                        }
                    }

                    override fun onComplete() {
                        debug("subscribeNotice", "onComplete")
                        d?.dispose()
                        subscribeNotice()
                    }

                    override fun onError(e: Throwable) {
                        debug("subscribeNotice", "onError")
                        d?.dispose()
                        subscribeNotice()
                    }
                })
    }

    override fun unsubscribe() {

    }
}