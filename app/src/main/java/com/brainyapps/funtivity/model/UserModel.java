package com.brainyapps.funtivity.model;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.UserListListener;
import com.brainyapps.funtivity.listener.UserListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserModel {
	public static final int TYPE_USER = 100;
	public static final int TYPE_ADMIN = 200;

	public String documentId = "";
	public String userId = "";
	public int type = TYPE_USER;
	public String firstName = "";
	public String lastName = "";
	public String email = "";
	public String password = "";
	public String avatar = "";
	public String address = "";
	public String interests = "";
	public int age = 0;
	public String bio = "";
	public int ratingTotal = 0;
	public int ratingCount = 0;
	public Boolean isBanned = false;
	public String token = "";
	public int qbId = 0;
	public List<String> friends = new ArrayList<>();
	public List<Integer> activities = new ArrayList<>();
	public List<Integer> outdoor = new ArrayList<>();

	static FirebaseAuth mAuth = FirebaseAuth.getInstance();
	static FirebaseFirestore db = FirebaseFirestore.getInstance();

	public static void Register(Activity context, UserModel model, final ExceptionListener listener) {
		mAuth.createUserWithEmailAndPassword(model.email, model.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					Login(context, model.email, model.password, new ExceptionListener() {
						@Override
						public void done(String error) {
							if (error == null) {
								Map<String, Object> userObj = new HashMap<>();
								userObj.put(FirebaseConstants.KEY_USER_ID, AppGlobals.currentUser.getUid());
								userObj.put(FirebaseConstants.KEY_TYPE, model.type);
								userObj.put(FirebaseConstants.KEY_FIRST_NAME, model.firstName);
								userObj.put(FirebaseConstants.KEY_LAST_NAME, model.lastName);
								userObj.put(FirebaseConstants.KEY_EMAIL, model.email);
								userObj.put(FirebaseConstants.KEY_AVATAR, model.avatar);
								userObj.put(FirebaseConstants.KEY_ADDRESS, model.address);
								userObj.put(FirebaseConstants.KEY_INTERESTS, model.address);
								userObj.put(FirebaseConstants.KEY_AGE, model.age);
								userObj.put(FirebaseConstants.KEY_BIO, model.bio);
								userObj.put(FirebaseConstants.KEY_RATING_TOTAL, model.ratingTotal);
								userObj.put(FirebaseConstants.KEY_RATING_COUNT, model.ratingCount);
								userObj.put(FirebaseConstants.KEY_IS_BANNED, model.isBanned);
								userObj.put(FirebaseConstants.KEY_TOKEN, model.token);
								userObj.put(FirebaseConstants.KEY_QB_ID, model.qbId);
								userObj.put(FirebaseConstants.KEY_FRIENDS, model.friends);
								userObj.put(FirebaseConstants.KEY_ACTIVITIES, model.activities);
								userObj.put(FirebaseConstants.KEY_OUTDOOR, model.outdoor);

								db.collection(FirebaseConstants.TBL_USER)
										.add(userObj)
										.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
											@Override
											public void onSuccess(DocumentReference documentReference) {
												if (listener != null)
													listener.done(null);
											}
										})
										.addOnFailureListener(new OnFailureListener() {
											@Override
											public void onFailure(@NonNull Exception e) {
												if (listener != null)
													listener.done(e.getMessage());
											}
										});
							} else {
								if (listener != null)
									listener.done(error);
							}
						}
					});
				} else {
					if (listener != null)
						listener.done(task.getException().getMessage());
				}
			}
		});
	}

	public static void Login(Activity context, String email, String password, final ExceptionListener listener) {
		mAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							AppGlobals.currentUser = mAuth.getInstance().getCurrentUser();
							if (listener != null)
								listener.done(null);
						} else {
							if (listener != null)
								listener.done(task.getException().getMessage());
						}
					}
				});
	}

	public static void ResetPassword(String email, final ExceptionListener listener) {
		mAuth.sendPasswordResetEmail(email)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							if (listener != null)
								listener.done(null);
						} else {
							if (listener != null)
								listener.done(task.getException().getMessage());
						}
					}
				});
	}

	public static void GetUser(final String email, final UserListener listener) {
		db.collection(FirebaseConstants.TBL_USER)
				.whereEqualTo(FirebaseConstants.KEY_EMAIL, email)
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (task.isSuccessful()) {
							UserModel model = new UserModel();;
							for (QueryDocumentSnapshot document : task.getResult()) {
								model.documentId = document.getId();
								model.userId = document.getString(FirebaseConstants.KEY_USER_ID);
								model.type = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_TYPE)));
								model.email = document.getString(FirebaseConstants.KEY_EMAIL);
								model.firstName = document.getString(FirebaseConstants.KEY_FIRST_NAME);
								model.lastName = document.getString(FirebaseConstants.KEY_LAST_NAME);
								model.avatar = document.getString(FirebaseConstants.KEY_AVATAR);
								model.address = document.getString(FirebaseConstants.KEY_ADDRESS);
								model.interests = document.getString(FirebaseConstants.KEY_INTERESTS);
								model.bio = document.getString(FirebaseConstants.KEY_BIO);
								model.age = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_AGE)));
								model.ratingTotal = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_RATING_TOTAL)));
								model.ratingCount = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_RATING_COUNT)));
								model.isBanned = document.getBoolean(FirebaseConstants.KEY_IS_BANNED);
								model.token = document.getString(FirebaseConstants.KEY_TOKEN);
								model.qbId = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_QB_ID)));
								model.friends = (List<String>) document.get(FirebaseConstants.KEY_FRIENDS);
								model.activities = (List<Integer>) document.get(FirebaseConstants.KEY_ACTIVITIES);
								model.outdoor = (List<Integer>) document.get(FirebaseConstants.KEY_OUTDOOR);
							}
							if (listener != null)
								listener.done(model, null);
						} else {
							if (listener != null)
								listener.done(null, task.getException().getMessage());
						}
					}
				});
	}

	public static void GetAllUserList(final UserListListener listener) {
		db.collection(FirebaseConstants.TBL_USER)
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (task.isSuccessful()) {
							List<UserModel> dataList = new ArrayList<>();
							for (QueryDocumentSnapshot document : task.getResult()) {
								UserModel model = new UserModel();
								model.documentId = document.getId();
								model.userId = document.getString(FirebaseConstants.KEY_USER_ID);
								model.type = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_TYPE)));
								model.email = document.getString(FirebaseConstants.KEY_EMAIL);
								model.firstName = document.getString(FirebaseConstants.KEY_FIRST_NAME);
								model.lastName = document.getString(FirebaseConstants.KEY_LAST_NAME);
								model.avatar = document.getString(FirebaseConstants.KEY_AVATAR);
								model.address = document.getString(FirebaseConstants.KEY_ADDRESS);
								model.interests = document.getString(FirebaseConstants.KEY_INTERESTS);
								model.bio = document.getString(FirebaseConstants.KEY_BIO);
								model.age = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_AGE)));
								model.ratingTotal = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_RATING_TOTAL)));
								model.ratingCount = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_RATING_COUNT)));
								model.isBanned = document.getBoolean(FirebaseConstants.KEY_IS_BANNED);
								model.token = document.getString(FirebaseConstants.KEY_TOKEN);
								model.qbId = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_QB_ID)));
								model.friends = (List<String>) document.get(FirebaseConstants.KEY_FRIENDS);
								model.activities = (List<Integer>) document.get(FirebaseConstants.KEY_ACTIVITIES);
								model.outdoor = (List<Integer>) document.get(FirebaseConstants.KEY_OUTDOOR);
								dataList.add(model);
								if (AppGlobals.currentUser.getUid().equals(model.userId))
									AppGlobals.mCurrentUserModel = model;
							}
							AppGlobals.mUserList = new ArrayList<>();
							AppGlobals.mUserList.addAll(dataList);
							if (listener != null)
								listener.done(dataList, null);
						} else {
							if (listener != null)
								listener.done(null, task.getException().getMessage());
						}
					}
				});
	}

	public static void UpdateProfile(final ExceptionListener listener) {
		WriteBatch batch = db.batch();
		DocumentReference userRef = db.collection(FirebaseConstants.TBL_USER).document(AppGlobals.mCurrentUserModel.documentId);
		batch.update(userRef, FirebaseConstants.KEY_FIRST_NAME, AppGlobals.mCurrentUserModel.firstName);
		batch.update(userRef, FirebaseConstants.KEY_LAST_NAME, AppGlobals.mCurrentUserModel.lastName);
		batch.update(userRef, FirebaseConstants.KEY_ADDRESS, AppGlobals.mCurrentUserModel.address);
		batch.update(userRef, FirebaseConstants.KEY_INTERESTS, AppGlobals.mCurrentUserModel.interests);
		batch.update(userRef, FirebaseConstants.KEY_AVATAR, AppGlobals.mCurrentUserModel.avatar);
		batch.update(userRef, FirebaseConstants.KEY_AGE, AppGlobals.mCurrentUserModel.age);
		batch.update(userRef, FirebaseConstants.KEY_BIO, AppGlobals.mCurrentUserModel.bio);
		batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (listener != null) {
					if (task.isSuccessful())
						listener.done(null);
					else
						listener.done(task.getException().getMessage());
				}
			}
		});
	}

	public static void UpdateAvatar(final String avatar, final ExceptionListener listener) {
		WriteBatch batch = db.batch();
		DocumentReference userRef = db.collection(FirebaseConstants.TBL_USER).document(AppGlobals.mCurrentUserModel.documentId);
		batch.update(userRef, FirebaseConstants.KEY_AVATAR, avatar);
		batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (listener != null) {
					if (task.isSuccessful())
						listener.done(null);
					else
						listener.done(task.getException().getMessage());
				}
			}
		});
	}

	public static void UpdateActivites(List<Integer> activities, Boolean isOutdoor, final ExceptionListener listener) {
		WriteBatch batch = db.batch();
		DocumentReference userRef = db.collection(FirebaseConstants.TBL_USER).document(AppGlobals.mCurrentUserModel.documentId);
		if (!isOutdoor)
			batch.update(userRef, FirebaseConstants.KEY_ACTIVITIES, activities);
		else
			batch.update(userRef, FirebaseConstants.KEY_OUTDOOR, activities);
		batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (listener != null) {
					if (task.isSuccessful()) {
						if (!isOutdoor)
							AppGlobals.mCurrentUserModel.activities = activities;
						else
							AppGlobals.mCurrentUserModel.outdoor = activities;
						listener.done(null);
					} else {
						listener.done(task.getException().getMessage());
					}
				}
			}
		});
	}

	public static void UpdateToken(String token, final ExceptionListener listener) {
		WriteBatch batch = db.batch();
		DocumentReference userRef = db.collection(FirebaseConstants.TBL_USER).document(AppGlobals.mCurrentUserModel.documentId);
		batch.update(userRef, FirebaseConstants.KEY_TOKEN, token);
		batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (listener != null) {
					if (task.isSuccessful()) {
						AppGlobals.mCurrentUserModel.token = token;
						listener.done(null);
					} else {
						listener.done(task.getException().getMessage());
					}
				}
			}
		});
	}

	public static void UpdateRating(UserModel model, int rating, final ExceptionListener listener) {
		WriteBatch batch = db.batch();
		DocumentReference userRef = db.collection(FirebaseConstants.TBL_USER).document(model.documentId);
		batch.update(userRef, FirebaseConstants.KEY_RATING_TOTAL, (model.ratingTotal + rating));
		batch.update(userRef, FirebaseConstants.KEY_RATING_COUNT, (model.ratingCount + 1));
		batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (listener != null) {
					if (task.isSuccessful())
						listener.done(null);
					else
						listener.done(task.getException().getMessage());
				}
			}
		});
	}

	public static void UpdateFriend(UserModel model, boolean isAdd, final ExceptionListener listener) {
		List<String> myFriendList = new ArrayList<>();
		List<String> friendList = new ArrayList<>();
		for (int i = 0 ; i < AppGlobals.mCurrentUserModel.friends.size(); i ++) {
			if (!AppGlobals.mCurrentUserModel.friends.get(i).equals(model.userId))
				myFriendList.add(AppGlobals.mCurrentUserModel.friends.get(i));
		}
		for (int i = 0 ; i < model.friends.size(); i ++) {
			if (!model.friends.get(i).equals(AppGlobals.currentUser.getUid()))
				friendList.add(model.friends.get(i));
		}
		if (isAdd) {
			myFriendList.add(model.userId);
			friendList.add(AppGlobals.currentUser.getUid());
		}
		WriteBatch batch = db.batch();
		DocumentReference userRef = db.collection(FirebaseConstants.TBL_USER).document(AppGlobals.mCurrentUserModel.documentId);
		batch.update(userRef, FirebaseConstants.KEY_FRIENDS, myFriendList);
		batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (listener != null) {
					if (task.isSuccessful()) {
						AppGlobals.mCurrentUserModel.friends = myFriendList;
						WriteBatch friendBatch = db.batch();
						DocumentReference friendRef = db.collection(FirebaseConstants.TBL_USER).document(model.documentId);
						friendBatch.update(friendRef, FirebaseConstants.KEY_FRIENDS, friendList);
						friendBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (listener != null) {
									if (task.isSuccessful())
										listener.done(null);
									else
										listener.done(task.getException().getMessage());
								}
							}
						});
					} else {
						listener.done(task.getException().getMessage());
					}
				}
			}
		});
	}

	public static UserModel GetUserModel(String user_id) {
		for (int i = 0; i < AppGlobals.mUserList.size(); i ++) {
			if (AppGlobals.mUserList.get(i).userId.equals(user_id))
				return AppGlobals.mUserList.get(i);
		}
		return new UserModel();
	}

	public static String GetFullName(UserModel model) {
		return model.firstName + " " + model.lastName;
	}

	public static boolean isFriend(UserModel userModel) {
		List<String> friends = AppGlobals.mCurrentUserModel.friends;
		for (int i = 0; i < friends.size(); i ++) {
			if (friends.get(i).equals(userModel.userId))
				return true;
		}
		return false;
	}
}
