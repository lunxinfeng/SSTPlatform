package com.fintech.sst.ui.activity.aisle

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import com.fintech.sst.R
import com.fintech.sst.base.BaseActivity
import com.fintech.sst.helper.PermissionUtil
import com.fintech.sst.net.bean.UserInfoDetail
import com.fintech.sst.ui.activity.login.LoginActivity
import com.fintech.sst.ui.activity.notice.NoticeListActivity
import com.fintech.sst.ui.activity.order.OrderListActivity
import kotlinx.android.synthetic.main.activity_aisle_manager.*

class AisleManagerActivity : BaseActivity<AisleManagerContract.Presenter>(), AisleManagerContract.View {

    override fun updateUserInfo(userInfo: UserInfoDetail?) {
        tvUser.text = "当前用户：${userInfo?.nickName?:""}"
        et_aisle.setText(userInfo?.appId?:"")
        et_money.setText(userInfo?.totalAmount.toString())
    }

    override fun toLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun toOrderList() {
        startActivity(Intent(this, OrderListActivity::class.java))
    }

    override fun toSetting() {
    }

    override fun toNotifactionSetting() {
        PermissionUtil.setNotificationListener(this,100)
    }

    override fun toNoticeList() {
        startActivity(Intent(this, NoticeListActivity::class.java))
    }

    override val presenter: AisleManagerContract.Presenter = AisleManagerPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aisle_manager)
        lifecycle.addObserver(presenter)

        btnOrder.setOnClickListener { presenter.toOrder() }
        tv_refresh.setOnClickListener { presenter.userInfo() }
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN)
                presenter.toNoticeList()
            true
        }
    }

}
