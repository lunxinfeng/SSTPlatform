package com.fintech.sst.ui.fragment.notice

import com.fintech.sst.App
import com.fintech.sst.data.DataSource
import com.fintech.sst.data.db.DB
import com.fintech.sst.data.db.Notice
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

}