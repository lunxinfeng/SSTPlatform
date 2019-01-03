package com.fintech.sst.net.bean;

public class Sms {
    private String sendName;
    private String content;
    private String amount;

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Sms{" +
                "sendName='" + sendName + '\'' +
                ", content='" + content + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
