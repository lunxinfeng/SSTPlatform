package com.fintech.sst.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.fintech.sst.App
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

var DEBUG = true

var lastNoticeTime:Long = 0

fun Activity.toActivity(clazz: Class<*>) = startActivity(Intent(this,clazz))

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(applicationContext, msg, duration).show()

fun debug(tag: String, msg: String){
    if (DEBUG) Log.d(tag, msg)
}

fun EditText.onChange(onChange:(s: CharSequence?) -> Unit){
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onChange.invoke(s)
        }
    })
}

fun getColor(res:Int) = App.getAppContext().resources.getColor(res)

fun getTime(time:Long): String? {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    return format.format(Date(time))
}

/**
 * @param num 共点多少次
 * @param des 行为描述
 * @param listener 逻辑操作
 */
fun View.clickN(num:Int, des:String, listener:(v: View) -> Unit){

    val compositeDisposable = CompositeDisposable()
    var clickNum = 0

    fun click(listener:(v: View) -> Unit){
        if (++clickNum == num) {
            listener.invoke(this)
            return
        } else {
            compositeDisposable.clear()
            if (clickNum in 3..(num - 1))
                Toast.makeText(context,"再点${num - clickNum}次$des", Toast.LENGTH_SHORT).show()
        }
        val d = Single.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe { _ -> clickNum = 0 }
        compositeDisposable.add(d)
    }

    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN)
            click(listener)
        false
    }
}
