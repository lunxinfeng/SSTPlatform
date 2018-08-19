package com.fintech.sst.data.db;


import android.content.Context;

import java.util.List;

public class DB {
  public static long insert(Context context, Notice notice){
    return AppDatabase.getInstance(context).userDao().insertNotice(notice);
  }

  public static Notice queryLast(Context context, int type){
    return AppDatabase.getInstance(context).userDao().queryLast(type);
  }

  public static List<Notice> queryAll(Context context, int type){
    return AppDatabase.getInstance(context).userDao().queryAll(type);
  }

  public static int deleteAll(Context context, Notice... notices){
    return AppDatabase.getInstance(context).userDao().delAll(notices);
  }
  public static void deleteTable(Context context){
    AppDatabase.getInstance(context).userDao().delTable();
  }


}