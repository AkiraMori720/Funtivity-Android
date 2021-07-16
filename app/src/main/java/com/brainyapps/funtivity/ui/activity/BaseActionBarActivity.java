package com.brainyapps.funtivity.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.ui.dialog.MyProgressDialog;
import com.brainyapps.funtivity.utils.CommonUtil;

public class BaseActionBarActivity extends AppCompatActivity implements OnClickListener{
	public ActionBar actionBar;
	public TextView action_text_title;

	public View action_button_back;
	public View action_button_menu;

	public View action_button_notification;
	public View action_button_settings;
	public View action_button_filter;
	public View action_button_edit;
	public View action_button_report;
	public View action_button_call;
	public View action_button_video;

	public MyProgressDialog dlg_progress;

	
	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.orange));
		}
		actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange)));
			actionBar.setHomeButtonEnabled(false);
			actionBar.setElevation(0);

			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);

			LayoutInflater inflator = LayoutInflater.from(this);
			View v = inflator.inflate(R.layout.actionbar_title, null);
			v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

			action_text_title = v.findViewById(R.id.action_title);
			action_text_title.setText(this.getTitle());
			action_text_title.setVisibility(View.GONE);

			action_button_back = v.findViewById(R.id.action_back);
			action_button_back.setVisibility(View.GONE);
			action_button_menu = v.findViewById(R.id.action_menu);
			action_button_menu.setVisibility(View.GONE);

			action_button_notification = v.findViewById(R.id.action_notification);
			action_button_notification.setVisibility(View.GONE);
			action_button_settings = v.findViewById(R.id.action_settings);
			action_button_settings.setVisibility(View.GONE);
			action_button_filter = v.findViewById(R.id.action_filter);
			action_button_filter.setVisibility(View.GONE);
			action_button_edit = v.findViewById(R.id.action_edit);
			action_button_edit.setVisibility(View.GONE);
			action_button_report = v.findViewById(R.id.action_report);
			action_button_report.setVisibility(View.GONE);
			action_button_call = v.findViewById(R.id.action_call);
			action_button_call.setVisibility(View.GONE);
			action_button_video = v.findViewById(R.id.action_video);
			action_button_video.setVisibility(View.GONE);

			action_button_back.setOnClickListener(this);
			action_button_menu.setOnClickListener(this);
			action_button_notification.setOnClickListener(this);
			action_button_settings.setOnClickListener(this);
			action_button_filter.setOnClickListener(this);
			action_button_edit.setOnClickListener(this);
			action_button_report.setOnClickListener(this);
			action_button_call.setOnClickListener(this);
			action_button_video.setOnClickListener(this);
			actionBar.setCustomView(v);

			dlg_progress = new MyProgressDialog(this);
		}
	}

	public void setTheme() {
		Window window = getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(CommonUtil.getMainColor());
		}
		if (actionBar != null)
			actionBar.setBackgroundDrawable(new ColorDrawable(CommonUtil.getMainColor()));
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

	public void SetTitle(int titleResId, int colorResId) {
		if (titleResId > 0) {
			SetTitle(getString(titleResId), colorResId);

		} else {
			SetTitle(null, colorResId);
		}
	}

	public void SetTitle(String title, int colorResId) {
		if (actionBar != null) {
			action_text_title.setVisibility(View.VISIBLE);
			action_text_title.setText(title);
			if (colorResId > 0)
				action_text_title.setTextColor(getResources().getColor(colorResId));
		}
	}

	public void ShowActionBarIcons(boolean showActionBar, int... res_id_arr) {
		if (actionBar != null) {
			if (showActionBar)
				actionBar.show();
			else
				actionBar.hide();

			action_button_back.setVisibility(View.GONE);
			action_button_menu.setVisibility(View.GONE);
			action_button_notification.setVisibility(View.GONE);
			action_button_settings.setVisibility(View.GONE);
			action_button_filter.setVisibility(View.GONE);
			action_button_edit.setVisibility(View.GONE);
			action_button_report.setVisibility(View.GONE);
			action_button_call.setVisibility(View.GONE);
			action_button_video.setVisibility(View.GONE);

			if (res_id_arr != null) {
				for (int i = 0; i < res_id_arr.length; i++) {
					switch (res_id_arr[i]) {
						case R.id.action_back:
							action_button_back.setVisibility(View.VISIBLE);
							break;
						case R.id.action_menu:
							action_button_menu.setVisibility(View.VISIBLE);
							break;
						case R.id.action_notification:
							action_button_notification.setVisibility(View.VISIBLE);
							break;
						case R.id.action_settings:
							action_button_settings.setVisibility(View.VISIBLE);
							break;
						case R.id.action_filter:
							action_button_filter.setVisibility(View.VISIBLE);
							break;
						case R.id.action_edit:
							action_button_edit.setVisibility(View.VISIBLE);
							break;
						case R.id.action_report:
							action_button_report.setVisibility(View.VISIBLE);
							break;
						case R.id.action_call:
							action_button_call.setVisibility(View.VISIBLE);
							break;
						case R.id.action_video:
							action_button_video.setVisibility(View.VISIBLE);
							break;
					}
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.action_back:
			myBack();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (dlg_progress != null)
			dlg_progress.dismiss();
	}

	public void myBack() {
		finish();
		overridePendingTransition(R.anim.in_right, R.anim.out_right);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		myBack();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTheme();
	}
}