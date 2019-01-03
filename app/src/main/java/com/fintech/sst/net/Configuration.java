package com.fintech.sst.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.fintech.sst.App;
import com.fintech.sst.helper.ExpansionKt;

import static com.fintech.sst.net.Constants.KEY_ADDRESS;
import static com.fintech.sst.net.Constants.KEY_LOGIN_TOKEN_ALI;
import static com.fintech.sst.net.Constants.KEY_LOGIN_TOKEN_BANK;
import static com.fintech.sst.net.Constants.KEY_LOGIN_TOKEN_WECHAT;
import static com.fintech.sst.net.Constants.KEY_MCH_ID_ALI;
import static com.fintech.sst.net.Constants.KEY_MCH_ID_BANK;
import static com.fintech.sst.net.Constants.KEY_MCH_ID_WECHAT;
import static com.fintech.sst.net.Constants.KEY_SP_;
import static com.fintech.sst.net.Constants.KEY_USER_NAME_ALI;
import static com.fintech.sst.net.Constants.KEY_USER_NAME_BANK;
import static com.fintech.sst.net.Constants.KEY_USER_NAME_WECHAT;


/**
 * 信息管理类;公共管理类 sp
 */
public class Configuration {

    public Configuration() {

    }

    public static void putUserInfo(String key, String value) {
        SharedPreferences sp = App.getApplication().getSharedPreferences(KEY_SP_, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String getUserInfoByKey(String key) {
        SharedPreferences sp = App.getApplication().getSharedPreferences(KEY_SP_, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void removeUserInfoByKey(String key) {
        SharedPreferences sp = App.getApplication().getSharedPreferences(KEY_SP_, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.apply();
    }

    public static KEY_SP_ getKeySp(String type) {
        SharedPreferences sp = App.getApplication().getSharedPreferences(KEY_SP_, Context.MODE_PRIVATE);
        Configuration.KEY_SP_ key_sp_ = new KEY_SP_();
        switch (type){
            case ExpansionKt.METHOD_ALI:
                key_sp_.mchId = sp.getString(KEY_MCH_ID_ALI, "");
                key_sp_.userName = sp.getString(KEY_USER_NAME_ALI, "");
                key_sp_.loginToken = sp.getString(KEY_LOGIN_TOKEN_ALI, "");
                break;
            case ExpansionKt.METHOD_WECHAT:
                key_sp_.mchId = sp.getString(KEY_MCH_ID_WECHAT, "");
                key_sp_.userName = sp.getString(KEY_USER_NAME_WECHAT, "");
                key_sp_.loginToken = sp.getString(KEY_LOGIN_TOKEN_WECHAT, "");
                break;
            case ExpansionKt.METHOD_BANK:
                key_sp_.mchId = sp.getString(KEY_MCH_ID_BANK, "");
                key_sp_.userName = sp.getString(KEY_USER_NAME_BANK, "");
                key_sp_.loginToken = sp.getString(KEY_LOGIN_TOKEN_BANK, "");
                break;
        }

        return key_sp_;
    }

    public static boolean clearUserInfo(String type) {

//        AlarmCompact.cancelAlarm(App.getApplication().getApplicationContext());
//        JobServiceCompact.cancelAllJobs(App.getAppContext());

        SharedPreferences sp = App.getApplication().getSharedPreferences(KEY_SP_, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        switch (type){
            case ExpansionKt.METHOD_ALI:
                edit.remove(KEY_MCH_ID_ALI);
                edit.remove(KEY_LOGIN_TOKEN_ALI);
                break;
            case ExpansionKt.METHOD_WECHAT:
                edit.remove(KEY_MCH_ID_WECHAT);
                edit.remove(KEY_LOGIN_TOKEN_WECHAT);
                break;
            case ExpansionKt.METHOD_BANK:
                edit.remove(KEY_MCH_ID_BANK);
                edit.remove(KEY_LOGIN_TOKEN_BANK);
                break;
        }
        return edit.commit();
    }

    public static boolean noLogin(String type) {
        switch (type){
            case ExpansionKt.METHOD_ALI:
                return TextUtils.isEmpty(Configuration.getUserInfoByKey(KEY_LOGIN_TOKEN_ALI));
            case ExpansionKt.METHOD_WECHAT:
                return TextUtils.isEmpty(Configuration.getUserInfoByKey(KEY_LOGIN_TOKEN_WECHAT));
            case ExpansionKt.METHOD_BANK:
                return TextUtils.isEmpty(Configuration.getUserInfoByKey(KEY_LOGIN_TOKEN_BANK));
        }
        return false;
    }

    public static boolean noAddress() {
        return TextUtils.isEmpty(Configuration.getUserInfoByKey(KEY_ADDRESS));
    }

    public static boolean isLogin(String type) {
        return !noLogin(type);
    }

    public static class KEY_SP_ {
        private String mchId;

        private String userName;

        private String loginToken;

        public String getMchId() {
            return mchId;
        }

        public void setMchId(String mchId) {
            this.mchId = mchId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getLoginToken() {
            return loginToken;
        }

        public void setLoginToken(String loginToken) {
            this.loginToken = loginToken;
        }
    }
}
