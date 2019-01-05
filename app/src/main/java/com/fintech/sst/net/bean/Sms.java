package com.fintech.sst.net.bean;

public class Sms {
    private String sendName;
    private String content;
    private String amount;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

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
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Sms){
            return ((Sms) obj).time.equals(time) && Float.parseFloat(((Sms) obj).amount) == Float.parseFloat(amount);
        }
        return super.equals(obj);
    }
}
