package com.king.app.coolg.utils;

import android.content.pm.PackageManager;

import com.king.app.coolg.base.CoolApplication;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 10:06
 */
public class AppUtil {

    public static String getAppVersionName() {
        try {
            return CoolApplication.getInstance().getPackageManager().getPackageInfo(CoolApplication.getInstance().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
