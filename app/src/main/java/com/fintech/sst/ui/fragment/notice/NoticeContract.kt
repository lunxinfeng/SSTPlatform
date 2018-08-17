package com.fintech.sst.ui.fragment.notice

import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView
import com.fintech.sst.net.bean.OrderList

interface NoticeContract {
    interface View: BaseView<Presenter> {
        fun loadError(error:String)
        fun loadMore(orders:List<OrderList>)
        fun refreshData(orders:List<OrderList>)
    }

    interface Presenter: BasePresenter {
        /**
         * 通知列表  支付中 10    通知中 20  成功 30   关闭 40, 全部  不传
         */
        fun noticeList(type:Int, pageNow: Int = 1, pageSize:Int = 10, append:Boolean = false)
    }
}