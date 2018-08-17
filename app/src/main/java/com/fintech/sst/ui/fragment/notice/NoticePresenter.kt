package com.fintech.sst.ui.fragment.notice

import android.arch.lifecycle.LifecycleObserver
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.bean.OrderList
import com.fintech.sst.net.bean.PageList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class NoticePresenter(val view: NoticeContract.View,
                      private val model: NoticeModel = NoticeModel()) : NoticeContract.Presenter, LifecycleObserver {

    override val compositeDisposable = CompositeDisposable()

    override fun noticeList(type:Int, pageNow: Int, pageSize:Int, append:Boolean) {
        model.aisleList(type,pageNow,pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<PageList<OrderList>>, NoticeContract.View>(view,false) {
                    override fun onNext_(t: ResultEntity<PageList<OrderList>>) {
                        if (append)
                            view.loadMore(t.result.list)
                        else
                            view.refreshData(t.result.list)
                    }

                    override fun onError(error: String) {
                         view.loadError(error)
                    }
                })
    }
}