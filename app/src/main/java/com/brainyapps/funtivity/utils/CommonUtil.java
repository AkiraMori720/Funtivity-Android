package com.brainyapps.funtivity.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.core.app.ActivityCompat;
import com.brainyapps.funtivity.AppPreference;
import com.brainyapps.funtivity.FuntivityApp;
import com.brainyapps.funtivity.R;
import java.io.File;

public class CommonUtil {
	public static void hideKeyboard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static boolean isValidEmail(String emailAddr) {
		return !TextUtils.isEmpty(emailAddr) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddr).matches();
	}

	public static void SendEmail(Context context, String toEmail, String subject, String body, String attachment_url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.parse("mailto:"
				+ toEmail
				+ "?subject=" + subject + "&body=" + body +"");
		intent.setData(data);
		if (!TextUtils.isEmpty(attachment_url)) {
			Uri photoURI = Uri.fromFile(new File(attachment_url));
			intent.putExtra(Intent.EXTRA_STREAM, photoURI);
		}
		context.startActivity(intent);
	}

	public static void launchMarket() {
		Uri uri = Uri.parse("market://details?id=" + FuntivityApp.getContext().getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			FuntivityApp.getContext().startActivity(goToMarket);

		} catch (Exception e) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=" + FuntivityApp.getContext().getPackageName()));
			browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			FuntivityApp.getContext().startActivity(browserIntent);
		}
	}

	public static int getMainColor() {
		int theme = AppPreference.getInt(AppPreference.KEY.THEME, 0);
		if (theme == 0)
			return FuntivityApp.getContext().getColor(R.color.theme_a);
		else if (theme == 1)
			return FuntivityApp.getContext().getColor(R.color.theme_b);
		else if (theme == 2)
			return FuntivityApp.getContext().getColor(R.color.theme_c);
		else if (theme == 3)
			return FuntivityApp.getContext().getColor(R.color.theme_d);
		else if (theme == 4)
			return FuntivityApp.getContext().getColor(R.color.theme_e);
		else if (theme == 5)
			return FuntivityApp.getContext().getColor(R.color.theme_f);
		else
			return FuntivityApp.getContext().getColor(R.color.orange);
	}

	public static int getMainLightColor() {
		int theme = AppPreference.getInt(AppPreference.KEY.THEME, 0);
		if (theme == 0)
			return FuntivityApp.getContext().getColor(R.color.theme_a_light);
		else if (theme == 1)
			return FuntivityApp.getContext().getColor(R.color.theme_b_light);
		else if (theme == 2)
			return FuntivityApp.getContext().getColor(R.color.theme_c_light);
		else if (theme == 3)
			return FuntivityApp.getContext().getColor(R.color.theme_d_light);
		else if (theme == 4)
			return FuntivityApp.getContext().getColor(R.color.theme_e_light);
		else if (theme == 5)
			return FuntivityApp.getContext().getColor(R.color.theme_f_light);
		else
			return FuntivityApp.getContext().getColor(R.color.orange_light);
	}

	public static int TYPE_CAMERA_PERMISSION =1;
	public static int TYPE_STORAGE_PERMISSION =2;
	public static boolean verifyStoragePermissions(final int type, final Activity activity) {
		int permission0 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int permission1 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA);
		if (type == TYPE_CAMERA_PERMISSION) {
			if (permission0 != PackageManager.PERMISSION_GRANTED
					|| permission1 != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		} else if (type == TYPE_STORAGE_PERMISSION) {
			if (permission0 != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}
}
