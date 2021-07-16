package com.brainyapps.funtivity.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.ui.activity.OnboardActivity;

public class OnboardCFragment extends BaseFragment{
	public static OnboardCFragment instance;
	// Data
	OnboardActivity mActivity;

	public static OnboardCFragment newInstance() {
		OnboardCFragment fragment = new OnboardCFragment();
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
		mView = inflater.inflate(R.layout.fragment_onboardc, container, false);
		return mView;
	}

	@Override
	public void onRefresh() {}
}


