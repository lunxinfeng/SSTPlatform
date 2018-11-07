package com.fintech.sst.ui.activity.aisle

import com.fintech.sst.data.DataSource
import com.fintech.sst.helper.METHOD_ALI
import com.fintech.sst.helper.METHOD_WECHAT
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.SignRequestBody
import com.fintech.sst.net.bean.AisleInfo
import io.reactivex.Observable
import java.util.*


class AisleManagerModel : DataSource {
    var aisleInfoAli: AisleInfo? = null
    var aisleInfoWeChat: AisleInfo? = null

    private fun getAisleInfo(type: String) = when (type) {
        METHOD_ALI -> aisleInfoAli
        METHOD_WECHAT -> aisleInfoWeChat
        else -> null
    }

    fun setAisleInfo(info: AisleInfo?, type: String) {
        when (type) {
            METHOD_ALI -> aisleInfoAli = info
            METHOD_WECHAT -> aisleInfoWeChat = info
        }
    }

    fun userInfo(type: String): Observable<ResultEntity<AisleInfo>> {
        val request = HashMap<String, String>()
        request["tradeChannel"] = type
        return service.userInfo(SignRequestBody(request).sign(type))
    }

    fun aisleStatus(open: Boolean, type: String): Observable<ResultEntity<String>> {
        val request = HashMap<String, String>()
        request["appLoginName"] = getAisleInfo(type)?.appLoginName.toString()
        request["loginUserId"] = Configuration.getUserInfoByKey(getMChId(type))
        request["type"] = type
        request["enable"] = if (open) "1" else "0"
        return service.aisleStatus(SignRequestBody(request).sign(type))
    }

    fun aisleRefresh(type: String): Observable<ResultEntity<String>> {
        val request = HashMap<String, String>()
        request["mchId"] = Configuration.getUserInfoByKey(getMChId(type))
        request["qrAccount"] = getAisleInfo(type)?.account.toString()
        request["tradeChannel"] = type
        return service.aisleRefresh(SignRequestBody(request).sign(type))
    }

    fun aisleDelete(type: String): Observable<ResultEntity<String>> {
        val request = HashMap<String, String>()
        request["accountId"] = getAisleInfo(type)?.accountId.toString()
        return service.aisleDelete(SignRequestBody(request).sign(type))
    }

//    fun localNoticeAmount(type: String): Observable<List<Notice>> {
//        val minSaveTime = Calendar.getInstance().apply {
//            set(Calendar.HOUR, 0)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }.timeInMillis
//
//        return Observable
//                .create<List<Notice>> {
//                    val result = DB.queryAll(type.toInt(),minSaveTime)
//                    if (result != null)
//                        it.onNext(result)
//                    it.onComplete()
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
}