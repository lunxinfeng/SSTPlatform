package com.fintech.sst.other.netty;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;

public interface ServerMessageHandler {
    void afterAuthentication(AuthenticationStatus var1);

    void afterConnectionEstablished(ConnectionStatus var1);

    void handleTransportError(Throwable var1);

    void handlerSignKey(String var1, String var2);

    void requeyPayHandler(UserPay var1, JSONObject jsonObject);

    Context getContext();
}
