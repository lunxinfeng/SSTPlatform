package com.fintech.sst.other.netty.handler;

import android.util.Log;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter {

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private String authToken;

    public ClientHeartBeatHandler(String authToken) {
        setAuthToken(authToken);
    }

    public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
        boolean idleState = (var2 instanceof IdleStateEvent && ((IdleStateEvent)var2).state() == IdleState.WRITER_IDLE);
        Log.i("XposedData-->","idleState==>"+idleState);
        if (idleState) {
            Log.i("XposedData-->","heartMsg==>"+getAuthToken());
            var1.channel().writeAndFlush(Unpooled.copiedBuffer(MessageConvert.convertHeartBeatMsg(getAuthToken())));
        }
        super.userEventTriggered(var1, var2);
    }
}
