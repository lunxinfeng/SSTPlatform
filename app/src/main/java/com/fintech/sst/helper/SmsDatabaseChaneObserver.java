package com.fintech.sst.helper;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.fintech.sst.net.Configuration;
import com.fintech.sst.net.Constants;
import com.fintech.sst.net.bean.Sms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库观察者
 */
public class SmsDatabaseChaneObserver extends ContentObserver {
    // 只检查收件箱
    private static final Uri MMSSMS_ALL_MESSAGE_URI = Uri.parse("content://sms/inbox");
    private static final String SORT_FIELD_STRING = "date desc";  // 排序
    private static final String DB_FIELD_ID = "_id";
    private static final String DB_FIELD_ADDRESS = "address";
    private static final String DB_FIELD_PERSON = "person";
    private static final String DB_FIELD_BODY = "body";
    private static final String DB_FIELD_DATE = "date";
    private static final String DB_FIELD_TYPE = "type";
    private static final String DB_FIELD_THREAD_ID = "thread_id";
    private static final String[] ALL_DB_FIELD_NAME = {
            DB_FIELD_ID, DB_FIELD_ADDRESS, DB_FIELD_PERSON, DB_FIELD_BODY,
            DB_FIELD_DATE, DB_FIELD_TYPE, DB_FIELD_THREAD_ID};
    private int mMessageCount = -1;

    private final long DELTA_TIME = 60 * 1000;
    private ContentResolver mResolver;
    private String bankCode;
    private String bankRegex;

    public SmsDatabaseChaneObserver(ContentResolver resolver, Handler handler) {
        super(handler);
        mResolver = resolver;
        bankCode = Configuration.getUserInfoByKey(Constants.KEY_BANK_CODE);
        bankRegex = Configuration.getUserInfoByKey(Constants.KEY_BANK_REGEX);
    }

    @Override
    public void onChange(boolean selfChange) {
        onReceiveSms();
    }

    public void onReceiveSms() {
        Cursor cursor = null;
        // 添加异常捕捉
        try {
            //选取十分钟之内的短信   按时间倒序排列
//            cursor = mResolver.query(MMSSMS_ALL_MESSAGE_URI, ALL_DB_FIELD_NAME,
//                    " date > " + (System.currentTimeMillis() - 10 * 60 * 1000), null, SORT_FIELD_STRING);
            cursor = mResolver.query(MMSSMS_ALL_MESSAGE_URI, ALL_DB_FIELD_NAME,
                    null, null, SORT_FIELD_STRING);
            if (cursor == null) return;
            final int count = cursor.getCount();
            System.out.println("--------------短信count--------------");
            System.out.println(count);
            System.out.println(mMessageCount);
            if (count <= mMessageCount) {
                mMessageCount = count;
                return;
            }
            // 发现收件箱的短信总数目比之前大就认为是刚接收到新短信
            // 同时认为id最大的那条记录为刚刚新加入的短信的id---这个大多数是这样的
            mMessageCount = count;
//            cursor.moveToLast();
//            final long smsdate = Long.parseLong(cursor.getString(cursor.getColumnIndex(DB_FIELD_DATE)));
//            final long nowdate = System.currentTimeMillis();
//            System.out.println("--------------短信time--------------");
//            System.out.println(smsdate);
//            System.out.println(nowdate);
            // 如果当前时间和短信时间间隔超过60秒,认为这条短信无效
//            if (nowdate - smsdate > DELTA_TIME) {
//                return;
//            }
            if (cursor.moveToFirst()) {
                final String strAddress = cursor.getString(cursor.getColumnIndex(DB_FIELD_ADDRESS));    // 短信号码
                final String strbody = cursor.getString(cursor.getColumnIndex(DB_FIELD_BODY));          // 在这里获取短信信息
                final int smsid = cursor.getInt(cursor.getColumnIndex(DB_FIELD_ID)); //短信id
                if (TextUtils.isEmpty(strAddress) || TextUtils.isEmpty(strbody)) {
                    return;
                }
                // 得到短信号码和内容之后进行相关处理
                if (bankCode == null)
                    bankCode = Configuration.getUserInfoByKey(Constants.KEY_BANK_CODE);
                System.out.println("--------------短信--------------");
//                Sms sms = new Sms();
//                sms.setSendName(strAddress);
//                sms.setContent(strbody);
//                sms.setAmount(getAmount(strbody));
//                System.out.println(sms);
//                RxBus.getDefault().send(sms);
                if (bankCode != null && bankCode.equalsIgnoreCase(strAddress)) {
                    Sms sms = new Sms();
                    sms.setSendName(strAddress);
                    sms.setContent(strbody);
                    sms.setAmount(getAmount(strbody));
                    System.out.println(sms);
                    RxBus.getDefault().send(sms);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                try {  // 有可能cursor都没有创建成功
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getAmount(String content) {
        if (bankRegex == null)
            bankRegex = Configuration.getUserInfoByKey(Constants.KEY_BANK_REGEX);
        if (bankRegex != null) {
            Pattern r = Pattern.compile(bankRegex);
            Matcher m = r.matcher(content);
            if (m.find()) {
                String group = m.group(2);
                System.out.println("短信收款：" + group + "\t" + content + "\t" + m);
                return group.replaceAll(",","");
            }
            System.out.println("短信收款：" + 0 + "\t" + content + "\t" + m);
        }
        return "0";
    }
}