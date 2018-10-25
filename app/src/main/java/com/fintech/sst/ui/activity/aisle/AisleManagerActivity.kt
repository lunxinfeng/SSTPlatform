package com.fintech.sst.ui.activity.aisle

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.fintech.sst.R
import com.fintech.sst.base.BaseActivity
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.*
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants
import com.fintech.sst.net.bean.AisleInfo
import com.fintech.sst.service.HeartService
import com.fintech.sst.ui.activity.config.ConfigActivity
import com.fintech.sst.ui.activity.login.LoginActivity
import com.fintech.sst.ui.activity.notice.NoticeListActivity
import com.fintech.sst.ui.activity.order.OrderListActivity
import com.fintech.sst.ui.activity.setting.SettingActivity
import com.fintech.sst.ui.dialog.AisleManagerDialog
import kotlinx.android.synthetic.main.activity_aisle_manager2.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class AisleManagerActivity : BaseActivity<AisleManagerContract.Presenter>()
        , AisleManagerContract.View, EasyPermissions.PermissionCallbacks {

    private val adapter = NoticeAdapter(R.layout.item_notice_manager, null)

    override fun updateNoticeList(notice: Notice) {
        adapter.data.add(0, notice)
        adapter.notifyItemInserted(0)
        adapter.notifyDataSetChanged()
    }

    override fun updateUserInfo(info: AisleInfo?, type: String) {
        when (type) {
            METHOD_ALI -> {
                et_aisle_ali.setText(info?.account ?: "")
                val text = et_money_ali.text.toString().split("/")
                et_money_ali.setText("${info?.realAmount.toString()}/${text[1]}")
                et_successRate_ali.setText("${info?.ok?.toString()?.toFloatOrNull() ?: 0 * 100}%")
                switch_aisle_ali.isChecked = info?.enable == "1"
            }
            METHOD_WECHAT -> {
                et_aisle_wechat.setText(info?.account ?: "")
                val text = et_money_wechat.text.toString().split("/")
                et_money_wechat.setText("${info?.realAmount.toString()}/${text[1]}")
                et_successRate_wechat.setText("${info?.ok?.toString()?.toFloatOrNull() ?: 0 * 100}%")
                switch_aisle_wechat.isChecked = info?.enable == "1"
            }
        }
//        tvUser.text = "${info?.appLoginName ?: ""}"
    }

    override fun updateLocalInfo(notices: List<Notice>, type: String) {
        var amountTotal = 0.0
        notices.forEach { amountTotal += it.amount.toFloat() }

        when (type) {
            METHOD_ALI -> {
                val text = et_money_ali.text.toString().split("/")
                et_money_ali.setText("${text[0]}/$amountTotal")
            }
            METHOD_WECHAT -> {
                val text = et_money_wechat.text.toString().split("/")
                et_money_wechat.setText("${text[0]}/$amountTotal")
            }
        }
    }

    override fun aisleStatusResult(success: Boolean, type: String) {
        showToast(if (success) "操作成功" else "操作失败")
        when (type) {
            METHOD_ALI -> {
                if (!success)
                    switch_aisle_ali.isChecked = !switch_aisle_ali.isChecked
                if (success) {
                    lastNoticeTimeAli = System.currentTimeMillis()
                }
            }
            METHOD_WECHAT -> {
                if (!success)
                    switch_aisle_wechat.isChecked = !switch_aisle_wechat.isChecked
                if (success) {
                    lastNoticeTimeWeChat = System.currentTimeMillis()
                }
            }
        }
    }

    override fun aisleRefreshResult(success: Boolean) {
        showToast(if (success) "操作成功" else "操作失败")
    }

    override fun aisleDeleteResult(success: Boolean, type: String) {
        showToast(if (success) "操作成功" else "操作失败")
        if (success) {
            Configuration.clearUserInfo(type)
            exitLogin(type)
        }
    }

    override fun exitLogin(type: String) {
        when (type) {
            METHOD_ALI -> {
                tvLoginAli.visibility = View.VISIBLE
                hideAli.visibility = View.VISIBLE
                exitAli.visibility = View.GONE
            }
            METHOD_WECHAT -> {
                tvLoginWeChat.visibility = View.VISIBLE
                hideWechat.visibility = View.VISIBLE
                exitWechat.visibility = View.GONE
            }
        }
    }

    override fun checkOrderType() {
        MaterialDialog(this)
                .listItems(
                        items = listOf("支付宝通道", "微信通道"),
                        selection = { dialog, index, _ ->
                            when (index) {
                                0 -> toOrderList(METHOD_ALI)
                                1 -> toOrderList(METHOD_WECHAT)
                            }
                            dialog.dismiss()
                        }
                )
                .show()
    }

    override fun toOrderList(type: String) {
        startActivity(Intent(this, OrderListActivity::class.java).apply {
            putExtra("type", type)
        })
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

    override fun toAisleManager(type: String) {
        AisleManagerDialog(this, object : AisleManagerDialog.ClickListener {
            override fun onRefresh() {
                presenter.aisleRefresh(type)
            }

            override fun onDel() {
                presenter.aisleDelete(type)
            }
        }).show()
    }

    override fun viewHideOrShow(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
        menuShowAll?.isVisible = !show
//        if (show && view.visibility != View.GONE) return
//        if (!show && view.visibility != View.VISIBLE) return
//
//        val va = if (show) ValueAnimator.ofFloat(0f,1f) else ValueAnimator.ofFloat(1f,0f)
//        va.duration = 400
//        val height = 280
//        va.addUpdateListener {
//            val f = it.animatedValue as Float
//            println("$f\t${view.height}\t${(height * f).toInt()}")
//            view.layoutParams = view.layoutParams.apply {
//                this.height = (height * f).toInt()
//            }
//            view.alpha = f
////            view.requestLayout()
//        }
//        va.addListener(object : Animator.AnimatorListener {
//            override fun onAnimationRepeat(animation: Animator?) {
//            }
//
//            override fun onAnimationEnd(animation: Animator?) {
//                if (!show){
////                    view.alpha = 0f
//                    println("onAnimationEnd:${view.height}\t${view.layoutParams.height}\t${view.alpha}")
//                    view.visibility = View.GONE
//                }
//            }
//
//            override fun onAnimationCancel(animation: Animator?) {
//            }
//
//            override fun onAnimationStart(animation: Animator?) {
//                if (show){
////                    view.alpha = 0f
////                    view.layoutParams.height = 0
////                    view.requestLayout()
//                    println("onAnimationStart:${view.height}\t${view.layoutParams.height}\t${view.alpha}")
//                    view.visibility = View.VISIBLE
//                }
//            }
//        })
//        va.start()
    }

    override val presenter: AisleManagerContract.Presenter = AisleManagerPresenter(this)
    private var menuShowAll:MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aisle_manager2)
        lifecycle.addObserver(presenter)

        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.menu_asile)

