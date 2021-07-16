package com.brainyapps.funtivity.ui.dialog;

import com.brainyapps.funtivity.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

public class MyProgressDialog extends Dialog {

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MyProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_progress);

		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		WindowManager.LayoutParams lp = getWindow().getAttributes();  
		lp.dimAmount = 0f; 
		getWindow().setAttributes(lp);

		setCancelable(false);
	}
	
	@Override
	public void show() {
		// we are using try - catch in order to prevent crashing issue
		// when the activity is finished but the AsyncTask is still processing
		try {
			super.show();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
