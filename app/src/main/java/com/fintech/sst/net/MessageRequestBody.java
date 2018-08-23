package com.fintech.sst.net;

public class MessageRequestBody extends SignRequestBody {

    public MessageRequestBody() {
        super();
    }


    private int count;

    public int getNotifyCount() {
        return count;
    }

    public void addNotifyCount() {
        this.count += 1;
    }

    public String key() {
        return String.valueOf(this.get("type")) + String.valueOf(this.get("time")) + String.valueOf(this.get("amount"));
    }

}
