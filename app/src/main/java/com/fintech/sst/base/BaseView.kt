package com.fintech.sst.base

import android.widget.Toast
import com.fintech.sst.App

interface BaseView<out T> {
    val presenter: T

    fun showToast(content: String){
        Toast.makeText(App.getAppContext(),content,Toast.LENGTH_SHORT).show()
    }

    fun showProgress(show:Boolean)
}
