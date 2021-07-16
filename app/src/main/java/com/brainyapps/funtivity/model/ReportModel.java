package com.brainyapps.funtivity.model;

import androidx.annotation.NonNull;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportModel {
    public String documentId = "";;
    public String userId = "";
    public String reporterId = "";
    public String message = "";
    public String meetupId = "";
    public Date date = Calendar.getInstance().getTime();

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void Register(ReportModel model, final ExceptionListener listener) {
        Map<String, Object> reportObj = new HashMap<>();
        reportObj.put(FirebaseConstants.KEY_USER_ID, model.userId);
        reportObj.put(FirebaseConstants.KEY_REPORTER_ID, model.reporterId);
        reportObj.put(FirebaseConstants.KEY_MESSAGE, model.message);
        reportObj.put(FirebaseConstants.KEY_DATE, model.date);
        reportObj.put(FirebaseConstants.KEY_MEET_UP_ID, model.meetupId);

        db.collection(FirebaseConstants.TBL_REPORT)
                .add(reportObj)
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
}
