package com.fintech.sst.ui.fragment.login.ali

import android.arch.lifecycle.LifecycleObserver
import android.text.TextUtils
import com.alipay.sdk.app.AuthTask
import com.fintech.sst.helper.METHOD_ALI
import com.fintech.sst.helper.WX_APPID
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.ui.activity.login.AuthResult
import com.fintech.sst.ui.dialog.BindDialog
import com.fintech.sst.ui.fragment.login.LoginModel
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginAliPresenter(val view: LoginAliContract.View) : LoginAliContract.Presenter, LifecycleObserver {
    private val model = LoginModel()
    private var ali_user_id = ""
    override fun aliLogin() {
        model.aliLoginUrl()
                .subscribeOn(Schedulers.io())
                .flatMap { resultEntity ->
                    val authTask = AuthTask(view.context)
                    val url = resultEntity.result["url"]
                    // 调用授权接口，获取授权结果
                    val result = authTask.authV2(url, true)
                    Observable.just(result)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { stringStringMap ->
                    val authResult = AuthResult(stringStringMap, true)
                    val resultStatus = authResult.resultStatus

                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.resultCode, "200")){
                        Observable.just(authResult.alipayUserId)
                    }else{
                        Observable.error(Exception("授权失败"))
                    }
                }
                .observeOn(Schedulers.io())
                .flatMap { uid ->
                    ali_user_id = uid
                    model.postAliCode(ali_user_id)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, LoginAliContract.View>(view) {
                    override fun onNext_(s: ResultEntity<Map<String, String>>) {
                        when (s.code) {
                            "40002" -> {
                                val dialog = BindDialog(view.context, BindDialog.TYPE_BIND, BindDialog.ClickListener { name, password ->
                                    model.bindAli(name,password,ali_user_id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, LoginAliContract.View>(view) {
                                                override fun onNext_(resultEntity: ResultEntity<Map<String, String>>) {
                                                    val result = resultEntity.result
                                                    if (result == null){
                                                        view.loginFail(resultEntity.subMsg?:resultEntity.msg)
                                                        return
                                                    }
                                                    model.saveData(result, METHOD_ALI,password)

                                                    view.loginSuccess()
                                                }

                                                override fun onError(error: String) {
                                                    view.loginFail(error)
                                                }
                                            })
                                })
                                dialog.show()
                            }
                            "10000" -> {
                                val result = s.result

                                model.saveData(result,METHOD_ALI)

                                view.loginSuccess()
                            }
                            else ->{
                                view.loginFail(s.subMsg)
                            }
                        }
                    }

                    override fun onError(error: String) {
                        view.loginFail(error)
                    }
                })
    }

    override fun wechatLogin() {
        val api = WXAPIFactory.createWXAPI(view.context,WX_APPID,true)
        api.registerApp(WX_APPID)

        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        api.sendReq(req)
    }

    override val compositeDisposable = CompositeDisposable()
}