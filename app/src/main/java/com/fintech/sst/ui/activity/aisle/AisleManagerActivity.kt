package com.fintech.sst.ui.activity.aisle

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.fintech.sst.R
import com.fintech.sst.base.BaseActivity
import com.fintech.sst.data.db.DB
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.*
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants
import com.fintech.sst.net.Constants.*
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.bean.AisleInfo
import com.fintech.sst.service.AliService
import com.fintech.sst.service.BankService
import com.fintech.sst.service.HeartService
import com.fintech.sst.ui.activity.config.ConfigActivity
import com.fintech.sst.ui.activity.login.LoginActivity
import com.fintech.sst.ui.activity.notice.NoticeListActivity
import com.fintech.sst.ui.activity.order.OrderListActivity
import com.fintech.sst.ui.activity.setting.SettingActivity
import com.fintech.sst.ui.dialog.AisleManagerDialog
import com.fintech.sst.ui.dialog.BindDialog
import com.fintech.sst.ui.widget.MenuCardView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_aisle_manager2.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class AisleManagerActivity : BaseActivity<AisleManagerContract.Presenter>()
        , AisleManagerContract.View, EasyPermissions.PermissionCallbacks {
    private val TAG = "AisleManagerActivity"
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
                et_money_ali.setText(info?.realAmount.toString())
                et_money_notice_ali.setText(info?.tradeNoticeLogAmount.toString())
                et_successRate_ali.setText("${(info?.ok?.toString()?.toFloatOrNull()
                        ?: 0f) * 100}%")
                switch_aisle_ali.isChecked = info?.enable == "1"
                if (switch_aisle_ali.isChecked){
                    if (!isServiceRunning(this@AisleManagerActivity,AliService::class.java.name)){
                        debug(TAG,"支付宝监听服务没有启动，开始启动")
                        startAliService()
                    }
                }
            }
            METHOD_WECHAT -> {
                et_aisle_wechat.setText(info?.account ?: "")
                et_money_wechat.setText(info?.realAmount.toString())
                et_money_notice_wechat.setText(info?.tradeNoticeLogAmount.toString())
                et_successRate_wechat.setText("${(info?.ok?.toString()?.toFloatOrNull()
                        ?: 0f) * 100}%")
                switch_aisle_wechat.isChecked = info?.enable == "1"
            }
            METHOD_BANK -> {
                et_aisle_Bank.setText(info?.account ?: "")
                et_money_Bank.setText(info?.realAmount.toString())
                et_money_notice_Bank.setText(info?.tradeNoticeLogAmount.toString())
                et_successRate_Bank.setText("${(info?.ok?.toString()?.toFloatOrNull()
                        ?: 0f) * 100}%")
                switch_aisle_Bank.isChecked = info?.enable == "1"
                if (switch_aisle_Bank.isChecked){
                    if (!isServiceRunning(this@AisleManagerActivity,BankService::class.java.name)){
                        debug(TAG,"短信监听服务没有启动，开始启动")
                        startBankService()
                    }
                }
            }
        }
