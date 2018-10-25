package com.fintech.sst.ui.fragment.order

import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView
import com.fintech.sst.net.bean.OrderCount
import com.fintech.sst.net.bean.OrderList

interface OrderContract {
    interface View: BaseView<Presenter> {
        fun loadError(error:String)
        fun loadMore(orders:List<OrderList>?)
        fun refreshData(orders:List<OrderList>?)
        fun reOrderSuccess()
        fun showReOrderHint(orderCount:OrderCount,orderList: OrderList)
    }

    interface Presenter: BasePresenter {
        /**
         * 订单状态  支付中 10    通知中 20  成功 30   关闭 40, 全部  不传
         */
        fun orderList(tradeStatus:Int,type:String, pageNow: Int = 1, pageSize:Int = 10,append:Boolean = false)

        /**
         * 获取订单数
         */
        fun orderCount(orderList: OrderList,type:String)

        /**
         * 补单
         */
        fun reOrder(orderNo:String,type:String)
    }
}