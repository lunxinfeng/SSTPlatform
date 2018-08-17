package com.fintech.sst.ui.activity.aisle

import com.fintech.sst.data.DataSource
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.SignRequestBody
import com.fintech.sst.net.bean.UserInfoDetail
import io.reactivex.Observable


class AisleManagerModel:DataSource {
    fun userInfo(): Observable<ResultEntity<UserInfoDetail>> {
        return service.userInfo(SignRequestBody().sign())
    }
}