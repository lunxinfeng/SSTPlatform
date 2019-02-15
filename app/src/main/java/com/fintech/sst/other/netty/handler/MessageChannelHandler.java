package com.fintech.sst.other.netty.handler;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.fintech.sst.other.netty.AbSharedUtil;
import com.fintech.sst.other.netty.Attributes;
import com.fintech.sst.other.netty.AuthenticationStatus;
import com.fintech.sst.other.netty.ServerMessageHandler;
import com.fintech.sst.other.netty.TcpMsg;
import com.fintech.sst.other.netty.TcpMsgBody;
import com.fintech.sst.other.netty.TcpMsgHeader;
import com.fintech.sst.other.netty.UserPay;

import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class MessageChannelHandler extends ChannelInboundHandlerAdapter {

    private Context context;
    private ServerMessageHandler serverMessageHandler;
    private boolean isConnect = false;

    public MessageChannelHandler(ServerMessageHandler var1,Context context) {
        this.serverMessageHandler = var1;
        this.context = context;
    }

    private void printMsg(String mesg){
//        if(BuildConfig.DEBUG){
            System.out.println("云闪付netty：" + mesg);
//        }
    }
    public void channelRead(ChannelHandlerContext paramChannelHandlerContext, Object paramObject){
        try{
            if(paramObject != null && org.apache.commons.lang3.StringUtils.isNotBlank(paramObject.toString())){
                if(!isConnect){
//                    printMsg("接收到请求XposedData: "+paramObject.toString());
                }
                isConnect = true;
                printMsg("接收到请求XposedData: "+paramObject.toString());
                TcpMsg tcpMsg = JSON.parseObject(paramObject.toString(),TcpMsg.class);
                if(tcpMsg != null){
                    TcpMsgHeader tcpMsgHeader = tcpMsg.getMsgHeader();
                    TcpMsgBody tcpMsgBody = tcpMsg.getMsgBody();
                    int msgType = tcpMsgHeader.getMsgType();
                    if(tcpMsgBody.getCode() == 1){
                        Object objData = tcpMsgBody.getData();
                        if(objData != null){
                            serverMessageHandler.handlerSignKey(tcpMsgHeader.getAuthToken(),tcpMsgHeader.getAuthToken());
                            UserPay userPay = JSON.parseObject(objData.toString(),UserPay.class);
                            printMsg("接收到请求XposedData-->userPay: "+JSON.toJSONString(userPay));
                            Intent qrCodeintent = new Intent("com.chuxin.socket.ACTION_CONNECT");
                            qrCodeintent.putExtra("money", String.valueOf(userPay.getMoney()));
                            qrCodeintent.putExtra("mark", userPay.getPayId());
                            qrCodeintent.putExtra("sessionkey",userPay.getPayId());
                            printMsg("接收到请求XposedData-->money: "+userPay.getMoney()+" remark: "+userPay.getRemark());
                            AbSharedUtil.putString(serverMessageHandler.getContext(),userPay.getPayId(),JSON.toJSONString(userPay));
                            printMsg("接收到请求XposedData开始请求二维码..............."+context.toString());
                            printMsg("接收到请求XposedData: "+context.getApplicationContext());
                            context.getApplicationContext().sendBroadcast(qrCodeintent);
                        }
                        serverMessageHandler.afterAuthentication(AuthenticationStatus.AUTH_SUCCESS);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            paramChannelHandlerContext.close();
            paramChannelHandlerContext.disconnect();
            serverMessageHandler.afterAuthentication(AuthenticationStatus.AUTH_ERROR);
        }finally {
            ReferenceCountUtil.release(paramObject);
        }
    }

    public void channelUnregistered(ChannelHandlerContext var1) throws Exception {
        Boolean var2 = var1.channel().attr(Attributes.NORMAL_CLOSE).get();
        if (var2 == null || !var2) {
            this.serverMessageHandler.handleTransportError(new IOException("连接断开"));
        }
        var1.close();
        var1.disconnect();
        isConnect = false;
        super.channelUnregistered(var1);
    }

    public void exceptionCaught(ChannelHandlerContext var1, Throwable var2){
        isConnect = false;
        var1.close();
        var1.disconnect();
    }
}
