package com.fintech.sst.data.db;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Notice.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
  private static AppDatabase INSTANCE;
  private static final Object sLock = new Object();

  public abstract NoticeDao noticeDao();

  public static AppDatabase getInstance(Context context) {
    synchronized (sLock) {
      if (INSTANCE == null) {
        INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "notice.db")
//                        .addMigrations(MIGRATION_1_2)
                .build();
      }
      return INSTANCE;
    }
  }

//    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE Notice "
//                    + " ADD COLUMN pos_start int");
//            database.execSQL("ALTER TABLE Notice "
//                    + " ADD COLUMN offset_total int");
//        }
//    };

}