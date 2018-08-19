package com.fintech.sst.data.db;

import android.arch.persistence.room.Entity;

@Entity(primaryKeys = {"account", "pos_curr", "offset", "type"})
public class Notice {

  /**
   * 通知内容
   */
  public String content = "";

  /**
   * 金额
   */
  public double amount;

  /**
   * 类型：100支付宝   200微信  300银行卡
   */
  public int type;

  /**
   * 状态： 0未提交  1提交成功  2提交失败
   */
  public int status;

  public long saveTime;

  public Notice() {
    saveTime = System.currentTimeMillis();
  }
}