package com.brainyapps.funtivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import com.google.firebase.storage.FirebaseStorage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FuntivityApp extends MultiDexApplication {
	public static Context mContext;

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = getApplicationContext();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		AppPreference.initialize(pref);
		AppGlobals.mFirebaseStorage = FirebaseStorage.getInstance();
		checkSignatures();
	}

	public static Context getContext() {
		return mContext;
	}

	public void checkSignatures() {
		try {
			@SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo (
					"com.brainyapps.funtivity", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());

				String hashkey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
				Log.d("KeyHash:", hashkey);
			}
		} catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
