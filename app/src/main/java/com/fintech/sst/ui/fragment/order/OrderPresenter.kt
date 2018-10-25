package com.fintech.sst.ui.fragment.order

import android.arch.lifecycle.LifecycleObserver
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.bean.OrderCount
import com.fintech.sst.net.bean.OrderList
import com.fintech.sst.net.bean.PageList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class OrderPresenter(val view: OrderContract.View,
                     private val model:OrderModel = OrderModel()) : OrderContract.Presenter, LifecycleObserver {

    override val compositeDisposable = CompositeDisposable()

    override fun orderList(tradeStatus:Int,type:String, pageNow: Int, pageSize:Int,append:Boolean) {
        model.orderList(tradeStatus,pageNow,pageSize,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<PageList<OrderList>>, OrderContract.View>(view,false) {
                    override fun onNext_(t: ResultEntity<PageList<OrderList>>?) {
                        if (append)
                            view.loadMore(t?.result?.list)
                        else
                            view.refreshData(t?.result?.list)
                    }

                    override fun onError(error: String) {
                         view.loadError(error)
                    }
                })
    }

    override fun orderCount(orderList: OrderList,type:String) {
        model.orderCount(orderList.realAmount.toString(),orderList.createTime,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<OrderCount>, OrderContract.View>(view,false) {
                    override fun onNext_(t: ResultEntity<OrderCount>?) {
                        t?.result?.let {
                            view.showReOrderHint(it,orderList)
                        }?:view.showToast("获取数据失败")
                    }

                    override fun onError(error: String) {
                        view.loadError(error)
                    }
                })
    }

    override fun reOrder(orderNo: String,type:String) {
        model.reOrder(orderNo,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<String>, OrderContract.View>(view,false) {
                    override fun onNext_(t: ResultEntity<String>?) {
                        view.reOrderSuccess()
                    }

                    override fun onError(error: String) {
                        view.loadError(error)
                    }
                })

    }
}