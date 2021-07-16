package com.brainyapps.funtivity.model;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.http.ResponseModel;
import com.brainyapps.funtivity.http.RetrofitAPI;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.NotificationListListener;
import com.brainyapps.funtivity.listener.NotificationListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationModel {
	public static final int TYPE_FRIEND = 0;
	public static final int TYPE_MEET_UP = 1;
	public static final int TYPE_CHAT = 2;
	public static final int STATE_PENDING = 0;
	public static final int STATE_ACCEPT = 1;
	public static final int STATE_DECLINE = 2;
	public static final int STATE_REMOVE = 3;

	public String documentId = "";;
	public int type = TYPE_FRIEND;
	public int state = STATE_PENDING;
	public String sender = "";
	public String receiver = "";
	public Date date = Calendar.getInstance().getTime();
	public String message = "";
	public String meetupId = "";

	static FirebaseFirestore db = FirebaseFirestore.getInstance();

	public static void Register(NotificationModel model, String token, final ExceptionListener listener) {
		Map<String, Object> notificationObj = new HashMap<>();
		notificationObj.put(FirebaseConstants.KEY_TYPE, model.type);
		notificationObj.put(FirebaseConstants.KEY_STATE, model.state);
		notificationObj.put(FirebaseConstants.KEY_SENDER, model.sender);
		notificationObj.put(FirebaseConstants.KEY_RECEIVER, model.receiver);
		notificationObj.put(FirebaseConstants.KEY_DATE, model.date);
		notificationObj.put(FirebaseConstants.KEY_MESSAGE, model.message);
		notificationObj.put(FirebaseConstants.KEY_MEET_UP_ID, model.meetupId);

		db.collection(FirebaseConstants.TBL_NOTIFICATION)
				.add(notificationObj)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
					@Override
					public void onSuccess(DocumentReference documentReference) {
						if (!TextUtils.isEmpty(token))
							sendPush(model, token, null);
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
	}

	public static void Update(NotificationModel model, String token, final ExceptionListener listener) {
		WriteBatch batch = db.batch();
		DocumentReference contactRef = db.collection(FirebaseConstants.TBL_NOTIFICATION).document(model.documentId);
		batch.update(contactRef, FirebaseConstants.KEY_STATE, model.state);
		batch.update(contactRef, FirebaseConstants.KEY_DATE, Calendar.getInstance().getTime());
		batch.update(contactRef, FirebaseConstants.KEY_SENDER, AppGlobals.currentUser.getUid());
		batch.update(contactRef, FirebaseConstants.KEY_RECEIVER, model.receiver);
		batch.update(contactRef, FirebaseConstants.KEY_MESSAGE, model.message);
		batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (listener != null) {
					if (task.isSuccessful()) {
						if (!TextUtils.isEmpty(token))
							sendPush(model, token, null);
						listener.done(null);
					} else {
						listener.done(task.getException().getMessage());
					}
				}
			}
		});
	}

	public static void GetNotificationList(NotificationListListener listener) {
		db.collection(FirebaseConstants.TBL_NOTIFICATION)
				.whereEqualTo(FirebaseConstants.KEY_RECEIVER, AppGlobals.currentUser.getUid())
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (task.isSuccessful()) {
							List<NotificationModel> dataList = new ArrayList<>();
							for (QueryDocumentSnapshot document : task.getResult()) {
								NotificationModel model = new NotificationModel();
								model.documentId = document.getId();
								model.type = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_TYPE)));
								model.state = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_STATE)));
								model.sender = document.getString(FirebaseConstants.KEY_SENDER);
								model.receiver = document.getString(FirebaseConstants.KEY_RECEIVER);
								model.date = document.getDate(FirebaseConstants.KEY_DATE);
								model.message = document.getString(FirebaseConstants.KEY_MESSAGE);
								model.meetupId = document.getString(FirebaseConstants.KEY_MEET_UP_ID);
								dataList.add(model);
							}
							if (listener != null)
								listener.done(dataList, null);
						} else {
							if (listener != null)
								listener.done(null, task.getException().getMessage());
						}
					}
				});
	}

	public static void GetFriendNotification(NotificationListener listener) {
		db.collection(FirebaseConstants.TBL_NOTIFICATION)
				.whereEqualTo(FirebaseConstants.KEY_SENDER, AppGlobals.currentUser.getUid())
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (task.isSuccessful()) {
							for (QueryDocumentSnapshot document : task.getResult()) {
								NotificationModel model = new NotificationModel();
								model.documentId = document.getId();
								model.type = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_TYPE)));
								model.state = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_STATE)));
								model.sender = document.getString(FirebaseConstants.KEY_SENDER);
								model.receiver = document.getString(FirebaseConstants.KEY_RECEIVER);
								model.date = document.getDate(FirebaseConstants.KEY_DATE);
								model.message = document.getString(FirebaseConstants.KEY_MESSAGE);
								model.meetupId = document.getString(FirebaseConstants.KEY_MEET_UP_ID);
								if (model.type == TYPE_FRIEND && model.state == STATE_PENDING)
									listener.done(model, null);
							}
							if (listener != null)
								listener.done(null, null);
						} else {
							if (listener != null)
								listener.done(null, task.getException().getMessage());
						}
					}
				});
	}

	public static class RequestNotificaton {
		@SerializedName("to") //  "to" changed to token
		public String to;
		@SerializedName("data")
		public NotificationModel data;
	}

	public static void sendPush(NotificationModel notificationModel, String token, ExceptionListener listener) {
		String url = "https://fcm.googleapis.com/";
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(url)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		RequestNotificaton requestNotificaton = new RequestNotificaton();
		requestNotificaton.data = notificationModel;
		requestNotificaton.to = token;

		RetrofitAPI service = retrofit.create(RetrofitAPI.class);

		Call<ResponseModel> call = service.sendPushNotification(requestNotificaton);
		call.enqueue(new Callback<ResponseModel>() {
			@Override
			public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
				if (listener != null)
					listener.done(null);
			}
			@Override
			public void onFailure(Call<ResponseModel> call, Throwable t) {
				if (listener != null)
					listener.done(t.getLocalizedMessage());
			}
		});
	}
}