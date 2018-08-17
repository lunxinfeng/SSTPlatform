package com.fintech.sst.ui.activity.config

import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView


interface ConfigContract {
    interface View: BaseView<Presenter> {

        fun checkSuccess(hint:String)
        fun checkFail(hint:String)
    }

    interface Presenter: BasePresenter {
        fun check(address:String)
    }
}