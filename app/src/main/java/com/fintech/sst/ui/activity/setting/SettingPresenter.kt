package com.fintech.sst.ui.activity.setting

import android.arch.lifecycle.LifecycleObserver
import com.fintech.sst.net.Configuration
import io.reactivex.disposables.CompositeDisposable

class SettingPresenter(val view: SettingContract.View) : SettingContract.Presenter, LifecycleObserver {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun exitAccount() {
        Configuration.clearUserInfo()
        view.toLogin()
    }
}