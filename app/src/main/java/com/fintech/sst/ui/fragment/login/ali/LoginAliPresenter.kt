package com.fintech.sst.ui.fragment.login.ali

import android.arch.lifecycle.LifecycleObserver
import android.text.TextUtils
import com.alipay.sdk.app.AuthTask
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.ui.activity.login.AuthResult
import com.fintech.sst.ui.dialog.BindDialog
import com.fintech.sst.ui.fragment.login.LoginModel
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class LoginAliPresenter(val view: LoginAliContract.View) : LoginAliContract.Presenter, LifecycleObserver {
    private val model = LoginModel()
    private var ali_user_id = ""
    override fun aliLogin() {
        model.aliLoginUrl()
                .subscribeOn(Schedulers.io())
                .flatMap(Function<ResultEntity<Map<String, String>>, ObservableSource<Map<String, String>>> { resultEntity ->
                    val authTask = AuthTask(view.context)
                    val url = resultEntity.result["url"]
                    // 调用授权接口，获取授权结果
                    val result = authTask.authV2(url, true)
                    Observable.just(result)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Function<Map<String, String>, ObservableSource<String>> { stringStringMap ->
                    val authResult = AuthResult(stringStringMap, true)
                    val resultStatus = authResult.resultStatus

                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.resultCode, "200")){
                        Observable.just(authResult.alipayUserId)
                    }else{
                        Observable.error(Exception("授权失败"))
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(Function<String, ObservableSource<ResultEntity<Map<String, String>>>> { uid ->
                    ali_user_id = uid
                    model.postAliCode(ali_user_id)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, LoginAliContract.View>(view) {
                    override fun onNext_(s: ResultEntity<Map<String, String>>) {
                        when (s.code) {
                            "40002" -> {
                                val dialog = BindDialog(view.context, BindDialog.TYPE_BIND, object : BindDialog.ClickListener {
                                    override fun onClick(name: String, password: String) {
                                        model.bindAli(name,password,ali_user_id)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, LoginAliContract.View>(view) {
                                                    override fun onNext_(resultEntity: ResultEntity<Map<String, String>>) {
                                                        val result = resultEntity.result
                                                        if (result == null){
                                                            view.loginFail(resultEntity.subMsg)
                                                            return
                                                        }
                                                        model.saveData(result)

                                                        view.loginSuccess()
                                                    }

                                                    override fun onError(error: String) {
                                                        view.loginFail(error)
                                                    }
                                                })
                                    }
                                })
                                dialog.show()
                            }
                            "10000" -> {
                                val result = s.result

                                model.saveData(result)

                                view.loginSuccess()
                            }
                        }
                    }

                    override fun onError(error: String) {
                        view.loginFail(error)
                    }
                })
    }

    override val compositeDisposable = CompositeDisposable()
}