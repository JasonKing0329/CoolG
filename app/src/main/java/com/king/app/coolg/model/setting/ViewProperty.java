package com.king.app.coolg.model.setting;

import com.google.gson.Gson;
import com.king.app.coolg.model.bean.BannerParams;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/3/18 9:20
 */
public class ViewProperty extends BaseProperty {

    public static BannerParams getHomeBannerParams() {
        String json = getString("banner_params_home");
        Gson gson = new Gson();
        BannerParams bean = null;
        try {
            bean = gson.fromJson(json, BannerParams.class);
        } catch (Exception e) {}
        if (bean == null) {
            bean = new BannerParams();
        }
        return bean;
    }

    public static void setHomeBannerParams(BannerParams bean) {
        setString("banner_params_home", new Gson().toJson(bean));
    }

    public static BannerParams getVideoHomeBannerParams() {
        String json = getString("banner_params_video_home");
        Gson gson = new Gson();
        BannerParams bean = null;
        try {
            bean = gson.fromJson(json, BannerParams.class);
        } catch (Exception e) {}
        if (bean == null) {
            bean = new BannerParams();
        }
        return bean;
    }

    public static void setVideoHomeBannerParams(BannerParams bean) {
        setString("banner_params_video_home", new Gson().toJson(bean));
    }

    public static BannerParams getRecordBannerParams() {
        String json = getString("banner_params_record");
        Gson gson = new Gson();
        BannerParams bean = null;
        try {
            bean = gson.fromJson(json, BannerParams.class);
        } catch (Exception e) {}
        if (bean == null) {
            bean = new BannerParams();
        }
        return bean;
    }

    public static void setRecordBannerParams(BannerParams bean) {
        setString("banner_params_record", new Gson().toJson(bean));
    }

    public static BannerParams getStarBannerParams() {
        String json = getString("banner_params_star");
        Gson gson = new Gson();
        BannerParams bean = null;
        try {
            bean = gson.fromJson(json, BannerParams.class);
        } catch (Exception e) {}
        if (bean == null) {
            bean = new BannerParams();
        }
        return bean;
    }

    public static void setStarBannerParams(BannerParams bean) {
        setString("banner_params_star", new Gson().toJson(bean));
    }

}
