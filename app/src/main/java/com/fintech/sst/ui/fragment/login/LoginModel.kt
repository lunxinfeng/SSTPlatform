package com.fintech.sst.ui.fragment.login

import com.fintech.sst.App
import com.fintech.sst.data.DataSource
import com.fintech.sst.helper.METHOD_ALI
import com.fintech.sst.helper.METHOD_BANK
import com.fintech.sst.helper.METHOD_WECHAT
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants.*
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.SignRequestBody
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class LoginModel: DataSource {
    companion object {
        private const val KEY_MCH_ID = "mchId"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_LOGIN_TOKEN = "loginToken"
        private const val KEY_ACCOUNT = "account"
        private const val KEY_ALLOW_LOAD = "allowLoad"
        private const val KEY_BEGIN_NUM = "beginNum"
        private const val KEY_END_NUM = "endNum"
        private const val KEY_MAX_NUM = "maxNum"
        private const val KEY_CODE = "bankAuthCode"
        private const val KEY_REGEX = "regex"
        private const val KEY_TYPE = "dateFrt"
        private const val KEY_ACCOUNT_ID = "accountId"
    }

    fun accountLogin(name: String, password: String,type: String = METHOD_ALI): Observable<ResultEntity<Map<String, String>>> {
        val request = HashMap<String, String>()
        request["userName"] = name
        request["password"] = password
        request["payMethod"] = type
        request["app_version"] = App.getAppContext().packageManager
                .getPackageInfo(App.getAppContext().packageName,0).versionCode.toString()
        return service.login(SignRequestBody(request))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun aliLoginUrl(): Observable<ResultEntity<Map<String, String>>> {
        return service.getAliLoginUrl()
    }

    fun postAliCode(uid:String): Observable<ResultEntity<Map<String, String>>>? {
        val request = HashMap<String, String>()
        request["uid"] = uid
        request["app_version"] = App.getAppContext().packageManager
                .getPackageInfo(App.getAppContext().packageName,0).versionCode.toString()
        return service.postAliCode(SignRequestBody(request))
    }

    fun bindAli(name: String, password: String,ali_user_id:String): Observable<ResultEntity<Map<String, String>>> {
        val request = HashMap<String, String>()
        request["userName"] = name
        request["password"] = password
        request["uid"] = ali_user_id
        request["payMethod"] = "2001"
        request["app_version"] = App.getAppContext().packageManager
                .getPackageInfo(App.getAppContext().packageName,0).versionCode.toString()
        return service.bindAli(SignRequestBody(request))
    }

//    fun delLocalData() {
//        Single.just(1)
//                .subscribeOn(Schedulers.io())
//                .subscribe(Consumer {
//                    //                        DB.deleteTable(context);
//                    Configuration.removeUserInfoByKey(KEY_ACCOUNT)
//                    Configuration.removeUserInfoByKey(KEY_ALLOW_LOAD)
//                    Configuration.removeUserInfoByKey(KEY_BEGIN_NUM)
//                    Configuration.removeUserInfoByKey(KEY_END_NUM)
//                    Configuration.removeUserInfoByKey(KEY_MAX_NUM)
//                })
//    }


    fun saveData(result: Map<String, String>,type:String,password: String = "") {
        when(type){
            METHOD_ALI -> {
                Configuration.putUserInfo(KEY_MCH_ID_ALI, result[KEY_MCH_ID])
                Configuration.putUserInfo(KEY_USER_NAME_ALI, result[KEY_USER_NAME])
                if (password != "")
                    Configuration.putUserInfo(KEY_PASSWORD_ALI, password)
                Configuration.putUserInfo(KEY_LOGIN_TOKEN_ALI, result[KEY_LOGIN_TOKEN])

                Configuration.putUserInfo(KEY_ACCOUNT_ALI, result[KEY_ACCOUNT])
                Configuration.putUserInfo(KEY_ALLOW_LOAD_ALI, result[KEY_ALLOW_LOAD])
                Configuration.putUserInfo(KEY_BEGIN_NUM_ALI, result[KEY_BEGIN_NUM])
                Configuration.putUserInfo(KEY_END_NUM_ALI, result[KEY_END_NUM])
                Configuration.putUserInfo(KEY_MAX_NUM_ALI, result[KEY_MAX_NUM])
            }
            METHOD_WECHAT -> {
                Configuration.putUserInfo(KEY_MCH_ID_WECHAT, result[KEY_MCH_ID])
                Configuration.putUserInfo(KEY_USER_NAME_WECHAT, result[KEY_USER_NAME])
                if (password != "")
                    Configuration.putUserInfo(KEY_PASSWORD_WECHAT, password)
                Configuration.putUserInfo(KEY_LOGIN_TOKEN_WECHAT, result[KEY_LOGIN_TOKEN])

                Configuration.putUserInfo(KEY_ACCOUNT_WECHAT, result[KEY_ACCOUNT])
                Configuration.putUserInfo(KEY_ALLOW_LOAD_WECHAT, result[KEY_ALLOW_LOAD])
                Configuration.putUserInfo(KEY_BEGIN_NUM_WECHAT, result[KEY_BEGIN_NUM])
                Configuration.putUserInfo(KEY_END_NUM_WECHAT, result[KEY_END_NUM])
                Configuration.putUserInfo(KEY_MAX_NUM_WECHAT, result[KEY_MAX_NUM])
            }
            METHOD_BANK -> {
                Configuration.putUserInfo(KEY_MCH_ID_BANK, result[KEY_MCH_ID])
                Configuration.putUserInfo(KEY_USER_NAME_BANK, result[KEY_USER_NAME])
                if (password != "")
                    Configuration.putUserInfo(KEY_PASSWORD_BANK, password)
                Configuration.putUserInfo(KEY_LOGIN_TOKEN_BANK, result[KEY_LOGIN_TOKEN])

                Configuration.putUserInfo(KEY_ACCOUNT_BANK, result[KEY_ACCOUNT])
                Configuration.putUserInfo(KEY_ALLOW_LOAD_BANK, result[KEY_ALLOW_LOAD])
                Configuration.putUserInfo(KEY_BEGIN_NUM_BANK, result[KEY_BEGIN_NUM])
                Configuration.putUserInfo(KEY_END_NUM_BANK, result[KEY_END_NUM])
                Configuration.putUserInfo(KEY_MAX_NUM_BANK, result[KEY_MAX_NUM])
                Configuration.putUserInfo(KEY_BANK_CODE, result[KEY_CODE])
                Configuration.putUserInfo(KEY_BANK_REGEX, result[KEY_REGEX])
                Configuration.putUserInfo(KEY_BANK_TYPE, result[KEY_TYPE])
                Configuration.putUserInfo(KEY_ACCOUNT_ID_BANK, result[KEY_ACCOUNT_ID])
            }
        }
    }

    fun clearLoginInfo(type:String){
        Configuration.clearUserInfo(type)
    }
}