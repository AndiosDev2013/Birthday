package com.chen.birthday;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigMgr {
	/** the configuratrion name. */
	private static String CONFIG_NAME = "birthday_config"; 

	/** the preferences. */
	private static SharedPreferences mPreference;
	
	/** variable configuration information */

	public static void initialize(Context context) {
		mPreference = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
	}
	
	private static final String APP_FIRST_START = "app_first_start";
	public static boolean getAppFirstStartStatus(){
		return mPreference.getBoolean(APP_FIRST_START, false);//Retrieve a String value from the preferences
	}
	public static void setAppFirstStartStatus(boolean status) {
		Editor edit = mPreference.edit();
		edit.putBoolean(APP_FIRST_START, status);
		edit.commit();
	}
}
