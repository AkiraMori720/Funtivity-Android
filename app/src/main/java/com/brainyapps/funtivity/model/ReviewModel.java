package com.brainyapps.funtivity.model;

import androidx.annotation.NonNull;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.ReviewListListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewModel {
    public String documentId = "";;
    public String userId = "";
    public String meetupId = "";
    public int rating = 0;
    public Date date = Calendar.getInstance().getTime();
    public String message = "";
    public String photo = "";
    public String video = "";

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void Register(ReviewModel model, final ExceptionListener listener) {
        Map<String, Object> reviewObj = new HashMap<>();
        reviewObj.put(FirebaseConstants.KEY_USER_ID, model.userId);
        reviewObj.put(FirebaseConstants.KEY_MEET_UP_ID, model.meetupId);
        reviewObj.put(FirebaseConstants.KEY_RATING, model.rating);
        reviewObj.put(FirebaseConstants.KEY_DATE, model.date);
        reviewObj.put(FirebaseConstants.KEY_MESSAGE, model.message);
        reviewObj.put(FirebaseConstants.KEY_PHOTO, model.photo);
        reviewObj.put(FirebaseConstants.KEY_VIDEO, model.video);

        db.collection(FirebaseConstants.TBL_REVIEW)
                .add(reviewObj)
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

    public static void GetReviewList(String meetup_id, final ReviewListListener listener) {
        db.collection(FirebaseConstants.TBL_REVIEW)
                .whereEqualTo(FirebaseConstants.KEY_MEET_UP_ID, meetup_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ReviewModel> dataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReviewModel model = new ReviewModel();
                                model.documentId = document.getId();
                                model.userId = document.getString(FirebaseConstants.KEY_USER_ID);
                                model.meetupId = document.getString(FirebaseConstants.KEY_MEET_UP_ID);
                                model.rating = Integer.parseInt(String.valueOf(document.getLong(FirebaseConstants.KEY_RATING)));
                                model.date = document.getDate(FirebaseConstants.KEY_DATE);
                                model.message = document.getString(FirebaseConstants.KEY_MESSAGE);
                                model.photo = document.getString(FirebaseConstants.KEY_PHOTO);
                                model.video = document.getString(FirebaseConstants.KEY_VIDEO);
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
}
