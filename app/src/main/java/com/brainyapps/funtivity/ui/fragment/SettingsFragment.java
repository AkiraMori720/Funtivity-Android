package com.brainyapps.funtivity.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brainyapps.funtivity.AppConstant;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.ui.activity.AboutActivity;
import com.brainyapps.funtivity.ui.activity.EditProfileActivity;
import com.brainyapps.funtivity.ui.activity.MainActivity;
import com.brainyapps.funtivity.utils.CommonUtil;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {
	MainActivity mActivity;

	public static SettingsFragment newInstance() {
		SettingsFragment fragment = new SettingsFragment();
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
		mActivity = MainActivity.instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_settings, container, false);
		mView.findViewById(R.id.layout_edit_profile).setOnClickListener(this);
		mView.findViewById(R.id.layout_rate_app).setOnClickListener(this);
		mView.findViewById(R.id.layout_send_feedback).setOnClickListener(this);
		mView.findViewById(R.id.layout_about_app).setOnClickListener(this);
		mView.findViewById(R.id.layout_privacy_policy).setOnClickListener(this);
		mView.findViewById(R.id.layout_terms_conditions).setOnClickListener(this);
		return mView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.layout_edit_profile:
				startActivity(new Intent(mActivity, EditProfileActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.layout_rate_app:
				CommonUtil.launchMarket();
				break;
			case R.id.layout_send_feedback:
				CommonUtil.SendEmail(mActivity, AppConstant.ADMIN_EMAIL, "Send Feedback",	"Hi. ", "");
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.layout_about_app:
				AboutActivity.type = 0;
				startActivity(new Intent(mActivity, AboutActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.layout_privacy_policy:
				AboutActivity.type = 1;
				startActivity(new Intent(mActivity, AboutActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.layout_terms_conditions:
				AboutActivity.type = 2;
				startActivity(new Intent(mActivity, AboutActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
		}
	}

	@Override
	public void onRefresh() {}
}
