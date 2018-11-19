package com.fintech.sst.ui.activity.aisle

import android.support.v4.app.FragmentActivity
import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView
import com.fintech.sst.data.db.Notice
import com.fintech.sst.net.bean.AisleInfo

interface AisleManagerContract {
    interface View: BaseView<Presenter> {
        fun updateUserInfo(info: AisleInfo?,type: String)
        fun updateLocalInfo(notices: List<Notice>,type: String)
        fun updateNoticeList(notice:Notice)
        fun aisleStatusResult(success: Boolean,type: String)
        fun aisleRefreshResult(success: Boolean)
        fun aisleDeleteResult(success: Boolean,type: String)
        fun exitLogin(type: String)
        fun checkOrderType()
        fun toOrderList(type: String)
        fun toSetting()
        fun checkDaMaType()
        fun checkPassword(callback:(password: String) -> Unit)
        fun toDaMa(account:String,password:String,type:String)
        fun toNotifactionSetting()
        fun toNoticeList()
        fun toAisleManager(type: String)
        fun viewHideOrShow(view:android.view.View,show:Boolean)
        fun showHintDialog(content:String)

        //----- login
        val context: FragmentActivity
        fun accountLogin(type:String):Boolean
        fun loginSuccess(type:String)
        fun loginFail(hint:String)
    }

    interface Presenter: BasePresenter {
        fun userInfo(type: String)
        fun aisleStatus(open:Boolean,type: String)
        fun aisleRefresh(type: String)
        fun aisleDelete(type: String)
        fun toOrder()
        fun toNoticeList()
        fun toAisleManager(type: String)
        fun toSetting()
        fun toDaMa()
        fun aliLogin()
        fun wechatLogin()
        fun accountLogin(account:String,password:String,type:String)
        fun checkLogin(account:String,password:String,type:String)
        fun exitLogin(type:String)
        fun updateLocalAmount(type: String)
    }
}