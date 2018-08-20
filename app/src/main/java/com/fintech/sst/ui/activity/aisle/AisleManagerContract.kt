package com.fintech.sst.ui.activity.aisle

import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView
import com.fintech.sst.data.db.Notice
import com.fintech.sst.net.bean.UserInfoDetail

interface AisleManagerContract {
    interface View: BaseView<Presenter> {
        fun updateUserInfo(userInfo:UserInfoDetail?)
        fun updateNoticeList(notice:Notice)
        fun toLogin()
        fun toOrderList()
        fun toSetting()
        fun toNotifactionSetting()
        fun toNoticeList()
    }

    interface Presenter: BasePresenter {
        fun userInfo()
        fun toOrder()
        fun toNoticeList()
        fun toSetting()
    }
}