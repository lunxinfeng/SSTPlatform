package com.fintech.sst.data

import com.fintech.sst.helper.METHOD_ALI
import com.fintech.sst.helper.METHOD_BANK
import com.fintech.sst.helper.METHOD_WECHAT
import com.fintech.sst.net.ApiProducerModule
import com.fintech.sst.net.ApiService
import com.fintech.sst.net.Constants


interface DataSource {
    val service: ApiService
        get() = ApiProducerModule.create(ApiService::class.java)

    fun getMChId(type: String) = when (type) {
        METHOD_ALI -> Constants.KEY_MCH_ID_ALI
        METHOD_WECHAT -> Constants.KEY_MCH_ID_WECHAT
        METHOD_BANK -> Constants.KEY_MCH_ID_BANK
        else -> "unKnown"
    }

    fun getAccount(type: String) = when (type) {
        METHOD_ALI -> Constants.KEY_ACCOUNT_ALI
        METHOD_WECHAT -> Constants.KEY_ACCOUNT_WECHAT
        METHOD_BANK -> Constants.KEY_ACCOUNT_BANK
        else -> "unKnown"
    }

}