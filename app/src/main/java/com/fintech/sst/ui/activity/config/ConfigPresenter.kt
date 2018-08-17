package com.fintech.sst.ui.activity.config

import android.arch.lifecycle.LifecycleObserver
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class ConfigPresenter(val view: ConfigContract.View, private val model: ConfigModel = ConfigModel()) : ConfigContract.Presenter, LifecycleObserver {
    override val compositeDisposable: CompositeDisposable
        get() = CompositeDisposable()

    override fun check(address: String) {
        model.login()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, ConfigContract.View>(view) {
                    override fun onNext_(resultEntity: ResultEntity<Map<String, String>>?) {
                        resultEntity?.let {
                            if (it.subMsg.contains("用户不存在") ||
                                    it.subMsg.contains("通道不存在")) {
                                Configuration.putUserInfo(Constants.KEY_ADDRESS, address)
                                view.checkSuccess("服务器地址验证成功")
                            }
                        }
                    }

                    override fun onError(error: String?) {
                        if (error?.contains("用户不存在") == true ||
                                error?.contains("通道不存在") == true) {
                            Configuration.putUserInfo(Constants.KEY_ADDRESS, address)
                            view.checkSuccess("服务器地址验证成功")
                        } else {
                            view.checkFail(error ?: "")
                        }
                    }
                })
    }

}