package com.king.app.coolg.model.setting;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.king.app.coolg.base.CoolApplication;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 14:40
 */
public class SettingProperty {

    public static final String getString(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getString(key, "");
    }

    private static final void setString(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static final int getInt(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getInt(key, -1);
    }

    private static final int getInt(String key, int defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getInt(key, defaultValue);
    }

    private static final void setInt(String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static final long getLong(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getLong(key, -1);
    }

    private static final void setLong(String key, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private static final boolean getBoolean(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getBoolean(key, false);
    }

    private static final void setBoolean(String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setEnableFingerPrint(boolean enable) {
        setBoolean("enable_finger_print", enable);
    }

    public static boolean isEnableFingerPrint() {
        return getBoolean("enable_finger_print");
    }

    /**
     * shaprePreference文件版本(com.jing.app.jjgallery_preferences.xml)
     */
    public static String getPrefVersion() {
        return getString(PreferenceKey.PREF_VERSION);
    }

    /**
     * shaprePreference文件版本(com.jing.app.jjgallery_preferences.xml)
     * @param version version name
     */
    public static void setPrefVersion(String version) {
        setString(PreferenceKey.PREF_VERSION, version);
    }

    public static boolean isNoImageMode() {
        return getBoolean(PreferenceKey.PREF_GDB_NO_IMAGE);
    }

    public static String getNavHeadImage() {
        return getString(PreferenceKey.PREF_GDB_NAV_HEADER_BG);
    }

    public static void saveNavHeadImage(String path) {
        setString(PreferenceKey.PREF_GDB_NAV_HEADER_BG, path);
    }

    public static String getServerUrl() {
        return getString(PreferenceKey.PREF_HTTP_SERVER);
    }

    public static boolean isRandomRecommend() {
        return getBoolean(PreferenceKey.PREF_GDB_REC_ANIM_RANDOM);
    }

    public static void setRandomRecommend(boolean random) {
        setBoolean(PreferenceKey.PREF_GDB_REC_ANIM_RANDOM, random);
    }

    public static int getRecommendAnimType() {
        return getInt(PreferenceKey.PREF_GDB_REC_ANIM_TYPE);
    }

    public static void setRecommendAnimType(int random) {
        setInt(PreferenceKey.PREF_GDB_REC_ANIM_TYPE, random);
    }

    public static int getRecommendAnimTime() {
        int time = getInt(PreferenceKey.PREF_GDB_REC_ANIM_TIME);
        if (time < 3000) {
            return 3000;
        }
        else {
            return time;
        }
    }

    public static void setRecommendAnimTime(int random) {
        setInt(PreferenceKey.PREF_GDB_REC_ANIM_TIME, random);
    }

    public static boolean isStarRandomRecommend() {
        return getBoolean(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_RANDOM);
    }

    public static void setStarRandomRecommend(boolean random) {
        setBoolean(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_RANDOM, random);
    }

    public static int getStarRecommendAnimType() {
        return getInt(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_TYPE);
    }

    public static void setStarRecommendAnimType(int random) {
        setInt(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_TYPE, random);
    }

    public static int getStarRecommendAnimTime() {
        int time = getInt(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_TIME);
        if (time < 3000) {
            return 3000;
        }
        else {
            return time;
        }
    }

    public static void setStarRecommendAnimTime(int random) {
        setInt(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_TIME, random);
    }

    public static String getRecordFilterModel() {
        return getString(PreferenceKey.PREF_GDB_FILTER_MODEL);
    }

    public static void setRecordFilterModel(String random) {
        setString(PreferenceKey.PREF_GDB_FILTER_MODEL, random);
    }

    public static int getStarListViewMode() {
        return getInt(PreferenceKey.PREF_STAR_LIST_VIEW_MODE);
    }

    public static void setStarListViewMode(int random) {
        setInt(PreferenceKey.PREF_STAR_LIST_VIEW_MODE, random);
    }

}
