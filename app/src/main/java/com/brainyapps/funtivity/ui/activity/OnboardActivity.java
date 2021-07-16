package com.brainyapps.funtivity.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.brainyapps.funtivity.AppPreference;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.ui.fragment.OnboardAFragment;
import com.brainyapps.funtivity.ui.fragment.OnboardBFragment;
import com.brainyapps.funtivity.ui.fragment.OnboardCFragment;
import com.brainyapps.funtivity.ui.view.CustomDurationViewPager;
import com.viewpagerindicator.CirclePageIndicator;

public class OnboardActivity extends BaseActivity implements View.OnClickListener {
	public static OnboardActivity instance = null;
	// UI
	private CustomDurationViewPager pager;
	private CirclePageIndicator indicator;
	Button btn_next;
	TextView txt_skip;
	CheckBox check_again;
	// Data
	int index = 0;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_onboard);
		txt_skip = findViewById(R.id.txt_skip);
		btn_next = findViewById(R.id.btn_next);
		check_again = findViewById(R.id.check_again);
		FragmentStatePagerAdapter adapter = new LasyAdapter(getSupportFragmentManager());
		pager = findViewById(R.id.pager);
		pager.setScrollDurationFactor(3);
		pager.setAdapter(adapter);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		indicator = findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		pager.setCurrentItem(0);

		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setState(position, false);
				indicator.setCurrentItem(position);
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if (position == 2 && index == 2)
					gotoNextActivity();
				index = position;
			}
			@Override
			public void onPageScrollStateChanged(int state) {}
		});
		findViewById(R.id.btn_next).setOnClickListener(this);
		findViewById(R.id.txt_skip).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_next:
				if (pager.getCurrentItem() < 2)
					setState(pager.getCurrentItem() + 1, true);
				else
					gotoNextActivity();
				break;
			case R.id.txt_skip:
				gotoNextActivity();
				break;
		}
	}

	// set state from button click
	private void setState(int position, boolean state) {
		if (state)
			pager.setCurrentItem(position);
		if (position < 2) {
			btn_next.setText(getString(R.string.next));
			txt_skip.setVisibility(View.VISIBLE);
			check_again.setVisibility(View.GONE);
		} else {
			btn_next.setText(getString(R.string.done));
			txt_skip.setVisibility(View.GONE);
			check_again.setVisibility(View.VISIBLE);
		}
	}

	class LasyAdapter extends FragmentStatePagerAdapter {

		public LasyAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Fragment getItem(int position) {
			if (position == 0)
				return new OnboardAFragment().newInstance();
			else if (position == 1)
				return new OnboardBFragment().newInstance();
			else if (position == 2)
				return new OnboardCFragment().newInstance();
			return null;
		}
		@Override
		public int getCount() {
			return 3;
		}
	}

	// goto next activity
	private void gotoNextActivity() {
		if (check_again.isChecked())
			AppPreference.setBool(AppPreference.KEY.AGREE, true);
		startActivity(new Intent(instance, LoginActivity.class));
		finish();
	}
}

