package com.king.app.coolg.model.image;

import com.king.app.coolg.BuildConfig;
import com.king.app.coolg.conf.AppConfig;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.DebugLog;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/21 10:51
 */
public class ImageProvider {

    /**
     *
     * @param name
     * @param indexPackage save the real index, can be null
     * @return
     */
    public static String getRecordRandomPath(String name, IndexPackage indexPackage) {
        return getImagePath(AppConfig.GDB_IMG_RECORD, name, -1, indexPackage);
    }

    public static String getRecordPath(String name, int index) {
        return getImagePath(AppConfig.GDB_IMG_RECORD, name, index, null);
    }

    public static List<String> getRecordPathList(String name) {
        return getImagePathList(AppConfig.GDB_IMG_RECORD, name);
    }

    public static boolean hasRecordFolder(String name) {
        return hasFolder(AppConfig.GDB_IMG_RECORD, name);
    }

    /**
     *
     * @param name
     * @param indexPackage save the real index, can be null
     * @return
     */
    public static String getStarRandomPath(String name, IndexPackage indexPackage) {
        return getImagePath(AppConfig.GDB_IMG_STAR, name, -1, indexPackage);
    }

    public static String getStarPath(String name, int index) {
        return getImagePath(AppConfig.GDB_IMG_STAR, name, index, null);
    }

    public static List<String> getStarPathList(String name) {
        return getImagePathList(AppConfig.GDB_IMG_STAR, name);
    }

    public static boolean hasStarFolder(String name) {
        return hasFolder(AppConfig.GDB_IMG_STAR, name);
    }

    private static boolean hasFolder(String parent, String name) {
        File file = new File(parent + "/" + name);
        return file.exists() && file.isDirectory();
    }

    public static int getRecordPicNumber(String name) {
        return getPicNumber(AppConfig.GDB_IMG_RECORD, name);
    }

    public static int getStarPicNumber(String name) {
        return getPicNumber(AppConfig.GDB_IMG_STAR, name);
    }

    private static int getPicNumber(String parent, String name) {
        int count = 0;
        String path;
        if (hasFolder(parent, name)) {
            File file = new File(parent + "/" + name);
            count = countImageFiles(file);
        }
        if (count == 0) {
            path = parent + "/" + name;
            if (!name.endsWith(".png")) {
                path = path.concat(".png");
            }
            if (new File(path).exists()) {
                count ++;
            }
        }
        return count;
    }

    /**
     *
     * @param parent
     * @param name
     * @param index if random, then -1
     * @param indexPackage save the true index, can be null
     * @return
     */
    private static String getImagePath(String parent, String name, int index, IndexPackage indexPackage) {
        if (SettingProperty.isNoImageMode()) {
            return "";
        }
        if (SettingProperty.isDemoImageMode()) {
            return getRandomDemoImage(index, indexPackage);
        }

        String path;
        if (hasFolder(parent, name)) {
            File file = new File(parent + "/" + name);
            List<File> fileList = new ArrayList<>();
            getImageFiles(file, fileList);
            if (fileList.size() == 0) {
                path = parent + "/" + name;
                if (!name.endsWith(".png")) {
                    path = path.concat(".png");
                }
            }
            else {
                if (index == -1 || index >= fileList.size()) {
                    int pos = Math.abs(new Random().nextInt()) % fileList.size();
                    if (indexPackage != null) {
                        indexPackage.index = pos;
                    }
                    return fileList.get(pos).getPath();
                }
                else {
                    return fileList.get(index).getPath();
                }
            }
        }
        else {
            path = parent + "/" + name;
            if (!name.endsWith(".png")) {
                path = path.concat(".png");
            }
        }
        return path;
    }

    public static String getRandomDemoImage(int index, IndexPackage indexPackage) {
        String path = null;
        File[] fileList = new File(AppConfig.GDB_IMG_DEMO).listFiles(fileFilter);
        if (index >=0 && index < fileList.length) {
            path = fileList[index].getPath();
        }
        else {
            if (fileList.length > 0) {
                int pos = Math.abs(new Random().nextInt()) % fileList.length;
                if (indexPackage != null) {
                    indexPackage.index = pos;
                }
                path = fileList[pos].getPath();
            }
        }
        return path;
    }

    private static List<String> getDemoImages() {
        List<String> list = new ArrayList<>();
        File[] fileList = new File(AppConfig.GDB_IMG_DEMO).listFiles(fileFilter);
        for (File file:fileList) {
            list.add(file.getPath());
        }
        return list;
    }

    /**
     * @param file
     */
    private static int countImageFiles(File file) {
        int result = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles(fileFilter);
            for (File f:files) {
                result += countImageFiles(f);
            }
            return result;
        }
        else {
            result = 1;
        }
        return result;
    }

    /**
     * v2.0.2 it supported multi-level directories since v2.0.1
     * @param file
     * @param list
     */
    private static void getImageFiles(File file, List<File> list) {
        if (file.isDirectory()) {
            File[] files = file.listFiles(fileFilter);
            for (File f:files) {
                getImageFiles(f, list);
            }
        }
        else {
            list.add(file);
        }
    }

    private static FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return !file.getName().endsWith(".nomedia");
        }
    };

    private static List<String> getImagePathList(String parent, String name) {
        if (SettingProperty.isDemoImageMode()) {
            return getDemoImages();
        }

        List<String> list = new ArrayList<>();
        File file = new File(parent + "/" + name);
        List<File> fileList = new ArrayList<>();
        getImageFiles(file, fileList);
        if (fileList != null) {
            for (File f:fileList) {
                if (SettingProperty.isNoImageMode()) {
                    list.add("");
                }
                else {
                    list.add(f.getPath());
                }
            }
            Collections.shuffle(list);
        }
        return list;
    }

    /**
     * 控制无图模式
     * @param path
     * @return
     */
    public static String parseFilePath(String path) {
        if (SettingProperty.isNoImageMode()) {
            return "";
        }
        else {
            return path;
        }
    }

    public static String getRecordCuPath(String name) {
        if (SettingProperty.isNoImageMode()) {
            return "";
        }
        String path = AppConfig.GDB_IMG_RECORD + "/" + name + "/cu";
        File folder = new File(path);
        if (folder.exists()) {
            File[] files = folder.listFiles(fileFilter);
            if (files.length > 0) {
                return files[0].getPath();
            }
        }
        return null;
    }

    public static String parseCoverUrl(String coverUrl) {
        if (SettingProperty.isNoImageMode()) {
            return "";
        }
        if (SettingProperty.isDemoImageMode()) {
            return getRandomDemoImage(-1, null);
        }
        return coverUrl;
    }

    public static class IndexPackage {
        public int index;
    }
}
