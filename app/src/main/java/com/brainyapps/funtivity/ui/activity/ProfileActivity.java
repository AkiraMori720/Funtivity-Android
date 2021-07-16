package com.brainyapps.funtivity.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brainyapps.funtivity.AppConstant;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.NotificationModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.fragment.FriendsFragment;
import com.brainyapps.funtivity.ui.fragment.SearchFragment;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends BaseActionBarActivity {
	public static ProfileActivity instance = null;
	ImageView img_photo;
	CircleImageView img_avatar;
	TextView txt_email;
	TextView txt_name;
	TextView txt_sex;
	TextView txt_address;
	TextView txt_activities;
	TextView txt_interests;
	LinearLayout layout_add;
	LinearLayout layout_remove;

	public static UserModel mUserModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		SetTitle(R.string.profile, -1);
		ShowActionBarIcons(true, R.id.action_back, R.id.action_report);
		setContentView(R.layout.activity_profile);
		img_photo = findViewById(R.id.img_photo);
		img_avatar = findViewById(R.id.img_avatar);
		txt_email = findViewById(R.id.txt_email);
		txt_name = findViewById(R.id.txt_name);
		txt_sex = findViewById(R.id.txt_sex);
		txt_address = findViewById(R.id.txt_address);
		txt_activities = findViewById(R.id.txt_activities);
		txt_interests = findViewById(R.id.txt_interests);
		layout_add = findViewById(R.id.layout_add);
		layout_remove = findViewById(R.id.layout_remove);
		findViewById(R.id.layout_add).setOnClickListener(this);
		findViewById(R.id.layout_remove).setOnClickListener(this);
		findViewById(R.id.layout_message).setOnClickListener(this);
		initialize();
	}

	private void initialize() {
		txt_email.setText(mUserModel.email);
		String name = UserModel.GetFullName(mUserModel);
		if (mUserModel.age > 0)
			name = name + ", " + mUserModel.age;
		txt_name.setText(name);
		txt_address.setText(mUserModel.address);
		img_avatar.setImageResource(R.drawable.default_profile);
		if (!TextUtils.isEmpty(mUserModel.avatar)) {
			Picasso.get().load(mUserModel.avatar).into(img_photo);
			Picasso.get().load(mUserModel.avatar).into(img_avatar);
		}
		txt_interests.setText(mUserModel.interests);
		String activity = "";
		for (int i = 0; i < mUserModel.activities.size(); i ++) {
			int index = Integer.parseInt(String.valueOf(mUserModel.activities.get(i)));
			if (i == 0)
				activity = AppConstant.CATEGORY_ARRAY[index];
			else
				activity = activity + ", " + AppConstant.CATEGORY_ARRAY[index];
		}
		txt_activities.setText(activity);
		layout_add.setVisibility(View.GONE);
		layout_remove.setVisibility(View.GONE);
		if (UserModel.isFriend(mUserModel))
			layout_remove.setVisibility(View.VISIBLE);
		else
			layout_add.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
			case R.id.action_report:
				ReportActivity.mUser = mUserModel;
				ReportActivity.mMeetupModel = null;
				startActivity(new Intent(instance, ReportActivity.class));
				break;
			case R.id.layout_add:
				if (DeviceUtil.isNetworkAvailable(instance))
					addFriend();
				break;
			case R.id.layout_remove:
				if (DeviceUtil.isNetworkAvailable(instance))
					removeFriend();
				break;
			case R.id.layout_message:
				ChatActivity.mFriendModel = mUserModel;
				startActivity(new Intent(instance, ChatActivity.class));
				break;
		}
	}

	private void addFriend() {
		NotificationModel model = new NotificationModel();
		model.type = NotificationModel.TYPE_FRIEND;
		model.state = NotificationModel.STATE_ACCEPT;
		model.sender = AppGlobals.currentUser.getUid();
		model.receiver = mUserModel.userId;
		model.message = String.format(getString(R.string.notification_friend_request), UserModel.GetFullName(AppGlobals.mCurrentUserModel));
		dlg_progress.show();
		NotificationModel.Register(model, mUserModel.token, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}

	private void removeFriend() {
		dlg_progress.show();
		UserModel.UpdateFriend(mUserModel, false, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
					NotificationModel model = new NotificationModel();
					model.type = NotificationModel.TYPE_FRIEND;
					model.state = NotificationModel.STATE_REMOVE;
					model.sender = AppGlobals.currentUser.getUid();
					model.receiver = mUserModel.userId;
					model.message = String.format(getString(R.string.notification_friend_remove), UserModel.GetFullName(AppGlobals.mCurrentUserModel));
					NotificationModel.sendPush(model, mUserModel.token, null);
					if (FriendsFragment.instance != null)
						FriendsFragment.instance.list_users.refresh();
					if (SearchFragment.instance != null)
						SearchFragment.instance.list_users.refresh();
					finish();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}
}
