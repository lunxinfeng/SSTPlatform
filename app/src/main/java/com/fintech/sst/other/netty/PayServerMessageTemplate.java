package com.fintech.sst.other.netty;

import com.fintech.sst.other.netty.handler.MessageConvert;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.Unpooled;

public class PayServerMessageTemplate {
    private NettyConnectionFactory nettyConnectionFactory;

    public PayServerMessageTemplate(NettyConnectionFactory var1) {
        this.nettyConnectionFactory = var1;
    }

    private void printMsg(String mesg){
//        if(BuildConfig.DEBUG){
            System.out.println(mesg);
//        }
    }

    public void responsePay(ResponsePay responsePay, String key) {
        try {
            responsePay.setAuthToken(this.nettyConnectionFactory.getAuthToken());
            printMsg("XposedData-->准备将消息回传....");
            if (this.nettyConnectionFactory.getChannel().writeAndFlush(Unpooled.copiedBuffer(MessageConvert.convertResponsePayMsg(responsePay, key))).sync().isSuccess()) {
                printMsg("XposedData-->消息写入成功....");
            }
            printMsg("XposedData-->准备将消息回传结束.");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
