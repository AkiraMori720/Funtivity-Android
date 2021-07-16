package com.brainyapps.funtivity.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import androidx.core.app.ActivityCompat;
import com.brainyapps.funtivity.AppPreference;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.UserListListener;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class SplashActivity extends BaseActivity {
    public static SplashActivity instance = null;

    private static final int REQUEST_PERMISSION = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        verifyStoragePermissions(this);
    }

    public void verifyStoragePermissions(Activity activity) {
        int permission0 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission0 != PackageManager.PERMISSION_GRANTED
                || permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String email = AppPreference.getStr(AppPreference.KEY.SIGN_IN_USERNAME, "");
                    String password = AppPreference.getStr(AppPreference.KEY.SIGN_IN_PASSWORD, "");
                    if (AppPreference.getBool(AppPreference.KEY.SIGN_IN_AUTO, false) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                        login(email, password);
                    else
                        gotoNextActivity();
                }
            }, 1000);
        }
    }

    public void login(String email, String password) {
        dlg_progress.show();
        UserModel.Login(instance, email, password, new ExceptionListener() {
            @Override
            public void done(String error) {
                if (error == null) {
                    getUserList();
                } else {
                    dlg_progress.cancel();
                    gotoNextActivity();
                }
            }
        });
    }

    private void getUserList() {
        UserModel.GetAllUserList(new UserListListener() {
            @Override
            public void done(List<UserModel> users, String error) {
                dlg_progress.cancel();
                if (error == null && users.size() > 0) {
                    startActivity(new Intent(instance, MainActivity.class));
                    finish();
                } else {
                    MessageUtil.showToast(instance, error);
                }
            }
        });
    }

    private void gotoNextActivity() {
        if (AppPreference.getBool(AppPreference.KEY.AGREE, false))
            startActivity(new Intent(instance, LoginActivity.class));
        else
            startActivity(new Intent(instance, OnboardActivity.class));
        finish();
    }
}
