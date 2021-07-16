package com.brainyapps.funtivity.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.MeetupModel;
import com.brainyapps.funtivity.model.NotificationModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.fragment.HomeFragment;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.ui.view.MySquareImageView;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DateTimeUtils;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MeetupMyActivity extends BaseActionBarActivity {
	public static MeetupMyActivity instance = null;
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
	TextView txt_join;
	LinearLayout layout_join;
	TextView txt_interested;
	LinearLayout layout_interested;

	public static MeetupModel mMeetupModel;
	List<ShowModel> mDataList = new ArrayList<>();
	boolean isChanged = false;

	class ShowModel {
		String key = "";
		UserModel userModel;
		int state = 0;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		SetTitle(R.string.app_name, -1);
		ShowActionBarIcons(true, R.id.action_back, R.id.action_edit);
		setContentView(R.layout.activity_meetup_my);
		txt_title = findViewById(R.id.txt_title);
		txt_location = findViewById(R.id.txt_location);
		img_photo_a = findViewById(R.id.img_photo_a);
		img_photo_b = findViewById(R.id.img_photo_b);
		img_photo_c = findViewById(R.id.img_photo_c);
		txt_description = findViewById(R.id.txt_description);
		img_avatar = findViewById(R.id.img_avatar);
		txt_name = findViewById(R.id.txt_name);
		txt_creator = findViewById(R.id.txt_creator);
		txt_join = findViewById(R.id.txt_join);
		layout_join = findViewById(R.id.layout_join);
		txt_interested = findViewById(R.id.txt_interested);
		layout_interested = findViewById(R.id.layout_interested);

		initialize();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
			case R.id.action_edit:
				MeetupRegisterActivity.mMeetupModel = mMeetupModel;
				startActivity(new Intent(instance, MeetupRegisterActivity.class));
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		txt_join.setBackgroundColor(CommonUtil.getMainColor());
		txt_interested.setBackgroundColor(CommonUtil.getMainColor());
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isChanged && HomeFragment.instance != null)
			HomeFragment.instance.list_meetup.refresh();
	}

	public void initialize() {
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
		showInterested();
		getServerData();
	}

	private void showInterested() {
		layout_interested.removeAllViews();

		for (int i = 0; i < mMeetupModel.interestedUsers.size(); i ++) {
			UserModel model = UserModel.GetUserModel(mMeetupModel.interestedUsers.get(i));
			LinearLayout layer = (LinearLayout) LayoutInflater.from(instance).inflate(R.layout.item_users, null);
			CircleImageView img_avatar = layer.findViewById(R.id.img_avatar);
			TextView txt_name = layer.findViewById(R.id.txt_name);
			ImageView btn_accept = layer.findViewById(R.id.btn_accept);
			ImageView btn_decline = layer.findViewById(R.id.btn_decline);
			ImageView btn_more = layer.findViewById(R.id.btn_more);

			txt_name.setText(UserModel.GetFullName(model));
			img_avatar.setImageResource(R.drawable.default_profile);
			if (!TextUtils.isEmpty(model.avatar))
				Picasso.get().load(model.avatar).into(img_avatar);
			btn_accept.setVisibility(View.GONE);
			btn_decline.setVisibility(View.GONE);
			btn_more.setVisibility(View.GONE);

			layout_interested.addView(layer);
		}
	}

	private void getServerData() {
		mDataList.clear();
		for (int i = 0; i < mMeetupModel.joinUsers.size(); i ++) {
			String[] key = mMeetupModel.joinUsers.get(i).split("-----");
			ShowModel model = new ShowModel();
			model.key = mMeetupModel.joinUsers.get(i);
			model.userModel = UserModel.GetUserModel(key[0]);
			model.state = Integer.parseInt(key[1]);
			mDataList.add(model);
		}
		showRequests();
	}

	private void showRequests() {
		layout_join.removeAllViews();

		for (int i = 0; i < mDataList.size(); i ++) {
			final int position = i;
			LinearLayout layer = (LinearLayout) LayoutInflater.from(instance).inflate(R.layout.item_users, null);
			CircleImageView img_avatar = layer.findViewById(R.id.img_avatar);
			TextView txt_name = layer.findViewById(R.id.txt_name);
			ImageView btn_accept = layer.findViewById(R.id.btn_accept);
			ImageView btn_decline = layer.findViewById(R.id.btn_decline);
			ImageView btn_more = layer.findViewById(R.id.btn_more);

			txt_name.setText(UserModel.GetFullName(mDataList.get(position).userModel));
			img_avatar.setImageResource(R.drawable.default_profile);
			if (!TextUtils.isEmpty(mDataList.get(position).userModel.avatar))
				Picasso.get().load(mDataList.get(position).userModel.avatar).into(img_avatar);
			btn_accept.setVisibility(View.GONE);
			btn_decline.setVisibility(View.GONE);
			btn_more.setVisibility(View.GONE);
			if (mDataList.get(position).state == MeetupModel.STATE_PENDING) {
				btn_accept.setVisibility(View.VISIBLE);
				btn_decline.setVisibility(View.VISIBLE);
			}
			btn_accept.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setState(mDataList.get(position).userModel, MeetupModel.STATE_ACCEPTED);
				}
			});
			btn_decline.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setState(mDataList.get(position).userModel, MeetupModel.STATE_DECLINED);
				}
			});
			layout_join.addView(layer);
		}
	}

	private void setState(UserModel selectedUser, int state) {
		List<String> newList = new ArrayList<>();
		for (int i = 0; i < mDataList.size(); i ++) {
			if (!mDataList.get(i).userModel.userId.equals(selectedUser.userId))
				newList.add(mDataList.get(i).key);
		}
		if (state == MeetupModel.STATE_ACCEPTED)
			newList.add(selectedUser.userId + "-----" + state);
		dlg_progress.show();
		MeetupModel.UpdateState(false, mMeetupModel.documentId, state, newList, selectedUser.userId, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					isChanged = true;
					MessageUtil.showToast(instance, R.string.Success);
					NotificationModel model = new NotificationModel();
					model.type = NotificationModel.TYPE_MEET_UP;
					model.state = state;
					model.sender = AppGlobals.currentUser.getUid();
					model.receiver = selectedUser.userId;
					model.meetupId = mMeetupModel.documentId;
					model.message = String.format(getString(R.string.notification_join_accepted), UserModel.GetFullName(AppGlobals.mCurrentUserModel));
					if (state == NotificationModel.STATE_DECLINE)
						model.message = String.format(getString(R.string.notification_join_declined), UserModel.GetFullName(AppGlobals.mCurrentUserModel));
					NotificationModel.Register(model, mMeetupModel.ownerModel.token, null);

					mMeetupModel.joinUsers.clear();
					mMeetupModel.joinUsers.addAll(newList);
					getServerData();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}
}
