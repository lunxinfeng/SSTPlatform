package com.fintech.sst.other.netty;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;

public class SimpleServerMessageHandler implements ServerMessageHandler {
    private String key;
    private NettyConnectionFactory nettyConnectionFactory;
    private Context context;

    private void printMsg(String mesg){
//        if(BuildConfig.DEBUG){
            System.out.println(mesg);
//        }
    }
    
    public SimpleServerMessageHandler() {
    }
    public SimpleServerMessageHandler(Context context){
        this.context = context;
    }

    public Context getContext(){
        return this.context;
    }

    public void afterAuthentication(AuthenticationStatus var1) {
        if (var1 == AuthenticationStatus.AUTH_SUCCESS) {
            printMsg("XposedData-->AuthenticationStatus success");
        } else {
            printMsg("XposedData-->AuthenticationStatus error " + this.nettyConnectionFactory.isActive());
        }
    }

    public void afterConnectionEstablished(ConnectionStatus var1) {
        if (var1 == ConnectionStatus.CONNECTION_SUCCESS) {
            printMsg("XposedData-->connection success " + this.nettyConnectionFactory.isActive());
        } else {
            printMsg("XposedData-->connection error");
        }
    }

    public NettyConnectionFactory getNettyConnectionFactory() {
        return this.nettyConnectionFactory;
    }

    public void handleTransportError(Throwable var1) {
    }

    public void handlerSignKey(String var1, String var2) {
        this.key = var2;
        printMsg("XposedData-->receive new Key:" + var2);
    }

    public void requeyPayHandler(UserPay userPay, JSONObject jsonObject) {
        printMsg("XposedData已获得二维码数据: "+JSON.toJSONString(userPay));
        PayServerMessageTemplate payServerMessageTemplate = new PayServerMessageTemplate(this.nettyConnectionFactory);
        ResponsePay responsePay = new ResponsePay();
        responsePay.setOrderNo(jsonObject.getJSONObject("params").getString("orderId"));
        responsePay.setAuthToken(userPay.getAuthToken());
        responsePay.setCode(1);
        responsePay.setMoney(String.valueOf(userPay.getMoney()));
        responsePay.setPayId(userPay.getPayId());
        responsePay.setRemark(userPay.getRemark());
        responsePay.setTime(userPay.getTime());
        responsePay.setType(userPay.getType());
        responsePay.setPayUrl(jsonObject.getJSONObject("params").getString("certificate"));
        responsePay.setSessionKey(userPay.getSessionKey());
        String collectionName = AbSharedUtil.getString(context.getApplicationContext(),"userNameStr");
        collectionName = StringUtils.isNotBlank(collectionName) ? collectionName :  "";
        responsePay.setCollectionName(collectionName);
        printMsg("XposedData已获得二维码数据【"+JSON.toJSONString(responsePay)+"】，准备回传给服务器..........");
        payServerMessageTemplate.responsePay(responsePay, this.key);
    }

    public void setNettyConnectionFactory(NettyConnectionFactory var1) {
        this.nettyConnectionFactory = var1;
    }
}