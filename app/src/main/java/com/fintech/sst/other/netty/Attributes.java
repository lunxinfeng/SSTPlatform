package com.fintech.sst.other.netty;

import io.netty.util.AttributeKey;

public class Attributes {
    public static AttributeKey<Boolean> NORMAL_CLOSE = AttributeKey.newInstance("normal_close");

    public Attributes() {
    }
}
