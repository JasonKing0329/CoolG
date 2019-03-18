package com.king.app.coolg.model.setting;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.king.app.coolg.base.CoolApplication;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/3/18 9:19
 */
public class BaseProperty {

    public static final String getString(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getString(key, "");
    }

    public static final void setString(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static final int getInt(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getInt(key, -1);
    }

    public static final int getInt(String key, int defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getInt(key, defaultValue);
    }

    public static final void setInt(String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static final long getLong(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getLong(key, -1);
    }

    public static final void setLong(String key, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static final boolean getBoolean(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        return sp.getBoolean(key, false);
    }

    public static final void setBoolean(String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

}
