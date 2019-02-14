package com.fintech.sst.other.xposed;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import de.robv.android.xposed.XposedHelpers;

public class PayHelperUtils {

	public static String MSGRECEIVED_ACTION = "com.tools.payhelper.msgreceived";
    public static String TRADENORECEIVED_ACTION = "com.tools.payhelper.tradenoreceived";
    public static String GETTRADEINFO_ACTION = "com.tools.payhelper.gettradeinfo";
    public static String LOGINIDRECEIVED_ACTION = "com.tools.payhelper.loginidreceived";

    public static boolean isFirst=true;

	public static void sendmsg(Context context, String msg) {
		Intent broadCastIntent = new Intent();
		broadCastIntent.putExtra("msg", msg);
		broadCastIntent.setAction(MSGRECEIVED_ACTION);
		context.sendBroadcast(broadCastIntent);

//		Message message = new Message();
//		message.setContent(msg);
//		RxBus.getDefault().send(message);
	}

    public static void sendTradeInfo(Context context) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(GETTRADEINFO_ACTION);
        context.sendBroadcast(broadCastIntent);
    }

    public static void sendLoginId(String loginId, String type, Context context) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(LOGINIDRECEIVED_ACTION);
        broadCastIntent.putExtra("type", type);
        broadCastIntent.putExtra("loginid", loginId);
        context.sendBroadcast(broadCastIntent);
    }

    /**
     * 获取版本号名称
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            sendmsg(context, "getVerName异常" + e.getMessage());
        }
        return verName;
    }


    public static String getCookieStr(ClassLoader appClassLoader) {
		String cookieStr = "";
		// 获得cookieStr
		XposedHelpers.callStaticMethod(XposedHelpers.findClass(
				"com.alipay.mobile.common.transportext.biz.appevent.AmnetUserInfo", appClassLoader), "getSessionid");
		Context context = (Context) XposedHelpers.callStaticMethod(XposedHelpers.findClass(
				"com.alipay.mobile.common.transportext.biz.shared.ExtTransportEnv", appClassLoader), "getAppContext");
		if (context != null) {
			Object readSettingServerUrl = XposedHelpers.callStaticMethod(
					XposedHelpers.findClass("com.alipay.mobile.common.helper.ReadSettingServerUrl", appClassLoader),
					"getInstance");
			if (readSettingServerUrl != null) {
				// String gWFURL = (String)
				// XposedHelpers.callMethod(readSettingServerUrl, "getGWFURL",
				// context);
				String gWFURL = ".alipay.com";
				cookieStr = (String) XposedHelpers.callStaticMethod(XposedHelpers
								.findClass("com.alipay.mobile.common.transport.http.GwCookieCacheHelper", appClassLoader),
						"getCookie", gWFURL);
			} else {
				sendmsg(context, "异常readSettingServerUrl为空");
			}
		} else {
			sendmsg(context, "异常context为空");
		}
		return cookieStr;
	}


	public static String getOrderId() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String newDate=sdf.format(new Date());
		String result="";
		Random random=new Random();
		for(int i=0;i<3;i++){
			result+=random.nextInt(10);
		}
		return newDate+result;
	}

	public static void getTradeInfo(final Context context,final String cookie) {
		sendmsg(context, "有新的商家服务订单进来！！！");
		String url="https://mbillexprod.alipay.com/enterprise/walletTradeList.json?lastTradeNo=&lastDate=&pageSize=1&shopId=&_input_charset=utf-8&ctoken==&_ksTS=&_callback=&t="+System.currentTimeMillis();
		HttpUtils httpUtils = new HttpUtils(15000);
		httpUtils.configResponseTextCharset("GBK");
		RequestParams params = new RequestParams();
		params.addHeader("Cookie", cookie);
		params.addHeader("Referer", "https://render.alipay.com/p/z/merchant-mgnt/simple-order.html");
		params.addHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 7.1.1; zh-cn; Redmi Note 3 Build/LRX22G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.100 U3/0.8.0 Mobile Safari/534.30 Nebula AlipayDefined(nt:WIFI,ws:360|640|3.0) AliApp(AP/10.1.22.835) AlipayClient/10.1.22.835 Language/zh-Hans useStatusBar/true");
		httpUtils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
//				sendmsg(context, "服务器异常" + arg1);
				sendmsg(context, "请求支付宝API失败，出现掉单，5秒后启动补单");
				sendTradeInfo(context);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String result = arg0.result.replace("/**/(", "").replace("})", "}");
				try {
					JSONObject jsonObject = new JSONObject(result);
					if(jsonObject.has("status")){
						String status=jsonObject.getString("status");
						if(!status.equals("deny")){
							JSONObject res = jsonObject.getJSONObject("result");
							JSONArray jsonArray = res.getJSONArray("list");
							if (jsonArray != null && jsonArray.length() > 0) {
								JSONObject object = jsonArray.getJSONObject(0);
								String tradeNo = object.getString("tradeNo");
								Intent broadCastIntent = new Intent();
								broadCastIntent.putExtra("tradeno", tradeNo);
								broadCastIntent.putExtra("cookie", cookie);
								broadCastIntent.setAction(TRADENORECEIVED_ACTION);
								context.sendBroadcast(broadCastIntent);
							}
						}else{
							sendmsg(context, "getTradeInfo=>>支付宝cookie失效，出现掉单，5秒后启动补单");
							sendTradeInfo(context);
						}
					}
				} catch (Exception e) {
					sendmsg(context, "getTradeInfo出现异常=>>"+result);
					sendmsg(context, "出现掉单，5秒后启动补单");
					sendTradeInfo(context);
				}
			}
		});
	}

    public static String getAlipayLoginId(ClassLoader classLoader) {
        String loginId="";
        try {
            Class<?> AlipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication",
                    classLoader);
            Class<?> SocialSdkContactService = XposedHelpers
                    .findClass("com.alipay.mobile.personalbase.service.SocialSdkContactService", classLoader);
            Object instace = XposedHelpers.callStaticMethod(AlipayApplication, "getInstance");
            Object MicroApplicationContext = XposedHelpers.callMethod(instace, "getMicroApplicationContext");
            Object service = XposedHelpers.callMethod(MicroApplicationContext, "findServiceByInterface",
                    SocialSdkContactService.getName());
            Object MyAccountInfoModel = XposedHelpers.callMethod(service, "getMyAccountInfoModelByLocal");
            loginId = XposedHelpers.getObjectField(MyAccountInfoModel, "userId").toString();
            //loginId = XposedHelpers.getObjectField(MyAccountInfoModel, "loginId").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginId;
    }
}
