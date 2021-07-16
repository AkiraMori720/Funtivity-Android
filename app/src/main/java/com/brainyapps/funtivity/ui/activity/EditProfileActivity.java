package com.brainyapps.funtivity.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.BooleanListener;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.FileModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.fragment.ProfileFragment;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.brainyapps.funtivity.utils.ResourceUtil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;

public class EditProfileActivity extends BaseActionBarActivity {
	public static EditProfileActivity instance = null;
	View view_background;
	CircleImageView img_avatar;
	EditText edt_first_name;
	EditText edt_last_name;
	EditText edt_interests;
	EditText edt_location;
	EditText edt_age;
	EditText edt_bio;
	TextView txt_upload;

	final int PICTURE_PICK = 1000;
	final int CAMERA_CAPTURE = 1001;
	Bitmap mOrgBmp = null;
	boolean isPhotoAdded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		SetTitle(R.string.edit_profile, -1);
		ShowActionBarIcons(true, R.id.action_back);
		setContentView(R.layout.activity_edit_profile);
		view_background = findViewById(R.id.view_background);
		img_avatar = findViewById(R.id.img_avatar);
		edt_first_name = findViewById(R.id.edt_first_name);
		edt_last_name = findViewById(R.id.edt_last_name);
		edt_interests = findViewById(R.id.edt_interests);
		edt_location = findViewById(R.id.edt_location);
		edt_age = findViewById(R.id.edt_age);
		edt_bio = findViewById(R.id.edt_bio);
		txt_upload = findViewById(R.id.txt_upload);

