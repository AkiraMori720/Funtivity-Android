package com.brainyapps.funtivity.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements OnClickListener {
	// UI
	protected View mView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		onRefresh();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

	public abstract void onRefresh();
}