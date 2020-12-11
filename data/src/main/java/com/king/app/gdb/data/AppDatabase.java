package com.king.app.gdb.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.king.app.gdb.data.dao.FavDao;
import com.king.app.gdb.data.dao.PlayDao;
import com.king.app.gdb.data.dao.RecordDao;
import com.king.app.gdb.data.dao.StarDao;
import com.king.app.gdb.data.entity.CountRecord;
import com.king.app.gdb.data.entity.CountStar;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorStar;
import com.king.app.gdb.data.entity.FavorStarOrder;
import com.king.app.gdb.data.entity.GProperties;
import com.king.app.gdb.data.entity.PlayDuration;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.PlayOrder;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.entity.TagRecord;
import com.king.app.gdb.data.entity.TagStar;
import com.king.app.gdb.data.entity.TopStar;
import com.king.app.gdb.data.entity.TopStarCategory;
import com.king.app.gdb.data.entity.VideoCoverPlayOrder;
import com.king.app.gdb.data.entity.VideoCoverStar;

/**
 * @description:
 * @author：Jing
 * @date: 2020/12/9 22:26
 */
@Database(entities = {CountStar.class, StarRating.class, Star.class
            , Record.class, RecordType1v1.class, RecordType3w.class, CountRecord.class, RecordStar.class
            , FavorRecordOrder.class, FavorRecord.class, FavorStarOrder.class, FavorStar.class
            , GProperties.class, TopStar.class, TopStarCategory.class
            , PlayDuration.class, PlayItem.class, PlayOrder.class, VideoCoverPlayOrder.class, VideoCoverStar.class
            , Tag.class, TagRecord.class, TagStar.class
            }
            , version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "gdata.db";
    private static AppDatabase instance;

    public abstract StarDao getStarDao();
    public abstract RecordDao getRecordDao();
    public abstract FavDao getFavDao();
    public abstract PlayDao getPlayDao();

    public static void destroy() {
        instance = null;
    }

    public static AppDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public static AppDatabase buildDatabase(Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        //生成数据库时使用,可以初始化一些信息
                        super.onCreate(db);
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
                .allowMainThreadQueries()  //允许主线程查询
//                .addMigrations(migration3_4)
                .build();
    }

}
