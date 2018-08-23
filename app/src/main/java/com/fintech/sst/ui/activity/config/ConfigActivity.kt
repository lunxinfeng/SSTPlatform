package com.fintech.sst.ui.activity.config

import android.content.Intent
import android.os.Bundle
import com.fintech.sst.R
import com.fintech.sst.base.BaseActivity
import com.fintech.sst.helper.Utils
import com.fintech.sst.helper.onChange
import com.fintech.sst.net.Constants
import com.fintech.sst.ui.activity.login.LoginActivity
import kotlinx.android.synthetic.main.activity_config.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class ConfigActivity : BaseActivity<ConfigContract.Presenter>(), ConfigContract.View, EasyPermissions.PermissionCallbacks {

    override var presenter: ConfigContract.Presenter = ConfigPresenter(this)

    override fun checkSuccess(hint: String) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun checkFail(hint: String) {
        showToast(hint)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        setSupportActionBar(toolbar)

        etAddress.onChange {
            Constants.baseUrl = it.toString()
//            btnNext.isEnabled = RegexUtil.isMatch(RegexUtil.URL,Constants.baseUrl)
            btnNext.isEnabled = true
        }
        btnNext.setOnClickListener { presenter.check(Constants.baseUrl) }
    }


    override fun onResume() {
        super.onResume()
        requestAllPermission()
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
}
