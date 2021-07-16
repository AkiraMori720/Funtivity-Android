package com.brainyapps.funtivity.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.BooleanListener;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.FileModel;
import com.brainyapps.funtivity.model.MeetupModel;
import com.brainyapps.funtivity.model.ReviewModel;
import com.brainyapps.funtivity.ui.view.MySquareImageView;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.brainyapps.funtivity.utils.ResourceUtil;
import com.hedgehog.ratingbar.RatingBar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;

public class ReviewActivity extends BaseActionBarActivity {
	public static ReviewActivity instance = null;
	// UI
	RatingBar rating_bar;
	EditText edt_message;
	MySquareImageView img_photo;

	final int PICTURE_PICK = 1000;
	final int CAMERA_CAPTURE = 1001;
	Bitmap mOrgBmp = null;
	boolean isPhotoAdded = false;
	int rate = 0;
	public static MeetupModel mMeetupModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		SetTitle(R.string.write_review, -1);
		ShowActionBarIcons(true, R.id.action_back);
		setContentView(R.layout.activity_review);
		rating_bar = findViewById(R.id.rating_bar);
		edt_message = findViewById(R.id.edt_message);
		img_photo = findViewById(R.id.img_photo);
		findViewById(R.id.txt_upload).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		rating_bar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
			@Override
			public void onRatingChange(float RatingCount) {
				rate = (int) RatingCount;
			}
		});
		img_photo.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
			case R.id.txt_upload:
				if (CommonUtil.verifyStoragePermissions(CommonUtil.TYPE_CAMERA_PERMISSION, this))
					showPhotoDialog();
				else
					MessageUtil.showError(instance, R.string.msg_error_permission);
				break;
			case R.id.btn_save:
				if (isValid() && DeviceUtil.isNetworkAvailable(instance))
					uploadPhoto();
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
				File file = new File(ResourceUtil.getPhotoFilePath());
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
			File file = new File(ResourceUtil.getPhotoFilePath());
			Uri photoURI = FileProvider.getUriForFile(instance, getApplicationContext().getPackageName() + ".provider", file);
			startCropImageActivity(photoURI);
		}

		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				String strFileName = ResourceUtil.getPhotoFilePath();
				try {
					Bitmap bm = ResourceUtil.decodeUri(instance, result.getUri(), FileModel.PHOTO_SIZE);
					if (bm != null) {
						ResourceUtil.saveBitmapToSdcard(bm, strFileName);
						if (mOrgBmp != null)
							mOrgBmp.recycle();
						mOrgBmp = bm;
						img_photo.setVisibility(View.VISIBLE);
						img_photo.setImageBitmap(mOrgBmp);
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
				.setAspectRatio(4, 3)
				.start(this);
	}

	private boolean isValid() {
		String message = edt_message.getText().toString().trim();
		if (rate == 0) {
			MessageUtil.showError(instance, R.string.valid_No_rate);
			return false;
		}
		if (TextUtils.isEmpty(message)) {
			MessageUtil.showError(instance, R.string.valid_No_message);
			edt_message.requestFocus();
			return false;
		}
		return true;
	}

	private void uploadPhoto(){
		dlg_progress.show();
		if (isPhotoAdded) {
			FileModel.UploadPhoto(ResourceUtil.getPhotoFilePath(), new BooleanListener() {
				@Override
				public void done(boolean flag, String error) {
					if (flag) {
						register(error);
					} else {
						dlg_progress.cancel();
						MessageUtil.showToast(instance, error);
					}
				}
			});
		} else {
			register("");
		}
	}

	public void register(String photo) {
		ReviewModel model = new ReviewModel();
		model.userId = AppGlobals.currentUser.getUid();
		model.meetupId = mMeetupModel.documentId;
		model.rating = rate;
		model.message = edt_message.getText().toString().trim();
		model.photo = photo;

		ReviewModel.Register(model, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
					if (MeetupDetailActivity.instance != null)
						MeetupDetailActivity.instance.getServerData();
					myBack();
				} else {
					MessageUtil.showError(instance, error);
				}
			}
		});
	}
}
