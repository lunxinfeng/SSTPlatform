package com.fintech.sst.other.xposed;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fintech.sst.other.xposed.qq.QQDBManager;
import com.fintech.sst.other.xposed.qq.QQHook;
import com.fintech.sst.other.xposed.qq.QQPlugHook;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import dalvik.system.BaseDexClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main implements IXposedHookLoadPackage {
    public static String WECHAT_PACKAGE = "com.tencent.mm";
    public static String ALIPAY_PACKAGE = "com.eg.android.AlipayGphone";
    public static String QQ_PACKAGE = "com.tencent.mobileqq";
    public static String QQ_WALLET_PACKAGE = "com.qwallet";
    public static String UNIONPAY_PACKAGE = "com.unionpay";
    public static boolean WECHAT_PACKAGE_ISHOOK = false;
    public static boolean ALIPAY_PACKAGE_ISHOOK = false;
    public static boolean QQ_PACKAGE_ISHOOK = false;
    public static boolean QQ_WALLET_ISHOOK = false;


    public static String mSignkey = "";//密钥
    public static String mNotifyurl = "";//地址

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("lxf handleLoadPackage: " + lpparam.packageName);

        if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM |
                ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }
        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;
//        if (WECHAT_PACKAGE.equals(packageName)) {
//    		try {
//                XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        Context context = (Context) param.args[0];
//                        ClassLoader appClassLoader = context.getClassLoader();
//                        if(WECHAT_PACKAGE.equals(processName) && !WECHAT_PACKAGE_ISHOOK){
//                        	WECHAT_PACKAGE_ISHOOK=true;
//                        	//注册广播
//                        	StartWechatReceived stratWechat=new StartWechatReceived();
//                    		IntentFilter intentFilter = new IntentFilter();
//                            intentFilter.addAction("com.payhelper.wechat.start");
//                            context.registerReceiver(stratWechat, intentFilter);
//                        	XposedBridge.log("handleLoadPackage: " + packageName);
//                        	PayHelperUtils.sendmsg(context, "微信Hook成功，当前微信版本:"+PayHelperUtils.getVerName(context));
//                        	new WechatHook().hook(appClassLoader,context);
//                        }
//                    }
//                });
//            } catch (Throwable e) {
//                XposedBridge.log(e);
//            }
//        }else
        if (ALIPAY_PACKAGE.equals(packageName)) {
            try {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Context context = (Context) param.args[0];
                        ClassLoader appClassLoader = context.getClassLoader();
                        if (ALIPAY_PACKAGE.equals(processName) && !ALIPAY_PACKAGE_ISHOOK) {
                            ALIPAY_PACKAGE_ISHOOK = true;
                            //注册广播
                            StartAlipayReceived startAlipay = new StartAlipayReceived();
                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction("com.payhelper.alipay.start");

                            intentFilter.addAction("com.payhelper.alipay.setData");

                            context.registerReceiver(startAlipay, intentFilter);
                            XposedBridge.log("支付宝Hook成功，当前支付宝版本:" + PayHelperUtils.getVerName(context));
                            PayHelperUtils.sendmsg(context, "支付宝Hook成功，当前支付宝版本:" + PayHelperUtils.getVerName(context));
                            new AlipayHook().hook(appClassLoader, context);
                        }
                    }
                });
            } catch (Throwable e) {
                XposedBridge.log(e);
            }
        }
        else if(QQ_PACKAGE.equals(packageName)){
        	try {
        		 XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Context context = (Context) param.args[0];
                        ClassLoader appClassLoader = context.getClassLoader();
                        if(QQ_PACKAGE.equals(processName) && !QQ_PACKAGE_ISHOOK){
                        	QQ_PACKAGE_ISHOOK=true;
                        	//注册广播
                        	StartQQReceived startQQ=new StartQQReceived();
                    		IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction("com.payhelper.qq.start");
                            context.registerReceiver(startQQ, intentFilter);
                        	XposedBridge.log("handleLoadPackage: " + packageName);
                        	PayHelperUtils.sendmsg(context, "QQHook成功，当前QQ版本:"+PayHelperUtils.getVerName(context));
    						new QQHook().hook(appClassLoader,context);
                        }
                    }
                });

    		 XposedHelpers.findAndHookConstructor("dalvik.system.BaseDexClassLoader",
                     lpparam.classLoader, String.class, File.class, String.class, ClassLoader.class, new XC_MethodHook() {
                 @Override
                 protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                     if (param.args[0].toString().contains("qwallet_plugin.apk")) {
                         ClassLoader classLoader = (BaseDexClassLoader) param.thisObject;
                         new QQPlugHook().hook(classLoader);
                     }
                 }
             });
    		}catch (Exception e) {
                XposedBridge.log(e);
            }
        }
        else if (UNIONPAY_PACKAGE.equals(packageName)){
            try {
                if (!MIHOOK) {
                    Class<?> upPushEventReceiverMiui = XposedHelpers.findClass("com.unionpay.push.receiver.miui.UPPushEventReceiverMiui", lpparam.classLoader);
                    XposedBridge.hookAllMethods(upPushEventReceiverMiui, "onNotificationMessageArrived", xc_methodHookNotificationMessageArrived);
                    MIHOOK = true;
                    XposedBridge.log("MIUI 通知");
                    mlog("");
                    mlog("==================网络记录===============");
                    HttpHook.initAllHooks(lpparam);
                    mlog("==================网络记录===============");
                    mlog("");
                }


            } catch (XposedHelpers.ClassNotFoundError e) {
                XposedBridge.log(e.toString());
                XposedBridge.log("非 MIUI 通知");
            }

            XposedBridge.log("loadPackageParam.processName =" + lpparam.processName);
            mClassLoader = lpparam.classLoader;
            XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, xc_methodHookReceiverMiui);
            XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, xc_methodHookUPActivityMain);
        }
    }



    //自定义启动支付宝广播
    class StartAlipayReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            XposedBridge.log("启动支付宝Activity");

            if (intent.getAction().equals("com.payhelper.alipay.start")) {
                Intent intent2 = new Intent(context, XposedHelpers.findClass("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", context.getClassLoader()));
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.putExtra("mark", intent.getStringExtra("mark"));
                intent2.putExtra("money", intent.getStringExtra("money"));
                context.startActivity(intent2);
            } else {
                String sign = intent.getStringExtra("sign");
                String notifyurl = intent.getStringExtra("notifyurl");
                mSignkey = sign;
                mNotifyurl = notifyurl;
                XposedBridge.log(mSignkey+"------llllll==="+mNotifyurl);
            }
        }
    }

