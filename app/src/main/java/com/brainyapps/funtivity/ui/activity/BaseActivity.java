package com.brainyapps.funtivity.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.fragment.app.FragmentActivity;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.ui.dialog.MyProgressDialog;
import com.brainyapps.funtivity.utils.CommonUtil;

public class BaseActivity extends FragmentActivity  {
	// UI
	public MyProgressDialog dlg_progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dlg_progress = new MyProgressDialog(this);
	}

	public void setTheme() {
		Window window = getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(CommonUtil.getMainColor());
		}
	}

	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
		overridePendingTransition(R.anim.in_left, R.anim.out_left);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.in_left, R.anim.out_left);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTheme();
	}

	public void myBack() {
		finish();
		overridePendingTransition(R.anim.in_right, R.anim.out_right);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (dlg_progress != null)
			dlg_progress.dismiss();
	}

	@Override
	public void onBackPressed() {
		myBack();
	}
}