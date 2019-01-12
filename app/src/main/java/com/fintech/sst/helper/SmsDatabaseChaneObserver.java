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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    private static final String[] ALL_DB_FIELD_NAME = {DB_FIELD_ADDRESS, DB_FIELD_BODY, DB_FIELD_DATE};
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
                final String smsTime = cursor.getString(cursor.getColumnIndex(DB_FIELD_DATE)); //短信时间
//                final int smsid = cursor.getInt(cursor.getColumnIndex(DB_FIELD_ID)); //短信id
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
//                sms.setTime(smsTime);
//                System.out.println(sms);
//                RxBus.getDefault().send(sms);
                if (bankCode != null && bankCode.equalsIgnoreCase(strAddress)) {
                    Sms sms = new Sms();
                    sms.setSendName(strAddress);
                    sms.setContent(strbody);
                    sms.setTime(smsTime);
                    configSms(sms);
//                    sms.setAmount(getAmount(strbody));
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

    private void configSms(Sms sms) {
        if (bankRegex == null)
            bankRegex = Configuration.getUserInfoByKey(Constants.KEY_BANK_REGEX);
        if (bankRegex != null) {
            Pattern r = Pattern.compile(bankRegex);
            Matcher m = r.matcher(sms.getContent());
            if (m.find()) {
                int month;
                int day;
                int hour;
                int min;
                Calendar calendar;
                switch (bankCode) {
                    case "95566"://中国银行
//                        month = 0;
//                        day = 0;
//                        hour = Integer.parseInt(m.group(1));
//                        min = Integer.parseInt(m.group(2));

//                        calendar = Calendar.getInstance();
//                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, min, 0);

//                        sms.setTime(String.valueOf(calendar.getTimeInMillis()));
                        sms.setAmount(m.group(1).replaceAll(",", ""));
                        break;
                    case "95595"://光大银行
//                        month = 0;
//                        day = 0;
                        hour = Integer.parseInt(m.group(1));
                        min = Integer.parseInt(m.group(2));

                        calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, min, 0);

                        sms.setTime(String.valueOf(calendar.getTimeInMillis()));
                        sms.setAmount(m.group(3).replaceAll(",", ""));
                        break;
                    case "95561"://兴业银行
//                        month = 0;
                        day = Integer.parseInt(m.group(1));
                        hour = Integer.parseInt(m.group(2));
                        min = Integer.parseInt(m.group(3));

                        calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day, hour, min, 0);

                        sms.setTime(String.valueOf(calendar.getTimeInMillis()));
                        sms.setAmount(m.group(4).replaceAll(",", ""));
                        break;
                    default:
                        month = Integer.parseInt(m.group(1)) - 1;
                        day = Integer.parseInt(m.group(2));
                        hour = Integer.parseInt(m.group(3));
                        min = Integer.parseInt(m.group(4));

                        calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), month, day, hour, min, 0);

//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        System.out.println(sdf.format(calendar.getTime()));

                        sms.setTime(String.valueOf(calendar.getTimeInMillis()));
                        sms.setAmount(m.group(5).replaceAll(",", ""));

                        break;
                }

                return;
            }
        }
        sms.setAmount("0");
    }

    public List<Sms> query50() {
        Cursor cursor = null;
        // 添加异常捕捉
        try {//
            cursor = mResolver.query(MMSSMS_ALL_MESSAGE_URI, ALL_DB_FIELD_NAME,
                    " address = " + bankCode + " AND date > " + (System.currentTimeMillis() - 60 * 60 * 1000), null, SORT_FIELD_STRING);
            List<Sms> smsList = new ArrayList<>();
            if (cursor == null) return smsList;
            while (cursor.moveToNext() && smsList.size() < 50) {
                final String strAddress = cursor.getString(cursor.getColumnIndex(DB_FIELD_ADDRESS));    // 短信号码
                final String strbody = cursor.getString(cursor.getColumnIndex(DB_FIELD_BODY));          // 在这里获取短信信息
                final String smsTime = cursor.getString(cursor.getColumnIndex(DB_FIELD_DATE)); //短信时间
                // 得到短信号码和内容之后进行相关处理
                if (bankCode == null)
                    bankCode = Configuration.getUserInfoByKey(Constants.KEY_BANK_CODE);
                System.out.println("--------------短信--------------");
                if (bankCode != null && bankCode.equalsIgnoreCase(strAddress)) {
                    Sms sms = new Sms();
                    sms.setSendName(strAddress);
                    sms.setContent(strbody);
                    sms.setTime(smsTime);
                    configSms(sms);
//                    sms.setAmount(getAmount(strbody));
                    if (!sms.getAmount().equals("0"))
                        smsList.add(sms);
                }
            }
            return smsList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
}