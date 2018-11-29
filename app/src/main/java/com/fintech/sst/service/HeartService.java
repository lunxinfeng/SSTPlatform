package com.fintech.sst.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.fintech.sst.data.db.Notice;
import com.fintech.sst.helper.ExpansionKt;
import com.fintech.sst.helper.RxBus;
import com.fintech.sst.net.ApiProducerModule;
import com.fintech.sst.net.ApiService;
import com.fintech.sst.net.Configuration;
import com.fintech.sst.net.ResultEntity;
import com.fintech.sst.net.SignRequestBody;
import com.fintech.sst.net.bean.OrderList;
import com.fintech.sst.net.bean.PageList;
import com.fintech.sst.ui.fragment.order.OrderModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.fintech.sst.helper.ExpansionKt.METHOD_ALI;
import static com.fintech.sst.helper.ExpansionKt.METHOD_WECHAT;
import static com.fintech.sst.helper.ExpansionKt.debug;
import static com.fintech.sst.net.Constants.KEY_MCH_ID_ALI;
import static com.fintech.sst.net.Constants.KEY_MCH_ID_WECHAT;
import static com.fintech.sst.net.Constants.KEY_USER_NAME_ALI;
import static com.fintech.sst.net.Constants.KEY_USER_NAME_WECHAT;


public class HeartService extends Service {
    private static final String TAG = "HeartService";
    private OrderModel orderModel = new OrderModel();
    private Disposable disposableAli;
    private Disposable disposableWeChat;

