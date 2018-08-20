package com.fintech.sst.ui.fragment.notice

import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView
import com.fintech.sst.data.db.Notice

interface NoticeContract {
    interface View: BaseView<Presenter> {
        fun loadError(error:String)
        fun loadMore(notices:List<Notice>?)
        fun refreshData(notices:List<Notice>?)
    }

    interface Presenter: BasePresenter {
        /**
         * 通知列表
         */
        fun noticeList(status:Int, pageNow: Int = 1, pageSize:Int = 10, append:Boolean = false)
    }
}