//        tvUser.text = "${info?.appLoginName ?: ""}"
    }

    override fun updateLocalInfo(notices: List<Notice>, type: String) {
//        var amountTotal = 0.0
//        notices.forEach { amountTotal += it.amount.toFloat() }
//
//        when (type) {
//            METHOD_ALI -> {
//                et_money_notice_ali.setText(amountTotal.toString())
//            }
//            METHOD_WECHAT -> {
//                et_money_notice_wechat.setText(amountTotal.toString())
//            }
//        }
    }

    override fun aisleStatusResult(success: Boolean, type: String) {
        showToast(if (success) "操作成功" else "操作失败")
        presenter.userInfo(type)
//        when (type) {
//            METHOD_ALI -> {
//                if (!success)
//                    switch_aisle_ali.isChecked = !switch_aisle_ali.isChecked
////                if (success) {
////                    lastNoticeTimeAli = System.currentTimeMillis()
////                }
//            }
//            METHOD_WECHAT -> {
//                if (!success)
//                    switch_aisle_wechat.isChecked = !switch_aisle_wechat.isChecked
////                if (success) {
////                    lastNoticeTimeWeChat = System.currentTimeMillis()
////                }
//            }
//            METHOD_BANK -> {
//                if (!success)
//                    switch_aisle_Bank.isChecked = !switch_aisle_Bank.isChecked
////                if (success) {
////                    lastNoticeTimeWeChat = System.currentTimeMillis()
////                }
//            }
//        }
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
                cardViewAli.setStatus(MenuCardView.Status.CLOSE)
            }
            METHOD_WECHAT -> {
                tvLoginWeChat.visibility = View.VISIBLE
                hideWechat.visibility = View.VISIBLE
                exitWechat.visibility = View.GONE
                cardViewWechat.setStatus(MenuCardView.Status.CLOSE)
            }
            METHOD_BANK -> {
                tvLoginBank.visibility = View.VISIBLE
                hideBank.visibility = View.VISIBLE
                exitBank.visibility = View.GONE
                cardViewBank.setStatus(MenuCardView.Status.CLOSE)
            }
        }
    }

    override fun checkOrderType() {
        MaterialDialog(this)
                .listItems(
                        items = listOf("支付宝通道", "微信通道", "银行通道"),
                        selection = { dialog, index, _ ->
                            when (index) {
                                0 -> toOrderList(METHOD_ALI)
                                1 -> toOrderList(METHOD_WECHAT)
                                2 -> toOrderList(METHOD_BANK)
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

    override fun checkDaMaType() {
        MaterialDialog(this)
                .listItems(
                        items = listOf("支付宝通道", "微信通道"),
                        selection = { dialog, index, _ ->
                            when (index) {
                                0 -> {
                                    val account = Configuration.getUserInfoByKey(Constants.KEY_USER_NAME_ALI)
                                    val password = Configuration.getUserInfoByKey(Constants.KEY_PASSWORD_ALI)
                                    if (password.isNullOrEmpty()) {
                                        checkPassword {
                                            presenter.checkLogin(account, it, METHOD_ALI)
                                        }
                                    } else {
                                        toDaMa(account, password, METHOD_ALI)
                                    }
                                }
                                1 -> {
                                    val account = Configuration.getUserInfoByKey(Constants.KEY_USER_NAME_WECHAT)
                                    val password = Configuration.getUserInfoByKey(Constants.KEY_PASSWORD_WECHAT)
                                    if (password.isNullOrEmpty()) {
                                        checkPassword {
                                            presenter.checkLogin(account, it, METHOD_WECHAT)
                                        }
                                    } else {
                                        toDaMa(account, password, METHOD_WECHAT)
                                    }
                                }
                            }
                            dialog.dismiss()
                        }
                )
                .show()
    }

    override fun checkPassword(callback: (password: String) -> Unit) {
        MaterialDialog(this)
                .title(
                        text = "验证密码"
                )
                .input(
                        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                )
                .positiveButton(
                        text = "确定",
                        click = {
                            val password = it.getInputField()?.text.toString()
                            callback.invoke(password)
                        }
                )
                .show()
    }

    override fun toDaMa(account: String, password: String, type: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_MAIN
            component = ComponentName("com.fintech.match.pay_3", "com.fintech.lxf.ui.activity.login.LoginActivity")
            putExtra("account", account)
            putExtra("password", password)
            putExtra("loginType", type)
        }
        startActivity(intent)
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

    override fun showHintDialog(content: String) {
        MaterialDialog(this)
                .title(
                        text = "警告"
                )
                .message(
                        text = content
                )
                .positiveButton {
                    stopWarning()
                }
                .show()
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
    private var menuShowAll: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aisle_manager2)
        lifecycle.addObserver(presenter)

        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.menu_asile)

//        startHeartService()

        tvLoginAli.setOnClickListener {
            presenter.aliLogin()
        }
        tvLoginAli.setOnLongClickListener { accountLogin(METHOD_ALI) }
        tvLoginWeChat.setOnClickListener { presenter.wechatLogin() }
        tvLoginWeChat.setOnLongClickListener { accountLogin(METHOD_WECHAT) }
        tvLoginBank.setOnClickListener { accountLogin(METHOD_BANK) }

        hideAli.setOnClickListener { viewHideOrShow(cardViewAli, false) }
        hideWechat.setOnClickListener { viewHideOrShow(cardViewWechat, false) }
        hideBank.setOnClickListener { viewHideOrShow(cardViewBank, false) }

        exitAli.setOnClickListener {
            Configuration.removeUserInfoByKey(KEY_USER_NAME_ALI)
            Configuration.removeUserInfoByKey(KEY_PASSWORD_ALI)
            presenter.exitLogin(METHOD_ALI)
            stopAliService()
        }
        exitWechat.setOnClickListener {
            Configuration.removeUserInfoByKey(KEY_USER_NAME_WECHAT)
            Configuration.removeUserInfoByKey(KEY_PASSWORD_WECHAT)
            presenter.exitLogin(METHOD_WECHAT)
        }
        exitBank.setOnClickListener {
            Configuration.removeUserInfoByKey(KEY_USER_NAME_BANK)
            Configuration.removeUserInfoByKey(KEY_PASSWORD_BANK)
            presenter.exitLogin(METHOD_BANK)
            stopBankService()
        }

        tv_refresh_ali.setOnClickListener { presenter.userInfo(METHOD_ALI) }
        tv_refresh_wechat.setOnClickListener { presenter.userInfo(METHOD_WECHAT) }
        tv_refresh_Bank.setOnClickListener { presenter.userInfo(METHOD_BANK) }

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
        switch_aisle_Bank.apply {
            setOnCheckedChangeListener { view, isChecked ->
                if (view.isPressed)
                    presenter.aisleStatus(isChecked, METHOD_BANK)
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
        textView222.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && !switch_aisle_Bank.isChecked)
                presenter.toAisleManager(METHOD_BANK)
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
        closeOrderNum = Configuration.getUserInfoByKey(Constants.KEY_CLOSE_ORDER_NUM).toIntOrNull()?:15

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
        viewHideOrShow(cardViewWechat, false)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_show_all -> {
                viewHideOrShow(cardViewAli, true)
                viewHideOrShow(cardViewWechat, true)
                viewHideOrShow(cardViewBank, true)
            }
            R.id.action_order_manager -> {
                presenter.toOrder()
            }
            R.id.action_setting -> {
                presenter.toSetting()
            }
//            R.id.action_dama -> {
//                presenter.toDaMa()
//            }
            R.id.action_check -> {
                MaterialDialog(this@AisleManagerActivity)
                        .listItems(
                                items = listOf("支付宝通道","微信通道"),
                                selection = { dialog, index, text ->
                                    when(index){
                                        0 -> check(METHOD_ALI)
                                        1 -> check(METHOD_WECHAT)
                                    }
                                }
                        )
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun check(type: String) {
        Observable
                .create<List<Notice>> {
                    val list = DB.queryLastTwo(type.toInt())
                    if (list == null)
                        it.onNext(listOf<Notice>())
                    else
                        it.onNext(list)
                    it.onComplete()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<List<Notice>, AisleManagerContract.View>(this,true) {
                    override fun onNext_(t: List<Notice>) {
                        MaterialDialog(this@AisleManagerActivity)
                                .message(
                                        text = when {
                                            t.size == 2 -> "最后一条：${t[0]}\n倒数二条：${t[1]}"
                                            t.size == 1 -> "数据库只有一条：${t[0]}"
                                            t.isEmpty() -> "数据库无数据"
                                            else -> "代码异常"
                                        }
                                )
                                .show()
                    }

                    override fun onError(error: String?) {

                    }
                })
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

    private fun stopAliService() {
        stopService(Intent(this, AliService::class.java))
    }

    private fun startAliService() {
        startService(Intent(this, AliService::class.java))
    }

    private fun stopBankService() {
        stopService(Intent(this, BankService::class.java))
    }

    private fun startBankService() {
        startService(Intent(this, BankService::class.java))
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

    override fun accountLogin(type: String): Boolean {
        BindDialog(this, BindDialog.TYPE_LOGIN) { name, password ->
            presenter.accountLogin(name, password, type)
        }.show()
        return true
    }

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
            METHOD_BANK -> {
                tvLoginBank.visibility = View.GONE
                hideBank.visibility = View.GONE
                exitBank.visibility = View.VISIBLE
            }
        }
        startHeartService()
    }

    override fun loginFail(hint: String) {
        showToast(hint)
    }
}
