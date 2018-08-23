package com.fintech.sst.ui.activity.aisle

import com.fintech.sst.R
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.getTime
import com.lxf.recyclerhelper.BaseQuickAdapter
import com.lxf.recyclerhelper.BaseViewHolder


class NoticeAdapter(layoutId:Int, data:List<Notice>?): BaseQuickAdapter<Notice, BaseViewHolder>(layoutId, data) {
    override fun getItemCount(): Int {
        return Math.min(data?.size?:0,10)
    }
    override fun convert(holder: BaseViewHolder?, notice: Notice?) {
        holder?.setText(R.id.tv_content,notice?.content?:"")
                ?.setText(R.id.tv_time,getTime(notice?.saveTime?:0).toString())
    }
}