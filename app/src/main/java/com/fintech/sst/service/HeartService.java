package com.fintech.sst.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fintech.sst.data.db.Notice;
import com.fintech.sst.helper.ExpansionKt;
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

import static com.fintech.sst.helper.ExpansionKt.METHOD_ALI;
import static com.fintech.sst.helper.ExpansionKt.METHOD_WECHAT;
import static com.fintech.sst.helper.ExpansionKt.debug;
import static com.fintech.sst.helper.ExpansionKt.getCloseTime;
import static com.fintech.sst.helper.ExpansionKt.getLastNoticeTimeAli;
import static com.fintech.sst.helper.ExpansionKt.getLastNoticeTimeWeChat;
import static com.fintech.sst.helper.ExpansionKt.setLastNoticeTimeAli;
import static com.fintech.sst.net.Constants.KEY_MCH_ID_ALI;
import static com.fintech.sst.net.Constants.KEY_MCH_ID_WECHAT;
import static com.fintech.sst.net.Constants.KEY_USER_NAME_ALI;
import static com.fintech.sst.net.Constants.KEY_USER_NAME_WECHAT;


public class HeartService extends Service {
    private static final String TAG = "HeartService";
    private Disposable disposable;

    private void heartBeat(final String type) {
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
                        long lastNoticeTime = 0;
                        String keyUserName = null;
                        String keyMChId = null;
                        switch (type){
                            case ExpansionKt.METHOD_ALI:
                                lastNoticeTime = getLastNoticeTimeAli();
                                keyUserName = KEY_USER_NAME_ALI;
                                keyMChId = KEY_MCH_ID_ALI;
                                break;
                            case ExpansionKt.METHOD_WECHAT:
                                lastNoticeTime = getLastNoticeTimeWeChat();
                                keyUserName = KEY_USER_NAME_WECHAT;
                                keyMChId = KEY_MCH_ID_WECHAT;
                                break;
                        }
                        if (System.currentTimeMillis() - lastNoticeTime > getCloseTime() && lastNoticeTime!=0){
                            HashMap<String, String> request = new HashMap<>();
                            request.put("appLoginName", Configuration.getUserInfoByKey(keyUserName));
                            request.put("loginUserId", Configuration.getUserInfoByKey(keyMChId));
                            request.put("type", type);
                            request.put("enable", "0");
                            ApiProducerModule.create(ApiService.class).aisleStatus(new SignRequestBody(request).sign(type))
                                    .subscribe(new Consumer<ResultEntity<String>>() {
                                        @Override
                                        public void accept(ResultEntity<String> stringResultEntity) throws Exception {
                                            setLastNoticeTimeAli(0);
                                            Notice notice = new Notice();
                                            int noticeType = 0;
                                            switch (type){
                                                case ExpansionKt.METHOD_ALI:
                                                    noticeType = 1;
                                                    break;
                                                case ExpansionKt.METHOD_WECHAT:
                                                    noticeType = 2;
                                                    break;
                                            }
                                            notice.type = noticeType;
                                            RxBus.getDefault().send(notice);
                                        }
                                    });
                        }
                        return ApiProducerModule.create(ApiService.class).heartbeat(new SignRequestBody().sign(type));
                    }
                })
                .subscribe(new Observer<ResultEntity<Boolean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        debug(TAG,"=======heartBeat " + type + " onSubscribe======");
                    }

                    @Override
                    public void onNext(ResultEntity<Boolean> booleanResultEntity) {
                        debug(TAG,"=======heartBeat " + type + " onNext======");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        disposable.dispose();
                        debug(TAG,"=======heartBeat " + type + " onError======");
                        heartBeat(type);
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                        debug(TAG,"=======heartBeat " + type + " onComplete======");
                        heartBeat(type);
                    }
                });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        debug(TAG,"=======onCreate======");
        if (Configuration.isLogin(METHOD_ALI)) {//登录的时候开启心跳
            try {
                heartBeat(METHOD_ALI);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Configuration.isLogin(METHOD_WECHAT)) {//登录的时候开启心跳
            try {
                heartBeat(METHOD_WECHAT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        debug(TAG,"=======onDestroy======");
        if (disposable!=null)
            disposable.dispose();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
