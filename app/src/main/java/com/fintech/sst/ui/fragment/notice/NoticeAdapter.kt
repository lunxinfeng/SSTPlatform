package com.fintech.sst.ui.fragment.notice

import com.fintech.sst.R
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.getColor
import com.lxf.recyclerhelper.BaseQuickAdapter
import com.lxf.recyclerhelper.BaseViewHolder

class NoticeAdapter(layoutId:Int, data:List<Notice>? = null): BaseQuickAdapter<Notice, BaseViewHolder>(layoutId, data) {
    init {
        showWithAnimation(true)
    }
    override fun convert(holder: BaseViewHolder?, notice: Notice?) {
        holder?.setText(R.id.tv_content,notice?.content?:"")
                ?.setTextColor(R.id.tv_content,if (notice?.status == 1) getColor(R.color.success) else getColor(R.color.fail))
    }
}