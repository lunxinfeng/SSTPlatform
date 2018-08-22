package com.fintech.sst.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.fintech.sst.data.db.DB;
import com.fintech.sst.data.db.Notice;
import com.fintech.sst.helper.RxBus;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.fintech.sst.helper.ExpansionKt.debug;

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
        localHashSet.add("com.eg.android.AlipayGphone".toLowerCase());
        PACKAGES_LOWER_CASE = Collections.unmodifiableSet(localHashSet);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "onListenerConnected:");
    }

    @Override
    public final void onNotificationPosted(StatusBarNotification statusBarNotification) {
        Log.d(TAG, "onNotificationPosted: " + statusBarNotification);

        if (statusBarNotification == null || statusBarNotification.getNotification() == null) {
            return;
        }

        String str = statusBarNotification.getPackageName();
        if (TextUtils.isEmpty(str)) {
            return;
        }

        Log.d(TAG, "onNotificationPosted: " + statusBarNotification.getNotification().tickerText);
        Log.d(TAG, "onNotificationPosted: " + statusBarNotification.getNotification().when);
        Date date = new Date(statusBarNotification.getNotification().when);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        Log.d(TAG, "onNotificationPosted: " + sdf.format(date));

        if (PACKAGES_LOWER_CASE.contains(str.toLowerCase())) {//监控 微信 and 支付宝  Notification
            sLock.lock();
            try {
                onPostedAsync(statusBarNotification);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                sLock.unlock();
            }
        }
    }

    protected void onPostedAsync(StatusBarNotification statusBarNotification) {
        if (statusBarNotification == null) return;
        String packageName = statusBarNotification.getPackageName();
        Notification notification = statusBarNotification.getNotification();
        if (notification == null) return;
        Notice notice = new Notice();
        notice.content = statusBarNotification.getNotification().tickerText.toString();
        notice.saveTime = statusBarNotification.getNotification().when;
        notice.status = 0;
        switch (packageName){
            case "com.tencent.mm":
                notice.type = 200;
                break;
            case "com.eg.android.AlipayGphone":
                notice.type = 100;
                break;
        }
        notices.offer(notice);
    }

    @Override
    public final void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        Log.d(TAG, "onNotificationRemoved: " + statusBarNotification);
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

    private void db(){
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (notices.size()>0){
                            Notice notice = notices.poll();
                            RxBus.getDefault().send(notice);
                            long id = DB.insert(NotificationListener.this,notice);
                            debug(TAG,"=========DB========: " + id);
                        }else{
                            if (aLong%50 == 0)
                                debug(TAG,"=========DB========: null");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        disposable.dispose();
                        db();
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                        db();
                    }
                });
    }

    public static boolean isActive(){
        return disposable!=null && !disposable.isDisposed();
    }
}


