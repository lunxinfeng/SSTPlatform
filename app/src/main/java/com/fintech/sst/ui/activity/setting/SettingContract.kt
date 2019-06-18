package com.fintech.sst.ui.activity.setting

import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView

interface SettingContract {
    interface View: BaseView<Presenter> {
        fun toAppDetailActivity()
        fun toNotifactionSetting()
        fun toLogin()
    }

    interface Presenter: BasePresenter {
        fun cleatLocalDB()
//        fun exitAccount()

        fun configAddress(web:String,netty:String)
    }
}