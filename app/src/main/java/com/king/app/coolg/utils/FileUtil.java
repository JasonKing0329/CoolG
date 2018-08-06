package com.king.app.coolg.utils;

import android.database.sqlite.SQLiteDatabase;

import com.king.app.coolg.base.CoolApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 14:29
 */
public class FileUtil {

    /**
     * 从assets目录复制的方法
     * @param dbFile
     */
    public static void copyDbFromAssets(String dbFile) {

        SQLiteDatabase db = null;
        //先检查是否存在，不存在才复制
        String dbPath = CoolApplication.getInstance().getFilesDir().getParent() + "/databases";
        try {
            db = SQLiteDatabase.openDatabase(dbPath + "/" + dbFile
                    , null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            db = null;
        }
        if (db == null) {
            try {
                InputStream assetsIn = CoolApplication.getInstance().getAssets().open(dbFile);
                File file = new File(dbPath);
                if (!file.exists()) {
                    file.mkdir();
                }
                OutputStream fileOut = new FileOutputStream(dbPath + "/" + dbFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = assetsIn.read(buffer))>0){
                    fileOut.write(buffer, 0, length);
                }

                fileOut.flush();
                fileOut.close();
                assetsIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (db != null) {
            db.close();
        }
    }

    public static boolean replaceDatabases(File folder) {
        if (folder == null || !folder.exists()) {
            return false;
        }

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file:files) {
                replaceDatabase(file, file.getName());
            }
        }
        else {
            replaceDatabase(folder, folder.getName());
        }
        return true;
    }
    public static boolean replaceDatabase(File source, String fileName) {
        if (source == null || !source.exists()) {
            return false;
        }

        // 删除源目录database
        String dbPath = CoolApplication.getInstance().getFilesDir().getParent() + "/databases";
        File targetFolder = new File(dbPath);
        if (targetFolder.exists()) {
            File file = new File(dbPath + "/" + fileName);
            if (file.exists()) {
                file.delete();
            }
        }
        try {
            InputStream in = new FileInputStream(source);
            File file = new File(dbPath + "/" + fileName);
            OutputStream fileOut = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer))>0){
                fileOut.write(buffer, 0, length);
            }

            fileOut.flush();
            fileOut.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
