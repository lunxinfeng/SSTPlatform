package com.fintech.sst.ui.fragment.order

import com.fintech.sst.R
import com.fintech.sst.net.bean.OrderList
import com.lxf.recyclerhelper.BaseQuickAdapter
import com.lxf.recyclerhelper.BaseViewHolder


class OrderAdapter(layoutId:Int,
                   orderList: List<OrderList>? = null): BaseQuickAdapter<OrderList, BaseViewHolder>(layoutId,orderList) {
    init {
        showWithAnimation(true)
    }
    override fun convert(helper: BaseViewHolder?, item: OrderList?) {
        helper?.setText(R.id.tv_zishanghu,"商户：${item?.mchName}(${item?.mchId})")
                ?.setText(R.id.tv_order_num,"订单编码：${item?.tradeNo}")
                ?.setText(R.id.tv_order_money,"订单金额：${item?.totalAmount}元")
                ?.setText(R.id.tv_money,"收款金额：${item?.realAmount}元")
                ?.setText(R.id.tv_order_time,"订单时间：${item?.createTime}")
    }
}