		findViewById(R.id.layout_upload).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);

		initialize();
	}

	private void initialize() {
		txt_upload.setText(Html.fromHtml(getString(R.string.upload_photo_)));
		img_avatar.setImageResource(R.drawable.ic_upload_photo);
		edt_first_name.setText(AppGlobals.mCurrentUserModel.firstName);
		edt_last_name.setText(AppGlobals.mCurrentUserModel.lastName);
		edt_interests.setText(AppGlobals.mCurrentUserModel.interests);
		edt_location.setText(AppGlobals.mCurrentUserModel.address);
		if (AppGlobals.mCurrentUserModel.age > 0)
			edt_age.setText(String.valueOf(AppGlobals.mCurrentUserModel.age));
		edt_bio.setText(AppGlobals.mCurrentUserModel.bio);
		if (!TextUtils.isEmpty(AppGlobals.mCurrentUserModel.avatar))
			Picasso.get().load(AppGlobals.mCurrentUserModel.avatar).into(img_avatar);
	}

	@Override
	public void onResume() {
		super.onResume();
		view_background.setBackgroundColor(CommonUtil.getMainColor());
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		CommonUtil.hideKeyboard(instance, edt_first_name);
		switch (view.getId()) {
			case R.id.layout_upload:
				if (CommonUtil.verifyStoragePermissions(CommonUtil.TYPE_CAMERA_PERMISSION, this))
					showPhotoDialog();
				else
					MessageUtil.showError(instance, R.string.msg_error_permission);
				break;
			case R.id.btn_save:
				if (isValid() && DeviceUtil.isNetworkAvailable(instance))
					uploadAvatar();
				break;
		}
	}

	private void showPhotoDialog() {
		new AlertDialog.Builder(instance)
				.setTitle(R.string.upload_photo)
				.setPositiveButton(R.string.select_gallery, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						chooseTakePhoto(false);
					}
				})
				.setNegativeButton(R.string.take_new_photo, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						chooseTakePhoto(true);
					}
				})
				.show();
	}


	private void chooseTakePhoto(boolean isTake) {
		if (!isTake) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, PICTURE_PICK);
		} else {
			try {
				Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File(ResourceUtil.getAvatarFilePath());
				if (file.exists())
					file.delete();
				Uri photoURI = FileProvider.getUriForFile(instance, getApplicationContext().getPackageName() + ".provider", file);
				captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(captureIntent, CAMERA_CAPTURE);
			} catch (ActivityNotFoundException anfe) {
				String errorMessage = "Whoops - your device doesn't support capturing images!";
				Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	@SuppressLint("MissingSuperCall")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!CommonUtil.verifyStoragePermissions(CommonUtil.TYPE_CAMERA_PERMISSION, instance)) {
			MessageUtil.showToast(instance, R.string.msg_error_permission);
			return;
		}
		if (requestCode == PICTURE_PICK && resultCode == Activity.RESULT_OK) {
			Uri imageUri = CropImage.getPickImageResultUri(this, data);
			startCropImageActivity(imageUri);
		}

		if (requestCode == CAMERA_CAPTURE && resultCode == Activity.RESULT_OK) {
			File file = new File(ResourceUtil.getAvatarFilePath());
			Uri photoURI = FileProvider.getUriForFile(instance, getApplicationContext().getPackageName() + ".provider", file);
			startCropImageActivity(photoURI);
		}

		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				String strFileName = ResourceUtil.getAvatarFilePath();
				try {
					Bitmap bm = ResourceUtil.decodeUri(instance, result.getUri(), FileModel.AVATAR_SIZE);
					if (bm != null) {
						ResourceUtil.saveBitmapToSdcard(bm, strFileName);
						if (mOrgBmp != null)
							mOrgBmp.recycle();
						mOrgBmp = bm;
						img_avatar.setImageDrawable(new BitmapDrawable(mOrgBmp));
						isPhotoAdded = true;
					} else {
						Log.i(getString(R.string.app_name), "Bitmap is null");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				MessageUtil.showError(instance, "Cropping failed: " + result.getError());
			}
		}
	}

	private void startCropImageActivity(Uri imageUri) {
		CropImage.activity(imageUri)
				.setGuidelines(CropImageView.Guidelines.ON)
				.setMultiTouchEnabled(true)
				.setAspectRatio(1, 1)
				.start(this);
	}

	private boolean isValid() {
		String first_name = edt_first_name.getText().toString().trim();
		String last_name = edt_last_name.getText().toString().trim();
		String interests = edt_interests.getText().toString().trim();
		String location = edt_location.getText().toString();
		String age = edt_age.getText().toString();
		String bio = edt_bio.getText().toString();
		if (TextUtils.isEmpty(first_name)) {
			MessageUtil.showError(instance, R.string.valid_No_first_name);
			edt_first_name.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(last_name)) {
			MessageUtil.showError(instance, R.string.valid_No_last_name);
			edt_last_name.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(interests)) {
			MessageUtil.showError(instance, R.string.valid_No_interests);
			edt_interests.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(location)) {
			MessageUtil.showError(instance, R.string.valid_No_location);
			return false;
		}
		if (TextUtils.isEmpty(age)) {
			MessageUtil.showError(instance, R.string.valid_No_age);
			edt_age.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(bio)) {
			MessageUtil.showError(instance, R.string.valid_No_bio);
			edt_bio.requestFocus();
			return false;
		}
		return true;
	}

	private void uploadAvatar(){
		dlg_progress.show();
		if (isPhotoAdded) {
			FileModel.UploadAvatar(ResourceUtil.getAvatarFilePath(), new BooleanListener() {
				@Override
				public void done(boolean flag, String error) {
					if (flag) {
						AppGlobals.mCurrentUserModel.avatar = error;
						save();
					} else {
						dlg_progress.cancel();
						MessageUtil.showToast(instance, error);
					}
				}
			});
		} else {
			save();
		}
	}

	public void save() {
		AppGlobals.mCurrentUserModel.firstName = edt_first_name.getText().toString().trim();
		AppGlobals.mCurrentUserModel.lastName = edt_last_name.getText().toString().trim();
		AppGlobals.mCurrentUserModel.interests = edt_interests.getText().toString().trim();
		AppGlobals.mCurrentUserModel.address = edt_location.getText().toString().trim();
		AppGlobals.mCurrentUserModel.age = Integer.parseInt(edt_age.getText().toString().trim());
		AppGlobals.mCurrentUserModel.bio = edt_bio.getText().toString().trim();
		UserModel.UpdateProfile(new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
					if (MainActivity.instance != null)
						MainActivity.instance.initialize();
					if (ProfileFragment.instance != null)
						ProfileFragment.instance.initialize();
					myBack();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}
}
