package com.fintech.sst.net;


import android.Manifest;

import okhttp3.MediaType;

public class Constants {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

//    public static final String baseUrl = "https://api.pay.hccf8.com";
    public static String baseUrl = "http://api.3721sz.com";
//    public static String baseUrl = "http://api.96taichi.tk";
//    public static String baseUrl = "http://api.trueinfo.cn";
    public static String nettyAddress = "47.52.100.118";//正式
//    public static String nettyAddress = "47.244.97.157";//正式
//    public static String nettyAddress = "47.96.69.207";//测试

    public static final int RC_PERMISSION = 110;
    public static final int ALL_PERMISSION = 122;

    public static String[] PERMISSIONS_GROUP = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE
    };

    public static final String KEY_SP_ = "userInfo_sst";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CLOSE_TIME = "closeTime";
    public static final String KEY_CLOSE_ORDER_NUM = "closeOrderNum";


    public static final String KEY_MCH_ID_ALI = "mchIdAli";
    public static final String KEY_USER_NAME_ALI = "userNameAli";
    public static final String KEY_PASSWORD_ALI = "passwordAli";
    public static final String KEY_ACCOUNT_ALI = "accountAli";
    public static final String KEY_ALLOW_LOAD_ALI = "allowLoadAli";
    public static final String KEY_BEGIN_NUM_ALI = "beginNumAli";
    public static final String KEY_END_NUM_ALI = "endNumAli";
    public static final String KEY_MAX_NUM_ALI = "maxNumAli";
    public static final String KEY_LOGIN_TOKEN_ALI = "loginTokenAli";


    public static final String KEY_MCH_ID_WECHAT = "mchIdWechat";
    public static final String KEY_USER_NAME_WECHAT = "userNameWechat";
    public static final String KEY_PASSWORD_WECHAT = "passwordWechat";
    public static final String KEY_ACCOUNT_WECHAT = "accountWechat";
    public static final String KEY_ALLOW_LOAD_WECHAT = "allowLoadWechat";
    public static final String KEY_BEGIN_NUM_WECHAT = "beginNumWechat";
    public static final String KEY_END_NUM_WECHAT = "endNumWechat";
    public static final String KEY_MAX_NUM_WECHAT = "maxNumWechat";
    public static final String KEY_LOGIN_TOKEN_WECHAT = "loginTokenWechat";
    public static final String KEY_WECHAT_REGEX = "wechatRegex";


    public static final String KEY_MCH_ID_BANK = "mchIdBANK";
    public static final String KEY_USER_NAME_BANK = "userNameBANK";
    public static final String KEY_PASSWORD_BANK = "passwordBANK";
    public static final String KEY_ACCOUNT_BANK = "accountBANK";
    public static final String KEY_ALLOW_LOAD_BANK = "allowLoadBANK";
    public static final String KEY_BEGIN_NUM_BANK = "beginNumBANK";
    public static final String KEY_END_NUM_BANK = "endNumBANK";
    public static final String KEY_MAX_NUM_BANK = "maxNumBANK";
    public static final String KEY_LOGIN_TOKEN_BANK = "loginTokenBANK";
    public static final String KEY_BANK_CODE = "bankCode";
    public static final String KEY_BANK_REGEX = "bankRegex";
    public static final String KEY_BANK_TYPE = "bankType";
    public static final String KEY_ACCOUNT_ID_BANK = "accountIdBank";


    public static final String KEY_MCH_ID_YUN = "mchIdYUN";
    public static final String KEY_USER_NAME_YUN = "userNameYUN";
    public static final String KEY_PASSWORD_YUN = "passwordYUN";
    public static final String KEY_ACCOUNT_YUN = "accountYUN";
    public static final String KEY_ALLOW_LOAD_YUN = "allowLoadYUN";
    public static final String KEY_BEGIN_NUM_YUN = "beginNumYUN";
    public static final String KEY_END_NUM_YUN = "endNumYUN";
    public static final String KEY_MAX_NUM_YUN = "maxNumYUN";
    public static final String KEY_LOGIN_TOKEN_YUN = "loginTokenYUN";
    public static final String KEY_ACCOUNT_ID_YUN = "accountIdYUN";

    public static final String KEY_MCH_ID_QQ = "mchIdQQ";
    public static final String KEY_USER_NAME_QQ = "userNameQQ";
    public static final String KEY_PASSWORD_QQ = "passwordQQ";
    public static final String KEY_ACCOUNT_QQ = "accountQQ";
    public static final String KEY_ALLOW_LOAD_QQ = "allowLoadQQ";
    public static final String KEY_BEGIN_NUM_QQ = "beginNumQQ";
    public static final String KEY_END_NUM_QQ = "endNumQQ";
    public static final String KEY_MAX_NUM_QQ = "maxNumQQ";
    public static final String KEY_LOGIN_TOKEN_QQ = "loginTokenQQ";
    public static final String KEY_ACCOUNT_ID_QQ = "accountIdQQ";


    public static final String KEY_ADDRESS_WEB = "address_web";
    public static final String KEY_ADDRESS_NETTY = "address_netty";
}
