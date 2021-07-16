package com.brainyapps.funtivity.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import com.brainyapps.funtivity.AppConstant;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.AppPreference;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.model.FileModel;
import com.brainyapps.funtivity.model.NotificationModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.fragment.BaseFragment;
import com.brainyapps.funtivity.ui.fragment.FriendsFragment;
import com.brainyapps.funtivity.ui.fragment.HomeFragment;
import com.brainyapps.funtivity.ui.fragment.MessagesFragment;
import com.brainyapps.funtivity.ui.fragment.NotificationsFragment;
import com.brainyapps.funtivity.ui.fragment.ProfileFragment;
import com.brainyapps.funtivity.ui.fragment.SearchFragment;
import com.brainyapps.funtivity.ui.fragment.SettingsFragment;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.ui.view.slidemenu.SlideMenu;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.brainyapps.funtivity.utils.ResourceUtil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class MainActivity extends BaseActionBarActivity {
	public static MainActivity instance = null;
	static final int MENU_COUNT = 8;
	SlideMenu mSlideMenu;
	View mSlideView;
	View menuView[] = new View[MENU_COUNT];
	CircleImageView img_avatar;
	TextView txt_name;

	BaseFragment mCurrentFragment;
	int mCurrentFragmentIndex = -1;
	int mFirstFragmentIndex = AppConstant.SW_FRAGMENT_MAIN_HOME;
	final int PICTURE_PICK = 1000;
	final int CAMERA_CAPTURE = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		SetTitle(R.string.app_name, -1);
		ShowActionBarIcons(true, R.id.action_menu);
		setContentView(R.layout.activity_main);
		mSlideMenu = findViewById(R.id.slideMenu);

		mSlideView = getLayoutInflater().inflate(R.layout.slidemenu_main_left, mSlideMenu, true);
		img_avatar = mSlideView.findViewById(R.id.img_avatar);
		txt_name = mSlideView.findViewById(R.id.txt_name);
		mSlideMenu.setEdgeSlideEnable(true);
		mSlideMenu.setSlideMode(SlideMenu.MODE_SLIDE_WINDOW);
		mSlideMenu.setOnSlideStateChangeListener(new SlideMenu.OnSlideStateChangeListener() {
			@Override
			public void onSlideStateChange(int slideState) {
				// TODO Auto-generated method stub`
				if (slideState == SlideMenu.STATE_OPEN_LEFT)
					CommonUtil.hideKeyboard(instance, mSlideView);
			}

			@Override
			public void onSlideOffsetChange(float offsetPercent) {}
		});

		menuView[0] = findViewById(R.id.menu0);
		menuView[1] = findViewById(R.id.menu1);
		menuView[2] = findViewById(R.id.menu2);
		menuView[3] = findViewById(R.id.menu3);
		menuView[4] = findViewById(R.id.menu4);
		menuView[5] = findViewById(R.id.menu5);
		menuView[6] = findViewById(R.id.menu6);
		menuView[7] = findViewById(R.id.menu7);
		for (int i = 0; i < MENU_COUNT; i++)
			menuView[i].setOnClickListener(this);
		if (getIntent().getExtras() != null) {
			int type = getIntent().getIntExtra(AppConstant.EXTRA_TYPE, 0);
			if (type == NotificationModel.TYPE_CHAT)
				mFirstFragmentIndex = AppConstant.SW_FRAGMENT_MAIN_MESSAGES;
			else
				mFirstFragmentIndex = AppConstant.SW_FRAGMENT_MAIN_NOTIFICATIONS;
		}
		SwitchContent(mFirstFragmentIndex, null);
		initialize();
	}

	public void initialize() {
		txt_name.setText(UserModel.GetFullName(AppGlobals.mCurrentUserModel));
		img_avatar.setImageResource(R.drawable.default_profile);
		if (!TextUtils.isEmpty(AppGlobals.mCurrentUserModel.avatar))
			Picasso.get().load(AppGlobals.mCurrentUserModel.avatar).into(img_avatar);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.action_menu:
				mSlideMenu.open(false, true);
				break;
			case R.id.action_notification:
				SwitchContent(AppConstant.SW_FRAGMENT_MAIN_NOTIFICATIONS, null);
				break;
			case R.id.action_settings:
				SwitchContent(AppConstant.SW_FRAGMENT_MAIN_SETTINGS, null);
				break;
			case R.id.menu0: selectMenu(0);	break;
			case R.id.menu1: selectMenu(1);	break;
			case R.id.menu2: selectMenu(2);	break;
			case R.id.menu3: selectMenu(3);	break;
			case R.id.menu4: selectMenu(4);	break;
			case R.id.menu5: selectMenu(5);	break;
			case R.id.menu6: selectMenu(6);	break;
			case R.id.menu7: selectMenu(7);	break;
		}
	}


	public void selectMenu(int index) {
		switch (index) {
			case 0: // Home
				SwitchContent(AppConstant.SW_FRAGMENT_MAIN_HOME, null);
				break;
			case 1: // Profile
				SwitchContent(AppConstant.SW_FRAGMENT_MAIN_PROFILE, null);
				break;
			case 2: // Friends
				SwitchContent(AppConstant.SW_FRAGMENT_MAIN_FRIENDS, null);
				break;
			case 3: // Search
				SwitchContent(AppConstant.SW_FRAGMENT_MAIN_SEARCH, null);
				break;
			case 4: // Messages
				SwitchContent(AppConstant.SW_FRAGMENT_MAIN_MESSAGES, null);
				break;
			case 5: // Notifications
				SwitchContent(AppConstant.SW_FRAGMENT_MAIN_NOTIFICATIONS, null);
				break;
			case 6: // Settings
				SwitchContent(AppConstant.SW_FRAGMENT_MAIN_SETTINGS, null);
				break;
			case 7: // Logout
				logout();
				break;
		}
		mSlideMenu.close(true);
	}

	public void SwitchContent(int fragment_index, Bundle bundle) {
		if (mCurrentFragmentIndex != fragment_index) {
			mCurrentFragmentIndex = fragment_index;
			if (mCurrentFragmentIndex == AppConstant.SW_FRAGMENT_MAIN_HOME) {
				SetTitle(R.string.app_name, 0);
				ShowActionBarIcons(true, R.id.action_menu);
				mCurrentFragment = HomeFragment.newInstance();
			} else if (mCurrentFragmentIndex == AppConstant.SW_FRAGMENT_MAIN_PROFILE) {
				SetTitle(R.string.profile, 0);
				ShowActionBarIcons(true, R.id.action_menu, R.id.action_notification, R.id.action_settings);
				mCurrentFragment = ProfileFragment.newInstance();
			} else if (mCurrentFragmentIndex == AppConstant.SW_FRAGMENT_MAIN_FRIENDS) {
				SetTitle(R.string.friends, 0);
				ShowActionBarIcons(true, R.id.action_menu);
				mCurrentFragment = FriendsFragment.newInstance();
			} else if (mCurrentFragmentIndex == AppConstant.SW_FRAGMENT_MAIN_SEARCH) {
				SetTitle(R.string.search, 0);
				ShowActionBarIcons(true, R.id.action_menu);
				mCurrentFragment = SearchFragment.newInstance();
			} else if (mCurrentFragmentIndex == AppConstant.SW_FRAGMENT_MAIN_MESSAGES) {
				SetTitle(R.string.messages, 0);
				ShowActionBarIcons(true, R.id.action_menu);
				mCurrentFragment = MessagesFragment.newInstance();
			} else if (mCurrentFragmentIndex == AppConstant.SW_FRAGMENT_MAIN_NOTIFICATIONS) {
				SetTitle(R.string.notifications, 0);
				ShowActionBarIcons(true, R.id.action_menu);
				mCurrentFragment = NotificationsFragment.newInstance();
			} else if (mCurrentFragmentIndex == AppConstant.SW_FRAGMENT_MAIN_SETTINGS) {
				SetTitle(R.string.settings, 0);
				ShowActionBarIcons(true, R.id.action_menu);
				mCurrentFragment = SettingsFragment.newInstance();
			}

			if (mCurrentFragment != null) {
				try {
					if (bundle != null)
						mCurrentFragment.setArguments(bundle);
					FragmentManager fragmentManager = this.getSupportFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.main_content, mCurrentFragment).commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (mSlideMenu.isOpen())
			mSlideMenu.close(true);
	}

	private void logout() {
		new AlertDialog.Builder(instance)
				.setTitle(R.string.log_out)
				.setMessage(R.string.label_log_out)
				.setPositiveButton(R.string.CONFIRM, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						AppPreference.setBool(AppPreference.KEY.SIGN_IN_AUTO, false);
						if (!AppPreference.getBool(AppPreference.KEY.SIGN_IN_REMEMBER, false)) {
							AppPreference.setStr(AppPreference.KEY.SIGN_IN_USERNAME, "");
							AppPreference.setStr(AppPreference.KEY.SIGN_IN_PASSWORD, "");
						}
						startActivity(new Intent(instance, LoginActivity.class));
						finish();
					}
				})
				.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	public void chooseTakePhoto(boolean isTake) {
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
				Toast toast = Toast.makeText(instance, errorMessage, Toast.LENGTH_SHORT);
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
		if (requestCode == PICTURE_PICK && resultCode == RESULT_OK) {
			Uri imageUri = CropImage.getPickImageResultUri(instance, data);
			startCropImageActivity(imageUri);
		}

		if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
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
						img_avatar.setImageBitmap(bm);
						if (ProfileFragment.instance != null)
							ProfileFragment.instance.setImage(bm);
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
		if (ProfileFragment.instance != null)
			ProfileFragment.instance.onActivityResult(requestCode, resultCode, data);
	}

	private void startCropImageActivity(Uri imageUri) {
		CropImage.activity(imageUri)
				.setGuidelines(CropImageView.Guidelines.ON)
				.setMultiTouchEnabled(true)
				.setAspectRatio(1, 1)
				.start(instance);
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(instance)
				.setTitle(R.string.app_name)
				.setMessage(R.string.msg_exit_label)
				.setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						myBack();
					}
				})
				.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
	}

	public static int getStatusBarHeight() {
		if (instance == null)
			return 0;

		Point p = getNavigationBarSize(instance);
		if (Build.VERSION.SDK_INT < 21 || p.x == 0)
			return 0;

		int result = 0;
		int resourceId = instance.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = instance.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static Point getNavigationBarSize(Context context) {
		Point appUsableSize = getAppUsableScreenSize(context);
		Point realScreenSize = getRealScreenSize(context);

		if (appUsableSize.x < realScreenSize.x) {
			return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
		}

		if (appUsableSize.y < realScreenSize.y) {
			return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
		}

		return new Point();
	}

	public static Point getAppUsableScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static Point getRealScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();

		if (Build.VERSION.SDK_INT >= 17) {
			display.getRealSize(size);
		} else if (Build.VERSION.SDK_INT >= 14) {
			try {
				size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
				size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			}
			catch (IllegalAccessException e) {}
			catch (InvocationTargetException e) {}
			catch (NoSuchMethodException e) {}
		}

		return size;
	}
}