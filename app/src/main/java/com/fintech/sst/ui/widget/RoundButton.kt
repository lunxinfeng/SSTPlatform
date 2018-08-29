package com.fintech.sst.ui.widget

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import com.fintech.sst.R


class RoundButton(context: Context,attrs: AttributeSet): AppCompatButton(context,attrs) {
    var cornerRadius = 0f
    var colorBackground = Color.WHITE

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerButton)
    }
}