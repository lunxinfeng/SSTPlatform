package com.fintech.sst.base

import android.support.v7.app.AppCompatActivity
import com.fintech.sst.R
import com.fintech.sst.ui.dialog.WaitDialog


abstract class BaseActivity<out T>: AppCompatActivity(),BaseView<T>{
    var waitDialog: WaitDialog? = null
    override fun showProgress(show: Boolean) {
        if (waitDialog == null)
            waitDialog = WaitDialog(this, R.style.dialog)
        if (show)
            waitDialog?.show()
        else
            waitDialog?.dismiss()
    }
}