package com.fintech.sst.ui.widget

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * 包含2个子view，第一个表示滑动菜单，第二个表示显示内容
 */
class MenuCardView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
) : CardView(context, attrs, defStyleAttr) {

    constructor(
            context: Context,
            attrs: AttributeSet?
    ) : this(context, attrs, 0)

    constructor(
            context: Context
    ) : this(context, null, 0)

    private lateinit var content: View
    private lateinit var menu: View
    private var viewDragHelper: ViewDragHelper

    init {
        viewDragHelper = ViewDragHelper.create(this, DragCallback())
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        content = getChildAt(1)
        menu = getChildAt(0)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }


    override fun computeScroll() {
        super.computeScroll()
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }


    inner class DragCallback : ViewDragHelper.Callback() {

        private var left = 0

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == content
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            //child 表示当前正在移动的view
            //left 表示当前的view正要移动到左边距为left的地方
            //dx 表示和上一次滑动的距离间隔
            //返回值就是child要移动的目标位置.可以通过控制返回值,从而控制child只能在ViewGroup的范围中移动.
            this.left = left
            if (left < 0) {
                return if (Math.abs(left) < menu.width)
                    left
                else
                    menu.width * -1
            }
            return 0
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (Math.abs(left) > menu.width / 2)
                viewDragHelper.settleCapturedViewAt(menu.width * -1, 0)
            else
                viewDragHelper.settleCapturedViewAt(0, 0)

            postInvalidate()
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return menu.width
        }
    }
}