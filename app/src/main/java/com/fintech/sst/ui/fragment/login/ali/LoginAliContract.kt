package com.fintech.sst.ui.fragment.login.ali

import android.support.v4.app.FragmentActivity
import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView

interface LoginAliContract {
    interface View: BaseView<Presenter> {
        val context:FragmentActivity
        fun loginSuccess()
        fun loginFail(hint:String)
    }

    interface Presenter: BasePresenter {
        fun aliLogin()
    }
}