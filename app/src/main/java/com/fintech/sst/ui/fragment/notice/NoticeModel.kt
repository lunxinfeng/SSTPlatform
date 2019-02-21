package com.fintech.sst.ui.fragment.notice

import com.fintech.sst.App
import com.fintech.sst.data.DataSource
import com.fintech.sst.data.db.DB
import com.fintech.sst.data.db.Notice
import com.fintech.sst.net.MessageRequestBody
import com.fintech.sst.net.ResultEntity
import io.reactivex.Observable

class NoticeModel : DataSource {
    fun noticeList(status: Int = 0, pageNow: Int = 1, pageSize:Int = 10): Observable<List<Notice>?> {

        return Observable.create { emitter ->
            val noticeList = if (status == 0)
                DB.queryAll(App.getAppContext(),pageNow,pageSize)
            else
                DB.queryAll(App.getAppContext(),status,pageNow,pageSize)

            emitter.onNext(noticeList)
            emitter.onComplete()
        }
    }

    fun sendNotice(notice: Notice,type:String): Observable<ResultEntity<Notice>> {
        val body = MessageRequestBody()
        body.put("uuid", notice.uuid)
        body.put("amount", notice.amount)
        body.put("title", notice.title)
        body.put("content", notice.content)
        body.put("time", notice.saveTime)
        body.put("type", notice.type)
        body.put("packageName", notice.packageName)
        body.put("id", notice.noticeId)
        body.put("tag", notice.tag)
        body.put("memo", notice.mark)
        body.sign(type)
        return service.notifyLog(body)
    }

    fun insertDB(notice: Notice){
        DB.insert(App.getAppContext(),notice)
    }

    fun updateDB(notice: Notice){
        DB.updateAll(App.getAppContext(),notice)
    }
}