package com.fintech.sst.helper;

import android.app.Notification;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ParsedNotification {

    private Notification mRawNotifiction;

    private String mTickerText = "";
    private int mFlags = 0;
    private String mCategory = "";
    private String mGroup = "";
    private Bundle mExtras = null;

    public ParsedNotification(Notification notification) {
        if (notification == null) {
            this.mRawNotifiction = null;
            return;
        }

        this.mRawNotifiction = notification;
        this.mFlags = notification.flags;
        this.mCategory = NotificationCompat.getCategory(notification);
        this.mGroup = NotificationCompat.getGroup(notification);
        this.mExtras = NotificationCompat.getExtras(notification);

        if (notification.tickerText != null) {
            this.mTickerText = notification.tickerText.toString();
        }

        if (notification.contentView != null) {
            parseRemoteViews(notification.contentView, true);
        }

        if (notification.bigContentView != null) {
            parseRemoteViews(notification.bigContentView, false);
        }
    }


    public float parseAmountAli() {

        String pattern = "付款([0-9]+.[0-9][0-9])元$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(mTickerText);
        if (m.find()) {
            String group = m.group(1);
            return Float.parseFloat(group);
        }else{
            pattern = "成功收款([0-9]+.[0-9][0-9])元。";
            r = Pattern.compile(pattern);
            Matcher n = r.matcher(mTickerText);
            String group = null;
            while (n.find()){
                group = n.group(1);
            }
            if (group!=null)
                return Float.parseFloat(group);
            else
                return 0;
        }
    }

    public float parseAmountWeChat() {

        String pattern = "收款([0-9]+.[0-9][0-9])元$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(mTickerText);
        if (m.find()) {
            String group = m.group(1);
            return Float.parseFloat(group);
        }
        return 0;
    }

    private void parseRemoteViews(RemoteViews remoteViews, boolean b) {
        if (remoteViews == null) {
            return;
        }

        //TODO  do parseRomoteViews
    }

    public Notification getmRawNotifiction() {
        return mRawNotifiction;
    }

    public void setmRawNotifiction(Notification mRawNotifiction) {
        this.mRawNotifiction = mRawNotifiction;
    }

    public String getmTickerText() {
        return mTickerText;
    }

    public void setmTickerText(String mTickerText) {
        this.mTickerText = mTickerText;
    }

    public int getmFlags() {
        return mFlags;
    }

    public void setmFlags(int mFlags) {
        this.mFlags = mFlags;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getmGroup() {
        return mGroup;
    }

    public void setmGroup(String mGroup) {
        this.mGroup = mGroup;
    }

    public Bundle getmExtras() {
        return mExtras;
    }

    public void setmExtras(Bundle mExtras) {
        this.mExtras = mExtras;
    }

    @Override
    public String toString() {
        return "ParsedNotification{" +
                "mRawNotifiction=" + mRawNotifiction +
                ", mTickerText='" + mTickerText + '\'' +
                ", mFlags=" + mFlags +
                ", mCategory='" + mCategory + '\'' +
                ", mGroup='" + mGroup + '\'' +
                ", mExtras=" + mExtras +
                '}';
    }
}

