package com.brainyapps.funtivity.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.brainyapps.funtivity.AppConstant;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.BooleanListener;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.FileModel;
import com.brainyapps.funtivity.model.MeetupModel;
import com.brainyapps.funtivity.ui.dialog.MyProgressDialog;
import com.brainyapps.funtivity.ui.fragment.HomeFragment;
import com.brainyapps.funtivity.ui.view.MySquareImageView;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DateTimeUtils;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.brainyapps.funtivity.utils.ResourceUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;

public class MeetupRegisterActivity extends Activity implements View.OnClickListener {
	public static MeetupRegisterActivity instance = null;
	// UI
	TextView txt_title;
	EditText edt_name;
	TextView txt_category;
	EditText edt_location;
	TextView txt_date;
	TextView txt_time;
	EditText edt_guests;
	TextView txt_sex;
	EditText edt_description;
	TextView txt_friend;
	RangeSeekBar seek_age;
	TextView txt_start;
	TextView txt_end;
	RelativeLayout layout_photo_a;
	RelativeLayout layout_photo_b;
	RelativeLayout layout_photo_c;
	MySquareImageView img_photo_a;
	MySquareImageView img_photo_b;
	MySquareImageView img_photo_c;
	Button btn_create;
	Button btn_save;

	public static MeetupModel mMeetupModel;
	final int PICTURE_PICK = 1000;
	final int CAMERA_CAPTURE = 1001;
	int category = 0;
	int sex = 0;
	int friends = 0;
	Date date = Calendar.getInstance().getTime();
	int mHour = 0;
	int mMinute = 0;
	boolean isPhotoAAdded = false;
	boolean isPhotoBAdded = false;
	boolean isPhotoCAdded = false;
	MyProgressDialog dlg_progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_meetup_register);
		txt_title = findViewById(R.id.txt_title);
		edt_name = findViewById(R.id.edt_name);
		txt_category = findViewById(R.id.txt_category);
		edt_location = findViewById(R.id.edt_location);
		txt_date = findViewById(R.id.txt_date);
		txt_time = findViewById(R.id.txt_time);
		edt_guests = findViewById(R.id.edt_guests);
		txt_sex = findViewById(R.id.txt_sex);
		edt_description = findViewById(R.id.edt_description);
		txt_friend = findViewById(R.id.txt_friend);
		seek_age = findViewById(R.id.seek_age);
		txt_start = findViewById(R.id.txt_start);
		txt_end = findViewById(R.id.txt_end);
		layout_photo_a = findViewById(R.id.layout_photo_a);
		layout_photo_b = findViewById(R.id.layout_photo_b);
		layout_photo_c = findViewById(R.id.layout_photo_c);
		img_photo_a = findViewById(R.id.img_photo_a);
		img_photo_b = findViewById(R.id.img_photo_b);
		img_photo_c = findViewById(R.id.img_photo_c);
		btn_create = findViewById(R.id.btn_create);
		btn_save = findViewById(R.id.btn_save);

		findViewById(R.id.layout_category).setOnClickListener(this);
		findViewById(R.id.txt_date).setOnClickListener(this);
		findViewById(R.id.txt_time).setOnClickListener(this);
		findViewById(R.id.layout_sex).setOnClickListener(this);
		findViewById(R.id.layout_friend).setOnClickListener(this);
		findViewById(R.id.txt_upload).setOnClickListener(this);
		findViewById(R.id.btn_delete_a).setOnClickListener(this);
		findViewById(R.id.btn_delete_b).setOnClickListener(this);
		findViewById(R.id.btn_delete_c).setOnClickListener(this);
		findViewById(R.id.btn_create).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		dlg_progress = new MyProgressDialog(this);
		seek_age.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
			@Override
			public void onProgressChanged(final RangeSeekBar seekBar, final int progressStart, final int progressEnd, final boolean fromUser) {
				txt_start.setText(String.valueOf(progressStart));
				txt_end.setText(String.valueOf(progressEnd));
			}

			@Override
			public void onStartTrackingTouch(final RangeSeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(final RangeSeekBar seekBar) { }
		});
		initialize();
	}

	private void initialize() {
		txt_title.setText(getString(R.string.create_meetup));
		edt_name.setText("");
		txt_category.setText("");
		edt_location.setText("");
		txt_date.setText("");
		txt_time.setText("");
		edt_guests.setText("");
		txt_sex.setText("");
		edt_description.setText("");
		txt_friend.setText("");
		seek_age.setProgress(18, 48);
		btn_create.setVisibility(View.VISIBLE);
		btn_save.setVisibility(View.GONE);
		Calendar calendar = Calendar.getInstance();
		date = calendar.getTime();
		mHour = calendar.get(Calendar.HOUR_OF_DAY);
		mMinute = calendar.get(Calendar.MINUTE);
		layout_photo_a.setVisibility(View.GONE);
		layout_photo_b.setVisibility(View.GONE);
		layout_photo_c.setVisibility(View.GONE);

		if (mMeetupModel != null) {
			txt_title.setText(getString(R.string.edit_meetup));
			edt_name.setText(mMeetupModel.meetupName);
			txt_category.setText(AppConstant.CATEGORY_ARRAY[mMeetupModel.category]);
			edt_location.setText(mMeetupModel.location);
			txt_date.setText(DateTimeUtils.dateToString(mMeetupModel.date, DateTimeUtils.DATE_STRING_FORMAT));
			txt_time.setText(DateTimeUtils.dateToString(mMeetupModel.date, DateTimeUtils.TIME_STRING_FORMAT));
			edt_guests.setText(String.valueOf(mMeetupModel.guests));
			txt_sex.setText(AppConstant.SEX_ARRAY[mMeetupModel.sex]);
			edt_description.setText(mMeetupModel.description);
			txt_friend.setText(AppConstant.KIND_ARRAY[mMeetupModel.kind]);
			seek_age.setProgress(mMeetupModel.startAge, mMeetupModel.endAge);
			category = mMeetupModel.category;
			date = mMeetupModel.date;
			calendar.setTime(date);
			mHour = calendar.get(Calendar.HOUR_OF_DAY);
			mMinute = calendar.get(Calendar.MINUTE);
			sex = mMeetupModel.sex;
			friends = mMeetupModel.kind;
			if (!TextUtils.isEmpty(mMeetupModel.photoA)) {
				layout_photo_a.setVisibility(View.VISIBLE);
				isPhotoAAdded = true;
				img_photo_a.showImage(mMeetupModel.photoA);
			}
			if (!TextUtils.isEmpty(mMeetupModel.photoB)) {
				layout_photo_b.setVisibility(View.VISIBLE);
				isPhotoBAdded = true;
				img_photo_b.showImage(mMeetupModel.photoB);
			}
			if (!TextUtils.isEmpty(mMeetupModel.photoC)) {
				layout_photo_c.setVisibility(View.VISIBLE);
				isPhotoCAdded = true;
				img_photo_c.showImage(mMeetupModel.photoC);
			}
			btn_create.setVisibility(View.GONE);
			btn_save.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		CommonUtil.hideKeyboard(instance, edt_name);
		switch (view.getId()) {
			case R.id.layout_category:
				showCategoryDialog();
				break;
			case R.id.txt_date:
				showDatePicker();
				break;
			case R.id.txt_time:
				showTimePicker();
				break;
			case R.id.layout_sex:
				showSexDialog();
				break;
			case R.id.layout_friend:
				showKindDialog();
				break;
			case R.id.txt_upload:
				if (CommonUtil.verifyStoragePermissions(CommonUtil.TYPE_CAMERA_PERMISSION, this))
					showPhotoDialog();
				else
					MessageUtil.showError(instance, R.string.msg_error_permission);
				break;
			case R.id.btn_delete_a:
				isPhotoAAdded = false;
				layout_photo_a.setVisibility(View.GONE);
				if (mMeetupModel != null)
					mMeetupModel.photoA = "";
				break;
			case R.id.btn_delete_b:
				isPhotoBAdded = false;
				layout_photo_b.setVisibility(View.GONE);
				if (mMeetupModel != null)
					mMeetupModel.photoB = "";
				break;
			case R.id.btn_delete_c:
				isPhotoCAdded = false;
				layout_photo_c.setVisibility(View.GONE);
				if (mMeetupModel != null)
					mMeetupModel.photoC = "";
				break;
			case R.id.btn_create:
			case R.id.btn_save:
				if (isValid() && DeviceUtil.isNetworkAvailable(instance))
					uploadPhotoA();
				break;
			case R.id.btn_cancel:
				finish();
				break;
		}
	}

	private void showCategoryDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(instance);
		builder.setTitle(getString(R.string.choose_category));
		builder.setSingleChoiceItems(AppConstant.CATEGORY_ARRAY, category, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				category = item;
				txt_category.setText(AppConstant.CATEGORY_ARRAY[category]);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void showSexDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(instance);
		builder.setTitle(getString(R.string.choose_sex));
		builder.setSingleChoiceItems(AppConstant.SEX_ARRAY, sex, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				sex = item;
				txt_sex.setText(AppConstant.SEX_ARRAY[sex]);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void showKindDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(instance);
		builder.setTitle(getString(R.string.choose_kind));
		builder.setSingleChoiceItems(AppConstant.KIND_ARRAY, friends, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				friends = item;
				txt_friend.setText(AppConstant.KIND_ARRAY[friends]);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void showDatePicker() {
		final Calendar cal = Calendar.getInstance();
		if (date != null)
			cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int monthOfYear = cal.get(Calendar.MONTH);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog dialog = new DatePickerDialog(instance, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, monthOfYear);
				cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				date = cal.getTime();
				txt_date.setText(DateTimeUtils.dateToString(date, DateTimeUtils.DATE_STRING_FORMAT));
			}
		}, year, monthOfYear, dayOfMonth);
		dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
		dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis() + Long.valueOf("2592000000"));
		dialog.show();

	}

	// show Timer Dialog
	private void showTimePicker() {
		TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						mHour = hourOfDay;
						mMinute = minute;
						txt_time.setText(DateTimeUtils.getStrTime(mHour, mMinute));
					}
				}, mHour, mMinute, false);
		timePickerDialog.show();
	}

	private void showPhotoDialog() {
		if (isPhotoAAdded && isPhotoBAdded && isPhotoCAdded)
			return;
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
				File file;
				if (!isPhotoAAdded)
					file = new File(ResourceUtil.getPhotoAFilePath());
				else if (!isPhotoBAdded)
					file = new File(ResourceUtil.getPhotoBFilePath());
				else
					file = new File(ResourceUtil.getPhotoCFilePath());
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
			File file;
			if (!isPhotoAAdded)
				file = new File(ResourceUtil.getPhotoAFilePath());
			else if (!isPhotoBAdded)
				file = new File(ResourceUtil.getPhotoBFilePath());
			else
				file = new File(ResourceUtil.getPhotoCFilePath());
			Uri photoURI = FileProvider.getUriForFile(instance, getApplicationContext().getPackageName() + ".provider", file);
			startCropImageActivity(photoURI);
		}

		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				String strFileName = "";
				if (!isPhotoAAdded)
					strFileName = ResourceUtil.getPhotoAFilePath();
				else if (!isPhotoBAdded)
					strFileName = ResourceUtil.getPhotoBFilePath();
				else
					strFileName = ResourceUtil.getPhotoCFilePath();
				try {
					Bitmap bm = ResourceUtil.decodeUri(instance, result.getUri(), FileModel.PHOTO_SIZE);
					if (bm != null) {
						ResourceUtil.saveBitmapToSdcard(bm, strFileName);
						if (!isPhotoAAdded) {
							isPhotoAAdded = true;
							layout_photo_a.setVisibility(View.VISIBLE);
							img_photo_a.setImageDrawable(new BitmapDrawable(bm));
						} else if (!isPhotoBAdded) {
							isPhotoBAdded = true;
							layout_photo_b.setVisibility(View.VISIBLE);
							img_photo_b.setImageDrawable(new BitmapDrawable(bm));
						} else if (!isPhotoCAdded) {
							isPhotoCAdded = true;
							layout_photo_c.setVisibility(View.VISIBLE);
							img_photo_c.setImageDrawable(new BitmapDrawable(bm));
						}
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
		String name = edt_name.getText().toString().trim();
		String category = txt_category.getText().toString().trim();
		String location = edt_location.getText().toString().trim();
		String strDate = txt_date.getText().toString();
		String strTime = txt_time.getText().toString();
		String guests = edt_guests.getText().toString();
		String sex = txt_sex.getText().toString();
		String description = edt_description.getText().toString().trim();
		String kind = txt_friend.getText().toString();

		if (TextUtils.isEmpty(name)) {
			MessageUtil.showError(instance, R.string.valid_No_meetup_name);
			edt_name.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(category)) {
			MessageUtil.showError(instance, R.string.valid_No_activity_category);
			return false;
		}
		if (TextUtils.isEmpty(location)) {
			MessageUtil.showError(instance, R.string.valid_No_location);
			return false;
		}
		if (TextUtils.isEmpty(strDate)) {
			MessageUtil.showError(instance, R.string.valid_No_date);
			return false;
		}
		if (TextUtils.isEmpty(strTime)) {
			MessageUtil.showError(instance, R.string.valid_No_time);
			return false;
		}
		if (DateTimeUtils.getDate(date, mHour, mMinute).getTime() < Calendar.getInstance().getTimeInMillis()) {
			MessageUtil.showError(instance, R.string.valid_Invalid_date_current);
			return false;
		}
		if (TextUtils.isEmpty(guests)) {
			MessageUtil.showError(instance, R.string.valid_No_guests);
			edt_guests.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(sex)) {
			MessageUtil.showError(instance, R.string.valid_No_sex);
			return false;
		}
		if (TextUtils.isEmpty(description)) {
			MessageUtil.showError(instance, R.string.valid_No_description);
			return false;
		}
		if (TextUtils.isEmpty(kind)) {
			MessageUtil.showError(instance, R.string.valid_No_kind);
			return false;
		}
		if (!isPhotoAAdded && !isPhotoBAdded && !isPhotoCAdded) {
			MessageUtil.showError(instance, R.string.valid_No_photo);
			return false;
		}
		return true;
	}

	private void uploadPhotoA(){
		dlg_progress.show();
		if (isPhotoAAdded && (mMeetupModel == null || TextUtils.isEmpty(mMeetupModel.photoA))) {
			FileModel.UploadPhoto(ResourceUtil.getPhotoAFilePath(), new BooleanListener() {
				@Override
				public void done(boolean flag, String error) {
					if (flag) {
						uploadPhotoB(error);
					} else {
						dlg_progress.cancel();
						MessageUtil.showToast(instance, error);
					}
				}
			});
		} else {
			uploadPhotoB("");
		}
	}

	private void uploadPhotoB(String photoA){
		if (isPhotoBAdded && (mMeetupModel == null || TextUtils.isEmpty(mMeetupModel.photoB))) {
			FileModel.UploadPhoto(ResourceUtil.getPhotoBFilePath(), new BooleanListener() {
				@Override
				public void done(boolean flag, String error) {
					if (flag) {
						uploadPhotoC(photoA, error);
					} else {
						dlg_progress.cancel();
						MessageUtil.showToast(instance, error);
					}
				}
			});
		} else {
			uploadPhotoC(photoA, "");
		}
	}

	private void uploadPhotoC(String photoA, String photoB){
		if (isPhotoCAdded && (mMeetupModel == null || TextUtils.isEmpty(mMeetupModel.photoC))) {
			FileModel.UploadPhoto(ResourceUtil.getPhotoCFilePath(), new BooleanListener() {
				@Override
				public void done(boolean flag, String error) {
					if (flag) {
						if (mMeetupModel == null)
							register(photoA, photoB, error);
						else
							save(photoA, photoB, error);
					} else {
						dlg_progress.cancel();
						MessageUtil.showToast(instance, error);
					}
				}
			});
		} else {
			if (mMeetupModel == null)
				register(photoA, photoB, "");
			else
				save(photoA, photoB, "");
		}
	}

	private void register(String photoA, String photoB, String photoC) {
		MeetupModel model = new MeetupModel();
		model.userId = AppGlobals.currentUser.getUid();
		model.meetupName = edt_name.getText().toString().trim();
		model.category = category;
		model.location = edt_location.getText().toString().trim();
		model.date = DateTimeUtils.getDate(date, mHour, mMinute);
		model.guests = Integer.parseInt(edt_guests.getText().toString().trim());
		model.sex = sex;
		model.description = edt_description.getText().toString().trim();
		model.kind = friends;
		model.startAge = seek_age.getProgressStart();
		model.endAge = seek_age.getProgressEnd();
		List<String> photos = new ArrayList<>();
		if (!TextUtils.isEmpty(photoA))
			photos.add(photoA);
		if (!TextUtils.isEmpty(photoB))
			photos.add(photoB);
		if (!TextUtils.isEmpty(photoC))
			photos.add(photoC);
		if (photos.size() > 0)
			model.photoA = photos.get(0);
		if (photos.size() > 1)
			model.photoB = photos.get(1);
		if (photos.size() > 2)
			model.photoC = photos.get(2);
		MeetupModel.Register(model, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
					if (HomeFragment.instance != null)
						HomeFragment.instance.list_meetup.refresh();
					finish();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}

	private void save(String photoA, String photoB, String photoC) {
		mMeetupModel.meetupName = edt_name.getText().toString().trim();
		mMeetupModel.category = category;
		mMeetupModel.location = edt_location.getText().toString().trim();
		mMeetupModel.date = DateTimeUtils.getDate(date, mHour, mMinute);
		mMeetupModel.guests = Integer.parseInt(edt_guests.getText().toString().trim());
		mMeetupModel.sex = sex;
		mMeetupModel.description = edt_description.getText().toString().trim();
		mMeetupModel.kind = friends;
		mMeetupModel.startAge = seek_age.getProgressStart();
		mMeetupModel.endAge = seek_age.getProgressEnd();
		List<String> photos = new ArrayList<>();
		if (!TextUtils.isEmpty(mMeetupModel.photoA))
			photos.add(mMeetupModel.photoA);
		else if (!TextUtils.isEmpty(photoA))
			photos.add(photoA);
		if (!TextUtils.isEmpty(mMeetupModel.photoB))
			photos.add(mMeetupModel.photoB);
		else if (!TextUtils.isEmpty(photoB))
			photos.add(photoB);
		if (!TextUtils.isEmpty(mMeetupModel.photoC))
			photos.add(mMeetupModel.photoC);
		else if (!TextUtils.isEmpty(photoC))
			photos.add(photoC);
		if (photos.size() > 0)
			mMeetupModel.photoA = photos.get(0);
		if (photos.size() > 1)
			mMeetupModel.photoB = photos.get(1);
		if (photos.size() > 2)
			mMeetupModel.photoC = photos.get(2);
		MeetupModel.Update(mMeetupModel, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
					if (HomeFragment.instance != null)
						HomeFragment.instance.list_meetup.refresh();
					if (MeetupMyActivity.instance != null) {
						MeetupMyActivity.mMeetupModel = mMeetupModel;
						MeetupMyActivity.instance.initialize();
					}
					finish();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}
}
