package com.fintech.sst.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.fintech.sst.helper.PermissionUtil;
import com.fintech.sst.net.ApiProducerModule;
import com.fintech.sst.net.ApiService;
import com.fintech.sst.net.Configuration;
import com.fintech.sst.net.ResultEntity;
import com.fintech.sst.net.SignRequestBody;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.fintech.sst.helper.ExpansionKt.debug;


public class HeartJobService extends JobService {
    private static final String TAG = "HeartJobService";
    private Subscription subscription;
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
        if (subscription!=null)
            subscription.cancel();
        Flowable.interval(0,10*1000, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
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
                .flatMap(new Function<Long, Publisher<ResultEntity<Boolean>>>() {
                    @Override
                    public Publisher<ResultEntity<Boolean>> apply(Long aLong) throws Exception {
                        return ApiProducerModule.create(ApiService.class).heartbeat(new SignRequestBody().sign());
                    }
                })
                .subscribe(new Subscriber<ResultEntity<Boolean>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                    }

                    @Override
                    public void onNext(ResultEntity<Boolean> booleanResultEntity) {
                        debug(TAG,"=======heartBeat onNext======");
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        heartBeat();
                    }

                    @Override
                    public void onComplete() {
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
