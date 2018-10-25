package com.fintech.sst.ui.fragment.order

import com.fintech.sst.data.DataSource
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.SignRequestBody
import com.fintech.sst.net.bean.OrderCount
import com.fintech.sst.net.bean.OrderList
import com.fintech.sst.net.bean.PageList
import io.reactivex.Observable

class OrderModel : DataSource {
    fun orderList(tradeStatus: Int = 0, pageNow: Int = 1, pageSize:Int = 10,type:String): Observable<ResultEntity<PageList<OrderList>>> {
        val body = SignRequestBody()
        if (tradeStatus!=0)
            body.put("tradeStatus", tradeStatus)
        body.put("account", Configuration.getUserInfoByKey(getAccount(type)))
        body.put("pageSize", pageSize)
        body.put("pageNow", pageNow)
        return service.orders(SignRequestBody(body.sign(type)))
    }

    fun orderCount(realAmount:String,beginTime:String,type:String): Observable<ResultEntity<OrderCount>> {
        val body = SignRequestBody()
        body.put("mchId", Configuration.getUserInfoByKey(getMChId(type)))
        body.put("realAmount", realAmount)
        body.put("qrAccount", Configuration.getUserInfoByKey(getAccount(type)))
        body.put("beginTime", beginTime)
        return service.orderCount(body.sign(type))
    }

    fun reOrder(orderNo: String,type:String): Observable<ResultEntity<String>> {
        val signRequestBody = SignRequestBody()
        signRequestBody.put("tradeNo", orderNo)
        return service.sendOrderNotify(signRequestBody.sign(type))
    }
}