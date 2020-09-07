package com.king.app.coolg.model.setting;

import com.google.gson.Gson;
import com.king.app.coolg.model.bean.HsvColorBean;
import com.king.app.coolg.phone.star.random.RandomData;
import com.king.app.coolg.phone.video.home.RecommendBean;

import java.util.ArrayList;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 14:40
 */
public class SettingProperty extends BaseProperty {

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

    public static void setServerUrl(String url) {
        setString(PreferenceKey.PREF_HTTP_SERVER, url);
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

    public static int getSceneSortType() {
        return getInt(PreferenceKey.PREF_SCENE_SORT, 0);
    }

    public static void setSceneSortType(int random) {
        setInt(PreferenceKey.PREF_SCENE_SORT, random);
    }

    public static int getVideoServerSortType() {
        return getInt(PreferenceKey.PREF_VIDEO_SERVER_SORT, 0);
    }

    public static void setVideoServerSortType(int type) {
        setInt(PreferenceKey.PREF_VIDEO_SERVER_SORT, type);
    }

    /**
     *
     */
    public static String getUploadVersion() {
        return getString(PreferenceKey.PREF_UPLOAD_VERSION);
    }

    /**
     *
     * @param version version name
     */
    public static void setUploadVersion(String version) {
        setString(PreferenceKey.PREF_UPLOAD_VERSION, version);
    }

    public static int getTagSortType() {
        return getInt(PreferenceKey.PREF_TAG_SORT, 0);
    }

    public static void setTagSortType(int random) {
        setInt(PreferenceKey.PREF_TAG_SORT, random);
    }

    public static RandomData getStarRandomData() {
        String sql = getString(PreferenceKey.PREF_STAR_RANDOM_DATA);
        RandomData bean = null;
        try {
            bean = new Gson().fromJson(sql, RandomData.class);
        } catch (Exception e) {}
        if (bean == null) {
            bean = new RandomData();
            bean.setMarkedList(new ArrayList<>());
            bean.setCandidateList(new ArrayList<>());
        }
        return bean;
    }

    public static void setStarRandomData(RandomData bean) {
        String sql = null;
        try {
            sql = new Gson().toJson(bean);
        } catch (Exception e) {}
        setString(PreferenceKey.PREF_STAR_RANDOM_DATA, sql);
    }

}
