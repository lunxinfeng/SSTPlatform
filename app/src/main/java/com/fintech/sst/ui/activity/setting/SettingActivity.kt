package com.fintech.sst.ui.activity.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.fintech.sst.R
import com.fintech.sst.base.BaseActivity
import com.fintech.sst.helper.PermissionUtil
import com.fintech.sst.ui.activity.login.LoginActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity<SettingContract.Presenter>(),SettingContract.View {
    override fun toNotifactionSetting() {
        PermissionUtil.setNotificationListener(this,100)
    }

    override fun toAppDetailActivity() {
        PermissionUtil.toAppDetailActivity(this)
    }

    override fun toLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override val presenter: SettingContract.Presenter = SettingPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        tv_notice_setting.setOnClickListener { toNotifactionSetting() }
        tv_permission_setting.setOnClickListener { toAppDetailActivity() }
        tv_exit_account.setOnClickListener {
            AlertDialog.Builder(this)
                    .setMessage("是否确定退出当前账号？")
                    .setPositiveButton("确定"){dialog, _ ->
                        dialog.dismiss()
                        presenter.exitAccount()
                    }
                    .setNegativeButton("取消"){dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }
}
