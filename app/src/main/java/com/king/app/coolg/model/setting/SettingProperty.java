package com.king.app.coolg.model.setting;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.model.bean.HsvColorBean;
import com.king.app.coolg.phone.video.home.RecommendBean;

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

    public static boolean isEnableFingerPrint() {
        return getBoolean(PreferenceKey.PREF_SAFETY_FP);
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

    public static boolean isDemoImageMode() {
        return getBoolean(PreferenceKey.PREF_DEMO_IMAGE);
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

    public static int getRecordSortType() {
        return getInt(PreferenceKey.PREF_GDB_RECORD_ORDER, 0);
    }

    public static void setRecordSortType(int random) {
        setInt(PreferenceKey.PREF_GDB_RECORD_ORDER, random);
    }

    public static boolean isRecordSortDesc() {
        return getBoolean(PreferenceKey.PREF_GDB_RECORD_ORDER_DESC);
    }

    public static void setRecordSortDesc(boolean random) {
        setBoolean(PreferenceKey.PREF_GDB_RECORD_ORDER_DESC, random);
    }

    public static int getStarRecordsSortType() {
        return getInt(PreferenceKey.PREF_GDB_STAR_ORDER);
    }

    public static void setStarRecordsSortType(int random) {
        setInt(PreferenceKey.PREF_GDB_STAR_ORDER, random);
    }

    public static boolean isStarRecordsSortDesc() {
        return getBoolean(PreferenceKey.PREF_GDB_STAR_ORDER_DESC);
    }

    public static void setStarRecordsSortDesc(boolean random) {
        setBoolean(PreferenceKey.PREF_GDB_STAR_ORDER_DESC, random);
    }

    public static HsvColorBean getSceneHsvColor() {
        String json = getString(PreferenceKey.PREF_GDB_SCENE_HSV_COLOR);
        Gson gson = new Gson();
        try {
            HsvColorBean bean = gson.fromJson(json, HsvColorBean.class);
            return bean;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setSceneHsvColor(HsvColorBean bean) {
        setString(PreferenceKey.PREF_GDB_SCENE_HSV_COLOR, new Gson().toJson(bean));
    }

    public static int getPhoneOrderSortType() {
        return getInt("record_order_sort_type", 0);
    }

    public static void setPhoneOrderSortType(int random) {
        setInt("record_order_sort_type", random);
    }

    public static int getStudioListType() {
        return getInt("studio_list_type");
    }

    public static void setStudioListType(int type) {
        setInt("studio_list_type", type);
    }

    public static int getStudioListSortType() {
        return getInt("studio_list_sort_type");
    }

    public static void setStudioListSortType(int type) {
        setInt("studio_list_sort_type", type);
    }

    public static int getVideoStarOrderViewType() {
        return getInt(PreferenceKey.PREF_VIDEO_STAR_ORDER_VIEW_TYPE);
    }

    public static void setVideoStarOrderViewType(int type) {
        setInt(PreferenceKey.PREF_VIDEO_STAR_ORDER_VIEW_TYPE, type);
    }

    public static int getVideoPlayOrderViewType() {
        return getInt(PreferenceKey.PREF_VIDEO_PLAY_ORDER_VIEW_TYPE);
    }

    public static void setVideoPlayOrderViewType(int type) {
        setInt(PreferenceKey.PREF_VIDEO_PLAY_ORDER_VIEW_TYPE, type);
    }

    public static RecommendBean getVideoRecBean() {
        String sql = getString(PreferenceKey.PREF_VIDEO_REC_BEAN);
        try {
            RecommendBean bean = new Gson().fromJson(sql, RecommendBean.class);
            return bean;
        } catch (Exception e) {}
        return null;
    }

    public static void setVideoRecBean(RecommendBean bean) {
        String sql = null;
        try {
            sql = new Gson().toJson(bean);
        } catch (Exception e) {}
        setString(PreferenceKey.PREF_VIDEO_REC_BEAN, sql);
    }

    public static String getDemoImageVersion() {
        return getString(PreferenceKey.PREF_DEMO_IMAGE_VERSION);
    }

    public static void setDemoImageVersion(String version) {
        setString(PreferenceKey.PREF_DEMO_IMAGE_VERSION, version);
    }

    public static RecommendBean getHomeRecBean() {
        String sql = getString(PreferenceKey.PREF_HOME_REC_BEAN);
        try {
            RecommendBean bean = new Gson().fromJson(sql, RecommendBean.class);
            return bean;
        } catch (Exception e) {}
        return null;
    }

    public static void setHomeRecBean(RecommendBean bean) {
        String sql = null;
        try {
            sql = new Gson().toJson(bean);
        } catch (Exception e) {}
        setString(PreferenceKey.PREF_HOME_REC_BEAN, sql);
    }

}
