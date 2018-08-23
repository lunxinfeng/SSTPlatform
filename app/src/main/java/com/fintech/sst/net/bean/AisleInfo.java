package com.fintech.sst.net.bean;


public class AisleInfo {
    private String realAmount;
    private String AppLoginName;
    private String ok;
    private String account;
    private String accountId;
    private String enable;

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(String realAmount) {
        this.realAmount = realAmount;
    }

    public String getAppLoginName() {
        return AppLoginName;
    }

    public void setAppLoginName(String appLoginName) {
        AppLoginName = appLoginName;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "AisleInfo{" +
                "realAmount='" + realAmount + '\'' +
                ", AppLoginName='" + AppLoginName + '\'' +
                ", ok='" + ok + '\'' +
                ", account='" + account + '\'' +
                '}';
    }
}
