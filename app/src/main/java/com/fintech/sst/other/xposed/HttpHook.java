package com.fintech.sst.other.xposed;

import android.os.Build;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class HttpHook extends XC_MethodHook {

    public static final String TAG = "Inspeckage_Http:";

    public static void initAllHooks(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            final Class<?> httpUrlConnection = findClass("java.net.HttpURLConnection", loadPackageParam.classLoader);
            hookAllConstructors(httpUrlConnection, new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (param.args.length != 1 || param.args[0].getClass() != URL.class) {
                        return;
                    }
                    XposedBridge.log(TAG + "HttpURLConnection: " + param.args[0] + "");
                }
            });
        } catch (Exception e) {
            XposedBridge.log("HttpHook错误：" + e.getMessage());
        }

        XC_MethodHook RequestHook = new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) {
                HttpURLConnection urlConn = (HttpURLConnection) param.thisObject;
                if (urlConn != null) {
                    StringBuilder sb = new StringBuilder();
                    boolean connected = (boolean) getObjectField(param.thisObject, "connected");
                    if (!connected) {
                        Map<String, List<String>> properties = urlConn.getRequestProperties();
                        if (properties != null && properties.size() > 0) {
                            for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
                                sb.append(entry.getKey() + ": " + entry.getValue() + ", ");
                            }
                        }
                        //DataOutputStream dos = (DataOutputStream) param.getResult();
                        XposedBridge.log(TAG + "REQUEST: method=" + urlConn.getRequestMethod() + " " + "URL=" + urlConn.getURL().toString() + " " + "Params=" + sb.toString());
                    }
                }
            }
        };


        XC_MethodHook ResponseHook = new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                HttpURLConnection urlConn = (HttpURLConnection) param.thisObject;
                if (urlConn != null) {
                    StringBuilder sb = new StringBuilder();
                    int code = urlConn.getResponseCode();
                    if (code == 200) {
                        Map<String, List<String>> properties = urlConn.getHeaderFields();
                        if (properties != null && properties.size() > 0) {
                            for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
                                sb.append(entry.getKey() + ": " + entry.getValue() + ", ");
                            }
                        }
                    }
                    XposedBridge.log(TAG + "RESPONSE: method=" + urlConn.getRequestMethod() + " " + "URL=" + urlConn.getURL().toString() + " " + "Params=" + sb.toString());
                }
            }
        };

//        try {
//            final Class<?> okHttpClient = findClass("okhttp3.OkHttpClient", loadPackageParam.classLoader);
//            if (okHttpClient != null) {
//                findAndHookMethod(okHttpClient, "open", URI.class, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        URI uri = null;
//                        if (param.args[0] != null)
//                            uri = (URI) param.args[0];
//                        XposedBridge.log(TAG + "OkHttpClient: " + uri.toString() + "");
//                    }
//                });
//            }
//        } catch (Exception e) {
//            XposedBridge.log(e.getMessage());
//        }

        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                findAndHookMethod("libcore.net.http.HttpURLConnectionImpl", loadPackageParam.classLoader, "getOutputStream", RequestHook);
            } else {
                final Class<?> httpURLConnectionImpl = findClass("com.android.okhttp.internal.http.HttpURLConnectionImpl", loadPackageParam.classLoader);
                if (httpURLConnectionImpl != null) {
                    findAndHookMethod("com.android.okhttp.internal.http.HttpURLConnectionImpl", loadPackageParam.classLoader, "getOutputStream", RequestHook);
                    findAndHookMethod("com.android.okhttp.internal.http.HttpURLConnectionImpl", loadPackageParam.classLoader, "getInputStream", ResponseHook);
                }
            }
        } catch (Exception e) {
            XposedBridge.log("HttpHook错误：" + e.getMessage());
        }

        findAndHookMethod(SSLContext.class, "init",
                KeyManager[].class, TrustManager[].class, SecureRandom.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) {
                        KeyManager[] km = (KeyManager[]) param.args[0];
                        TrustManager[] tm_ = (TrustManager[]) param.args[1];

                        if (tm_ != null && tm_[0] != null) {
                            X509TrustManager tm = (X509TrustManager) tm_[0];
                            X509Certificate[] chain = new X509Certificate[]{};
                            XposedBridge.log(TAG + "Possible pinning.");
                        }
                    }
                });
    }
}