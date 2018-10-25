package com.fintech.match.pay_2.wxapi

import android.content.Intent
import android.os.Bundle
import com.fintech.sst.App
import com.fintech.sst.base.BaseActivity
import com.fintech.sst.helper.METHOD_WECHAT
import com.fintech.sst.helper.WX_APPID
import com.fintech.sst.helper.WX_APPSECRET
import com.fintech.sst.helper.toast
import com.fintech.sst.net.*
import com.fintech.sst.ui.activity.aisle.AisleManagerActivity
import com.fintech.sst.ui.dialog.BindDialog
import com.fintech.sst.ui.fragment.login.LoginModel
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.*

class WXEntryActivity : BaseActivity<WXContract.Presenter>(), IWXAPIEventHandler, WXContract.View {
    override val presenter: WXContract.Presenter = WXPresenter(this)

    override fun onResp(resp: BaseResp) {
        when (resp.errCode) {
            0 -> {
                if (resp is SendAuth.Resp) {
                    val code = resp.code
                    getAccessToken(code)
                } else {
                    toast("请求类型错误")
                    finish()
                }
            }
            else -> {
                toast(resp.errStr)
                finish()
            }
        }
    }

    override fun onReq(req: BaseReq?) {

    }

    private lateinit var wxApi: IWXAPI
    private val model = LoginModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wxApi = WXAPIFactory.createWXAPI(this, WX_APPID, false)
        wxApi.handleIntent(intent, this)
    }

    private fun getAccessToken(code: String) {
        var wx_openId = ""
        val url = StringBuffer()
        url.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=$WX_APPID")
                .append("&secret=$WX_APPSECRET")
                .append("&code=$code")
                .append("&grant_type=authorization_code")
        ApiProducerModule.create(ApiService::class.java)
                .getWXAccessToken(url.toString())
                .subscribeOn(Schedulers.io())
                .map {
                    val result = it.string()
                    val jsonObject = JSONObject(result)
                    val openId = jsonObject.getString("openid")
                    openId
                }
                .flatMap { openId ->
                    wx_openId = openId
                    val request = HashMap<String, String>()
                    request["uid"] = openId
                    request["app_version"] = App.getAppContext().packageManager
                            .getPackageInfo(App.getAppContext().packageName, 0).versionCode.toString()
                    ApiProducerModule.create(ApiService::class.java).postAliCode(SignRequestBody(request))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, WXContract.View>(this) {
                    override fun onNext_(s: ResultEntity<Map<String, String>>) {

                        when (s.code) {
                            "40002" -> {
                                val dialog = BindDialog(this@WXEntryActivity, BindDialog.TYPE_BIND, BindDialog.ClickListener { name, password ->
                                    val request = HashMap<String, String>()
                                    request["userName"] = name
                                    request["password"] = password
                                    request["uid"] = wx_openId
                                    request["payMethod"] = METHOD_WECHAT
                                    request["app_version"] = App.getAppContext().packageManager
                                            .getPackageInfo(App.getAppContext().packageName, 0).versionCode.toString()
                                    ApiProducerModule.create(ApiService::class.java).bindAli(SignRequestBody(request))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, WXContract.View>(this@WXEntryActivity) {
                                                override fun onNext_(resultEntity: ResultEntity<Map<String, String>>) {
                                                    val result = resultEntity.result
                                                    if (result == null) {
                                                        toast(resultEntity.subMsg?: resultEntity.msg)
                                                        return
                                                    }

                                                    model.saveData(result, METHOD_WECHAT)
                                                    startActivity(Intent(this@WXEntryActivity,AisleManagerActivity::class.java).apply {
                                                        putExtra("typeLogin", METHOD_WECHAT)
                                                    })
                                                }

                                                override fun onError(error: String) {
                                                    toast(error)
                                                }
                                            })
                                }).apply {
                                    setOnDismissListener { finish() }
                                }
                                dialog.show()
                            }
                            "10000" -> {
                                val result = s.result

                                model.saveData(result, METHOD_WECHAT)
                                startActivity(Intent(this@WXEntryActivity,AisleManagerActivity::class.java).apply {
                                    putExtra("typeLogin", METHOD_WECHAT)
                                })

                                finish()
                            }
                        }
                    }

                    override fun onError(error: String) {
                        toast(error)
                        finish()
                    }
                })
    }
}
