package com.fintech.sst.service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;

import com.fintech.sst.data.db.Notice;
import com.fintech.sst.helper.PermissionUtil;
import com.fintech.sst.helper.RxBus;
import com.fintech.sst.net.ApiProducerModule;
import com.fintech.sst.net.ApiService;
import com.fintech.sst.net.Configuration;
import com.fintech.sst.net.ResultEntity;
import com.fintech.sst.net.SignRequestBody;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.fintech.sst.helper.ExpansionKt.debug;
import static com.fintech.sst.helper.ExpansionKt.getLastNoticeTime;
import static com.fintech.sst.helper.ExpansionKt.setLastNoticeTime;
import static com.fintech.sst.net.Constants.KEY_MCH_ID;
import static com.fintech.sst.net.Constants.KEY_USER_NAME;


public class HeartJobService extends JobService {
    private static final String TAG = "HeartJobService";
    private Disposable disposable;
    @Override
    public boolean onStartJob(JobParameters params) {
        debug(TAG,"=======onStartJob======");
        if (Configuration.isLogin()) {//登录的时候开启心跳
            try {
                heartBeat();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        debug(TAG,"=======onStopJob======");
        return true;
    }

    private void heartBeat() {
        if (disposable!=null)
            disposable.dispose();
        Observable.interval(0,10*1000, TimeUnit.MILLISECONDS)
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
//                        boolean alive = PermissionUtil.isNotificationListenerEnabled();
                        boolean alive = NotificationListener.isActive();
                        if (!alive) {
                            PermissionUtil.toggleNotificationListenerService(NotificationListener.class);
                        }
                        return alive;
                    }
                })
                .flatMap(new Function<Long, ObservableSource<ResultEntity<Boolean>>>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public ObservableSource<ResultEntity<Boolean>> apply(Long aLong) throws Exception {
                        if (System.currentTimeMillis() - getLastNoticeTime() > 120 * 1000 && getLastNoticeTime()!=0){
                            HashMap<String, String> request = new HashMap<>();
                            request.put("appLoginName", Configuration.getUserInfoByKey(KEY_USER_NAME));
                            request.put("loginUserId", Configuration.getUserInfoByKey(KEY_MCH_ID));
                            request.put("type", "2001");
                            request.put("enable", "0");
                            ApiProducerModule.create(ApiService.class).aisleStatus(new SignRequestBody(request).sign())
                                    .subscribe(new Consumer<ResultEntity<String>>() {
                                        @Override
                                        public void accept(ResultEntity<String> stringResultEntity) throws Exception {
                                            setLastNoticeTime(0);
                                            Notice notice = new Notice();
                                            notice.type = 1;
                                            RxBus.getDefault().send(notice);
                                        }
                                    });
                        }
                        return ApiProducerModule.create(ApiService.class).heartbeat(new SignRequestBody().sign());
                    }
                })
                .subscribe(new Observer<ResultEntity<Boolean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        debug(TAG,"=======heartBeat onSubscribe======");
                    }

                    @Override
                    public void onNext(ResultEntity<Boolean> booleanResultEntity) {
                        debug(TAG,"=======heartBeat onNext======");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        disposable.dispose();
                        debug(TAG,"=======heartBeat onError======");
                        heartBeat();
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                        debug(TAG,"=======heartBeat onComplete======");
                        heartBeat();
                    }
                });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        debug(TAG,"=======onCreate======");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        debug(TAG,"=======onDestroy======");
    }
}
