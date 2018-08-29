package com.fintech.sst.ui.fragment.order

import com.fintech.sst.R
import com.fintech.sst.helper.getColor
import com.fintech.sst.net.bean.OrderList
import com.lxf.recyclerhelper.BaseQuickAdapter
import com.lxf.recyclerhelper.BaseViewHolder


class OrderAdapter(layoutId:Int,
                   orderList: List<OrderList>? = null): BaseQuickAdapter<OrderList, BaseViewHolder>(layoutId,orderList) {
    init {
        showWithAnimation(true)
        showAnimOnlyFirst(true)
    }
    override fun convert(helper: BaseViewHolder, item: OrderList) {
        helper.setText(R.id.tv_zishanghu,"子商户：${item.mchName}(${item.mchId})")
                .setText(R.id.tv_order_num,"外部订单号：${item.outTradeNo}")
                .setText(R.id.tv_order_money,"订单金额：${item.totalAmount}元")
                .setText(R.id.tv_money,"收款金额：${item.realAmount}元")
                .setText(R.id.tvFeeRate,"费率：${item.feeRate * 1000}‰")
                .setText(R.id.tv_order_time,"订单时间：${item.createTime}")
                .setText(R.id.tv_status,when(item.tradeStatus){
                    "10" -> "支付中"
                    "20" -> "通知中"
                    "30" -> "支付成功"
                    "40" -> "订单关闭"
                    else -> "未知状态"
                 })
                .setTextColor(R.id.tv_status,when(item.tradeStatus){
                    "10" -> getColor(R.color.loading)
                    "20" -> getColor(R.color.loading)
                    "30" -> getColor(R.color.success)
                    "40" -> getColor(R.color.fail)
                    else -> getColor(R.color.fail)
                })
                .setVisible(R.id.btnOrder, item.tradeStatus == "40")
                .addOnClickListener(R.id.btnOrder)
    }
}