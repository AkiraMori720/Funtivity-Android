package com.brainyapps.funtivity.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.MeetupModel;
import com.brainyapps.funtivity.model.NotificationModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.dialog.MyProgressDialog;
import com.brainyapps.funtivity.ui.fragment.HomeFragment;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.ui.view.MySquareImageView;
import com.brainyapps.funtivity.utils.DateTimeUtils;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MeetupInterestActivity extends Activity implements View.OnClickListener {
	public static MeetupInterestActivity instance = null;
	// UI
	TextView txt_title;
	TextView txt_location;
	MySquareImageView img_photo_a;
	MySquareImageView img_photo_b;
	MySquareImageView img_photo_c;
	TextView txt_description;
	CircleImageView img_avatar;
	TextView txt_name;
	TextView txt_creator;
	Button btn_interested;
	Button btn_going;

	public static MeetupModel mMeetupModel;
	MyProgressDialog dlg_progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_meetup_interest);
		txt_title = findViewById(R.id.txt_title);
		txt_location = findViewById(R.id.txt_location);
		img_photo_a = findViewById(R.id.img_photo_a);
		img_photo_b = findViewById(R.id.img_photo_b);
		img_photo_c = findViewById(R.id.img_photo_c);
		txt_description = findViewById(R.id.txt_description);
		img_avatar = findViewById(R.id.img_avatar);
		txt_name = findViewById(R.id.txt_name);
		txt_creator = findViewById(R.id.txt_creator);

		btn_interested = findViewById(R.id.btn_interested);
		btn_going = findViewById(R.id.btn_going);

		findViewById(R.id.layout_profile).setOnClickListener(this);
		findViewById(R.id.btn_interested).setOnClickListener(this);
		findViewById(R.id.btn_going).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		dlg_progress = new MyProgressDialog(this);
		initialize();
	}

	private void initialize() {
		txt_title.setText(mMeetupModel.meetupName);
		txt_location.setText(mMeetupModel.location + " - " + DateTimeUtils.dateToString(mMeetupModel.date, DateTimeUtils.DATE_TIME_STRING_FORMAT));
		img_photo_a.showImage(mMeetupModel.photoA);
		img_photo_b.showImage(mMeetupModel.photoB);
		img_photo_c.showImage(mMeetupModel.photoC);
		txt_description.setText(mMeetupModel.description);
		txt_name.setText(UserModel.GetFullName(mMeetupModel.ownerModel));
		img_avatar.setImageResource(R.drawable.default_profile);
		if (!TextUtils.isEmpty(mMeetupModel.ownerModel.avatar))
			Picasso.get().load(mMeetupModel.ownerModel.avatar).into(img_avatar);
		btn_interested.setVisibility(View.GONE);
		btn_going.setVisibility(View.GONE);

		if (mMeetupModel.date.getTime() >= DateTimeUtils.getStartDate(Calendar.getInstance().getTime()).getTime()) {
			if (!MeetupModel.isInterested(mMeetupModel.interestedUsers, AppGlobals.currentUser.getUid()))
				btn_interested.setVisibility(View.VISIBLE);
			String joinState = MeetupModel.getJoinedString(mMeetupModel.joinUsers, AppGlobals.currentUser.getUid());
			if (TextUtils.isEmpty(joinState))
				btn_going.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.layout_profile:
				ProfileActivity.mUserModel = mMeetupModel.ownerModel;
				startActivity(new Intent(instance, ProfileActivity.class));
				break;
			case R.id.btn_interested:
				if (DeviceUtil.isNetworkAvailable(instance))
					interested();
				break;
			case R.id.btn_going:
				if (DeviceUtil.isNetworkAvailable(instance))
					sendRequest();
				break;
			case R.id.btn_cancel:
				finish();
				break;
		}
	}

	private void interested() {
		List<String> newList = new ArrayList<>();
		for (int i = 0; i < mMeetupModel.interestedUsers.size(); i ++) {
			if (!mMeetupModel.interestedUsers.get(i).equals(AppGlobals.currentUser.getUid()))
				newList.add(mMeetupModel.interestedUsers.get(i));
		}
		newList.add(AppGlobals.currentUser.getUid());
		dlg_progress.show();
		MeetupModel.UpdateState(true, mMeetupModel.documentId, MeetupModel.STATE_PENDING, newList, AppGlobals.currentUser.getUid(), new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
					NotificationModel model = new NotificationModel();
					model.type = NotificationModel.TYPE_MEET_UP;
					model.state = NotificationModel.STATE_PENDING;
					model.sender = AppGlobals.currentUser.getUid();
					model.receiver = mMeetupModel.ownerModel.userId;
					model.meetupId = mMeetupModel.documentId;
					model.message = String.format(getString(R.string.notification_interested_event), UserModel.GetFullName(AppGlobals.mCurrentUserModel));
					NotificationModel.Register(model, mMeetupModel.ownerModel.token, null);

					if (HomeFragment.instance != null)
						HomeFragment.instance.list_meetup.refresh();
					finish();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}

	private void sendRequest() {
		List<String> newList = new ArrayList<>();
		for (int i = 0; i < mMeetupModel.joinUsers.size(); i ++) {
			if (mMeetupModel.joinUsers.get(i).indexOf(AppGlobals.currentUser.getUid()) == -1)
				newList.add(mMeetupModel.joinUsers.get(i));
		}
		newList.add(AppGlobals.currentUser.getUid() + "-----" + MeetupModel.STATE_PENDING);
		dlg_progress.show();
		MeetupModel.UpdateState(false, mMeetupModel.documentId, MeetupModel.STATE_PENDING, newList, AppGlobals.currentUser.getUid(), new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
					NotificationModel model = new NotificationModel();
					model.type = NotificationModel.TYPE_MEET_UP;
					model.state = NotificationModel.STATE_PENDING;
					model.sender = AppGlobals.currentUser.getUid();
					model.receiver = mMeetupModel.ownerModel.userId;
					model.meetupId = mMeetupModel.documentId;
					model.message = String.format(getString(R.string.notification_join_request), UserModel.GetFullName(AppGlobals.mCurrentUserModel));
					NotificationModel.Register(model, mMeetupModel.ownerModel.token, null);

					if (HomeFragment.instance != null)
						HomeFragment.instance.list_meetup.refresh();
					finish();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}
}
