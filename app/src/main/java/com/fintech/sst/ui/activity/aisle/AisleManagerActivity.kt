package com.fintech.sst.ui.activity.aisle

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import com.fintech.sst.R
import com.fintech.sst.base.BaseActivity
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.PermissionUtil
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.bean.AisleInfo
import com.fintech.sst.ui.activity.login.LoginActivity
import com.fintech.sst.ui.activity.notice.NoticeListActivity
import com.fintech.sst.ui.activity.order.OrderListActivity
import com.fintech.sst.ui.activity.setting.SettingActivity
import com.fintech.sst.ui.dialog.AisleManagerDialog
import kotlinx.android.synthetic.main.activity_aisle_manager.*

class AisleManagerActivity : BaseActivity<AisleManagerContract.Presenter>(), AisleManagerContract.View {
    private val adapter = NoticeAdapter(R.layout.item_notice_manager, null)

    override fun updateNoticeList(notice: Notice) {
        adapter.data.add(0, notice)
        adapter.notifyItemInserted(0)
        adapter.notifyDataSetChanged()
    }

    override fun updateUserInfo(info: AisleInfo?) {
        tvUser.text = "当前用户：${info?.appLoginName ?: ""}"
        et_aisle.setText(info?.account ?: "")
        et_money.setText(info?.realAmount.toString())
        et_successRate.setText(info?.ok.toString())
        switch_aisle.isChecked = info?.enable == "1"
    }

    override fun aisleStatusResult(success: Boolean) {
        if (!success)
            switch_aisle.isChecked = !switch_aisle.isChecked
        showToast(if (success) "操作成功" else "操作失败")
    }

    override fun aisleRefreshResult(success: Boolean) {
        showToast(if (success) "操作成功" else "操作失败")
    }

    override fun aisleDeleteResult(success: Boolean) {
        showToast(if (success) "操作成功" else "操作失败")
        if (success){
            Configuration.clearUserInfo()
            toLogin()
        }
    }

    override fun toLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun toOrderList() {
        startActivity(Intent(this, OrderListActivity::class.java))
    }

    override fun toSetting() {
        startActivity(Intent(this, SettingActivity::class.java))
    }

    override fun toNotifactionSetting() {
        PermissionUtil.setNotificationListener(this, 100)
    }

    override fun toNoticeList() {
        startActivity(Intent(this, NoticeListActivity::class.java))
    }

    override fun toAisleManager() {
        AisleManagerDialog(this, object : AisleManagerDialog.ClickListener {
            override fun onRefresh() {
                presenter.aisleRefresh()
            }

            override fun onDel() {
                presenter.aisleDelete()
            }
        }).show()
    }

    override val presenter: AisleManagerContract.Presenter = AisleManagerPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aisle_manager)
        lifecycle.addObserver(presenter)

        btnOrder.setOnClickListener { presenter.toOrder() }
        btnSetting.setOnClickListener { presenter.toSetting() }
        tv_refresh.setOnClickListener { presenter.userInfo() }
        switch_aisle.apply {
            setOnCheckedChangeListener { view, isChecked ->
                if (view.isPressed)
                    presenter.aisleStatus(isChecked)
            }
        }
        textView2.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN)
                presenter.toAisleManager()
            false
        }
        recyclerView.apply {
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN)
                    presenter.toNoticeList()
                false
            }

            layoutManager = LinearLayoutManager(this@AisleManagerActivity)
            adapter = this@AisleManagerActivity.adapter
        }
    }

    override fun onBackPressed() {

    }
}
