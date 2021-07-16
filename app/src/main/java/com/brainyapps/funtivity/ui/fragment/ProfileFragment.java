package com.brainyapps.funtivity.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brainyapps.funtivity.AppConstant;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.BooleanListener;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.FileModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.activity.ActivitesActivity;
import com.brainyapps.funtivity.ui.activity.EditProfileActivity;
import com.brainyapps.funtivity.ui.activity.MainActivity;
import com.brainyapps.funtivity.ui.activity.MeetupListActivity;
import com.brainyapps.funtivity.ui.activity.ThemeActivity;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.brainyapps.funtivity.utils.ResourceUtil;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
	public static ProfileFragment instance;
	LinearLayout layout_invitations;
	LinearLayout layout_messages;
	ImageView img_photo;
	CircleImageView img_avatar;
	TextView txt_email;
	TextView txt_name;
	TextView txt_sex;
	TextView txt_address;
	TextView txt_activities;
	TextView txt_outdoor;

	MainActivity mActivity;

	public static ProfileFragment newInstance() {
		ProfileFragment fragment = new ProfileFragment();
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
		mActivity = MainActivity.instance;
		instance = this;
	}

	@Override
	public void onResume() {
		super.onResume();
		layout_invitations.setBackgroundColor(CommonUtil.getMainLightColor());
		layout_messages.setBackgroundColor(CommonUtil.getMainLightColor());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_profile, container, false);
		layout_invitations = mView.findViewById(R.id.layout_invitations);
		layout_messages = mView.findViewById(R.id.layout_messages);
		img_photo = mView.findViewById(R.id.img_photo);
		img_avatar = mView.findViewById(R.id.img_avatar);
		txt_email = mView.findViewById(R.id.txt_email);
		txt_name = mView.findViewById(R.id.txt_name);
		txt_sex = mView.findViewById(R.id.txt_sex);
		txt_address = mView.findViewById(R.id.txt_address);
		txt_activities = mView.findViewById(R.id.txt_activities);
		txt_outdoor = mView.findViewById(R.id.txt_outdoor);
		mView.findViewById(R.id.btn_edit).setOnClickListener(this);
		mView.findViewById(R.id.btn_camera).setOnClickListener(this);
		mView.findViewById(R.id.btn_theme).setOnClickListener(this);
		mView.findViewById(R.id.btn_activities).setOnClickListener(this);
		mView.findViewById(R.id.btn_outdoor).setOnClickListener(this);
		mView.findViewById(R.id.layout_invitations).setOnClickListener(this);
		mView.findViewById(R.id.layout_messages).setOnClickListener(this);
		initialize();
		return mView;
	}

	public void initialize() {
		txt_email.setText(AppGlobals.mCurrentUserModel.email);
		String name = UserModel.GetFullName(AppGlobals.mCurrentUserModel);
		if (AppGlobals.mCurrentUserModel.age > 0)
			name = name + ", " + AppGlobals.mCurrentUserModel.age;
		txt_name.setText(name);
		txt_address.setText(AppGlobals.mCurrentUserModel.address);
		setActivities();
		img_avatar.setImageResource(R.drawable.default_profile);
		if (!TextUtils.isEmpty(AppGlobals.mCurrentUserModel.avatar)) {
			Picasso.get().load(AppGlobals.mCurrentUserModel.avatar).into(img_photo);
			Picasso.get().load(AppGlobals.mCurrentUserModel.avatar).into(img_avatar);
		}
	}

	public void setActivities() {
		String activities = "";
		String outdoor = "";
		for (int i = 0; i < AppGlobals.mCurrentUserModel.activities.size(); i ++) {
			int index = Integer.parseInt(String.valueOf(AppGlobals.mCurrentUserModel.activities.get(i)));
			if (i == 0)
				activities = AppConstant.CATEGORY_ARRAY[index];
			else
				activities = activities + ", " + AppConstant.CATEGORY_ARRAY[index];
		}
		txt_activities.setText(activities);
		for (int i = 0; i < AppGlobals.mCurrentUserModel.outdoor.size(); i ++) {
			int index = Integer.parseInt(String.valueOf(AppGlobals.mCurrentUserModel.outdoor.get(i)));
			if (i == 0)
				outdoor = AppConstant.OUTDOOR_ARRAY[index];
			else
				outdoor = outdoor + ", " + AppConstant.OUTDOOR_ARRAY[index];
		}
		txt_outdoor.setText(outdoor);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_edit:
				startActivity(new Intent(mActivity, EditProfileActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.btn_camera:
				if (CommonUtil.verifyStoragePermissions(CommonUtil.TYPE_CAMERA_PERMISSION, mActivity))
					showPhotoDialog();
				else
					MessageUtil.showError(mActivity, R.string.msg_error_permission);
				break;
			case R.id.btn_theme:
				startActivity(new Intent(mActivity, ThemeActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.btn_activities:
				ActivitesActivity.isOutdoor = false;
				startActivity(new Intent(mActivity, ActivitesActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.btn_outdoor:
				ActivitesActivity.isOutdoor = true;
				startActivity(new Intent(mActivity, ActivitesActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.layout_invitations:
				startActivity(new Intent(mActivity, MeetupListActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.layout_messages:
				if (MainActivity.instance != null)
					MainActivity.instance.SwitchContent(AppConstant.SW_FRAGMENT_MAIN_MESSAGES, null);
				break;
		}
	}

	private void showPhotoDialog() {
		new AlertDialog.Builder(mActivity)
				.setTitle(R.string.upload_photo)
				.setPositiveButton(R.string.select_gallery, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MainActivity.instance.chooseTakePhoto(false);
					}
				})
				.setNegativeButton(R.string.take_new_photo, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MainActivity.instance.chooseTakePhoto(true);
					}
				})
				.show();
	}

	public void setImage(Bitmap bitmap) {
		img_avatar.setImageDrawable(new BitmapDrawable(bitmap));
		img_photo.setImageDrawable(new BitmapDrawable(bitmap));
		mActivity.dlg_progress.show();
		FileModel.UploadAvatar(ResourceUtil.getAvatarFilePath(), new BooleanListener() {
			@Override
			public void done(boolean flag, String error) {
				if (flag) {
					AppGlobals.mCurrentUserModel.avatar = error;
					UserModel.UpdateAvatar(error, new ExceptionListener() {
						@Override
						public void done(String error) {
							mActivity.dlg_progress.cancel();
							if (error == null)
								MessageUtil.showToast(mActivity, R.string.Success);
							else
								MessageUtil.showToast(mActivity, error);
						}
					});
				} else {
					mActivity.dlg_progress.cancel();
					MessageUtil.showToast(mActivity, error);
				}
			}
		});
	}

	@Override
	public void onRefresh() {}
}
