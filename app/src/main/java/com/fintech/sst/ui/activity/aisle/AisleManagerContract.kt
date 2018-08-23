package com.fintech.sst.ui.activity.aisle

import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView
import com.fintech.sst.data.db.Notice
import com.fintech.sst.net.bean.AisleInfo

interface AisleManagerContract {
    interface View: BaseView<Presenter> {
        fun updateUserInfo(info: AisleInfo?)
        fun updateNoticeList(notice:Notice)
        fun aisleStatusResult(success: Boolean)
        fun aisleRefreshResult(success: Boolean)
        fun aisleDeleteResult(success: Boolean)
        fun toLogin()
        fun toOrderList()
        fun toSetting()
        fun toNotifactionSetting()
        fun toNoticeList()
        fun toAisleManager()
    }

    interface Presenter: BasePresenter {
        fun userInfo()
        fun aisleStatus(open:Boolean)
        fun aisleRefresh()
        fun aisleDelete()
        fun toOrder()
        fun toNoticeList()
        fun toAisleManager()
        fun toSetting()
    }
}