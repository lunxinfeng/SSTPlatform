package com.fintech.sst.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@TargetApi(18)
public final class NotificationListener extends NotificationListenerService {
    private static final String TAG = "NotificationListener";
    private static final Set<String> PACKAGES_LOWER_CASE;
    private static final Lock sLock = new ReentrantLock();

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
//        KMessageManager.getInstance().onNotificationPosted(statusBarNotification);
    }

    @Override
    public final void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        Log.d(TAG, "onNotificationRemoved: " + statusBarNotification);
//        KMessageManager.getInstance().onNotificationRemoved(statusBarNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");
        return Service.START_STICKY;
    }
}


