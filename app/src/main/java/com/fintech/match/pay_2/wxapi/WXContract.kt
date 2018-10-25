package com.fintech.match.pay_2.wxapi

import com.fintech.sst.base.BasePresenter
import com.fintech.sst.base.BaseView

interface WXContract {
    interface View: BaseView<Presenter>

    interface Presenter: BasePresenter
}