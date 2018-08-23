package com.fintech.sst.ui.activity.setting

import android.arch.lifecycle.LifecycleObserver
import com.fintech.sst.App
import com.fintech.sst.data.db.DB
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.ProgressObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SettingPresenter(val view: SettingContract.View) : SettingContract.Presenter, LifecycleObserver {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun cleatLocalDB() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .filter {
                    DB.deleteTable(App.getAppContext())
                    DB.queryAll(App.getAppContext()).size == 0
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<Int,SettingContract.View>(view) {
                    override fun onNext_(t: Int?) {
                        view.showToast("清除本地数据成功")
                    }

                    override fun onError(error: String?) {
                        view.showToast("清除本地数据失败")
                    }
                })
    }

    override fun exitAccount() {
        Configuration.clearUserInfo()
        view.toLogin()
    }
}