package com.brainyapps.funtivity.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;

public class ResetPasswordActivity extends BaseActionBarActivity {
	public static ResetPasswordActivity instance = null;
	LinearLayout layout_background;
	EditText edt_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		SetTitle(R.string.reset_password, -1);
		ShowActionBarIcons(true, R.id.action_back);
		setContentView(R.layout.activity_reset_password);
		layout_background = findViewById(R.id.layout_background);
		edt_email = findViewById(R.id.edt_email);
		findViewById(R.id.btn_reset_password).setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		layout_background.setBackgroundColor(CommonUtil.getMainColor());
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		CommonUtil.hideKeyboard(instance, edt_email);
		super.onClick(view);
		switch (view.getId()) {
			case R.id.btn_reset_password:
				 if (isValid() && DeviceUtil.isNetworkAvailable(instance))
					 resetPassword();
			break;
		}
	}

	private boolean isValid() {
		String strEmail = edt_email.getText().toString().trim();
		if (TextUtils.isEmpty(strEmail)) {
			MessageUtil.showError(instance, R.string.valid_No_email);
			edt_email.requestFocus();
			return false;
		}
		if (!CommonUtil.isValidEmail(strEmail)) {
			MessageUtil.showError(instance, R.string.valid_Invalid_email);
			edt_email.requestFocus();
			return false;
		}
		return true;
	}

	private void resetPassword() {
		final String strEmail = edt_email.getText().toString().trim();
		dlg_progress.show();
		UserModel.ResetPassword(strEmail, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					showSuccessDialog();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}

	private void showSuccessDialog() {
		new AlertDialog.Builder(instance)
				.setTitle(R.string.Success)
				.setMessage(R.string.success_forgot_password)
				.setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						myBack();
					}
				})
				.show();
	}
}
