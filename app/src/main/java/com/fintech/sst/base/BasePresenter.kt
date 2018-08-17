package com.fintech.sst.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable

interface BasePresenter:LifecycleObserver {
    val compositeDisposable:CompositeDisposable

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unsubscribe(){
        compositeDisposable.clear()
    }
}