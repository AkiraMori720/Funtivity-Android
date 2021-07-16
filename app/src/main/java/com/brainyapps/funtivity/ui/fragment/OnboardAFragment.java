package com.brainyapps.funtivity.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.ui.activity.OnboardActivity;

public class OnboardAFragment extends BaseFragment{
	public static OnboardAFragment instance;
	// Data
	OnboardActivity mActivity;

	public static OnboardAFragment newInstance() {
		OnboardAFragment fragment = new OnboardAFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		mActivity = OnboardActivity.instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_onboarda, container, false);
		return mView;
	}

	@Override
	public void onRefresh() {}
}


