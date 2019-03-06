package com.king.app.coolg.conf;

import android.content.Context;
import android.os.Environment;

import com.king.app.coolg.utils.StorageUtil;

import java.io.File;
import java.io.IOException;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/23 15:46
 */
public class AppConfig {
    public static final String DB_NAME = "gdata.db";
    public static final String DEMO_IMAGE_VERSION = "1.0";

    public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();

    public static final String APP_ROOT = SDCARD + "/fileencrypt";
    public static final String APP_DIR_IMG = APP_ROOT + "/img";
    public static final String APP_DIR_CROP_IMG = APP_DIR_IMG + "/crop";
    public static final String DOWNLOAD_IMAGE_DEFAULT = APP_DIR_IMG + "/download";
    public static final String GDB_IMG = APP_DIR_IMG + "/gdb";
    public static final String GDB_IMG_STAR = GDB_IMG + "/star";
    public static final String GDB_IMG_RECORD = GDB_IMG + "/record";
    public static final String GDB_IMG_DEMO = GDB_IMG + "/demo";
    public static final String APP_DIR_IMG_SAVEAS = APP_ROOT + "/saveas";
    public static final String APP_DIR_DB_HISTORY = APP_ROOT + "/history";
    public static final String APP_DIR_GAME = APP_ROOT + "/game";

    public static final String APP_DIR_EXPORT = APP_ROOT + "/export";

    public static final String EXTEND_RES_DIR = APP_ROOT + "/res";
    public static final String EXTEND_RES_COLOR = EXTEND_RES_DIR + "/color.xml";

    public static final String APP_DIR_CONF = APP_ROOT + "/conf";
    public static final String APP_DIR_CONF_PREF = APP_DIR_CONF + "/shared_prefs";
    public static final String APP_DIR_CONF_PREF_DEF = APP_DIR_CONF_PREF + "/default";
    public static final String APP_DIR_CONF_CRASH = APP_DIR_CONF + "/crash";
    public static final String APP_DIR_CONF_APP = APP_DIR_CONF + "/app";
    // 采用自动更新替代gdata.db的方法，因为jornal的存在，会使重新使用这个db出现问题
    public static String GDB_DB_JOURNAL = APP_DIR_CONF + "/gdata.db-journal";

    public static String PREF_NAME="com.jing.app.jjgallery_preferences";

    public static String DISK_PREF_DEFAULT_PATH;

    public static final String[] DIRS = new String[] {
            APP_ROOT, APP_DIR_IMG, APP_DIR_CROP_IMG, DOWNLOAD_IMAGE_DEFAULT,
            GDB_IMG, GDB_IMG_STAR, GDB_IMG_RECORD, GDB_IMG_DEMO, APP_DIR_IMG_SAVEAS,
            APP_DIR_DB_HISTORY, APP_DIR_GAME, APP_DIR_EXPORT, EXTEND_RES_DIR,
            EXTEND_RES_COLOR, APP_DIR_CONF, APP_DIR_CONF_PREF, APP_DIR_CONF_PREF_DEF,
            APP_DIR_CONF_CRASH, APP_DIR_CONF_APP
    };

    /**
     * 遍历程序所有目录，创建.nomedia文件
     */
    public static void createNoMedia() {
        File file = new File(APP_ROOT);
        createNoMedia(file);
    }

    /**
     * 遍历file下所有目录，创建.nomedia文件
     * @param file
     */
    public static void createNoMedia(File file) {
        File[] files = file.listFiles();
        for (File f:files) {
            if (f.isDirectory()) {
                createNoMedia(f);
            }
        }
        File nomediaFile = new File(file.getPath() + "/.nomedia");
        if (!nomediaFile.exists()) {
            try {
                new File(file.getPath(), ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getGdbVideoDir(Context context) {
        return StorageUtil.getOutterStoragePath(context) + "/video";
    }
}
