package com.pham.accessmap.Object;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by mc976 on 12/12/15.
 */
public class LanguageHelper {
    private static LanguageHelper mInstance = null;
    public static final String PREFS_NAME = "MyPref";
    public static final String VIETNAMESE = "vi";
    public static final String ENGLISH = "en";
    private LanguageHelper () {

    }

    public static LanguageHelper getInstance() {
        if (mInstance == null) {
            mInstance = new LanguageHelper();
        }
        return mInstance;
    }

    public void setAppLanguage (String mLanguageCode, Context mContext) {
        Locale locale = new Locale(mLanguageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mContext.getResources().updateConfiguration(config, null);
        mContext.getResources().getDisplayMetrics();
        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putString("kLanguageCode", mLanguageCode);
        editor.commit();
    }

    public String getAppLanguage (Context mContext) {
        SharedPreferences tSharedPreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return tSharedPreferences.getString("kLanguageCode",null);
    }
}
