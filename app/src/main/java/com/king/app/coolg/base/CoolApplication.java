package com.king.app.coolg.base;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.king.app.coolg.conf.AppConfig;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.context.GDataContext;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.gdb.data.entity.DaoMaster;
import com.king.app.gdb.data.entity.DaoSession;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;
import com.king.app.gdb.data.entity.FavorStarDao;
import com.king.app.gdb.data.entity.FavorStarOrderDao;
import com.king.app.gdb.data.entity.PlayDurationDao;
import com.king.app.gdb.data.entity.PlayItemDao;
import com.king.app.gdb.data.entity.PlayOrder;
import com.king.app.gdb.data.entity.PlayOrderDao;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.StarRatingDao;
import com.king.app.gdb.data.entity.TagDao;
import com.king.app.gdb.data.entity.TagRecordDao;
import com.king.app.gdb.data.entity.TagStarDao;
import com.king.app.gdb.data.entity.TopStarCategoryDao;
import com.king.app.gdb.data.entity.TopStarDao;
import com.king.app.gdb.data.entity.VideoCoverPlayOrderDao;
import com.king.app.gdb.data.entity.VideoCoverStarDao;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import tcking.github.com.giraffeplayer2.GiraffePlayer;

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

        GiraffePlayer.debug = true;
        GiraffePlayer.nativeDebug = true;
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
        public void onCreate(Database db) {
            super.onCreate(db);
            insertTempPlayOrder(db);
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
                case 2:
                    StarRatingDao.createTable(db, true);
                case 3:
                    // 如果旧版本是1，已经执行了最新的createTable结构，只有是2和3的时候还是老结构需要改字段
                    if (oldVersion != 1) {
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
                    }
                case 4:
                    PlayOrderDao.createTable(db, true);
                    PlayItemDao.createTable(db, true);
                    PlayDurationDao.createTable(db, true);
                    insertTempPlayOrder(db);
                case 5:
                    TopStarCategoryDao.createTable(db, true);
                    TopStarDao.createTable(db, true);
                case 6:
                    VideoCoverStarDao.createTable(db, true);
                    VideoCoverPlayOrderDao.createTable(db, true);
                    if (oldVersion == 6) {
                        db.execSQL("ALTER TABLE " + PlayOrderDao.TABLENAME + " ADD COLUMN "
                                + PlayOrderDao.Properties.CoverUrl.columnName + " TEXT");
                    }
                case 7:
                    if (!isFieldExist(db, RecordDao.TABLENAME, RecordDao.Properties.ScoreBody.columnName)) {
                        db.execSQL("ALTER TABLE " + RecordDao.TABLENAME + " ADD COLUMN "
                                + RecordDao.Properties.ScoreBody.columnName + " INTEGER DEFAULT 0");
                    }
                    if (!isFieldExist(db, RecordDao.TABLENAME, RecordDao.Properties.ScoreCock.columnName)) {
                        db.execSQL("ALTER TABLE " + RecordDao.TABLENAME + " ADD COLUMN "
                                + RecordDao.Properties.ScoreCock.columnName + " INTEGER DEFAULT 0");
                    }
                    if (!isFieldExist(db, RecordDao.TABLENAME, RecordDao.Properties.ScoreAss.columnName)) {
                        db.execSQL("ALTER TABLE " + RecordDao.TABLENAME + " ADD COLUMN "
                                + RecordDao.Properties.ScoreAss.columnName + " INTEGER DEFAULT 0");
                    }
                case 8:
                    TagDao.createTable(db, true);
                    TagRecordDao.createTable(db, true);
                    TagStarDao.createTable(db, true);
                case 9:
                    db.execSQL("ALTER TABLE " + StarRatingDao.TABLENAME + " ADD COLUMN "
                            + StarRatingDao.Properties.Prefer.columnName + " REAL DEFAULT 0");
                    break;
            }
        }

        private void insertTempPlayOrder(Database db) {
            PlayOrderDao dao = new DaoMaster(db).newSession().getPlayOrderDao();
            long count = dao.queryBuilder()
                    .where(PlayOrderDao.Properties.Id.eq(AppConstants.PLAY_ORDER_TEMP_ID))
                    .buildCount().count();
            if (count == 0) {
                PlayOrder order = new PlayOrder(AppConstants.PLAY_ORDER_TEMP_ID, AppConstants.PLAY_ORDER_TEMP_NAME, null);
                dao.insert(order);
                dao.detachAll();
            }
        }

        /**
         * 判断某表里某字段是否存在
         *
         * @param db
         * @param tableName
         * @param fieldName
         * @return
         */
        private boolean isFieldExist(Database db, String tableName, String fieldName) {
            String queryStr = "select sql from sqlite_master where type = 'table' and name = '%s'";
            queryStr = String.format(queryStr, tableName);
            Cursor c = db.rawQuery(queryStr, null);
            String tableCreateSql = null;
            try {
                if (c != null && c.moveToFirst()) {
                    tableCreateSql = c.getString(c.getColumnIndex("sql"));
                }
            } finally {
                if (c != null)
                    c.close();
            }
            if (tableCreateSql != null && tableCreateSql.contains(fieldName))
                return true;
            return false;
        }
    }

}
