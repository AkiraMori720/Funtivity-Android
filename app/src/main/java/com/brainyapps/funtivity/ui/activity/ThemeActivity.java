package com.brainyapps.funtivity.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.brainyapps.funtivity.AppPreference;
import com.brainyapps.funtivity.R;

public class ThemeActivity extends BaseActionBarActivity {
	public static ThemeActivity instance = null;
	ImageView img_theme_a;
	ImageView img_theme_b;
	ImageView img_theme_c;
	ImageView img_theme_d;
	ImageView img_theme_e;
	ImageView img_theme_f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
        SetTitle(R.string.choose_theme, 0);
		ShowActionBarIcons(true, R.id.action_back);
		setContentView(R.layout.activity_theme);
		img_theme_a = findViewById(R.id.img_theme_a);
		img_theme_b = findViewById(R.id.img_theme_b);
		img_theme_c = findViewById(R.id.img_theme_c);
		img_theme_d = findViewById(R.id.img_theme_d);
		img_theme_e = findViewById(R.id.img_theme_e);
		img_theme_f = findViewById(R.id.img_theme_f);
		findViewById(R.id.layout_theme_a).setOnClickListener(this);
		findViewById(R.id.layout_theme_b).setOnClickListener(this);
		findViewById(R.id.layout_theme_c).setOnClickListener(this);
		findViewById(R.id.layout_theme_d).setOnClickListener(this);
		findViewById(R.id.layout_theme_e).setOnClickListener(this);
		findViewById(R.id.layout_theme_f).setOnClickListener(this);
		initialize();
	}

	private void initialize() {
		int theme = AppPreference.getInt(AppPreference.KEY.THEME, 0);
		img_theme_a.setVisibility(View.GONE);
		img_theme_b.setVisibility(View.GONE);
		img_theme_c.setVisibility(View.GONE);
		img_theme_d.setVisibility(View.GONE);
		img_theme_e.setVisibility(View.GONE);
		img_theme_f.setVisibility(View.GONE);
		if (theme == 0)
			img_theme_a.setVisibility(View.VISIBLE);
		else if (theme == 1)
			img_theme_b.setVisibility(View.VISIBLE);
		else if (theme == 2)
			img_theme_c.setVisibility(View.VISIBLE);
		else if (theme == 3)
			img_theme_d.setVisibility(View.VISIBLE);
		else if (theme == 4)
			img_theme_e.setVisibility(View.VISIBLE);
		else if (theme == 5)
			img_theme_f.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
			case R.id.layout_theme_a:
				chooseTheme(0);
				break;
			case R.id.layout_theme_b:
				chooseTheme(1);
				break;
			case R.id.layout_theme_c:
				chooseTheme(2);
				break;
			case R.id.layout_theme_d:
				chooseTheme(3);
				break;
			case R.id.layout_theme_e:
				chooseTheme(4);
				break;
			case R.id.layout_theme_f:
				chooseTheme(5);
				break;
		}
	}

	private void chooseTheme(int type) {
		AppPreference.setInt(AppPreference.KEY.THEME, type);
		initialize();
		setTheme();
	}
}
