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
    private String bankCode; //银行号码
    private String bankRegex;  //银行正则
    private String bankType;  //银行日期类别

    public SmsDatabaseChaneObserver(ContentResolver resolver, Handler handler) {
        super(handler);
        mResolver = resolver;
        bankCode = Configuration.getUserInfoByKey(Constants.KEY_BANK_CODE);
        bankRegex = Configuration.getUserInfoByKey(Constants.KEY_BANK_REGEX);
        bankType = Configuration.getUserInfoByKey(Constants.KEY_BANK_TYPE);
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
            System.out.println("--------------短信count-----" + count + "\t" + mMessageCount + "---------");
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
                System.out.println("--------------短信----" + bankCode + "\t" + strAddress + "----------");
//                Sms sms = new Sms();
//                sms.setSendName(strAddress);
//                sms.setContent(strbody);
//                sms.setAmount(getAmount(strbody));
//                sms.setTime(smsTime);
//                System.out.println(sms);
//                RxBus.getDefault().send(sms);
                if (bankCode != null && bankCode.contains(strAddress)) {
                    Sms sms = new Sms();
                    sms.setSendName(strAddress);
                    sms.setContent(strbody);
                    sms.setTime(smsTime);
                    configSms(sms);
//                    sms.setAmount(getAmount(strbody));
                    System.out.println("短信:" + sms);
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
        if (bankType == null)
            bankType = Configuration.getUserInfoByKey(Constants.KEY_BANK_TYPE);
        if (bankRegex == null)
            bankRegex = Configuration.getUserInfoByKey(Constants.KEY_BANK_REGEX);
        if (bankType != null && bankRegex != null) {
            Pattern r = Pattern.compile(bankRegex);
            Matcher m = r.matcher(sms.getContent());
            if (m.find()) {
                int month;
                int day;
                int hour;
                int min;
                Calendar calendar;
                switch (bankType) {
                    case "0"://mm-dd hh:mm
                        month = Integer.parseInt(m.group(1)) - 1;
                        day = Integer.parseInt(m.group(2));
                        hour = Integer.parseInt(m.group(3));
                        min = Integer.parseInt(m.group(4));

                        calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), month, day, hour, min, 59);
                        calendar.set(Calendar.MILLISECOND, 0);

//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        System.out.println(sdf.format(calendar.getTime()));
                        long time;
                        if (calendar.getTimeInMillis() > System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 100L) {//跨年
                            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                        }
                        time = calendar.getTimeInMillis();

                        sms.setTime(String.valueOf(time));
                        sms.setAmount(m.group(5).replaceAll(",", ""));
                        break;
                    case "1"://dd hh:mm
                        //                        month = 0;
                        day = Integer.parseInt(m.group(1));
                        hour = Integer.parseInt(m.group(2));
                        min = Integer.parseInt(m.group(3));

                        calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day, hour, min, 59);
                        calendar.set(Calendar.MILLISECOND, 0);

                        if (calendar.getTimeInMillis() > System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 100L) {//跨年
                            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                            calendar.set(Calendar.MONTH, 11);
                            time = calendar.getTimeInMillis();
                        } else if (calendar.getTimeInMillis() > System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 10) {//跨月
                            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                            time = calendar.getTimeInMillis();
                        } else if (calendar.getTimeInMillis() > System.currentTimeMillis() + 60 * 60 * 1000) {//跨天
                            time = calendar.getTimeInMillis() - 24 * 60 * 60 * 1000;
                        } else {//正常
                            time = calendar.getTimeInMillis();
                        }

                        sms.setTime(String.valueOf(time));
                        sms.setAmount(m.group(4).replaceAll(",", ""));
                        break;
                    case "2"://hh:mm
//                        month = 0;
//                        day = 0;
                        hour = Integer.parseInt(m.group(1));
                        min = Integer.parseInt(m.group(2));

                        calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, min, 59);
                        calendar.set(Calendar.MILLISECOND, 0);

                        if (calendar.getTimeInMillis() > System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 100L) {//跨年
                            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                            calendar.set(Calendar.MONTH, 11);
                            calendar.set(Calendar.DAY_OF_MONTH, 31);
                            time = calendar.getTimeInMillis();
                        } else if (calendar.getTimeInMillis() > System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 10) {//跨月
                            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                            calendar.set(Calendar.DAY_OF_MONTH, getMonthLastDay(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)));
                            time = calendar.getTimeInMillis();
                        } else if (calendar.getTimeInMillis() > System.currentTimeMillis() + 60 * 60 * 1000) {
                            time = calendar.getTimeInMillis() - 24 * 60 * 60 * 1000;
                        } else {
                            time = calendar.getTimeInMillis();
                        }

                        sms.setTime(String.valueOf(time));
                        sms.setAmount(m.group(3).replaceAll(",", ""));
                        break;
                    case "3": //mm-dd
//                        month = 0;
//                        day = 0;
//                        hour = Integer.parseInt(m.group(1));
//                        min = Integer.parseInt(m.group(2));

//                        calendar = Calendar.getInstance();
//                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, min, 0);

//                        sms.setTime(String.valueOf(calendar.getTimeInMillis()));
                        sms.setAmount(m.group(1).replaceAll(",", ""));
                        break;
                    default:
                        sms.setAmount("0");
                        break;
                }

                return;
            }
        }
        sms.setAmount("0");
    }
    /**
     * 得到指定月的天数
     * */
    private int getMonthLastDay(int year, int month)
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
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
                if (bankCode != null && bankCode.contains(strAddress)) {
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