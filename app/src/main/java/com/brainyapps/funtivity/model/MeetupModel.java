package com.brainyapps.funtivity.model;

import androidx.annotation.NonNull;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.MeetupListListener;
import com.brainyapps.funtivity.listener.MeetupListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetupModel {
    public String documentId = "";;
    public String userId = "";
    public String meetupName = "";
    public int category = 0;
    public String location = "";
    public Date date = Calendar.getInstance().getTime();
    public int guests = 0;
    public int sex = 0;
    public String description = "";
    public int kind = 0;
    public int startAge = 0;
    public int endAge = 100;
    public String photoA = "";
    public String photoB = "";
    public String photoC = "";
    public List<String> joinUsers = new ArrayList<>();
    public List<String> interestedUsers = new ArrayList<>();
    public UserModel ownerModel = new UserModel();

    public static int STATE_PENDING = 0;
    public static int STATE_ACCEPTED = 1;
    public static int STATE_DECLINED = 2;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void Register(MeetupModel model, final ExceptionListener listener) {
        Map<String, Object> meetupObj = new HashMap<>();
        meetupObj.put(FirebaseConstants.KEY_USER_ID, model.userId);
        meetupObj.put(FirebaseConstants.KEY_MEET_UP_NAME, model.meetupName);
        meetupObj.put(FirebaseConstants.KEY_CATEGORY, model.category);
        meetupObj.put(FirebaseConstants.KEY_LOCATION, model.location);
        meetupObj.put(FirebaseConstants.KEY_DATE, model.date);
        meetupObj.put(FirebaseConstants.KEY_GUESTS, model.guests);
        meetupObj.put(FirebaseConstants.KEY_SEX, model.sex);
        meetupObj.put(FirebaseConstants.KEY_DESCRIPTION, model.description);
        meetupObj.put(FirebaseConstants.KEY_KIND, model.kind);
        meetupObj.put(FirebaseConstants.KEY_START_AGE, model.startAge);
        meetupObj.put(FirebaseConstants.KEY_END_AGE, model.endAge);
        meetupObj.put(FirebaseConstants.KEY_PHOTO_A, model.photoA);
        meetupObj.put(FirebaseConstants.KEY_PHOTO_B, model.photoB);
        meetupObj.put(FirebaseConstants.KEY_PHOTO_C, model.photoC);
        meetupObj.put(FirebaseConstants.KEY_INTERESTED_USERS, model.interestedUsers);
        meetupObj.put(FirebaseConstants.KEY_JOIN_USERS, model.joinUsers);

        db.collection(FirebaseConstants.TBL_MEET_UP)
                .add(meetupObj)
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
    }

    public static void GetMeetupList(final MeetupListListener listener) {
        db.collection(FirebaseConstants.TBL_MEET_UP)
                .orderBy(FirebaseConstants.KEY_DATE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<MeetupModel> dataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MeetupModel model = new MeetupModel();
                                model.documentId = document.getId();
                                model.userId = document.getString(FirebaseConstants.KEY_USER_ID);
                                model.meetupName = document.getString(FirebaseConstants.KEY_MEET_UP_NAME);
                                model.category = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_CATEGORY)));
                                model.location = document.getString(FirebaseConstants.KEY_LOCATION);
                                model.date = document.getDate(FirebaseConstants.KEY_DATE);
                                model.guests = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_GUESTS)));
                                model.sex = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_SEX)));
                                model.description = document.getString(FirebaseConstants.KEY_DESCRIPTION);
                                model.kind = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_KIND)));
                                model.startAge = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_START_AGE)));
                                model.endAge = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_END_AGE)));
                                model.photoA = document.getString(FirebaseConstants.KEY_PHOTO_A);
                                model.photoB = document.getString(FirebaseConstants.KEY_PHOTO_B);
                                model.photoC = document.getString(FirebaseConstants.KEY_PHOTO_C);
                                model.ownerModel = UserModel.GetUserModel(model.userId);
                                model.interestedUsers = (List<String>) document.get(FirebaseConstants.KEY_INTERESTED_USERS);
                                model.joinUsers = (List<String>) document.get(FirebaseConstants.KEY_JOIN_USERS);
                                if (model.userId.equals(AppGlobals.currentUser.getUid()) || model.kind == 0 || isFriend(model.userId))
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

    public static void GetMeetup(String docId, final MeetupListener listener) {
        db.collection(FirebaseConstants.TBL_MEET_UP).document(docId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            MeetupModel model = new MeetupModel();
                            DocumentSnapshot document = task.getResult();
                            model.documentId = document.getId();
                            model.userId = document.getString(FirebaseConstants.KEY_USER_ID);
                            model.meetupName = document.getString(FirebaseConstants.KEY_MEET_UP_NAME);
                            model.category = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_CATEGORY)));
                            model.location = document.getString(FirebaseConstants.KEY_LOCATION);
                            model.date = document.getDate(FirebaseConstants.KEY_DATE);
                            model.guests = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_GUESTS)));
                            model.sex = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_SEX)));
                            model.description = document.getString(FirebaseConstants.KEY_DESCRIPTION);
                            model.kind = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_KIND)));
                            model.startAge = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_START_AGE)));
                            model.endAge = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_END_AGE)));
                            model.photoA = document.getString(FirebaseConstants.KEY_PHOTO_A);
                            model.photoB = document.getString(FirebaseConstants.KEY_PHOTO_B);
                            model.photoC = document.getString(FirebaseConstants.KEY_PHOTO_C);
                            model.ownerModel = UserModel.GetUserModel(model.userId);
                            model.interestedUsers = (List<String>) document.get(FirebaseConstants.KEY_INTERESTED_USERS);
                            model.joinUsers = (List<String>) document.get(FirebaseConstants.KEY_JOIN_USERS);
                            if (listener != null)
                                listener.done(model, null);
                        } else {
                            if (listener != null)
                                listener.done(null, task.getException().getMessage());
                        }
                    }
                });
    }

    public static Boolean isFriend(String userId) {
        for (int i = 0; i < AppGlobals.mCurrentUserModel.friends.size(); i ++) {
            if (AppGlobals.mCurrentUserModel.friends.get(i).equals(userId))
                return true;
        }
        return false;
    }

    public static String getJoinedString(List<String> users, String user_id) {
        for (int i = 0; i < users.size(); i ++) {
            if (users.get(i).indexOf(user_id) > -1)
                return users.get(i);
        }
        return "";
    }

    public static Boolean isInterested(List<String> users, String user_id) {
        for (int i = 0; i < users.size(); i ++) {
            if (users.get(i).equals(user_id))
                return true;
        }
        return false;
    }

    public static int getState(String key) {
        String[] values = key.split("-----");
        if (values.length > 0)
            return Integer.parseInt(values[1]);
        else
            return STATE_DECLINED;
    }

    public static void Delete(String docId, final ExceptionListener listener) {
        db.collection(FirebaseConstants.TBL_MEET_UP).document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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

    public static void Update(MeetupModel model, final ExceptionListener listener) {
        WriteBatch batch = db.batch();
        DocumentReference contactRef = db.collection(FirebaseConstants.TBL_MEET_UP).document(model.documentId);
        batch.update(contactRef, FirebaseConstants.KEY_MEET_UP_NAME, model.meetupName);
        batch.update(contactRef, FirebaseConstants.KEY_CATEGORY, model.category);
        batch.update(contactRef, FirebaseConstants.KEY_LOCATION, model.location);
        batch.update(contactRef, FirebaseConstants.KEY_DATE, model.date);
        batch.update(contactRef, FirebaseConstants.KEY_GUESTS, model.guests);
        batch.update(contactRef, FirebaseConstants.KEY_SEX, model.sex);
        batch.update(contactRef, FirebaseConstants.KEY_DESCRIPTION, model.description);
        batch.update(contactRef, FirebaseConstants.KEY_KIND, model.kind);
        batch.update(contactRef, FirebaseConstants.KEY_START_AGE, model.startAge);
        batch.update(contactRef, FirebaseConstants.KEY_END_AGE, model.endAge);
        batch.update(contactRef, FirebaseConstants.KEY_PHOTO_A, model.photoA);
        batch.update(contactRef, FirebaseConstants.KEY_PHOTO_B, model.photoB);
        batch.update(contactRef, FirebaseConstants.KEY_PHOTO_C, model.photoC);
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

    public static void UpdateState(boolean isInterested, String documentId, int state, List<String> users, String userId, final ExceptionListener listener) {
        WriteBatch batch = db.batch();
        DocumentReference contactRef = db.collection(FirebaseConstants.TBL_MEET_UP).document(documentId);
        if (isInterested)
            batch.update(contactRef, FirebaseConstants.KEY_INTERESTED_USERS, users);
        else
            batch.update(contactRef, FirebaseConstants.KEY_JOIN_USERS, users);
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
}