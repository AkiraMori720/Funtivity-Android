package com.brainyapps.funtivity.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.MeetupListener;
import com.brainyapps.funtivity.listener.NotificationListListener;
import com.brainyapps.funtivity.model.MeetupModel;
import com.brainyapps.funtivity.model.NotificationModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.activity.MainActivity;
import com.brainyapps.funtivity.ui.activity.MeetupDetailActivity;
import com.brainyapps.funtivity.ui.activity.MeetupInterestActivity;
import com.brainyapps.funtivity.ui.activity.MeetupMyActivity;
import com.brainyapps.funtivity.ui.view.DragListView;
import com.brainyapps.funtivity.utils.DateTimeUtils;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationsFragment extends BaseFragment implements DragListView.OnRefreshLoadingMoreListener {
	public static NotificationsFragment instance;
	public DragListView list_notifications;
	LinearLayout layout_nodata;

	ListAdapter adapter;
	List<NotificationModel> mDataList = new ArrayList<>();
	MainActivity mActivity;

	public static NotificationsFragment newInstance() {
		NotificationsFragment fragment = new NotificationsFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		mActivity = MainActivity.instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_notifications, container, false);
		list_notifications = mView.findViewById(R.id.listView);
		layout_nodata = mView.findViewById(R.id.layout_nodata);
		adapter = new ListAdapter();
		list_notifications.setAdapter(adapter);
		list_notifications.setOnRefreshListener(this);
		list_notifications.refresh();
		mView.findViewById(R.id.layout_nodata).setOnClickListener(this);
		return mView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.layout_nodata:
				layout_nodata.setVisibility(View.GONE);
				list_notifications.refresh();
				break;
		}
	}

	@Override
	public void onDragRefresh() {
		layout_nodata.setVisibility(View.GONE);
		list_notifications.setVisibility(View.VISIBLE);
		if (DeviceUtil.isNetworkAvailable(mActivity)) {
			getServerData();
		} else {
			layout_nodata.setVisibility(View.VISIBLE);
			list_notifications.setVisibility(View.GONE);
			list_notifications.onRefreshComplete();
		}
	}

	@Override
	public void onDragLoadMore() {}

	private void getServerData() {
		NotificationModel.GetNotificationList(new NotificationListListener() {
			@Override
			public void done(List<NotificationModel> notifications, String error) {
				mDataList.clear();
				if (error == null && notifications.size() > 0)
					mDataList.addAll(notifications);
				sortData();
			}
		});
	}

	private void sortData() {
		Collections.sort(mDataList, new Comparator<NotificationModel>() {
			@Override
			public int compare(NotificationModel model1, NotificationModel model2) {
				if (model1.date.getTime() > model2.date.getTime())
					return -1;
				else
					return 1;
			}
		});
		showData();
	}

	private void showData() {
		if (mDataList.size() > 0) {
			layout_nodata.setVisibility(View.GONE);
			list_notifications.setVisibility(View.VISIBLE);
		} else {
			layout_nodata.setVisibility(View.VISIBLE);
			list_notifications.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
		list_notifications.onRefreshComplete();
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
			View layout_container;
			ImageView img_done;
			TextView txt_message;
			TextView txt_tap;
			TextView txt_date;
			LinearLayout layout_accept;
			TextView txt_accept;
			TextView txt_decline;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.item_notification, null);

				holder = new ViewHolder();
				holder.layout_container = convertView.findViewById(R.id.layout_container);
				holder.img_done = convertView.findViewById(R.id.img_done);
				holder.txt_message = convertView.findViewById(R.id.txt_message);
				holder.txt_tap = convertView.findViewById(R.id.txt_tap);
				holder.txt_date = convertView.findViewById(R.id.txt_date);
				holder.layout_accept = convertView.findViewById(R.id.layout_accept);
				holder.txt_accept = convertView.findViewById(R.id.txt_accept);
				holder.txt_decline = convertView.findViewById(R.id.txt_decline);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.layout_accept.setVisibility(View.GONE);
			holder.txt_tap.setVisibility(View.GONE);
			NotificationModel model = mDataList.get(position);
			if (model.type == NotificationModel.TYPE_FRIEND)
				holder.layout_accept.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(model.meetupId))
				holder.txt_tap.setVisibility(View.VISIBLE);
			holder.txt_message.setText(model.message);
			holder.txt_date.setText(DateTimeUtils.dateToString(model.date, DateTimeUtils.DATE_TIME_STRING_FORMAT));
			holder.txt_tap.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (DeviceUtil.isNetworkAvailable(mActivity))
						getMeetupData(mDataList.get(position).meetupId);
				}
			});
			holder.txt_accept.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (DeviceUtil.isNetworkAvailable(mActivity))
						updateFriends(true, mDataList.get(position));
				}
			});
			holder.txt_decline.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (DeviceUtil.isNetworkAvailable(mActivity))
						updateFriends(false, mDataList.get(position));
				}
			});
			return convertView;
		}
	}

	private void getMeetupData(String meetupId) {
		mActivity.dlg_progress.show();
		MeetupModel.GetMeetup(meetupId, new MeetupListener() {
			@Override
			public void done(MeetupModel meetup, String error) {
				mActivity.dlg_progress.cancel();
				if (error == null && meetup != null) {
					String joinState = MeetupModel.getJoinedString(meetup.joinUsers, AppGlobals.currentUser.getUid());
					if (meetup.userId.equals(AppGlobals.currentUser.getUid())) {
						MeetupMyActivity.mMeetupModel = meetup;
						startActivity(new Intent(mActivity, MeetupMyActivity.class));
						mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					} else if (TextUtils.isEmpty(joinState) || MeetupModel.getState(joinState) == MeetupModel.STATE_PENDING) {
						MeetupInterestActivity.mMeetupModel = meetup;
						startActivity(new Intent(mActivity, MeetupInterestActivity.class));
						mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					} else if (MeetupModel.getState(joinState) == MeetupModel.STATE_ACCEPTED) {
						MeetupDetailActivity.mMeetupModel = meetup;
						startActivity(new Intent(mActivity, MeetupDetailActivity.class));
						mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
				} else {
					MessageUtil.showToast(mActivity, error);
				}
			}
		});
	}

	private void updateFriends(Boolean isAccepted, NotificationModel model) {
		UserModel friendModel = UserModel.GetUserModel(model.sender);
		model.type = NotificationModel.TYPE_FRIEND;
		model.state = NotificationModel.STATE_ACCEPT;
		model.sender = AppGlobals.currentUser.getUid();
		model.receiver = model.sender;
		model.message = String.format(getString(R.string.notification_friend_accepted), UserModel.GetFullName(AppGlobals.mCurrentUserModel));
		if (!isAccepted) {
			model.state = NotificationModel.STATE_DECLINE;
			model.message = String.format(getString(R.string.notification_friend_declined), UserModel.GetFullName(AppGlobals.mCurrentUserModel));
		}
		mActivity.dlg_progress.show();
		NotificationModel.Update(model, friendModel.token, new ExceptionListener() {
			@Override
			public void done(String error) {
				mActivity.dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(mActivity, R.string.Success);
					list_notifications.refresh();
					if (isAccepted)
						UserModel.UpdateFriend(friendModel, isAccepted, null);
				} else {
					MessageUtil.showToast(mActivity, error);
				}
			}
		});
	}

	@Override
	public void onRefresh() {}
}