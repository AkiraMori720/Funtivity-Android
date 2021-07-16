package com.brainyapps.funtivity;

import com.brainyapps.funtivity.model.UserModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;
import java.util.List;

public class AppGlobals {
	public static FirebaseUser currentUser;
	public static FirebaseStorage mFirebaseStorage;

	public static double latitude = 3.138675;
	public static double longitude = 101.6169488;
	public static List<UserModel> mUserList = new ArrayList<>();
	public static UserModel mCurrentUserModel;
}