//	 //自定义启动微信广播
//    class StartWechatReceived extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//        	XposedBridge.log("启动微信Activity");
//        	try {
//				Intent intent2=new Intent(context, XposedHelpers.findClass("com.tencent.mm.plugin.collect.ui.CollectCreateQRCodeUI", context.getClassLoader()));
//				intent2.putExtra("mark", intent.getStringExtra("mark"));
//				intent2.putExtra("money", intent.getStringExtra("money"));
//				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				context.startActivity(intent2);
//				XposedBridge.log("启动微信成功");
//			} catch (Exception e) {
//				XposedBridge.log("启动微信失败："+e.getMessage());
//			}
//        }
//    }


    //自定义启动QQ广播
    class StartQQReceived extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		XposedBridge.log("启动QQActivity");
    		try {
//    			PayHelperUtils.sendmsg(context, "启动QQActivity"+l);

    			String money=intent.getStringExtra("money");
    			String mark=intent.getStringExtra("mark");
    			if(!TextUtils.isEmpty(money) && !TextUtils.isEmpty(mark)){
    				QQDBManager qqdbManager=new QQDBManager(context);
        			qqdbManager.addQQMark(intent.getStringExtra("money"),intent.getStringExtra("mark"));
    				long l=System.currentTimeMillis();
        			String url="mqqapi://wallet/open?src_type=web&viewtype=0&version=1&view=7&entry=1&seq=" + l;
        			Intent intent2=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			context.startActivity(intent2);
    			}

//    			PayHelperUtils.sendmsg(context, "启动成功"+l);
    		} catch (Exception e) {
    			PayHelperUtils.sendmsg(context, "StartQQReceived异常"+e.getMessage());
			}
    	}
    }











    private boolean UNIONPAY_HOOK = false;
    private ClassLoader mClassLoader;
    private Activity activity;
    private Application app;
    private MyHandler handler;
    private Class UPPushService;
    private boolean MIHOOK = false;
    private static final String checkOrder = "com.android.unionpay.chexk";
    private ExecutorService fixedThread = Executors.newFixedThreadPool(5);

    private XC_MethodHook xc_methodHookNotificationMessageArrived = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            try{
                XposedBridge.log("UPPushEventReceiverMiui onNotificationMessageArrived");
                if (param.args != null && param.args.length > 0) {
                    XposedBridge.log("handleLoadPackage-->onNotificationMessageArrived" + new Gson().toJson(param.args[1]));
                    String s = (String) XposedHelpers.getObjectField(param.args[1], "c");
                    XposedBridge.log("handleLoadPackage-->动账通知" + s);
                    JSONObject object = new JSONObject(s);
                    JSONObject body = object.optJSONObject("body");
                    String mTitle = body.optString("title");
                    String mContent = body.optString("mContent");
                    mlog("UPPushEventReceiverMiuimContent: " + mContent);
                    if (mTitle.contains("动账通知") && mContent.contains("向您付款")) {
                        String pre = mContent.split("元,")[0];
                        String parts[] = pre.split("通过扫码向您付款");
                        if (parts.length == 2) {
                            final String u = parts[0];
                            final String m = parts[1];
                            mlog("New Push Msg u:" + u + " m:" + m);
                            Intent intent = new Intent(checkOrder);
                            intent.putExtra("name", u);
                            intent.putExtra("title", m);
                            if (getContext() != null) {
                                getContext().sendBroadcast(intent);
                            }
                        }
                    }
                }
            }catch (Exception e){
                mlog("UPPushEventReceiverMiuimContent异常信息为: " + e.getMessage());
            }
        }
    };

    private XC_MethodHook xc_methodHookReceiverMiui = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            String cls_name = (String) param.args[0];
            if (cls_name.equals("com.unionpay.push.UPPushService")) {
                if (UPPushService != null) return;
                UPPushService = (Class) param.getResult();
                hookPushService(UPPushService);
            }
            if (cls_name.equals("com.unionpay.push.receiver.miui.UPPushEventReceiverMiui")) {
                XposedBridge.hookAllMethods((Class) param.getResult(), "onNotificationMessageArrived", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        try{
                            XposedBridge.log("UPPushEventReceiverMiui onNotificationMessageArrived");
                            if (param.args != null && param.args.length > 0) {
                                XposedBridge.log("onNotificationMessageArrived" + new Gson().toJson(param.args[1]));
                                String s = (String) XposedHelpers.getObjectField(param.args[1], "c");
                                mlog("onNotificationMessageArrived动账通知: " + s);
                                JSONObject object = new JSONObject(s);
                                JSONObject body = object.optJSONObject("body");
                                String mTitle = body.optString("title");
                                String mContent = body.optString("mContent");
                                mlog("onNotificationMessageArrivedmContent: " + mContent);
                                if (mTitle.contains("动账通知") && mContent.contains("向您付款")) {
                                    String pre = mContent.split("元,")[0];
                                    String parts[] = pre.split("通过扫码向您付款");
                                    if (parts.length == 2) {
                                        final String u = parts[0];
                                        final String m = parts[1];
                                        mlog("New Push Msg u:" + u + " m:" + m);
                                        Intent intent = new Intent(checkOrder);
                                        intent.putExtra("name", u);
                                        intent.putExtra("title", m);
                                        if (getContext() != null)
                                            getContext().sendBroadcast(intent);
                                    }
                                }
                            }
                        }catch (Exception e){
                            mlog("onNotificationMessageArrived动账通知异常，信息为： " + e.getMessage());
                        }
                    }
                });
            }
        }
    };

    private XC_MethodHook xc_methodHookUPActivityMain = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) {
            if (param.thisObject.getClass().toString().contains("com.unionpay.activity.UPActivityMain")) {
                if (UNIONPAY_HOOK) {
                    return;
                }
                UNIONPAY_HOOK = true;
                activity = (Activity) param.thisObject;
                app = activity.getApplication();
                IntentFilter filter = new IntentFilter();
                filter.addAction("com.chuxin.socket.ACTION_CONNECT");
                filter.addAction(checkOrder);
                activity.registerReceiver(new MyBroadcastReceiver(), filter);
                handler = new MyHandler(activity.getMainLooper());
            }
        }
    };

    private void hookPushService(Class upPushService) {
        XposedBridge.hookAllMethods(upPushService, "a", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                XposedBridge.log("UPPushService a");
                try{
                    if (param.args != null && param.args.length > 0) {
                        Object uPPushMessage = param.args[0];
                        Object mText = XposedHelpers.callMethod(uPPushMessage, "getText");
                        String re = new Gson().toJson(mText);
                        XposedBridge.log("hookPushServicemText =" + re);
                        if (TextUtils.isEmpty(re)) return;
                        JSONObject object = new JSONObject(re);
                        String mTitle = object.getString("mTitle");
                        String mContent = object.getString("mContent");
                        mlog("hookPushServicemTextmContent: " + mContent);
                        if (mTitle.contains("动账通知") && mContent.contains("入账")) {
                            mlog("新消息提醒，无支付用户信息，需要获取订单列表..........");
                            Intent intent = new Intent(checkOrder);
                            intent.putExtra("name", "");
                            intent.putExtra("title", "");
                            if (getContext() != null)
                                getContext().sendBroadcast(intent);
                        }
                    }
                }catch (Exception e){
                    mlog("hookPushServicemTextmContent异常: " + e.getMessage());
                }
            }
        });
    }

    private static String encvirtualCardNo;

    private void getVirtualCardNum(final GetCardNumListener listener, final String sessionKey, final String money, final String remark) {
        fixedThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mlog("GetVirtualCardNum");
                    String str2 = "https://pay.95516.com/pay-web/restlet/qr/p2pPay/getInitInfo?cardNo=&cityCode=" + Enc(getcityCd());
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(str2).header("X-Tingyun-Id", getXTid())
                            .header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis()).header("sid", getSid()).header("urid", geturid())
                            .header("cityCd", getcityCd()).header("locale", "zh-CN").header("User-Agent", "Android CHSP").header("dfpSessionId", getDfpSessionId())
                            .header("gray", getgray()).header("key_session_id", "").header("Host", "pay.95516.com").build();
                    Response response = client.newCall(request).execute();
                    if (response != null && response.body() != null) {
                        String RSP = response.body().string();
                        mlog("GetVirtualCardNum str2=>" + str2 + " RSP=>" + RSP);
                        String Rsp = Dec(RSP);
                        mlog("GetVirtualCardNum str2=>" + str2 + " RSP=>" + Rsp);
                        try {
                            encvirtualCardNo = Enc(new JSONObject(Rsp).getJSONObject("params").getJSONArray("cardList").getJSONObject(0).getString("virtualCardNo"));
                            mlog("encvirtualCardNo:" + encvirtualCardNo);
                            if (listener != null) {
                                listener.success(encvirtualCardNo, sessionKey, money, remark);
                            }
                        } catch (Exception e) {
                            mlog(e);
                            if (listener != null) {
                                listener.error(e.getMessage() + e.getCause());
                            }
                        }
                    }
                } catch (Exception e) {
                    mlog(e);
                    if (listener != null) {
                        listener.error(e.getMessage() + e.getCause());
                    }
                }
            }
        });
    }

    private String Dec(String src) {
        try {
            return (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.encrypt.IJniInterface", mClassLoader), "decryptMsg", src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String Enc(String src) {
        try {
            return (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.encrypt.IJniInterface", mClassLoader), "encryptMsg", src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getXTid() {
        try {
            Class m_s = XposedHelpers.findClass("com.networkbench.agent.impl.m.s", mClassLoader);
            Object f = XposedHelpers.callStaticMethod(m_s, "f");
            Object h = XposedHelpers.callMethod(f, "H");
            mlog("getXTidh=>" + h);
            Object i = XposedHelpers.callStaticMethod(m_s, "I");
            Object xtidClass = m_s.getDeclaredMethod("a", String.class, int.class).invoke(null, h, i);
            String xtid = xtidClass.toString();
            mlog("getXTid:" + xtid + "");
            return xtid;
        } catch (Exception e) {
            mlog(e);
        }
        return "";
    }

    private String getSid() {
        String sid = "";
        try {
            Object b = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.network.aa", mClassLoader), "b");
            sid = XposedHelpers.callMethod(b, "e").toString();
        } catch (Exception e) {
            mlog(e);
        }
        mlog("sid:" + sid + "");
        return sid;
    }

    private String geturid() {
        String Cacheurid = "";
        try {
            Class data_d = XposedHelpers.findClass("com.unionpay.data.d", mClassLoader);
            Object o = XposedHelpers.callStaticMethod(data_d, "a", new Class[]{Context.class}, activity);
            String v1_2 = XposedHelpers.callMethod(XposedHelpers.callMethod(o, "A"), "getHashUserId").toString();
            if (!TextUtils.isEmpty(v1_2) && v1_2.length() >= 15) {
                Cacheurid = v1_2.substring(v1_2.length() - 15);
            }
        } catch (Exception e) {
            mlog(e);
        }
        mlog("Cacheurid:" + Cacheurid + "");
        return Cacheurid;
    }

    private String getDfpSessionId() {
        String CacheDfpSessionId = "";
        try {
            Object o = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.service.b", mClassLoader), "d");
            mlog("o=>" + o);
            CacheDfpSessionId = o.toString();
        } catch (Exception e) {
            mlog(e);
        }
        mlog("CacheDfpSessionId:" + CacheDfpSessionId + "");
        return CacheDfpSessionId;
    }

    private String getcityCd() {
        String cachecityCd = "";
        try {
            cachecityCd = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.location.a", mClassLoader), "i").toString();
        } catch (Exception e) {
            mlog(e);
        }
        mlog("CachecityCd: " + cachecityCd + "");
        return cachecityCd;
    }

    private String getgray() {
        String cachegray = "";
        try {
            Object b = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.network.aa", mClassLoader), "b");
            cachegray = XposedHelpers.callMethod(b, "d").toString();
        } catch (Exception e) {
            mlog(e);
        }
        mlog("Cachegray: " + cachegray + "");
        return cachegray;
    }

    private void GenQrCode(final String money, final String mark, final String sessionkey) {
        fixedThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String money1 = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", "");
                    String remark = mark;
                    mlog("准备请求二维码： money:" + money1 + " mark:" + remark);
                    String str2 = "https://pay.95516.com/pay-web/restlet/qr/p2pPay/applyQrCode?txnAmt=" + Enc(money1) + "&cityCode=" + Enc(getcityCd()) + "&comments=" + Enc(remark) + "&virtualCardNo=" + encvirtualCardNo;
                    mlog("请求二维码： " + str2);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(str2).header("X-Tingyun-Id", getXTid()).header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis())
                            .header("sid", getSid()).header("urid", geturid()).header("cityCd", getcityCd()).header("locale", "zh-CN").header("User-Agent", "Android CHSP")
                            .header("dfpSessionId", getDfpSessionId()).header("gray", getgray()).header("key_session_id", "").header("Host", "pay.95516.com").build();
                    Response response = client.newCall(request).execute();
                    if (response != null && response.body() != null) {
                        String RSP = response.body().string();
                        mlog("获取到的二维码数据：" + str2 + " RSP=>" + RSP);
                        String Rsp = Dec(RSP);
                        mlog("获取到的二维码数据（解密）：" + Rsp);
                        try {
                            Map<String, Object> dataMap = JSON.parseObject(Rsp, new TypeReference<Map<String, Object>>() {});
                            dataMap.put("mark", remark);
                            dataMap.put("money", money1);
                            dataMap.put("sessionkey", sessionkey);
                            android.os.Message message = new android.os.Message();
                            message.what = 1;
                            message.obj = "yunshanfuqrcode:" + JSON.toJSONString(dataMap);
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            mlog("GenQrCode异常，信息：" + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    android.os.Message message = new android.os.Message();
                    message.what = 4;
                    message.obj = money + "-" + mark + "-" + sessionkey;
                    handler.sendMessageDelayed(message, 3000);
                    mlog("GenQrCode异常，信息：" + e.getMessage());
                }
            }
        });
    }

    private String CheckNewOrder(final String user, final String money) {
        try {
            mlog("检查支付新订单:" + user + " money:" + money);
            Callable<String> callable = new Callable<String>() {
                public String call() {
                    try {
                        String str2 = "https://wallet.95516.com/app/inApp/order/list?currentPage=" + Enc("1") + "&month=" + Enc("0") + "&orderStatus=" + Enc("0") + "&orderType=" + Enc("A30000") + "&pageSize=" + Enc("10") + "";
                        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(50, TimeUnit.SECONDS).writeTimeout(50, TimeUnit.SECONDS).readTimeout(50, TimeUnit.SECONDS).build();
                        Request request = new Request.Builder().url(str2).header("X-Tingyun-Id", getXTid()).header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis())
                                .header("sid", getSid()).header("urid", geturid()).header("cityCd", getcityCd()).header("locale", "zh-CN")
                                .header("User-Agent", "Android CHSP").header("dfpSessionId", getDfpSessionId())
                                .header("gray", getgray()).header("Accept", "*/*").header("key_session_id", "").header("Host", "wallet.95516.com").build();
                        Response response = client.newCall(request).execute();
                        if (response != null && response.body() != null) {
                            String RSP = response.body().string();
                            mlog("获取到的订单列表：" + str2 + " RSP=>" + RSP);
                            String DecRsp = Dec(RSP);
                            mlog("获取到的订单列表(解密订单):" + str2 + " DecRSP=>" + DecRsp);
                            JSONArray o = new JSONObject(DecRsp).getJSONObject("params").getJSONArray("uporders");
                            List<com.alibaba.fastjson.JSONObject> orderResultList = null;
                            for (int i = 0; i < o.length(); i++) {
                                JSONObject p = o.getJSONObject(i);
                                String orderid = p.getString("orderId");
                                mlog("订单数据:" + p.toString());
                                String amount = p.getString("amount");
                                mlog("判断金额： " + (amount.equals(money) && p.getString("title").contains(user)));
                                if (amount.equals(money) && p.getString("title").contains(user)) {
                                    mlog("找到订单，开始获取订单详情： " + orderid);
                                    return "getpayresult:" +DoOrderInfoGet(orderid);
                                }
                            }
                            orderResultList = new ArrayList<>();
                            mlog("未找到订单，直接获取最近订单列表.........");
                            for (int i = 0; i < o.length(); i++) {
                                JSONObject p = o.getJSONObject(i);
                                String orderid = p.getString("orderId");
                                String singleOrder = DoOrderInfoGet(orderid);
                                mlog("singleOrder: " + singleOrder);
                                orderResultList.add(JSON.parseObject(singleOrder));
                                //Thread.sleep(300L);
                            }
                            if(!orderResultList.isEmpty()){
                                String payOrderResult = JSON.toJSONString(orderResultList);
                                mlog("检查支付新订单==>向平台发达结果： " + payOrderResult);
                                return "getpayresultlist:" + payOrderResult;
                            }
                        }
                    }catch (Exception e){
                        mlog("检查支付新订单异常:"+e.getMessage());
                    }
                    return "";
                }
            };
            Future<String> future = fixedThread.submit(callable);
            String result = future.get();
            mlog("检查支付新订单(获得返回结果): "+result);
            if(StringUtils.isNotBlank(result) && !result.startsWith("ERR")){
                mlog("检查支付新订单==>向平台发达结果： "+result);
                android.os.Message message = new android.os.Message();
                message.what = 1;
                message.obj = "getpayresult:" + result;
                handler.sendMessage(message);
                return "";
            }
            android.os.Message message = new android.os.Message();
            message.what = 2;
            message.obj = money + "-" + user;
            handler.sendMessageDelayed(message, 3000);
            return "5秒重新查询";
        } catch (Exception e) {
            android.os.Message message = new android.os.Message();
            message.what = 2;
            message.obj = money + "-" + user;
            handler.sendMessageDelayed(message, 3000);
            mlog(e);
            return "ERR:" + e.getLocalizedMessage();
        }
    }

    private String DoOrderInfoGet(final String orderid) {
        if (orderid.length() > 5) {
            try {
                Callable<String> callable = new Callable<String>() {
                    public String call() {
                        try {
                            Map<String,Object> dataMap = new HashMap<>();
                            dataMap.put("orderType",21);
                            dataMap.put("transTp","simple");
                            dataMap.put("orderId",orderid);
                            String args = JSON.toJSONString(dataMap);//"{\"orderType\":\"21\",\"transTp\":\"simple\",\"orderId\":\"" + orderid + "\"}";
                            String url = "https://wallet.95516.com/app/inApp/order/detail";
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder().url(url).header("X-Tingyun-Id", getXTid())
                                    .header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis())
                                    .header("sid", getSid()).header("urid", geturid())
                                    .header("cityCd", getcityCd()).header("locale", "zh-CN")
                                    .header("User-Agent", "Android CHSP").header("dfpSessionId", getDfpSessionId())
                                    .header("gray", getgray()).header("Accept", "*/*")
                                    .header("key_session_id", "").header("Content-Type", "application/json; charset=utf-8")
                                    .header("Host", "wallet.95516.com")
                                    .post(RequestBody.create(null, Enc(args))).build();
                            Response response = client.newCall(request).execute();
                            if (response != null && response.body() != null) {
                                String RSP = response.body().string();
                                mlog("获取订单详情=>" + url + " RSP=>" + RSP);
                                String DecRsp = Dec(RSP);
                                mlog("获取订单详情（解密）=>" + url + " DecRSP=>" + DecRsp);
                                JSONObject params = new JSONObject(DecRsp).getJSONObject("params");
//                                String orderDetail = params.getString("orderDetail");
//                                mlog("获取订单详情=>" + url + " orderDetail=>" + orderDetail);
//                                JSONObject o = new JSONObject(orderDetail);
//                                String u = o.getString("payUserName");
//                                String mark = o.getString("postScript");
//                                String totalAmount = params.getString("totalAmount");
//                                mlog("获取订单详情（数据）=>" + url + " u:" + u + " mark:" + mark + " totalAmount:" + totalAmount);
//                                Message message = new Message();
//                                message.what = 1;
//                                message.obj = "getpayresult:" + params.toString();
//                                handler.sendMessage(message);
                                mlog("获取订单详情（数据）发送成功=>: " + params.toString());
                                return params.toString();
                            }
                        } catch (Exception e) {
                            mlog("获取订单详情异常，信息为： " + e.getMessage());
                        }
                        return "";
                    }
                };
                Future<String> future = fixedThread.submit(callable);
                String result = future.get();
                mlog("获取订单详情（数据）: " + result);
                if (StringUtils.isNotBlank(result)) {
                    return result;
                }
            } catch (Exception e) {
                Message message = new Message();
                message.what = 3;
                message.obj = orderid;
                handler.sendMessageDelayed(message, 3000);
                mlog(e);
                return "ERR:" + e.getLocalizedMessage();
            }
        }
        return "ERROR_ORDER:" + orderid;
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.chuxin.socket.ACTION_CONNECT".equals(intent.getAction())) {
                String money = intent.getStringExtra("money");
                String mark = intent.getStringExtra("mark");
                mlog("接收到的金额：" + money + " mark:" + mark);
                if (money == null) money = "0.01";
                if (mark == null) mark = "测试";
                String sessionkey = intent.getStringExtra("sessionkey");
                mlog("接收到请求XposedData开始请求二维码sessionkey...............:" + sessionkey);
                mlog("接收到请求XposedData-->金额：" + money + "备注：" + mark);
                getVirtualCardNum(new GetCardNumListener() {
                    @Override
                    public void success(String re, String sessionkey, String money, String remark) {
                        mlog("获取虚拟卡号【" + re + "】成功，准备生成二维码: sessionkey:" + sessionkey + " money: " + money + " remark:" + remark);
                        GenQrCode(money, remark, sessionkey);
                    }

                    @Override
                    public void error(String error) {

                    }
                }, sessionkey, money, mark);
            } else if (checkOrder.equals(intent.getAction())) {
                final String name = intent.getStringExtra("name");
                final String title = intent.getStringExtra("title");
                CheckNewOrder(name, title);
            }
        }
    }

    private class MyHandler extends Handler {

        private MyHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String message = (String) msg.obj;
                Intent intent = new Intent("com.chuxin.socket.ACTION_NOTIFI");
                intent.putExtra("message", message);
                mlog(message);
                if (app == null) {
//                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    if (getContext() != null)
                        getContext().sendBroadcast(intent);
                } else {
//                    Toast.makeText(app, message, Toast.LENGTH_SHORT).show();
                    app.sendBroadcast(intent);
                }
            } else if (msg.what == 2) {
                String s = (String) msg.obj;
                String amount = s.split("-")[0];
                String name = s.split("-")[1];
                mlog("重新查询订单-->金额： " + amount + " 名字: " + name);
                CheckNewOrder(name, amount);
            }
            if (msg.what == 3) {
                String s = String.valueOf(msg.obj);
                mlog("重新查询订单详情-->订单号： " + s);
                DoOrderInfoGet(s);
            } else if (msg.what == 4) {
                String s = String.valueOf(msg.obj);
                String amount = s.split("-")[0];
                String remark = s.split("-")[1];
                GenQrCode(amount, remark, s.split("-")[2]);
            }
        }
    }

    private void mlog(String s) {
//        if(BuildConfig.DEBUG){
//            XposedBridge.log(s);
//        }
//        XposedBridge.log(s);
        System.out.println("云闪付：" + s);
    }

    private void mlog(Exception s) {
//        if(BuildConfig.DEBUG){
//            mlog("异常信息：【" + s + "】" + s.getMessage() + "--" + s.getCause());
//        }
        mlog("异常信息：【" + s + "】" + s.getMessage() + "--" + s.getCause());
    }

    public Context getContext() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            if (activityThread != null) {
                Method method = activityThread.getMethod("currentActivityThread");
                Object currentActivityThread = method.invoke(activityThread);//获取currentActivityThread 对象
                Method method2 = currentActivityThread.getClass().getMethod("getApplication");
                Context context = (Context) method2.invoke(currentActivityThread);//获取 Context对象
                XposedBridge.log("Context " + context);
                return context;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
