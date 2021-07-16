package com.brainyapps.funtivity.model;

import androidx.annotation.NonNull;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.RoomListListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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

public class RoomModel {
    public String documentId = "";;
    public String sender = "";
    public String receiver = "";
    public Date date = Calendar.getInstance().getTime();
    public String lastMessage = "";
    public String confirmUser = "";

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void Register(RoomModel model, final ExceptionListener listener) {
        Map<String, Object> roomObj = new HashMap<>();
        roomObj.put(FirebaseConstants.KEY_SENDER, model.sender);
        roomObj.put(FirebaseConstants.KEY_RECEIVER, model.receiver);
        roomObj.put(FirebaseConstants.KEY_DATE, model.date);
        roomObj.put(FirebaseConstants.KEY_LAST_MESSAGE, model.lastMessage);
        roomObj.put(FirebaseConstants.KEY_CONFIRM_USER, model.confirmUser);

        db.collection(FirebaseConstants.TBL_ROOM)
                .add(roomObj)
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

    public static void GetRoomList(final RoomListListener listener) {
        db.collection(FirebaseConstants.TBL_ROOM)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<RoomModel> dataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RoomModel model = new RoomModel();
                                model.documentId = document.getId();
                                model.sender = document.getString(FirebaseConstants.KEY_SENDER);
                                model.receiver = document.getString(FirebaseConstants.KEY_RECEIVER);
                                model.date = document.getDate(FirebaseConstants.KEY_DATE);
                                model.lastMessage = document.getString(FirebaseConstants.KEY_LAST_MESSAGE);
                                model.confirmUser = document.getString(FirebaseConstants.KEY_CONFIRM_USER);
                                if ((model.sender.equals(AppGlobals.currentUser.getUid())) || (model.receiver.equals(AppGlobals.currentUser.getUid())))
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

    public static void Update(String id, String message, String confirm, final ExceptionListener listener) {
        WriteBatch batch = db.batch();
        DocumentReference contactRef = db.collection(FirebaseConstants.TBL_ROOM).document(id);
        batch.update(contactRef, FirebaseConstants.KEY_DATE, Calendar.getInstance().getTime());
        batch.update(contactRef, FirebaseConstants.KEY_LAST_MESSAGE, message);
        batch.update(contactRef, FirebaseConstants.KEY_CONFIRM_USER, confirm);
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

    public static void Delete(String docId, final ExceptionListener listener) {
        db.collection(FirebaseConstants.TBL_ROOM).document(docId)
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
}