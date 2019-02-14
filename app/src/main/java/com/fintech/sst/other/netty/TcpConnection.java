package com.fintech.sst.other.netty;

public class TcpConnection {
    private String authToken;
    private String host;
    private int port;

    public String getAuthToken() {
        return this.authToken;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public void setAuthToken(String var1) {
        this.authToken = var1;
    }

    public void setHost(String var1) {
        this.host = var1;
    }

    public void setPort(int var1) {
        this.port = var1;
    }

    public String toString() {
        return "TcpConnection [host=" + this.host + ", port=" + this.port + ", authToken=" + this.authToken + "]";
    }
}