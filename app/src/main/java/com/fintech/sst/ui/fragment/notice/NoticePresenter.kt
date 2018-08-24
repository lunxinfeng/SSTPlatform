package com.fintech.sst.ui.fragment.notice

import android.arch.lifecycle.LifecycleObserver
import com.fintech.sst.data.db.Notice
import com.fintech.sst.helper.lastNoticeTime
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class NoticePresenter(val view: NoticeContract.View,
                      private val model: NoticeModel = NoticeModel()) : NoticeContract.Presenter, LifecycleObserver {

    override val compositeDisposable = CompositeDisposable()

    override fun noticeList(status:Int, pageNow: Int, pageSize:Int, append:Boolean) {
        model.noticeList(status,pageNow,pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<List<Notice>?, NoticeContract.View>(view,false) {
                    override fun onNext_(t: List<Notice>?) {
                        if (append)
                            view.loadMore(t)
                        else
                            view.refreshData(t)
                    }

                    override fun onError(error: String) {
                         view.loadError(error)
                    }
                })
    }

    override fun sendNotice(notice: Notice) {
        model.sendNotice(notice)
                .doOnSubscribe {
                    model.insertDB(notice)
                }
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    val result = it.result
                    if (it.msg == "success" && result != null) {
                        lastNoticeTime = result.saveTime
                        result.status = 1
                        model.updateDB(result)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<Notice>, NoticeContract.View>(view,false) {
                    override fun onNext_(resultEntity: ResultEntity<Notice>) {
                        val result = resultEntity.result
                        if (resultEntity.msg == "success" && result != null) {
                            view.sendNoticeComplete()
                        }
                    }

                    override fun onError(error: String) {
                        view.loadError(error)
                    }
                })
    }
}