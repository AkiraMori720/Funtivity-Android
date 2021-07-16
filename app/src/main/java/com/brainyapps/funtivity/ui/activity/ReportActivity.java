package com.brainyapps.funtivity.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.MeetupModel;
import com.brainyapps.funtivity.model.ReportModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;

public class ReportActivity extends BaseActionBarActivity {
	public static ReportActivity instance = null;
	EditText edt_content;

	public static UserModel mUser;
	public static MeetupModel mMeetupModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
        SetTitle(R.string.report, 0);
		ShowActionBarIcons(true, R.id.action_back);
		setContentView(R.layout.activity_report);
		edt_content = findViewById(R.id.edt_content);
		findViewById(R.id.btn_post).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		CommonUtil.hideKeyboard(instance, edt_content);
		super.onClick(view);
		switch (view.getId()) {
			case R.id.btn_post:
				if (isValid() && DeviceUtil.isNetworkAvailable(instance))
					register();
				break;
		}
	}

	private boolean isValid() {
		String message = edt_content.getText().toString().trim();
		if (TextUtils.isEmpty(message)) {
			MessageUtil.showError(instance, R.string.valid_No_text);
			edt_content.requestFocus();
			return false;
		}
		return true;
	}

	private void register() {
		final String message = edt_content.getText().toString().trim();
		ReportModel model = new ReportModel();
		model.userId = AppGlobals.currentUser.getUid();
		model.reporterId = mUser.userId;
		model.message = message;
		if (mMeetupModel != null)
			model.meetupId = mMeetupModel.documentId;
		dlg_progress.show();
		ReportModel.Register(model, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
					myBack();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}
}
