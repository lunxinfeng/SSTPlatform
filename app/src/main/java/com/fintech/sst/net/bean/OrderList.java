package com.fintech.sst.net.bean;

public class OrderList {

    private int id;
    private String mchId;
    private String mchName;
    private String agentId;
    private String tradeNo;
    private String outTradeNo;
    private String tradeStatus;
    private int tradeChannel;
    private double totalAmount;
    private double seviceFee;
    private double realAmount;
    private double discAmount;
    private int enable;
    private String createTime;

    private String qrAccount;

    public double getSeviceFee() {
        return seviceFee;
    }

    public void setSeviceFee(double seviceFee) {
        this.seviceFee = seviceFee;
    }

    public int getTradeChannel() {
        return tradeChannel;
    }

    public void setTradeChannel(int tradeChannel) {
        this.tradeChannel = tradeChannel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getMchName() {
        return mchName;
    }

    public void setMchName(String mchName) {
        this.mchName = mchName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(double realAmount) {
        this.realAmount = realAmount;
    }

    public double getDiscAmount() {
        return discAmount;
    }

    public void setDiscAmount(double discAmount) {
        this.discAmount = discAmount;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getQrAccount() {
        if (qrAccount == null)
            return "";
        return qrAccount;
    }

    public void setQrAccount(String qrAccount) {
        this.qrAccount = qrAccount;
    }
}
