package com.fintech.sst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    @org.junit.Test
    public void test(){
        String p = "收入(.*)\\)(\\d+(,\\d+)*(\\.\\d{0,2})?)元，余额(\\d+(,\\d+)*(\\.\\d{0,2})?)元。【工商银行】";
        String content = "您尾号0891卡12月26日14:55快捷支付收入(钟宇支付宝转账支付宝)1,999,999,998元，余额411.50元。【工商银行】";
        Pattern r = Pattern.compile(p);
        Matcher m = r.matcher(content);
        if (m.find()) {
            String group = m.group(2);
            System.out.println(group.replaceAll(",",""));
        }
        System.out.println("0");
    }
}
