package com.king.app.coolg.base;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.king.app.coolg.conf.AppConfig;
import com.king.app.coolg.context.GDataContext;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.gdb.data.entity.DaoMaster;
import com.king.app.gdb.data.entity.DaoSession;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;
import com.king.app.gdb.data.entity.FavorStarDao;
import com.king.app.gdb.data.entity.FavorStarOrderDao;
import com.king.app.gdb.data.entity.StarRatingDao;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 11:41
 */
public class CoolApplication extends Application {

    private static CoolApplication instance;

    private DaoSession daoSession;
    private RHelper helper;
    private Database database;

    public static CoolApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // logger
        Logger.addLogAdapter(new AndroidLogAdapter());

    }

    /**
     * 程序初始化使用外置数据库
     * 需要由外部调用，如果在onCreate里直接初始化会创建新的数据库
     */
    public void createGreenDao() {
        helper = new RHelper(new GDataContext(this), AppConfig.DB_NAME);
        database = helper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();

        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void reCreateGreenDao() {
        daoSession.clear();
        database.close();
        helper.close();
        createGreenDao();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Database getDatabase() {
        return database;
    }

    public static class RHelper extends DaoMaster.OpenHelper {

        public RHelper(Context context, String name) {
            super(context, name);
        }

        public RHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            DebugLog.e(" oldVersion=" + oldVersion + ", newVersion=" + newVersion);
            switch (oldVersion) {
                case 1:
                    // Dao已经是最新结构了，不能再执行case 3里的add column
                    FavorRecordDao.createTable(db, true);
                    FavorRecordOrderDao.createTable(db, true);
                    FavorStarDao.createTable(db, true);
                    FavorStarOrderDao.createTable(db, true);
                    StarRatingDao.createTable(db, true);
                    break;
                case 2:
                    StarRatingDao.createTable(db, true);
                case 3:
                    db.execSQL("ALTER TABLE " + FavorRecordOrderDao.TABLENAME + " ADD COLUMN "
                        + FavorRecordOrderDao.Properties.ParentId.columnName + " INTEGER DEFAULT 0");
                    db.execSQL("ALTER TABLE " + FavorRecordDao.TABLENAME + " ADD COLUMN "
                            + FavorRecordOrderDao.Properties.CreateTime.columnName + " INTEGER DEFAULT 0");
                    db.execSQL("ALTER TABLE " + FavorRecordDao.TABLENAME + " ADD COLUMN "
                            + FavorRecordOrderDao.Properties.UpdateTime.columnName + " INTEGER DEFAULT 0");
                    db.execSQL("ALTER TABLE " + FavorStarOrderDao.TABLENAME + " ADD COLUMN "
                            + FavorStarOrderDao.Properties.ParentId.columnName + " INTEGER DEFAULT 0");
                    db.execSQL("ALTER TABLE " + FavorStarOrderDao.TABLENAME + " ADD COLUMN "
                            + FavorStarOrderDao.Properties.CreateTime.columnName + " INTEGER DEFAULT 0");
                    db.execSQL("ALTER TABLE " + FavorStarOrderDao.TABLENAME + " ADD COLUMN "
                            + FavorStarOrderDao.Properties.UpdateTime.columnName + " INTEGER DEFAULT 0");
                    break;
            }
        }
    }

}
