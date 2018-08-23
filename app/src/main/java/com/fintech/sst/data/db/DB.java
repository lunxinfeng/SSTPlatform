package com.fintech.sst.data.db;


import android.content.Context;

import java.util.List;

public class DB {
  public static long insert(Context context, Notice notice){
    return AppDatabase.getInstance(context).noticeDao().insertNotice(notice);
  }

  public static void updateAll(Context context, Notice... notice){
    AppDatabase.getInstance(context).noticeDao().updateAll(notice);
  }

  public static Notice query(Context context,String uuid){
    return AppDatabase.getInstance(context).noticeDao().query(uuid);
  }

  public static List<Notice> queryAll(Context context){
    return AppDatabase.getInstance(context).noticeDao().queryAll();
  }

  public static List<Notice> queryAll(Context context,int pageIndex,int pageSize){
    return AppDatabase.getInstance(context).noticeDao().queryAll(pageIndex,pageSize);
  }

  public static List<Notice> queryAll(Context context, int status,int pageIndex,int pageSize){
    return AppDatabase.getInstance(context).noticeDao().queryAll(status,pageIndex,pageSize);
  }

  public static int deleteAll(Context context, Notice... notices){
    return AppDatabase.getInstance(context).noticeDao().delAll(notices);
  }
  public static void deleteTable(Context context){
    AppDatabase.getInstance(context).noticeDao().delTable();
  }


}