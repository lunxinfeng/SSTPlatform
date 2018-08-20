package com.fintech.sst.data.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Notice {

  @PrimaryKey(autoGenerate = true)
  public int id;
  /**
   * 通知内容
   */
  public String content = "";

  /**
   * 金额
   */
  public double amount;

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
    saveTime = System.currentTimeMillis();
  }

  @Override
  public String toString() {
    return "Notice{" +
            "id=" + id +
            ", content='" + content + '\'' +
            ", amount=" + amount +
            ", type=" + type +
            ", status=" + status +
            ", saveTime=" + saveTime +
            '}';
  }
}