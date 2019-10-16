package com.fintech.sst.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.fintech.sst.data.db.DB;
import com.fintech.sst.data.db.Notice;
import com.fintech.sst.helper.ExpansionKt;
import com.fintech.sst.helper.ParsedNotification;
import com.fintech.sst.helper.RxBus;
import com.fintech.sst.net.ApiProducerModule;
import com.fintech.sst.net.ApiService;
import com.fintech.sst.net.Configuration;
import com.fintech.sst.net.MessageRequestBody;
import com.fintech.sst.net.ResultEntity;
import com.fintech.sst.net.SignRequestBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.fintech.sst.helper.ExpansionKt.debug;
import static com.fintech.sst.net.Constants.KEY_MCH_ID_ALI;
import static com.fintech.sst.net.Constants.KEY_USER_NAME_ALI;

@TargetApi(18)
public final class NotificationListener extends NotificationListenerService {
    private static final String TAG = "NotificationListener";
    private static final Set<String> PACKAGES_LOWER_CASE;
    private static final Lock sLock = new ReentrantLock();
    private LinkedList<Notice> notices = new LinkedList<>();
    public static Disposable disposable;

    static {
        HashSet<String> localHashSet = new HashSet<>();
        localHashSet.add("com.tencent.mm");
//        localHashSet.add("com.eg.android.AlipayGphone".toLowerCase());
        PACKAGES_LOWER_CASE = Collections.unmodifiableSet(localHashSet);
    }

    @Override
    public final void onNotificationPosted(StatusBarNotification statusBarNotification) {
        Log.d(TAG, "onNotificationPosted: " + statusBarNotification + "\t" + this);

        if (statusBarNotification == null || statusBarNotification.getNotification() == null) {
            return;
        }

        String str = statusBarNotification.getPackageName();
        if (TextUtils.isEmpty(str)) {
            return;
        }

        debug(TAG, "onNotificationPosted: " + statusBarNotification.getNotification().tickerText);
        debug(TAG, "thread: " + Thread.currentThread().getName());

        if (PACKAGES_LOWER_CASE.contains(str.toLowerCase())) {//监控 微信 and 支付宝  Notification
            onPostedAsync(statusBarNotification);
//            sLock.lock();
//            try {
//                onPostedAsync(statusBarNotification);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                sLock.unlock();
//            }
        }
    }

    protected synchronized void onPostedAsync(StatusBarNotification statusBarNotification) {
        if (statusBarNotification == null) return;
        String packageName = statusBarNotification.getPackageName();

        Notification notification = statusBarNotification.getNotification();
        if (notification == null || notification.tickerText == null) return;
        debug(TAG, "notification: " + notification);
        debug(TAG, "notification text: " + notification.tickerText);
        Notice notice = new Notice();
        notice.content = notification.tickerText.toString();
//        notice.content = "支付宝通知: 收款100.01元";
        notice.saveTime = notification.when;
        notice.status = 2;
        notice.tag = statusBarNotification.getTag();
        notice.noticeId = statusBarNotification.getId();
        notice.packageName = packageName;
        ParsedNotification parsedNotification = new ParsedNotification(notification);
        notice.title = parsedNotification.getmExtras().getString("android.title");
//        notice.title = "微信支付";
//        notice.title = "支付宝通知";

        if (packageName.equalsIgnoreCase("com.eg.android.AlipayGphone")){//支付宝只监听风控信息
            check(packageName, notice);
            return;
        }

        float amount = 0;
        switch (packageName) {
            case "com.tencent.mm":
                notice.type = 1001;
                amount = parsedNotification.parseAmountWeChat();
                notice.amount = amount + "";
                break;
            case "com.eg.android.AlipayGphone":
                notice.type = 2001;
                amount = parsedNotification.parseAmountAli();
                notice.amount = amount + "";
                break;
        }
        debug(TAG, "通知收款: " + notice + "\t" +  parsedNotification.getmTickerText());
        System.out.println("++++offer" + notice);
        if (amount!=0)
            notices.offer(notice);
    }

    /**
     * 是否被风控
     */
    private boolean check(String packageName, Notice notice) {
        if (notice.content.startsWith("支付宝禁止一切提供赌博咨询或参与赌博的行为")){
            Notice close = new Notice();
            close.type = 111;


            HashMap<String, String> request = new HashMap<>();
            request.put("appLoginName", Configuration.getUserInfoByKey(KEY_USER_NAME_ALI));
            request.put("loginUserId", Configuration.getUserInfoByKey(KEY_MCH_ID_ALI));
            request.put("type", ExpansionKt.METHOD_ALI);
            request.put("enable", "0");
            ApiProducerModule.create(ApiService.class).aisleStatus(new SignRequestBody(request).sign(ExpansionKt.METHOD_ALI)).subscribe();

            RxBus.getDefault().send(close);
            return true;
        }
        return false;
    }

    @Override
    public final void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        Log.d(TAG, "onNotificationRemoved: " + statusBarNotification + "\t" + this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        db();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void db() {
        if (disposable != null)
            disposable.dispose();
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return notices.size() > 0 && Configuration.isLogin(notices.get(0).type + "");
                    }
                })
                .flatMap(new Function<Long, ObservableSource<ResultEntity<Notice>>>() {
                    @Override
                    public ObservableSource<ResultEntity<Notice>> apply(Long aLong) throws Exception {
                        Notice notice = notices.poll();
                        debug(TAG, "=========DB========: 发起请求" + notice.uuid);
                        MessageRequestBody body = new MessageRequestBody();
                        body.put("uuid", notice.uuid);
                        body.put("amount", notice.amount);
                        body.put("title", notice.title);
                        body.put("content", notice.content);
                        body.put("time", notice.saveTime);
                        body.put("type", notice.type);
                        body.put("packageName", notice.packageName);
                        body.put("id", notice.noticeId);
                        body.put("tag", notice.tag);
                        body.sign(notice.type + "");
                        System.out.println("++++insert" + notice);
                        DB.insert(NotificationListener.this, notice);
                        RxBus.getDefault().send(notice);
                        return ApiProducerModule
                                .create(ApiService.class)
                                .notifyLog(body);
                    }
                })
                .subscribe(new Observer<ResultEntity<Notice>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(ResultEntity<Notice> resultEntity) {
                        Notice notice = resultEntity.getResult();
                        if (resultEntity.getMsg().equals("success") && notice!=null){
                            notice.status = 1;
                            DB.updateAll(NotificationListener.this, notice);
                            cancelAllNotifications();
                        }
                        debug(TAG, "=========DB========: " + resultEntity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "db onError");
                        e.printStackTrace();
                        disposable.dispose();
                        db();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "db onComplete");
                        disposable.dispose();
                        db();
                    }
                });
    }

    public static boolean isActive() {
        return disposable != null && !disposable.isDisposed();
    }


    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "onListenerConnected:");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.d(TAG, "onListenerDisconnected:");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind:");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind:");
        return super.onUnbind(intent);
    }

    @Override
    public void onListenerHintsChanged(int hints) {
        super.onListenerHintsChanged(hints);
        Log.d(TAG, "onListenerHintsChanged:");
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind:");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory:");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "onTrimMemory:");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved:");
    }
}


