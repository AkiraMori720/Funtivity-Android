package com.brainyapps.funtivity;

import android.content.SharedPreferences;

public class AppPreference {
	private static SharedPreferences instance = null;

	public static class KEY {
		public static final String AGREE = "AGREE";
		public static final String SIGN_IN_AUTO = "SIGN_IN_AUTO";
		public static final String SIGN_IN_REMEMBER = "REMEMBER";
		public static final String SIGN_IN_USERNAME = "SIGN_IN_USERNAME";
		public static final String SIGN_IN_PASSWORD = "SIGN_IN_PASSWORD";
		public static final String THEME = "THEME";
	}
	
	public static void initialize(SharedPreferences pref) {
		instance = pref;
	}
	
	public static boolean getBool(String key, boolean def) {
		return instance.getBoolean(key, def);
	}
	public static void setBool(String key, boolean value) {
		SharedPreferences.Editor editor = instance.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static String getStr(String key, String def) {
		return instance.getString(key, def);
	}
	public static void setStr(String key, String value) {
		SharedPreferences.Editor editor = instance.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static int getInt(String key, int def) {
		return instance.getInt(key, def);
	}
	public static void setInt(String key, int value) {
		SharedPreferences.Editor editor = instance.edit();
		editor.putInt(key, value);
		editor.commit();
	}
}
