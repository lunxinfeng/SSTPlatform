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

  @Query("SELECT * FROM Notice WHERE uuid = :uuid")
  Notice query(String uuid);

  @Query("SELECT * FROM Notice")
  List<Notice> queryAll();

  @Query("SELECT * FROM Notice LIMIT :pageSize OFFSET (:pageIndex -1) * :pageSize")
  List<Notice> queryAll(int pageIndex,int pageSize);

  @Query("SELECT * FROM Notice WHERE status = :status LIMIT :pageSize OFFSET (:pageIndex -1) * :pageSize")
  List<Notice> queryAll(int status,int pageIndex,int pageSize);
}