//        startHeartService()

        tvLoginAli.setOnClickListener { presenter.aliLogin() }
        tvLoginWeChat.setOnClickListener { presenter.wechatLogin() }
        hideAli.setOnClickListener { viewHideOrShow(cardViewAli, false) }
        hideWechat.setOnClickListener { viewHideOrShow(cardViewWechat, false) }
        exitAli.setOnClickListener { presenter.exitLogin(METHOD_ALI) }
        exitWechat.setOnClickListener { presenter.exitLogin(METHOD_WECHAT) }

        tv_refresh_ali.setOnClickListener { presenter.userInfo(METHOD_ALI) }
        tv_refresh_wechat.setOnClickListener { presenter.userInfo(METHOD_WECHAT) }
        switch_aisle_ali.apply {
            setOnCheckedChangeListener { view, isChecked ->
                if (view.isPressed)
                    presenter.aisleStatus(isChecked, METHOD_ALI)
            }
        }
        switch_aisle_wechat.apply {
            setOnCheckedChangeListener { view, isChecked ->
                if (view.isPressed)
                    presenter.aisleStatus(isChecked, METHOD_WECHAT)
            }
        }
        textView22.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && !switch_aisle_ali.isChecked)
                presenter.toAisleManager(METHOD_ALI)
            false
        }
        textView2.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && !switch_aisle_wechat.isChecked)
                presenter.toAisleManager(METHOD_WECHAT)
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

        closeTime = when (Configuration.getUserInfoByKey(Constants.KEY_CLOSE_TIME).toIntOrNull()
                ?: 2) {
            0 -> 60 * 1000 * 1
            1 -> 60 * 1000 * 2
            2 -> 60 * 1000 * 3
            3 -> 60 * 1000 * 5
            4 -> 60 * 1000 * 10
            5 -> Long.MAX_VALUE
            else -> Long.MAX_VALUE
        }

        toolbar.clickN(10, "进入地址配置页面") { startActivity(Intent(this, ConfigActivity::class.java)) }

    }

    override fun onResume() {
        super.onResume()
        requestAllPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_asile, menu)
        menuShowAll = menu?.findItem(R.id.action_show_all)
        menuShowAll?.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_show_all -> {
                viewHideOrShow(cardViewAli, true)
                viewHideOrShow(cardViewWechat, true)
            }
            R.id.action_order_manager -> {
                presenter.toOrder()
            }
            R.id.action_setting -> {
                presenter.toSetting()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        stopHeartService()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getBooleanExtra("exit", false) == true) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        if (intent?.getStringExtra("typeLogin") == METHOD_WECHAT) {
            loginSuccess(METHOD_WECHAT)
        }
    }

    override fun onBackPressed() {

    }

    private fun startHeartService() {
        startService(Intent(this, HeartService::class.java))
    }

    private fun stopHeartService() {
        stopService(Intent(this, HeartService::class.java))
    }

    @AfterPermissionGranted(Constants.ALL_PERMISSION)
    fun requestAllPermission() {
        if (!EasyPermissions.hasPermissions(this, *Constants.PERMISSIONS_GROUP)) {
            EasyPermissions.requestPermissions(this, this.getString(R.string.rationale_message),
                    Constants.ALL_PERMISSION, *Constants.PERMISSIONS_GROUP)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Utils.goPermissionSetting(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }


    //----------------login---------------------
    override val context: FragmentActivity = this

    override fun loginSuccess(type: String) {
//        showToast("登录成功")
        when (type) {
            METHOD_ALI -> {
                tvLoginAli.visibility = View.GONE
                hideAli.visibility = View.GONE
                exitAli.visibility = View.VISIBLE
            }
            METHOD_WECHAT -> {
                tvLoginWeChat.visibility = View.GONE
                hideWechat.visibility = View.GONE
                exitWechat.visibility = View.VISIBLE
            }
        }
        startHeartService()
    }

    override fun loginFail(hint: String) {
        showToast(hint)
    }
}
