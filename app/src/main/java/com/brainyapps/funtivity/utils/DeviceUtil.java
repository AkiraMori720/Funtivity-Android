package com.brainyapps.funtivity.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.brainyapps.funtivity.R;

public class DeviceUtil {
	public static boolean isNetworkAvailable(Context context) {
		boolean isConnected = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		} catch (Exception e) {
			isConnected = false;
		}
		if (context != null && !isConnected)
			MessageUtil.showError(context, R.string.msg_error_network);
		return isConnected;
	}
}