    private void heartBeat(final String type) {
        if (type.equals(ExpansionKt.METHOD_ALI)) {
            if (disposableAli != null)
                disposableAli.dispose();
        } else {
            if (disposableWeChat != null)
                disposableWeChat.dispose();
        }

        Observable.interval(0, 10 * 1000, TimeUnit.MILLISECONDS)
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) {
//                        boolean alive = PermissionUtil.isNotificationListenerEnabled();
//                        boolean alive = NotificationListener.isActive();
//                        if (!alive) {
//                            PermissionUtil.toggleNotificationListenerService(NotificationListener.class);
//                        }
//                        return alive;
                        return true;
                    }
                })
                .flatMap(new Function<Long, ObservableSource<ResultEntity<Boolean>>>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public ObservableSource<ResultEntity<Boolean>> apply(Long aLong) {
                        if (aLong % 6 == 0) {
                            orderModel.orderList(0, 1, ExpansionKt.getCloseOrderNum() * 2, type)
                                    .flatMap(new Function<ResultEntity<PageList<OrderList>>, ObservableSource<ResultEntity<String>>>() {
                                        @Override
                                        public ObservableSource<ResultEntity<String>> apply(ResultEntity<PageList<OrderList>> pageListResultEntity) {
                                            if (pageListResultEntity != null && pageListResultEntity.getResult() != null) {
                                                List<OrderList> list = pageListResultEntity.getResult().getList();
                                                if (list.isEmpty()) {
                                                    return Observable.empty();
                                                }

                                                List<OrderList> result = new ArrayList<>();
                                                for (OrderList item : list) {
                                                    if (item.getTradeStatus().equals("10") || item.getTradeStatus().equals("20")) {
                                                        continue;
                                                    }
                                                    result.add(item);
                                                }

                                                if (result.size()>ExpansionKt.getCloseOrderNum())
                                                    result = result.subList(0,ExpansionKt.getCloseOrderNum());
                                                int num = 0;
                                                for (OrderList item : result) {
                                                    if (item.getTradeStatus().equals("30")) {
                                                        num++;
                                                    }
                                                }

                                                if (num == 0 && result.size() >= ExpansionKt.getCloseOrderNum()) {
                                                    String keyUserName = null;
                                                    String keyMChId = null;
                                                    switch (type) {
                                                        case ExpansionKt.METHOD_ALI:
                                                            keyUserName = KEY_USER_NAME_ALI;
                                                            keyMChId = KEY_MCH_ID_ALI;
                                                            break;
                                                        case ExpansionKt.METHOD_WECHAT:
                                                            keyUserName = KEY_USER_NAME_WECHAT;
                                                            keyMChId = KEY_MCH_ID_WECHAT;
                                                            break;
                                                    }
                                                    HashMap<String, String> request = new HashMap<>();
                                                    request.put("appLoginName", Configuration.getUserInfoByKey(keyUserName));
                                                    request.put("loginUserId", Configuration.getUserInfoByKey(keyMChId));
                                                    request.put("type", type);
                                                    request.put("enable", "0");
                                                    return ApiProducerModule.create(ApiService.class).aisleStatus(new SignRequestBody(request).sign(type));
                                                } else {
                                                    return Observable.empty();
                                                }
                                            }
                                            return Observable.empty();
                                        }
                                    })
                                    .subscribe(new Observer<ResultEntity<String>>() {
                                        Disposable d;

                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            this.d = d;
                                            debug(TAG, "=======heartBeat orderList " + type + " onSubscribe======");
                                        }

                                        @Override
                                        public void onNext(ResultEntity<String> stringResultEntity) {
                                            Notice notice = new Notice();
                                            int noticeType = 0;
                                            switch (type) {
                                                case ExpansionKt.METHOD_ALI:
                                                    noticeType = 1;
                                                    break;
                                                case ExpansionKt.METHOD_WECHAT:
                                                    noticeType = 2;
                                                    break;
                                            }
                                            notice.type = noticeType;
                                            RxBus.getDefault().send(notice);
                                            debug(TAG, "=======heartBeat orderList " + type + " onNext======");
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            if (this.d != null)
                                                this.d.dispose();
                                            debug(TAG, "=======heartBeat orderList " + type + " onError======");
                                        }

                                        @Override
                                        public void onComplete() {
                                            if (this.d != null)
                                                this.d.dispose();
                                            debug(TAG, "=======heartBeat orderList " + type + " onComplete======");
                                        }
                                    });
                        }

                        HashMap<String, String> request = new HashMap<>();
                        request.put("payMethod", type);
                        return ApiProducerModule.create(ApiService.class).heartbeat(new SignRequestBody(request).sign(type));
                    }
                })
                .subscribe(new Observer<ResultEntity<Boolean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (type.equals(ExpansionKt.METHOD_ALI)) {
                            disposableAli = d;
                        } else {
                            disposableWeChat = d;
                        }

                        debug(TAG, "=======heartBeat " + type + " onSubscribe======");
                    }

                    @Override
                    public void onNext(ResultEntity<Boolean> booleanResultEntity) {
                        debug(TAG, "=======heartBeat " + type + " onNext======");
                        if (booleanResultEntity.getCode().equals("20001") && (booleanResultEntity.getMsg().contains("签名错误") || booleanResultEntity.getMsg().contains("未登录") )) {
                            Notice notice = new Notice();
                            int noticeType = 0;
                            switch (type) {
                                case ExpansionKt.METHOD_ALI:
                                    noticeType = 11;
                                    disposableAli.dispose();
                                    break;
                                case ExpansionKt.METHOD_WECHAT:
                                    noticeType = 12;
                                    disposableWeChat.dispose();
                                    break;
                            }
                            notice.type = noticeType;
                            RxBus.getDefault().send(notice);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (type.equals(ExpansionKt.METHOD_ALI)) {
                            disposableAli.dispose();
                        } else {
                            disposableWeChat.dispose();
                        }
                        debug(TAG, "=======heartBeat " + type + " onError======");
                        SystemClock.sleep(10 * 1000);
                        heartBeat(type);
                    }

                    @Override
                    public void onComplete() {
                        if (type.equals(ExpansionKt.METHOD_ALI)) {
                            disposableAli.dispose();
                        } else {
                            disposableWeChat.dispose();
                        }
                        debug(TAG, "=======heartBeat " + type + " onComplete======");
                        SystemClock.sleep(10 * 1000);
                        heartBeat(type);
                    }
                });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        debug(TAG, "=======onCreate======");
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
        debug(TAG, "=======onDestroy======");
        if (disposableAli != null)
            disposableAli.dispose();
        if (disposableWeChat != null)
            disposableWeChat.dispose();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Configuration.isLogin(METHOD_ALI)) {//登录的时候开启心跳
            try {
                if (disposableAli == null || disposableAli.isDisposed())
                    heartBeat(METHOD_ALI);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Configuration.isLogin(METHOD_WECHAT)) {//登录的时候开启心跳
            try {
                if (disposableWeChat == null || disposableWeChat.isDisposed())
                    heartBeat(METHOD_WECHAT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
