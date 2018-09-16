package com.fintech.sst.ui.activity.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fintech.sst.R
import com.fintech.sst.helper.Utils
import com.fintech.sst.helper.clickN
import com.fintech.sst.net.Constants
import com.fintech.sst.ui.fragment.login.account.LoginAccountFragment
import com.fintech.sst.ui.fragment.login.ali.LoginAliFragment
import kotlinx.android.synthetic.main.activity_login2.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class LoginActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

//        if (Configuration.noAddress()) {
//            startActivity(Intent(this, ConfigActivity::class.java))
//            finish()
//            return
//        } else {
//            Constants.baseUrl = Configuration.getUserInfoByKey(Constants.KEY_ADDRESS)
//        }

        supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_content, LoginAliFragment.newInstance())
                .commit()

        app_bar_image.clickN(7,"进入账号登录"){
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_content,LoginAccountFragment.newInstance())
                    .commit()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame_content)
        if (fragment is LoginAccountFragment)
            fragment.back()
        else
            super.onBackPressed()
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
