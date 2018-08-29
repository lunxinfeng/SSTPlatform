package com.fintech.sst.ui.fragment.notice

import com.fintech.sst.R
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.getColor
import com.fintech.sst.helper.getTime
import com.lxf.recyclerhelper.BaseQuickAdapter
import com.lxf.recyclerhelper.BaseViewHolder

class NoticeAdapter(layoutId:Int, data:List<Notice>? = null): BaseQuickAdapter<Notice, BaseViewHolder>(layoutId, data) {
    init {
        showWithAnimation(true)
        showAnimOnlyFirst(true)
    }
    override fun convert(holder: BaseViewHolder?, notice: Notice?) {
        holder?.setText(R.id.tv_content,notice?.content?:"")
                ?.setTextColor(R.id.tv_content,if (notice?.status == 1) getColor(R.color.success) else getColor(R.color.fail))
                ?.setText(R.id.tv_time,getTime(notice?.saveTime?:0).toString())
                ?.setTextColor(R.id.tv_time,if (notice?.status == 1) getColor(R.color.success_time) else getColor(R.color.fail_time))
                ?.setVisible(R.id.btnSend,notice?.status == 2)
                ?.addOnClickListener(R.id.btnSend)
    }
}