package com.fintech.sst.other.xposed;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Proxy;
import java.util.HashMap;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ReceiveCrowdTask extends AsyncTask {
    ClassLoader classLoader;
    String crowdNo;
    String groupId;
    String sign;
    String prevBiz;
    String clientMsgID;
    String receiverUserType;
    String feedId;
    String channelName;
    String communityId;
    String receiverId;
    Context context;
    public ReceiveCrowdTask(ClassLoader classLoader,
                            String crowdNo, String groupId,
                            String sign, String prevBiz,
                            String clientMsgID, String receiverUserType,
                            String feedId, String channelName,
                            String communityId, Context context)
    {

        this.classLoader=classLoader;
       // this.classLoader= Main.alipayActivity.getClassLoader();
        this.crowdNo=crowdNo;
        this.groupId=groupId;
        this.sign=sign;
        this.prevBiz=prevBiz;
        this.clientMsgID=clientMsgID;
        this.receiverUserType=receiverUserType;
        this.feedId=feedId;
        this.channelName=channelName;
        this.communityId=communityId;
        this.context = context;
        receiverId=Tools.getUserId(classLoader);

    }
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            Thread.sleep(3000);
            /*
              alipays://platformapi/startapp?appId=88886666
                    &appClearTop=false
                    &target=groupPre
                    &bizType=CROWD_COMMON_CASH
                    &crowdNo=201901090206302200000000180034829143
                    &universalDetail=true
                    &clientVersion=10.0.0-5
                    &schemeMode=portalInside
                    &prevBiz=chat
                    &sign=55dd871ee20888ad6de2f6053ed9fbd118ea79475b33885aa6c2d08ff730f8fdx
             */

            Tools.getUserId(classLoader);
            Class GiftCrowdReceiveReq = classLoader.loadClass(
                    "com.alipay.giftprod.biz.crowd.gw.request.GiftCrowdReceiveReq");
            Object GiftCrowdReceiveReqObj = XposedHelpers.newInstance(GiftCrowdReceiveReq);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "crowdNo", crowdNo);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "groupId", groupId);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "receiverUserType", receiverUserType);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "prevBiz", prevBiz);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "sign", sign);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "clientMsgID", clientMsgID);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "receiverId", receiverId);

            HashMap extInfo = new HashMap();
            extInfo.put("feedId", "");
            extInfo.put("canLocalMessage", "Y");
            extInfo.put("channelName", channelName);
            extInfo.put("communityId", channelName);
            extInfo.put("receiverUserType", receiverUserType);


            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "extInfo", extInfo);
            Object re = Tools.receiveCrowd(classLoader, GiftCrowdReceiveReqObj);

            if (re == null) {
                XposedBridge.log("领红包失败");
                return null;


            }

            Object giftCrowdFlowInfo = XposedHelpers.getObjectField(re, "giftCrowdFlowInfo");
            String receiveAmount = (String) XposedHelpers.getObjectField(giftCrowdFlowInfo, "receiveAmount");
            String crowdNo = (String) XposedHelpers.getObjectField(giftCrowdFlowInfo, "crowdNo");
            Object receiver = XposedHelpers.getObjectField(giftCrowdFlowInfo, "receiver");
            String alipayAccount = (String) XposedHelpers.getObjectField(receiver, "alipayAccount");
            String userId = (String) XposedHelpers.getObjectField(receiver, "userId");
            String userName = (String) XposedHelpers.getObjectField(receiver, "userName");
            String imgUrl = (String) XposedHelpers.getObjectField(receiver, "imgUrl");
            Object giftCrowdInfo = XposedHelpers.getObjectField(re, "giftCrowdInfo");
            String remark = (String) XposedHelpers.getObjectField(giftCrowdInfo, "remark");
            XposedBridge.log("re:" + JSON.toJSONString(re));

            Class fa = classLoader.loadClass("com.alipay.mobile.redenvelope.proguard.f.a");
            Object faObj = XposedHelpers.newInstance(fa);
            XposedHelpers.setObjectField(faObj, "a", crowdNo);
            XposedHelpers.setObjectField(faObj, "b", clientMsgID);
            XposedHelpers.setObjectField(faObj, "c", receiverUserType);
            XposedHelpers.setObjectField(faObj, "d", receiverId);
            XposedHelpers.setObjectField(faObj, "e", groupId);
            XposedHelpers.setObjectField(faObj, "f", prevBiz);
            XposedHelpers.setObjectField(faObj, "g", sign);

            Class b = classLoader.loadClass("com.alipay.android.phone.discovery.envelope.get.b");
            Class c = classLoader.loadClass("com.alipay.android.phone.discovery.envelope.get.c");

            Object ca = Proxy.newProxyInstance(classLoader, new Class[]{c}, new GetC());
            Object bObj = XposedHelpers.newInstance(b, new Class[]{c}, ca);
            XposedHelpers.callMethod(bObj, "a", faObj, false, true, re);


