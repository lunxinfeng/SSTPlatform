package com.fintech.sst.ui.fragment.login.account

import android.arch.lifecycle.LifecycleObserver
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.ui.fragment.login.LoginModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginAccountPresenter(val view: LoginAccountContract.View) : LoginAccountContract.Presenter, LifecycleObserver {
    private val model = LoginModel()

    override fun accountLogin(name:String,password:String) {
        model.accountLogin(name,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, LoginAccountContract.View>(view) {
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


    override val compositeDisposable = CompositeDisposable()
}