package com.fintech.sst.other.netty;

public class TcpMsgBody implements java.io.Serializable{

    private int code;
    private Object data;
    private String define;

    public TcpMsgBody() {
    }

    public TcpMsgBody(int var1) {
        this.code = var1;
    }

    public TcpMsgBody(int var1, String var2) {
        this.code = var1;
        this.define = var2;
    }

    public TcpMsgBody(int var1, String var2, Object var3) {
        this.code = var1;
        this.define = var2;
        this.data = var3;
    }

    public int getCode() {
        return this.code;
    }

    public Object getData() {
        return this.data;
    }

    public String getDefine() {
        return this.define;
    }

    public void setCode(int var1) {
        this.code = var1;
    }

    public void setData(Object var1) {
        this.data = var1;
    }

    public void setDefine(String var1) {
        this.define = var1;
    }
}
