package com.fintech.sst.data.db;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import java.util.List;

@Dao
public interface NoticeDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insertNotice(Notice notice);

  @Delete
  int delAll(Notice... notices);

  @Query("DELETE FROM Notice")
  void delTable();

  @Update
  void updateAll(Notice... notices);

  @Query("SELECT * FROM Notice WHERE type = :type  ORDER BY `offset` DESC,pos_curr DESC LIMIT 1")
  Notice queryLast(int type);

  @Query("SELECT * FROM Notice WHERE type = :type")
  List<Notice> queryAll(int type);

}