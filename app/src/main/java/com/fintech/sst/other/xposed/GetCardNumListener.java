package com.fintech.sst.other.xposed;

public interface GetCardNumListener {
    void success(String re, String sessionKey, String money, String remark);
    void error(String error);
}
