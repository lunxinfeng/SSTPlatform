package com.fintech.sst.helper;

import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Handler;

public class SmsObserverUtil {
    private static final Uri SMS_MESSAGE_URI = Uri.parse("content://sms");
    public static SmsDatabaseChaneObserver mSmsDBChangeObserver;
    public static void registerSmsDatabaseChangeObserver(ContextWrapper contextWrapper) {
        //因为，某些机型修改rom导致没有getContentResolver
        if (mSmsDBChangeObserver != null) return;
        try {
            System.out.println("注册短信监听");
            mSmsDBChangeObserver = new SmsDatabaseChaneObserver(contextWrapper.getContentResolver(), new Handler());
            contextWrapper.getContentResolver().registerContentObserver(SMS_MESSAGE_URI, true, mSmsDBChangeObserver);
        } catch (Throwable b) {
            b.printStackTrace();
        }
    }

    public static void unregisterSmsDatabaseChangeObserver(ContextWrapper contextWrapper) {
        try {
            System.out.println("取消短信监听");
            contextWrapper.getContentResolver().unregisterContentObserver(mSmsDBChangeObserver);
            mSmsDBChangeObserver = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
