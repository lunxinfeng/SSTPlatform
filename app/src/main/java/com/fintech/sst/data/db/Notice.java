package com.fintech.sst.data.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity
public class Notice {

  @NonNull
  @PrimaryKey
  public String uuid = "";
  public String title;
  public String packageName;
  public int noticeId;
  public String tag;
  public String erroMsg;
  /**
   * 通知内容
   */
  public String content = "";

  /**
   * 金额
   */
  public float amount;

  /**
   * 类型：0全部  100支付宝   200微信  300银行卡
   */
  public int type;

  /**
   * 状态： 0全部  1提交成功  2提交失败
   */
  public int status;

  public long saveTime;

  public Notice() {
    uuid = UUID.randomUUID().toString().replaceAll("-","");
    saveTime = System.currentTimeMillis();
  }

}