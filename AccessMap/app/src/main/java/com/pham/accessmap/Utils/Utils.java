package com.pham.accessmap.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mc976 on 1/10/16.
 */
public class Utils {
    public static final String PREFS_NAME = "MyPref";

    public static void setCheckOnAllPlaceTypes (boolean isChecked, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putBoolean("kCheckAllPlaceTypes",isChecked);
        editor.commit();
    }

    public static boolean getCheckAllPlaceTypes (Context context) {
        SharedPreferences tSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return tSharedPreferences.getBoolean("kCheckAllPlaceTypes", true);
    }
}
