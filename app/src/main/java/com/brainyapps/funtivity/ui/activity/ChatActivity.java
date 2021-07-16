package com.brainyapps.funtivity.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.MessageListListener;
import com.brainyapps.funtivity.listener.RoomListListener;
import com.brainyapps.funtivity.model.MessageModel;
import com.brainyapps.funtivity.model.RoomModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.fragment.MessagesFragment;
import com.brainyapps.funtivity.ui.view.MyAvatarImageView;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DateTimeUtils;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BaseActionBarActivity {
    public static ChatActivity instance;
    ListView list_chat;
    EditText edtMessage;

    ListAdapter adapter;
    List<ShowModel> mDataList = new ArrayList<>();
    public static UserModel mFriendModel;
    RoomModel mRoomModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private class ShowModel {
        UserModel senderModel;
        UserModel receiverModel;
        String message;
        Date date;
        boolean ismymessage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        SetTitle(UserModel.GetFullName(mFriendModel), -1);
        ShowActionBarIcons(true, R.id.action_back);
        setContentView(R.layout.activity_chat);
        edtMessage = findViewById(R.id.edtMessage);
        list_chat = findViewById(R.id.lvChat);
        adapter = new ListAdapter();
        list_chat.setAdapter(adapter);
        findViewById(R.id.btnSend).setOnClickListener(this);
        initialize();
    }

    @Override
    public void onClick(View view) {
        CommonUtil.hideKeyboard(instance, edtMessage);
        switch (view.getId()) {
            case R.id.action_back:
                onBackPressed();
                break;
            case R.id.btnSend:
                if (isValid())
                    send();
                break;
        }
    }

    private void initialize() {
        dlg_progress.show();
        RoomModel.GetRoomList(new RoomListListener() {
            @Override
            public void done(List<RoomModel> rooms, String error) {
                dlg_progress.cancel();
                if (error == null && rooms.size() > 0) {
                    for (int i = 0; i < rooms.size(); i ++) {
                        if ((rooms.get(i).sender.equals(AppGlobals.currentUser.getUid()) && rooms.get(i).receiver.equals(mFriendModel.userId))
                                || (rooms.get(i).receiver.equals(AppGlobals.currentUser.getUid()) && rooms.get(i).sender.equals(mFriendModel.userId))) {
                            mRoomModel = rooms.get(i);
                        }
                    }
                }
                if (mRoomModel == null) {
                    RoomModel model = new RoomModel();
                    model.sender = AppGlobals.currentUser.getUid();
                    model.receiver = mFriendModel.userId;
                    RoomModel.Register(model, new ExceptionListener() {
                        @Override
                        public void done(String error) {
                            if (error == null)
                                initialize();
                        }
                    });
                } else {
                    getServerData();
                }
            }
        });
    }

    private void getServerData() {
        edtMessage.setText("");
        MessageModel.GetChatList(mRoomModel.documentId, new MessageListListener() {
            @Override
            public void done(List<MessageModel> messages, String error) {
                mDataList.clear();
                if (error == null && messages.size() > 0) {
                    for (int i = 0; i < messages.size(); i ++) {
                        ShowModel model = new ShowModel();
                        model.senderModel = UserModel.GetUserModel(messages.get(i).sender);
                        model.receiverModel = UserModel.GetUserModel(messages.get(i).receiver);
                        model.message = messages.get(i).message;
                        model.date = messages.get(i).date;
                        if (model.senderModel.userId.equals(AppGlobals.mCurrentUserModel.userId))
                            model.ismymessage = true;
                        else
                            model.ismymessage = false;
                        mDataList.add(model);
                    }
                }
                sortData();
            }
        });
    }

    private void sortData() {
        Collections.sort(mDataList, new Comparator<ShowModel>() {
            @Override
            public int compare(ShowModel model1, ShowModel model2) {
                if (model1.date.getTime() > model2.date.getTime())
                    return 1;
                else
                    return -1;
            }
        });
        adapter.notifyDataSetChanged();
    }

    private boolean isValid() {
        String message = edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            MessageUtil.showToast(instance, R.string.insert_message);
            return false;
        }
        return true;
    }

    private void send() {
        MessageModel model = new MessageModel();
        model.roomId = mRoomModel.documentId;
        model.sender = AppGlobals.mCurrentUserModel.userId;
        model.receiver = mFriendModel.userId;
        model.date = Calendar.getInstance().getTime();
        model.message = edtMessage.getText().toString().trim();
        MessageModel.SendMessage(model, mFriendModel, new ExceptionListener() {
            @Override
            public void done(String error) {
                getServerData();
            }
        });
    }

    class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            LinearLayout _lyMy;
            TextView _txtMyMessage;
            TextView _txtMyCreated;
            LinearLayout _lyFriend;
            TextView _txtFriendMessage;
            TextView _txtFriendCreated;
            MyAvatarImageView imgFriendAvatar;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(instance, R.layout.item_chat, null);
                holder = new ViewHolder();
                holder._lyMy = convertView.findViewById(R.id.lyMy);
                holder._txtMyMessage = convertView.findViewById(R.id.txtMyMessage);
                holder._txtMyCreated = convertView.findViewById(R.id.txtMyCreated);
                holder._lyFriend = convertView.findViewById(R.id.lyFriend);
                holder._txtFriendMessage = convertView.findViewById(R.id.txtFriendMessage);
                holder._txtFriendCreated = convertView.findViewById(R.id.txtFriendCreated);
                holder.imgFriendAvatar = convertView.findViewById(R.id.imgFriendAvatar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ShowModel model = mDataList.get(position);
            if (model.ismymessage) {
                holder._lyMy.setVisibility(View.VISIBLE);
                holder._lyFriend.setVisibility(View.GONE);
                holder._txtMyMessage.setText(model.message);
                holder._txtMyCreated.setText(DateTimeUtils.dateToString(model.date, DateTimeUtils.DATE_TIME_STRING_FORMAT));
            } else {
                holder._lyMy.setVisibility(View.GONE);
                holder._lyFriend.setVisibility(View.VISIBLE);
                holder._txtFriendMessage.setText(model.message);
                holder._txtFriendCreated.setText(DateTimeUtils.dateToString(model.date, DateTimeUtils.DATE_TIME_STRING_FORMAT));
                holder.imgFriendAvatar.showImage(model.senderModel.avatar);
            }
            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (MessagesFragment.instance != null)
            MessagesFragment.instance.list_message.refresh();
        myBack();
    }

    public void refreshData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getServerData();
            }
        });
    }
}