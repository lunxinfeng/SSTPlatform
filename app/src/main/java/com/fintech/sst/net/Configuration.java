package com.fintech.sst.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.fintech.sst.App;

import static com.fintech.sst.net.Constants.*;


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

    public static KEY_SP_ getKeySp() {
        SharedPreferences sp = App.getApplication().getSharedPreferences(KEY_SP_, Context.MODE_PRIVATE);
        Configuration.KEY_SP_ key_sp_ = new KEY_SP_();
        key_sp_.mchId = sp.getString(KEY_MCH_ID, "");
        key_sp_.userName = sp.getString(KEY_USER_NAME, "");
        key_sp_.loginToken = sp.getString(KEY_LOGIN_TOKEN, "");
        return key_sp_;
    }

    public static boolean clearUserInfo() {

//        AlarmCompact.cancelAlarm(App.getApplication().getApplicationContext());
//        JobServiceCompact.cancelAllJobs(App.getAppContext());

        SharedPreferences sp = App.getApplication().getSharedPreferences(KEY_SP_, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(KEY_MCH_ID);
        edit.remove(KEY_LOGIN_TOKEN);
        return edit.commit();
    }

    public static boolean noLogin() {
        return TextUtils.isEmpty(Configuration.getUserInfoByKey(KEY_LOGIN_TOKEN));
    }

    public static boolean noAddress() {
        return TextUtils.isEmpty(Configuration.getUserInfoByKey(KEY_ADDRESS));
    }

    public static boolean isLogin() {
        return !noLogin();
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
