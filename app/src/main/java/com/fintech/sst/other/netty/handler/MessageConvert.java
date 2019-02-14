package com.fintech.sst.other.netty.handler;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.fintech.sst.other.netty.ParamSignatureUtil;
import com.fintech.sst.other.netty.ResponsePay;
import com.fintech.sst.other.netty.TcpMsg;
import com.fintech.sst.other.netty.TcpMsgBody;
import com.fintech.sst.other.netty.TcpMsgHeader;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class MessageConvert {
    public MessageConvert() {
    }

    public static byte[] convertAuthMsg(String authToken) throws UnsupportedEncodingException {
        TcpMsgHeader tcpMsgHeader = new TcpMsgHeader();
        tcpMsgHeader.setAuthToken(authToken);
        tcpMsgHeader.setMsgType(11);
        return msgEncode(new TcpMsg(tcpMsgHeader,null));
    }

    public static byte[] convertHeartBeatMsg(String authToken) throws UnsupportedEncodingException {
        TcpMsgHeader tcpMsgHeader = new TcpMsgHeader();
        tcpMsgHeader.setAuthToken(authToken);
        tcpMsgHeader.setMsgType(12);
        Log.i("XposedData-->终端信息：",JSON.toJSONString(tcpMsgHeader));
        return msgEncode(new TcpMsg(tcpMsgHeader,null));
    }

    public static byte[] convertResponsePayMsg(ResponsePay var0, String var1) throws UnsupportedEncodingException {
        TcpMsgHeader var2 = new TcpMsgHeader();
        var2.setAuthToken(var0.getAuthToken());
        var2.setMsgType(22);
        TcpMsgBody var3 = new TcpMsgBody(var0.getCode(), var0.getDefine());
        HashMap var4 = new HashMap();
        var4.put("time", var0.getTime());
        var4.put("payId", var0.getPayId());
        var4.put("money", var0.getMoney());
        var4.put("remark", var0.getRemark());
        var4.put("type", var0.getType());
        var4.put("payUrl", var0.getPayUrl());
        var4.put("collectionAccount", var0.getCollectionAccount());
        var4.put("collectionUserId", var0.getCollectionUserId());
        var4.put("collectionName", var0.getCollectionName());
        var4.put("sessionKey", var0.getSessionKey());
        var4.put("sign", ParamSignatureUtil.getSign(var4, var1));
        var3.setData(var4);
        return msgEncode(new TcpMsg(var2, var3));
    }

    private static byte[] msgEncode(TcpMsg var0) throws UnsupportedEncodingException {
        return (JSON.toJSONString(var0) + "\n").getBytes("UTF-8");
    }
}