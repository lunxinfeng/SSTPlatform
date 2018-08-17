package com.fintech.sst.ui.fragment.order

import com.fintech.sst.data.DataSource
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.SignRequestBody
import com.fintech.sst.net.bean.OrderList
import com.fintech.sst.net.bean.PageList
import io.reactivex.Observable

class OrderModel : DataSource {
    fun orderList(tradeStatus: Int = 0, pageNow: Int = 1, pageSize:Int = 10): Observable<ResultEntity<PageList<OrderList>>> {
        val body = SignRequestBody()
        if (tradeStatus!=0)
            body.put("tradeStatus", tradeStatus)
        body.put("pageSize", pageSize)
        body.put("pageNow", pageNow)
        return service.orders(SignRequestBody(body.sign()))
    }

}