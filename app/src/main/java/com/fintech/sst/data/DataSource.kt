package com.fintech.sst.data

import com.fintech.sst.net.ApiProducerModule
import com.fintech.sst.net.ApiService


interface DataSource {
    val service: ApiService
        get() = ApiProducerModule.create(ApiService::class.java)
}