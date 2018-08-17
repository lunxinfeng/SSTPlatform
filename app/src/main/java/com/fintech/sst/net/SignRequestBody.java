package com.fintech.sst.net;

import android.util.Log;

import com.fintech.sst.App;
import com.fintech.sst.helper.Utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SignRequestBody extends HashMap<String, String> {


    public SignRequestBody() {
        this(null);
    }

    public SignRequestBody(Map map) {
        if (map != null) {
            this.putAll(map);
        }
        this.put("deviceId", Utils.getIMEI(App.getApplication().getApplicationContext()));
        this.put("deviceInfo", Utils.getDeviceInfo());
    }

    public SignRequestBody sign() {
        try {
            Configuration.KEY_SP_ keySp = Configuration.getKeySp();
            this.put("mchId", keySp.getMchId());
            this.put("userName", keySp.getUserName());
            this.put("timestamp", System.currentTimeMillis());
            this.put("nonceStr", SignatureHelper.generateNonceStr());
            this.put("signType", "MD5");

            String sign = SignatureHelper.generateSignature(this, keySp.getLoginToken());
            this.put("sign", sign);

            Log.d("--------", SignatureHelper.isSignatureValid(this, keySp.getLoginToken()) + "--" + keySp.getLoginToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void put(String key, Integer integer) {
        if (integer == null) {
            return;
        }
        this.put(key, String.valueOf(integer));
    }

    public String put(String key, String string) {
        if (string == null) {
            return string;
        }
        return super.put(key, string);
    }

    public void put(String key, Long l) {
        if (l == null) {
            return;
        }
        super.put(key, String.valueOf(l));
    }

    public void put(String key, BigDecimal l) {
        if (l == null) {
            return;
        }
        super.put(key, String.valueOf(l));
    }

    public void put(String key, Object l) {
        if (l == null) {
            return;
        }
        super.put(key, String.valueOf(l));
    }

}
