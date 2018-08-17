package com.fintech.sst.base

import android.support.v4.app.Fragment
import com.fintech.sst.R
import com.fintech.sst.ui.dialog.WaitDialog


abstract class BaseFragment<out T>: Fragment(),BaseView<T> {
    var waitDialog: WaitDialog? = null
    override fun showProgress(show: Boolean) {
        if (activity == null) return
        if (waitDialog == null)
            waitDialog = WaitDialog(this.activity, R.style.dialog)
        if (show)
            waitDialog?.show()
        else
            waitDialog?.dismiss()
    }
}