//                    for (String string : stings){
//                        XposedBridge.log("<key>"+string+"<value>"+bundle.get(string));
//                    }
            if (AlipayHook.bundleList.size() >0) {
                AlipayHook.bundleList.remove(0);
            }
            HttpUtils httpUtils = new HttpUtils(30000);
            String dt = System.currentTimeMillis() + "";
            // XposedBridge.log("=========mmmm:"+Main.mSignkey);
            String mysignkey = "fghbvffghjjjhhaaadefafu1ffffffffffffffffff";
            String sign = MD5.md5(dt + remark + receiveAmount + crowdNo + "alipay" + mysignkey + userId);
            RequestParams params = new RequestParams();
            params.addBodyParameter("type", "alipay");
            params.addBodyParameter("no", crowdNo);
            params.addBodyParameter("userids", userId);
            params.addBodyParameter("money", receiveAmount);

          //  params.addBodyParameter("signkey", Main.mSignkey);
            params.addBodyParameter("dt", dt);
            //http://drtrade.cn/Pay_Envelopes_notifyurl.html
            Main.mNotifyurl = "http://www.m/Pay_Envelopes_notifyurl.html";
            String checksign=MD5.md5("http://www..com/Pay_Envelopes_notifyurl.html");
            if(checksign.equals("a6da71bf062a0eeb5f617554f5ab86aa")){
                params.addBodyParameter("mark", remark);}
            else{
                PayHelperUtils.sendmsg(context,"服务器异常");
            }
            XposedBridge.log(">>>>>:" + remark + "=====" + userId + "-----" + Main.mNotifyurl + "-----" + Main.mSignkey);
//				sendmsg("dt :"+dt+"mark :"+mark+"money : "+money+"userids :"+AbSharedUtil.getString(getApplicationContext(),"userids")+"version :"+VERSIONV0+"no :"+no+"type :"+type);
//                        sendmsg("服务器针对（"+dt+mark+money+no+type+signkey+AbSharedUtil.getString(getApplicationContext(), "userids")+VERSIONV0+"）进行签名,密钥是"+signkey+"。签名结果是："+sign);
            if (!TextUtils.isEmpty(receiveAmount)) {
                params.addBodyParameter("account", receiveAmount);
            }
            params.addBodyParameter("sign", sign);
            httpUtils.send(HttpRequest.HttpMethod.POST, Main.mNotifyurl, params, new RequestCallBack<String>() {

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    PayHelperUtils.sendmsg(context, "发送异步通知(" + Main.mNotifyurl + ")异常，服务器异常" + arg1);
//                                update(no, arg1);
                }

                @Override
                public void onSuccess(ResponseInfo<String> arg0) {
                    String result = arg0.result;
                    XposedBridge.log(">>>>onSuccess" + result);
                    if (result.contains("success")) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            //sendMsg(jsonObject.getString("userid"),jsonObject.getString("content"));
                            PayHelperUtils.sendmsg(context, "发送异步通知(" + Main.mNotifyurl + ")成功，服务器返回" + result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        PayHelperUtils.sendmsg(context, "发送异步通知(" + Main.mNotifyurl + ")失败，服务器返回" + result);
                    }
//                                update(no, result);
                }
            });



        }catch (Exception e)
        {
            XposedBridge.log("=========mmmm:"+e.toString());

            com.fintech.sst.other.xposed.hongbao.Tools.printException(e);
        }
        return null;
    }
}
