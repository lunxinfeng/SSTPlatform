package com.fintech.sst.other.netty;

public class TcpMsgHeader implements java.io.Serializable{

    private String authToken;
    private int msgType;

    public TcpMsgHeader() {
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public void setAuthToken(String var1) {
        this.authToken = var1;
    }

    public void setMsgType(int var1) {
        this.msgType = var1;
    }
}
