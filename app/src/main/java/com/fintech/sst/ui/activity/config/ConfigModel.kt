package com.fintech.sst.ui.activity.config

import com.fintech.sst.App
import com.fintech.sst.data.DataSource
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.SignRequestBody
import io.reactivex.Observable
import java.util.*


class ConfigModel:DataSource {
    fun login(): Observable<ResultEntity<Map<String, String>>> {
        val request = HashMap<String, String>()
        request.put("userName", "13397610459@001")
        request.put("password", "123456")
        request.put("app_version", App.getAppContext().packageManager
                .getPackageInfo(App.getAppContext().packageName,0).versionCode.toString())
        return service.login(SignRequestBody(request))
    }
}