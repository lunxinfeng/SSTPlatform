package com.fintech.sst.ui.fragment.login.account

import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView

interface LoginAccountContract {
    interface View: BaseView<Presenter> {

        fun loginSuccess()
        fun loginFail(hint:String)
    }

    interface Presenter: BasePresenter {
        fun accountLogin(name:String,password:String)
    }
}