package com.fintech.sst.other.netty;

import java.io.Serializable;

public class UserPay implements Serializable {
    private static final long serialVersionUID = 1L;
    private String authToken;
    private String collectionUserId;
    private double money;
    private String payId;
    private String remark;
    private String sessionKey;
    private long time;
    private String type;
    private String userLoginId;

    public UserPay() {
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public String getCollectionUserId() {
        return this.collectionUserId;
    }

    public double getMoney() {
        return this.money;
    }

    public String getPayId() {
        return this.payId;
    }

    public String getRemark() {
        return this.remark;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public long getTime() {
        return this.time;
    }

    public String getType() {
        return this.type;
    }

    public String getUserLoginId() {
        return this.userLoginId;
    }

    public void setAuthToken(String var1) {
        this.authToken = var1;
    }

    public void setCollectionUserId(String var1) {
        this.collectionUserId = var1;
    }

    public void setMoney(double var1) {
        this.money = var1;
    }

    public void setPayId(String var1) {
        this.payId = var1;
    }

    public void setRemark(String var1) {
        this.remark = var1;
    }

    public void setSessionKey(String var1) {
        this.sessionKey = var1;
    }

    public void setTime(long var1) {
        this.time = var1;
    }

    public void setType(String var1) {
        this.type = var1;
    }

    public void setUserLoginId(String var1) {
        this.userLoginId = var1;
    }

    public String toString() {
        return "UserPay [authToken=" + this.authToken + ", sessionKey=" + this.sessionKey + ", time=" + this.time + ", payId=" + this.payId + ", money=" + this.money + ", remark=" + this.remark + ", type=" + this.type + ", userLoginId=" + this.userLoginId + ", collectionUserId=" + this.collectionUserId + "]";
    }
}
