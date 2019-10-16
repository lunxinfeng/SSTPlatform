package com.fintech.sst.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.alibaba.fastjson.JSON
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.METHOD_YUN
import com.fintech.sst.helper.RxBus
import com.fintech.sst.helper.SmsObserverUtil
import com.fintech.sst.helper.debug
import com.fintech.sst.net.*
import com.fintech.sst.net.bean.Sms
import com.fintech.sst.other.netty.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.concurrent.TimeUnit

class YunService : BaseService() {
    private var simpleServerMessageHandler: SimpleServerMessageHandler? = null
    private val billReceiver = YunShanFuReceiver()

    //-----------暂时监听短信
    private var checkDisposable: Disposable? = null

    override fun onCreate() {
        super.onCreate()
        //云闪付
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.chuxin.socket.ACTION_NOTIFI")
        registerReceiver(billReceiver, intentFilter)
        connectNetty()

        //-----------暂时监听短信
        if (Configuration.isLogin(METHOD_YUN)) {
            try {
                subscribeSms()
                SmsObserverUtil.registerSmsDatabaseChangeObserver(this)
                check()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        disConnectNetty()
        unregisterReceiver(billReceiver)

        //-----------暂时监听短信
        checkDisposable?.dispose()
        checkDisposable = null
        SmsObserverUtil.unregisterSmsDatabaseChangeObserver(this)
        super.onDestroy()
    }

    inner class YunShanFuReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action?.contentEquals("com.chuxin.socket.ACTION_NOTIFI") == true) {
                var message = intent.getStringExtra("message")
                log(message)

                if (message.startsWith("yunshanfuqrcode:")) {
                    message = message.substring("yunshanfuqrcode:".length, message.length)
                    log("XposedData已生成二维码，准备回传平台: $message")
                    val resObj = JSON.parseObject(message)
                    val sessionKey = resObj.getString("sessionkey")
                    log("XposedData生成二维码sessionKey: $sessionKey")
                    val resPay = getUserPayByKey(sessionKey)
                    if (resPay != null) {
                        log("XposedData生成二维码userPayStr: " + JSON.toJSONString(resPay))
                        simpleServerMessageHandler?.requeyPayHandler(resPay, resObj)
                    }
                    val paramsObj = resObj.getJSONObject("params")
                    log("XposedData生成二维码Image: " + paramsObj?.toJSONString())
//                    if (customImageDialog != null && paramsObj != null && paramsObj.containsKey("certificate")) {
//                        customImageDialog.show()
//                        customImageDialog.setImageViewByUrl(paramsObj.getString("certificate"))
//                        printMsg("XposedData生成二维码Image成功: " + paramsObj.toJSONString())
//                    }
                }
            }
        }

        private fun getUserPayByKey(key: String): UserPay? {
            try {
                if (StringUtils.isNotBlank(key)) {
                    val userPayJson = AbSharedUtil.getString(this@YunService, key)
                    if (StringUtils.isNotBlank(userPayJson)) {
                        return JSON.parseObject<UserPay>(userPayJson, UserPay::class.java)
                    }
                }
            } catch (e: Exception) {
                log("XposedData获取缓存信息异常: " + e.message)
            }
            return null
        }
    }

    private fun connectNetty() {
        val tcpConnection = TcpConnection()
        tcpConnection.host = Constants.nettyAddress
        tcpConnection.authToken = Configuration.getUserInfoByKey(Constants.KEY_ACCOUNT_ID_YUN)
        tcpConnection.port = 2195
        simpleServerMessageHandler = SimpleServerMessageHandler(this)
        simpleServerMessageHandler?.nettyConnectionFactory = NettyConnectionFactory(tcpConnection, simpleServerMessageHandler, this)
    }

    private fun disConnectNetty() {
        simpleServerMessageHandler?.nettyConnectionFactory?.closeCurrentChannel()
        simpleServerMessageHandler?.nettyConnectionFactory?.closeChannel()
    }


    //-----------暂时监听短信
    private fun subscribeSms() {
        RxBus.getDefault().toObservable(Sms::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Sms> {
                    var d: Disposable? = null
                    override fun onSubscribe(d: Disposable) {
                        this.d = d
                    }

                    override fun onNext(t: Sms) {
                        addToNoticeList(t, false)
                    }

                    override fun onComplete() {
                        debug("subscribeNotice", "onComplete")
                        d?.dispose()
                        subscribeSms()
                    }

                    override fun onError(e: Throwable) {
                        debug("subscribeNotice", "onError")
                        d?.dispose()
                        subscribeSms()
                    }
                })
    }

    private fun addToNoticeList(t: Sms, reSend: Boolean) {
        val notice = Notice()
        notice.content = t.content
        notice.saveTime = t.time.toLong()
        notice.status = 2
        notice.tag = ""
        notice.noticeId = 0
        notice.title = "云闪付"

        notice.packageName = t.sendName
        notice.type = METHOD_YUN.toInt()
        notice.orderNo = ""
        notice.amount = t.amount
        notice.mark = if (reSend) "补单：${t.amount}, ${t.time}" else "正常"
        if (notice.amount.toFloatOrNull() != 0f)
            notices.offer(notice)
    }

    private fun check() {
        checkDisposable = Observable.interval(4 * 60 * 1000, TimeUnit.MILLISECONDS)
                .map {
                    val listSql = SmsObserverUtil.mSmsDBChangeObserver.query50()
                    listSql
                }
                .delay(5000, TimeUnit.MILLISECONDS)
                .subscribe { listSql ->
                    val request = HashMap<String, String>()
                    request["accountId"] = Configuration.getUserInfoByKey(Constants.KEY_ACCOUNT_ID_YUN)
                    ApiProducerModule.create(ApiService::class.java).smsList(SignRequestBody(request).sign(METHOD_YUN))
                            .subscribeOn(Schedulers.io())
                            .map { result ->
                                val listNet = mutableListOf<Sms>()
                                result.result?.forEach {
                                    val time = it.split(",")[0]
                                    val amount = it.split(",")[1]

                                    listNet.add(Sms().apply {
                                        this.time = time
                                        this.amount = amount
                                    })
                                }
                                listNet
                            }
                            .subscribe { it ->

                                listSql.removeAll(it)

                                listSql.forEach {
                                    println("自动补单$it")
                                    addToNoticeList(it, true)
                                }
                            }
                }
    }
}
