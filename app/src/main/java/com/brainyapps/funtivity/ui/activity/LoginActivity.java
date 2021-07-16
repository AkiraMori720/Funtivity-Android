package com.brainyapps.funtivity.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.brainyapps.funtivity.AppConstant;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.AppPreference;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.UserListListener;
import com.brainyapps.funtivity.listener.UserListener;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends BaseActionBarActivity {
	public static LoginActivity instance;
	LinearLayout layout_background;
	EditText edt_email;
	EditText edt_password;
	TextView txt_forgot_password;
	TextView txt_signup;

	private static final int RC_SIGN_IN = 9001;
	FirebaseAuth mAuth;
	GoogleSignInClient mGoogleSignInClient;
	CallbackManager mCallbackManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;
		SetTitle(R.string.login, -1);
		ShowActionBarIcons(true, 0);
		setContentView(R.layout.activity_login);
		layout_background = findViewById(R.id.layout_background);
		edt_email = findViewById(R.id.edt_email);
		edt_password = findViewById(R.id.edt_password);
		txt_signup = findViewById(R.id.txt_signup);
		txt_forgot_password = findViewById(R.id.txt_forgot_password);
		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.btn_facebook).setOnClickListener(this);
		findViewById(R.id.btn_google).setOnClickListener(this);
		findViewById(R.id.txt_signup).setOnClickListener(this);
		findViewById(R.id.txt_forgot_password).setOnClickListener(this);
		initialize();
	}

	private void initialize() {
		mAuth = FirebaseAuth.getInstance();
		txt_forgot_password.setText(Html.fromHtml(getString(R.string.forgot_password)));
		txt_signup.setText(Html.fromHtml(getString(R.string.not_registered_sign_up)));
		edt_email.setText(AppPreference.getStr(AppPreference.KEY.SIGN_IN_USERNAME, ""));
		edt_password.setText(AppPreference.getStr(AppPreference.KEY.SIGN_IN_PASSWORD, ""));
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
		if (AppPreference.getBool(AppPreference.KEY.SIGN_IN_AUTO, false))
			login();
	}

	@Override
	public void onResume() {
		super.onResume();
		layout_background.setBackgroundColor(CommonUtil.getMainColor());
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub]
		super.onClick(view);
		CommonUtil.hideKeyboard(instance, edt_email);
		switch (view.getId()) {
			case R.id.txt_forgot_password:
				startActivity(new Intent(instance, ResetPasswordActivity.class));
				break;
			case R.id.btn_facebook:
				facebookLogin();
				break;
			case R.id.btn_google:
				Intent signInIntent = mGoogleSignInClient.getSignInIntent();
				startActivityForResult(signInIntent, RC_SIGN_IN);
				break;
			case R.id.btn_login:
				if (isValid() && DeviceUtil.isNetworkAvailable(instance))
					login();
				break;
			case R.id.txt_signup:
				startActivity(new Intent(instance, SignUpActivity.class));
				break;
		}
	}

	private boolean isValid() {
		String email = edt_email.getText().toString().trim();
		String password = edt_password.getText().toString().trim();
		if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
			MessageUtil.showError(instance, R.string.valid_No_email_password);
			edt_email.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(email)) {
			MessageUtil.showError(instance, R.string.valid_No_email);
			edt_email.requestFocus();
			return false;
		}
		if (!CommonUtil.isValidEmail(email)) {
			MessageUtil.showError(instance, R.string.valid_Invalid_email);
			edt_email.requestFocus();
			edt_email.setSelection(edt_email.getText().length());
			return false;
		}
		if (TextUtils.isEmpty(password)) {
			MessageUtil.showError(instance, R.string.valid_No_password);
			edt_password.requestFocus();
			return false;
		}
		return true;
	}

	private void facebookLogin() {
		mCallbackManager = CallbackManager.Factory.create();
		LoginManager loginManager = LoginManager.getInstance();
		loginManager.logInWithReadPermissions(instance, Arrays.asList("email", "public_profile"));
		loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				if (AccessToken.getCurrentAccessToken() != null) {
					GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
						@Override
						public void onCompleted(JSONObject object, GraphResponse response) {
							if (object != null) {
								try {
									String id = object.getString("id");
									String email = object.getString("email");
									String[] name = object.getString("name").split(" ");
									String first_name = name[0];
									String last_name = name[1];
									getSocialUser(id, "", email, first_name, last_name, "");
								} catch (Exception e) {
									MessageUtil.showToast(instance, e.getMessage());
								}
							}
						}
					});
					Bundle parameters = new Bundle();
					parameters.putString("fields", "id,name,email");
					request.setParameters(parameters);
					request.executeAsync();
				}
			}

			@Override
			public void onCancel() {
				MessageUtil.showToast(instance, "SignIn Canceled.");
			}

			@Override
			public void onError(FacebookException error) {
				MessageUtil.showToast(instance, error.getMessage());
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				String id = account.getId();
				String email = account.getEmail();
				String first_name = account.getGivenName();
				String last_name = account.getFamilyName();
				String photo = account.getPhotoUrl().getPath();
				getSocialUser("", id, email, first_name, last_name, photo);
			} catch (ApiException e) {
				MessageUtil.showToast(instance, e.getMessage());
			}
		} else {
			mCallbackManager.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void getSocialUser(String facebookId, String googleId, String email, String first_name, String last_name, String photo) {
		dlg_progress.show();
		UserModel.GetUser(email, new UserListener() {
			@Override
			public void done(UserModel user, String error) {
				dlg_progress.cancel();
				if (error == null) {
					if (TextUtils.isEmpty(user.email)) {
						register(facebookId, googleId, email, first_name, last_name, photo);
					} else {
						edt_email.setText(email);
						edt_password.setText(AppConstant.DEFAULT_PASSWORD);
						login();
					}
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}

	private void register(String facebookId, String googleId, String email, String first_name, String last_name, String photo) {
		UserModel model = new UserModel();
		model.email = email;
		model.password = AppConstant.DEFAULT_PASSWORD;
		model.firstName = first_name;
		model.lastName = last_name;
		model.avatar = photo;

		dlg_progress.show();
		UserModel.Register(instance, model, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.successfully_register);
					edt_email.setText(email);
					edt_password.setText(AppConstant.DEFAULT_PASSWORD);
					login();
				} else {
					MessageUtil.showToast(instance, error);
				}
			}
		});
	}

	private void login() {
		final String email = edt_email.getText().toString().trim();
		String password = edt_password.getText().toString().trim();

		dlg_progress.show();
		UserModel.Login(instance, email, password, new ExceptionListener() {
			@Override
			public void done(String error) {
				if (error == null) {
					getUserList();
				} else {
					dlg_progress.cancel();
					MessageUtil.showError(instance, error);
				}
			}
		});
	}

	private void getUserList() {
		UserModel.GetAllUserList(new UserListListener() {
			@Override
			public void done(List<UserModel> users, String error) {
				dlg_progress.cancel();
				if (error == null && users.size() > 0)
					getToken();
				else
					MessageUtil.showToast(instance, error);
			}
		});
	}

	private void getToken() {
		FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
			String newToken = instanceIdResult.getToken();
			if (!TextUtils.isEmpty(newToken) && !AppGlobals.mCurrentUserModel.token.equals(newToken)) {
				UserModel.UpdateToken(newToken, new ExceptionListener() {
					@Override
					public void done(String error) {
						gotoNextActivity();
					}
				});
			} else {
				gotoNextActivity();
			}
		});
	}

	private void gotoNextActivity() {
		AppPreference.setBool(AppPreference.KEY.SIGN_IN_AUTO, true);
		AppPreference.setStr(AppPreference.KEY.SIGN_IN_USERNAME, edt_email.getText().toString().trim());
		AppPreference.setStr(AppPreference.KEY.SIGN_IN_PASSWORD, edt_password.getText().toString());
		startActivity(new Intent(instance, MainActivity.class));
		finish();
	}


	private void firebaseAuthWithGoogle(String idToken) {
		dlg_progress.show();
		AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						dlg_progress.cancel();
						if (task.isSuccessful()) {
							AppGlobals.currentUser = mAuth.getCurrentUser();
							getUserList();
						} else {
							MessageUtil.showToast(instance, "Authentication Failed.");
						}
					}
				});
	}

	private void handleFacebookAccessToken(final AccessToken token) {
		dlg_progress.show();
		AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						dlg_progress.hide();
						if (task.isSuccessful()) {
							AppGlobals.currentUser = mAuth.getCurrentUser();
							getUserList();
						} else {
							MessageUtil.showToast(instance, "signInWithCredential:failure " + task.getException());
						}
					}
				});
	}
}
