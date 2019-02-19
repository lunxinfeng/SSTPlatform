package com.fintech.sst.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.alibaba.fastjson.JSON
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants
import com.fintech.sst.other.netty.*
import org.apache.commons.lang3.StringUtils

class YunService : BaseService() {
    private var simpleServerMessageHandler: SimpleServerMessageHandler? = null
    private val billReceiver = YunShanFuReceiver()

    override fun onCreate() {
        super.onCreate()
        //云闪付
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.chuxin.socket.ACTION_NOTIFI")
        registerReceiver(billReceiver, intentFilter)
        connectNetty()
    }

    override fun onDestroy() {
        disConnectNetty()
        unregisterReceiver(billReceiver)
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
        tcpConnection.host = "47.96.69.207"
        tcpConnection.authToken = Configuration.getUserInfoByKey(Constants.KEY_ACCOUNT_ID_YUN)
        tcpConnection.port = 2195
        simpleServerMessageHandler = SimpleServerMessageHandler(this)
        simpleServerMessageHandler?.nettyConnectionFactory = NettyConnectionFactory(tcpConnection, simpleServerMessageHandler, this)
    }

    private fun disConnectNetty() {
        simpleServerMessageHandler?.nettyConnectionFactory?.closeCurrentChannel()
        simpleServerMessageHandler?.nettyConnectionFactory?.closeChannel()
    }

}
