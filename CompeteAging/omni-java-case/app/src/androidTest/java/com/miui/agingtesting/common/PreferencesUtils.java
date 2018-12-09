package com.miui.agingtesting.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

/**
 * @author Kevin Salazar
 * @link kevicsalazar.com
 */

public class PreferencesUtils {

    private SharedPreferences pref;

    public PreferencesUtils(Context context) {
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Primitive

    public void putInt(String key, int value) {
        pref.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int def) {
        return pref.getInt(key, def);
    }

    public void putLong(String key, long value) {
        pref.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long def) {
        return pref.getLong(key, def);
    }

    public void putFloat(String key, float value) {
        pref.edit().putFloat(key, value).apply();
    }

    public float getFloat(String key, float def) {
        return pref.getFloat(key, def);
    }

    public void putBoolean(String key, boolean value) {
        pref.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean def) {
        return pref.getBoolean(key, def);
    }

    public void putString(String key, String value) {
        pref.edit().putString(key, value).apply();
    }

    public String getString(String key, String def) {
        return pref.getString(key, def);
    }

    // Date

    public void putDate(String key, Date date) {
        pref.edit().putLong(key, date.getTime()).apply();
    }

    public Date getDate(String key) {
        return new Date(pref.getLong(key, 0));
    }

}
