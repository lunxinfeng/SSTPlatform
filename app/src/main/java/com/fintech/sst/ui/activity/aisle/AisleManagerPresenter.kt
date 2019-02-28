package com.fintech.sst.ui.activity.aisle

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.text.TextUtils
import com.alipay.sdk.app.AuthTask
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.*
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants.*
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.bean.AisleInfo
import com.fintech.sst.ui.activity.login.AuthResult
import com.fintech.sst.ui.dialog.BindDialog
import com.fintech.sst.ui.fragment.login.LoginModel
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AisleManagerPresenter(val view: AisleManagerContract.View, private val model: AisleManagerModel = AisleManagerModel()) : AisleManagerContract.Presenter, LifecycleObserver {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var clickNum = 0

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
//        if (Configuration.noLogin()) {
//            view.exitLogin()
//            return
//        }
//        else {
//            Constants.baseUrl = Configuration.getUserInfoByKey(Constants.KEY_ADDRESS)
//        }

//        JobServiceCompact.startJob(1000)
        subscribeNotice()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
//        if (!PermissionUtil.isNotificationListenerEnabled()) {
//            view.toNotifactionSetting()
//            view.showToast("请打开随身听通知监听权限")
//        }
        clickNum = 0

        if (Configuration.isLogin(METHOD_ALI)){
            userInfo(METHOD_ALI)
            view.loginSuccess(METHOD_ALI)
        }

        if (Configuration.isLogin(METHOD_WECHAT)){
            userInfo(METHOD_WECHAT)
            view.loginSuccess(METHOD_WECHAT)
        }

        if (Configuration.isLogin(METHOD_BANK)){
            userInfo(METHOD_BANK)
            view.loginSuccess(METHOD_BANK)
        }

        if (Configuration.isLogin(METHOD_YUN)){
            userInfo(METHOD_YUN)
            view.loginSuccess(METHOD_YUN)
        }
    }

    override fun userInfo(type: String) {
        model.userInfo(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<AisleInfo>, AisleManagerContract.View>(view) {
                    override fun onNext_(t: ResultEntity<AisleInfo>?) {
                        model.setAisleInfo(t?.result,type)
                        view.updateUserInfo(t?.result,type)
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun aisleStatus(open: Boolean,type: String) {
        model.aisleStatus(open,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<String>, AisleManagerContract.View>(view) {
                    override fun onNext_(t: ResultEntity<String>?) {
                        view.aisleStatusResult(t?.msg.toString().contains("success"),type)
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun aisleRefresh(type: String) {
        model.aisleRefresh(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<String>, AisleManagerContract.View>(view) {
                    override fun onNext_(t: ResultEntity<String>?) {
                        view.aisleRefreshResult(t?.msg.toString().contains("success"))
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun aisleDelete(type: String) {
        model.aisleDelete(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<String>, AisleManagerContract.View>(view) {
                    override fun onNext_(t: ResultEntity<String>?) {
                        view.aisleDeleteResult(t?.msg.toString().contains("success"),type)
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun toOrder() {
        if ((Configuration.isLogin(METHOD_ALI) && Configuration.isLogin(METHOD_WECHAT)) ||
                (Configuration.isLogin(METHOD_ALI) && Configuration.isLogin(METHOD_BANK)) ||
                (Configuration.isLogin(METHOD_ALI) && Configuration.isLogin(METHOD_YUN)) ||
                (Configuration.isLogin(METHOD_WECHAT) && Configuration.isLogin(METHOD_YUN)) ||
                (Configuration.isLogin(METHOD_BANK) && Configuration.isLogin(METHOD_YUN)) ||
                (Configuration.isLogin(METHOD_WECHAT) && Configuration.isLogin(METHOD_BANK))){
            view.checkOrderType()
        }else if (Configuration.isLogin(METHOD_ALI)){
            view.toOrderList(METHOD_ALI)
        }else if (Configuration.isLogin(METHOD_WECHAT)){
            view.toOrderList(METHOD_WECHAT)
        }else if (Configuration.isLogin(METHOD_BANK)){
            view.toOrderList(METHOD_BANK)
        }else if (Configuration.isLogin(METHOD_YUN)){
            view.toOrderList(METHOD_YUN)
        }else{
            view.showToast("请先登录")
        }
    }

    override fun toSetting() {
        view.toSetting()
    }

    override fun toDaMa() {
        val loginAli = Configuration.isLogin(METHOD_ALI)
        val loginWechat = Configuration.isLogin(METHOD_WECHAT)
        if (loginAli && loginWechat){
            view.checkDaMaType()
        }else if (loginAli){
            val account = Configuration.getUserInfoByKey(KEY_USER_NAME_ALI)
            val password = Configuration.getUserInfoByKey(KEY_PASSWORD_ALI)
            if (password.isNullOrEmpty()) {
                view.checkPassword {
                    checkLogin(account,it, METHOD_ALI)
                }
            } else {
                view.toDaMa(account, password, METHOD_ALI)
            }
        }else if(loginWechat){
            val account = Configuration.getUserInfoByKey(KEY_USER_NAME_WECHAT)
            val password = Configuration.getUserInfoByKey(KEY_PASSWORD_WECHAT)
            if (password.isNullOrEmpty()) {
                view.checkPassword {
                    checkLogin(account,it, METHOD_WECHAT)
                }
            } else {
                view.toDaMa(account, password, METHOD_WECHAT)
            }
        }else{
            view.showToast("请先登录")
        }
    }

    override fun toNoticeList() {
        if (++clickNum >= 7) {
            view.toNoticeList()
            return
        } else {
            compositeDisposable.clear()
            if (clickNum > 2)
                view.showToast("再点${7 - clickNum}次进入通知详情页")
        }
        val d = Single.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe { _ -> clickNum = 0 }
        compositeDisposable.add(d)
    }

    override fun toAisleManager(type: String) {
        if (++clickNum == 7) {
            view.toAisleManager(type)
            return
        } else {
            compositeDisposable.clear()
            if (clickNum in 3..6)
                view.showToast("再点${7 - clickNum}次进入通道管理")
        }
        val d = Single.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe { _ -> clickNum = 0 }
        compositeDisposable.add(d)
    }

    override fun updateLocalAmount(type: String) {
//        model.localNoticeAmount(type)
//                .subscribe(object : ProgressObserver<List<Notice>, AisleManagerContract.View>(view) {
//                    override fun onNext_(t: List<Notice>) {
//                        view.updateLocalInfo(t,type)
//                    }
//
//                    override fun onError(error: String) {
//                        view.showToast(error)
//                    }
//                })
    }

    private fun subscribeNotice() {
        RxBus.getDefault().toObservable(Notice::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Notice> {
                    var d: Disposable? = null
                    override fun onSubscribe(d: Disposable) {
                        this.d = d
                    }

                    override fun onNext(t: Notice) {
                        when {
                            t.type == 1 -> {//支付宝连续10单不成功
                                userInfo(METHOD_ALI)
                                playWarning()
                                view.showHintDialog("支付宝连续10单不成功")
                            }
                            t.type == 2 -> {//微信连续10单不成功
                                userInfo(METHOD_WECHAT)
                                playWarning()
                                view.showHintDialog("微信连续10单不成功")
                            }
                            t.type == 3 -> {//银行连续10单不成功
                                userInfo(METHOD_BANK)
                                playWarning()
                                view.showHintDialog("银行连续10单不成功")
                            }
                            t.type == 4 -> {//云闪付连续10单不成功
                                userInfo(METHOD_YUN)
                                playWarning()
                                view.showHintDialog("云闪付连续10单不成功")
                            }
                            t.type == 111 -> {//支付宝风控
                                userInfo(METHOD_ALI)
                                playWarning()
                                view.showHintDialog("支付宝被风控")
                            }
                            t.type == 11 -> {
                                view.showToast("支付宝通道账号退出登录")
                                exitLogin(METHOD_ALI)
                            }
                            t.type == 12 -> {
                                view.showToast("微信通道账号退出登录")
                                exitLogin(METHOD_WECHAT)
                            }
                            t.type == 13 -> {
                                view.showToast("银行通道账号退出登录")
                                exitLogin(METHOD_BANK)
                            }
                            t.type == 14 -> {
                                view.showToast("云闪付通道账号退出登录")
                                exitLogin(METHOD_YUN)
                            }
                            t.type == 15 -> {
                                view.showToast("长连接断开")
                                playWarning()
                            }
                            t.type == 16 -> {
                                view.showToast("长连接连接成功")
                                stopWarning()
                                aisleStatus(true,METHOD_YUN)
                            }
                            else -> {
                                debug("重复通知last：", noticeLast.toString())
                                debug("重复通知curr：", t.toString())
                                if (noticeLast?.equals(t) == true && t.mark?.equals("补单") == false ){//有重复通知
                                    val type = t.type.toString()
                                    aisleStatus(false,type)
                                    playWarning()
                                    view.showHintDialog("${if (type == METHOD_ALI) "支付宝" else if (type == METHOD_WECHAT) "微信" else if (type == METHOD_BANK) "银行通道" else "未知通道"}有重复通知")
                                }

                                view.updateNoticeList(t)
//                                updateLocalAmount(t.type.toString())
                                userInfo(t.type.toString())
                                noticeLast = t
                            }
                        }
                    }

                    override fun onComplete() {
                        debug("subscribeNotice", "onComplete")
                        d?.dispose()
                        subscribeNotice()
                    }

                    override fun onError(e: Throwable) {
                        debug("subscribeNotice", "onError")
                        d?.dispose()
                        subscribeNotice()
                    }
                })
    }

    override fun unsubscribe() {

    }


    private val modelLogin = LoginModel()
    private var ali_user_id = ""
    override fun aliLogin() {
        modelLogin.aliLoginUrl()
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
                    modelLogin.postAliCode(ali_user_id)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, AisleManagerContract.View>(view) {
                    override fun onNext_(s: ResultEntity<Map<String, String>>) {
                        when (s.code) {
                            "40002" -> {
                                val dialog = BindDialog(view.context, BindDialog.TYPE_BIND, BindDialog.ClickListener { name, password ->
                                    modelLogin.bindAli(name,password,ali_user_id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, AisleManagerContract.View>(view) {
                                                override fun onNext_(resultEntity: ResultEntity<Map<String, String>>) {
                                                    val result = resultEntity.result
                                                    if (result == null){
                                                        view.loginFail(resultEntity.subMsg?:resultEntity.msg)
                                                        return
                                                    }
                                                    modelLogin.saveData(result,METHOD_ALI,password)
                                                    userInfo(METHOD_ALI)
                                                    view.loginSuccess(METHOD_ALI)
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

                                modelLogin.saveData(result,METHOD_ALI)
                                userInfo(METHOD_ALI)
                                view.loginSuccess(METHOD_ALI)
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
        val api = WXAPIFactory.createWXAPI(view.context, WX_APPID,true)
        api.registerApp(WX_APPID)

        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        api.sendReq(req)
    }

    override fun accountLogin(account: String, password: String, type: String) {
        modelLogin.accountLogin(account,password,type)
                .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, AisleManagerContract.View>(view) {
                    override fun onNext_(resultEntity: ResultEntity<Map<String, String>>) {
                        val result = resultEntity.result
                        if (result == null){
                            view.loginFail(resultEntity.subMsg?:resultEntity.msg)
                            return
                        }
                        modelLogin.saveData(result,type,password)
                        userInfo(type)
                        view.loginSuccess(type)
                    }

                    override fun onError(error: String) {
                        view.loginFail(error)
                    }
                })
    }

    override fun checkLogin(account: String, password: String, type: String) {
        modelLogin.accountLogin(account,password,type)
                .subscribe(object : ProgressObserver<ResultEntity<Map<String, String>>, AisleManagerContract.View>(view) {
                    override fun onNext_(resultEntity: ResultEntity<Map<String, String>>) {
                        val result = resultEntity.result
                        if (result == null){
                            view.showToast(resultEntity.subMsg?:resultEntity.msg)
                            return
                        }
                        modelLogin.saveData(result,type,password)
                        view.toDaMa(account,password,type)
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun exitLogin(type: String) {
        modelLogin.clearLoginInfo(type)
        view.exitLogin(type)
    }
}