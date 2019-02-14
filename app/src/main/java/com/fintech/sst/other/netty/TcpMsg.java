package com.fintech.sst.other.netty;

public class TcpMsg implements java.io.Serializable{

    private TcpMsgBody msgBody;
    private TcpMsgHeader msgHeader;

    public TcpMsg() {
    }

    public TcpMsg(TcpMsgHeader var1, TcpMsgBody var2) {
        this.msgHeader = var1;
        this.msgBody = var2;
    }

    public TcpMsgBody getMsgBody() {
        return this.msgBody;
    }

    public TcpMsgHeader getMsgHeader() {
        return this.msgHeader;
    }

    public void setMsgBody(TcpMsgBody var1) {
        this.msgBody = var1;
    }

    public void setMsgHeader(TcpMsgHeader var1) {
        this.msgHeader = var1;
    }
}
