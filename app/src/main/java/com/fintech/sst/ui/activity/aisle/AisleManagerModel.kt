package com.fintech.sst.ui.activity.aisle

import com.fintech.sst.data.DataSource
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants.KEY_MCH_ID
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.SignRequestBody
import com.fintech.sst.net.bean.AisleInfo
import io.reactivex.Observable
import java.util.*


class AisleManagerModel:DataSource {
    var aisleInfo:AisleInfo? = null

    fun userInfo(): Observable<ResultEntity<AisleInfo>> {
        return service.userInfo(SignRequestBody().sign())
    }

    fun aisleStatus(open:Boolean): Observable<ResultEntity<String>> {
        val request = HashMap<String, String>()
        request.put("appLoginName", aisleInfo?.appLoginName.toString())
        request.put("loginUserId", Configuration.getUserInfoByKey(KEY_MCH_ID))
        request.put("type", "2001")
        request.put("enable", if (open) "1" else "0")
        return service.aisleStatus(SignRequestBody(request).sign())
    }

    fun aisleRefresh(): Observable<ResultEntity<String>> {
        val request = HashMap<String, String>()
        request.put("mchId", Configuration.getUserInfoByKey(KEY_MCH_ID))
        request.put("qrAccount", aisleInfo?.account.toString())
        request.put("tradeChannel", "2001")
        return service.aisleRefresh(SignRequestBody(request).sign())
    }

    fun aisleDelete(): Observable<ResultEntity<String>> {
        val request = HashMap<String, String>()
        request.put("accountId", aisleInfo?.accountId.toString())
        return service.aisleDelete(SignRequestBody(request).sign())
    }
}