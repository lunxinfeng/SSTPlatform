package com.fintech.sst.ui.fragment.login

import android.content.Context
import com.fintech.sst.data.DataSource
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants.*
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.SignRequestBody
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*


class LoginModel: DataSource {
    fun accountLogin(name: String, password: String): Observable<ResultEntity<Map<String, String>>> {
        val request = HashMap<String, String>()
        request.put("userName", name)
        request.put("password", password)
        request.put("payMethod", "2001")
        return service.login(SignRequestBody(request))
    }

    fun aliLoginUrl(): Observable<ResultEntity<Map<String, String>>> {
        return service.getAliLoginUrl()
    }

    fun postAliCode(uid:String): Observable<ResultEntity<Map<String, String>>>? {
        val request = HashMap<String, String>()
        request.put("uid", uid)
        return service.postAliCode(SignRequestBody(request))
    }

    fun bindAli(name: String, password: String,ali_user_id:String): Observable<ResultEntity<Map<String, String>>> {
        val request = HashMap<String, String>()
        request.put("userName", name)
        request.put("password", password)
        request.put("uid", ali_user_id)
        request.put("payMethod", "2001")
        return service.bindAli(SignRequestBody(request))
    }

    fun delLocalData(context: Context) {
        Single.just(1)
                .subscribeOn(Schedulers.io())
                .subscribe(Consumer {
                    //                        DB.deleteTable(context);
                    Configuration.removeUserInfoByKey(KEY_ACCOUNT)
                    Configuration.removeUserInfoByKey(KEY_ALLOW_LOAD)
                    Configuration.removeUserInfoByKey(KEY_BEGIN_NUM)
                    Configuration.removeUserInfoByKey(KEY_END_NUM)
                    Configuration.removeUserInfoByKey(KEY_MAX_NUM)
                })
    }


    fun saveData(result: Map<String, String>) {
        Configuration.putUserInfo(KEY_USER_NAME, result[KEY_USER_NAME])
        Configuration.putUserInfo(KEY_MCH_ID, result[KEY_MCH_ID])
        Configuration.putUserInfo(KEY_LOGIN_TOKEN, result[KEY_LOGIN_TOKEN])

        Configuration.putUserInfo(KEY_ACCOUNT, result[KEY_ACCOUNT])
        Configuration.putUserInfo(KEY_ALLOW_LOAD, result[KEY_ALLOW_LOAD])
        Configuration.putUserInfo(KEY_BEGIN_NUM, result[KEY_BEGIN_NUM])
        Configuration.putUserInfo(KEY_END_NUM, result[KEY_END_NUM])
        Configuration.putUserInfo(KEY_MAX_NUM, result[KEY_MAX_NUM])
    }
}