package com.fintech.match.pay_2.wxapi

import android.arch.lifecycle.LifecycleObserver
import io.reactivex.disposables.CompositeDisposable

class WXPresenter(val view: WXContract.View) : WXContract.Presenter, LifecycleObserver {

    override val compositeDisposable = CompositeDisposable()
}