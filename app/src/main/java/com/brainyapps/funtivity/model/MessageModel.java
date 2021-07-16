package com.brainyapps.funtivity.model;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.FuntivityApp;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.MessageListListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageModel {
    public String documentId = "";;
    public String roomId = "";
    public String sender = "";
    public String receiver = "";
    public Date date = Calendar.getInstance().getTime();
    public String message = "";

    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static void GetChatList(final String room_id, final MessageListListener listener) {
        db.collection(FirebaseConstants.TBL_MESSAGE)
                .whereEqualTo(FirebaseConstants.KEY_ROOM_ID, room_id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<MessageModel> dataList = new ArrayList<>();
                        if (error != null) {
                            if (listener != null)
                                listener.done(dataList, error.getLocalizedMessage());
                        } else {
                            for (DocumentSnapshot document : value) {
                                if (document.get("roomId") != null) {
                                    MessageModel model = new MessageModel();
                                    model.documentId = document.getId();
                                    model.roomId = document.getString(FirebaseConstants.KEY_ROOM_ID);
                                    model.sender = document.getString(FirebaseConstants.KEY_SENDER);
                                    model.receiver = document.getString(FirebaseConstants.KEY_RECEIVER);
                                    model.date = document.getDate(FirebaseConstants.KEY_DATE);
                                    model.message = document.getString(FirebaseConstants.KEY_MESSAGE);
                                    dataList.add(model);
                                }
                            }
                            if (listener != null)
                                listener.done(dataList, null);
                        }
                    }
                });
    }

    public static void SendMessage(MessageModel model, UserModel user, final ExceptionListener listener) {
        Map<String, Object> messageObj = new HashMap<>();
        messageObj.put(FirebaseConstants.KEY_ROOM_ID, model.roomId);
        messageObj.put(FirebaseConstants.KEY_SENDER, model.sender);
        messageObj.put(FirebaseConstants.KEY_RECEIVER, model.receiver);
        messageObj.put(FirebaseConstants.KEY_DATE, model.date);
        messageObj.put(FirebaseConstants.KEY_MESSAGE, model.message);

        db.collection(FirebaseConstants.TBL_MESSAGE)
                .add(messageObj)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        RoomModel.Update(model.roomId, model.message, model.receiver, null);
                        if (!TextUtils.isEmpty(user.token)) {
                            NotificationModel notificationModel = new NotificationModel();
                            notificationModel.type = NotificationModel.TYPE_CHAT;
                            notificationModel.sender = AppGlobals.currentUser.getUid();
                            notificationModel.receiver = model.receiver;
                            notificationModel.message = String.format(FuntivityApp.getContext().getString(R.string.notification_chat), UserModel.GetFullName(AppGlobals.mCurrentUserModel), model.message);
                            NotificationModel.sendPush(notificationModel, user.token, null);
                        }
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