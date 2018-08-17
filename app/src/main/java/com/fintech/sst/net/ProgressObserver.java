package com.fintech.sst.net;

import com.fintech.sst.base.BaseView;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public abstract class ProgressObserver<T,V extends BaseView> implements Observer<T> {
    private V view;
    private Disposable mDisposable;
    private boolean showProgress;

    public ProgressObserver(V view) {
        this.view = view;
        this.showProgress = true;
    }

    public ProgressObserver(V view, boolean showProgress) {
        this.view = view;
        this.showProgress = showProgress;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        view.showProgress(showProgress);
    }

    @Override
    public void onNext(T t) {
        onNext_(t);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            onError("网络连接超时");
        } else if (e instanceof ConnectException) {
            onError("网络连接超时");
        } else if (e instanceof ServerException) {
            onError(e.getMessage());
        } else {
            onError(e.getMessage());
        }
        view.showProgress(false);
        onCancelProgress();
    }

    @Override
    public void onComplete() {
        view.showProgress(false);
        onCancelProgress();
    }

    private void onCancelProgress() {
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    public abstract void onNext_(T t);

    public abstract void onError(String error);
}
