package com.fintech.sst.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.fintech.sst.helper.debug
import io.reactivex.disposables.CompositeDisposable

interface BasePresenter:LifecycleObserver {
    val compositeDisposable:CompositeDisposable

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unsubscribe(){
        debug("BasePresenter","unsubscribe")
        compositeDisposable.clear()
    }
}