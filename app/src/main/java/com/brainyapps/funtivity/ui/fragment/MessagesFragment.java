package com.brainyapps.funtivity.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.brainyapps.funtivity.listener.RoomListListener;
import com.brainyapps.funtivity.model.RoomModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.activity.ChatActivity;
import com.brainyapps.funtivity.ui.activity.MainActivity;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.ui.view.DragListView;
import com.brainyapps.funtivity.ui.view.slidemenu.SlideMenu;
import com.brainyapps.funtivity.utils.DateTimeUtils;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends BaseFragment implements DragListView.OnRefreshLoadingMoreListener {
	public static MessagesFragment instance;
	public DragListView list_message;
	LinearLayout layout_nodata;

	ListAdapter adapter;
	List<RoomModel> mDataList = new ArrayList<>();
	MainActivity mActivity;
	private int openIndex = -1;

	public static MessagesFragment newInstance() {
		MessagesFragment fragment = new MessagesFragment();
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
		mView = inflater.inflate(R.layout.fragment_messages, container, false);
		list_message = mView.findViewById(R.id.listView);
		layout_nodata = mView.findViewById(R.id.layout_nodata);
		adapter = new ListAdapter();
		list_message.setAdapter(adapter);
		list_message.setOnRefreshListener(this);
		list_message.refresh();
		mView.findViewById(R.id.layout_nodata).setOnClickListener(this);
		return mView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.layout_nodata:
				layout_nodata.setVisibility(View.GONE);
				list_message.refresh();
				break;
		}
	}

	@Override
	public void onDragRefresh() {
		if (DeviceUtil.isNetworkAvailable(mActivity)) {
			layout_nodata.setVisibility(View.GONE);
			list_message.setVisibility(View.VISIBLE);
			getServerData();
		} else {
			layout_nodata.setVisibility(View.VISIBLE);
			list_message.setVisibility(View.GONE);
			list_message.onRefreshComplete();
		}
	}

	@Override
	public void onDragLoadMore() {}

	private void getServerData() {
		RoomModel.GetRoomList(new RoomListListener() {
			@Override
			public void done(List<RoomModel> rooms, String error) {
				mDataList.clear();
				if (error == null && rooms.size() > 0)
					mDataList.addAll(rooms);
				showData();
			}
		});
	}

	private void showData() {
		if (mDataList.size() > 0) {
			layout_nodata.setVisibility(View.GONE);
			list_message.setVisibility(View.VISIBLE);
		} else {
			layout_nodata.setVisibility(View.VISIBLE);
			list_message.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
		list_message.onRefreshComplete();
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
			CircleImageView img_avatar;
			TextView txt_name;
			TextView txt_date;
			TextView txt_message;
			SlideMenu slide_layer;
			ImageView btn_delete;
			ImageView img_unread;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.item_message, null);

				holder = new ViewHolder();
				holder.slide_layer = convertView.findViewById(R.id.slide_layer);
				holder.layout_container = convertView.findViewById(R.id.layout_container);
				holder.img_avatar = convertView.findViewById(R.id.img_avatar);
				holder.txt_name = convertView.findViewById(R.id.txt_name);
				holder.txt_date = convertView.findViewById(R.id.txt_date);
				holder.txt_message = convertView.findViewById(R.id.txt_message);
				holder.btn_delete = convertView.findViewById(R.id.btn_delete);
				holder.img_unread = convertView.findViewById(R.id.img_unread);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.img_unread.setVisibility(View.GONE);
			holder.slide_layer.setSecondaryShadowWidth(0);
			if (position != openIndex && holder.slide_layer.isOpen())
				holder.slide_layer.close(true);

			RoomModel model = mDataList.get(position);
			String friendId = model.sender;
			if (model.sender.equals(AppGlobals.currentUser.getUid()))
				friendId = model.receiver;
			UserModel friendModel = UserModel.GetUserModel(friendId);

			holder.txt_name.setText(UserModel.GetFullName(friendModel));
			holder.txt_date.setText(DateTimeUtils.dateToString(model.date, DateTimeUtils.DATE_TIME_STRING_FORMAT));
			holder.txt_message.setText(model.lastMessage);
			holder.img_avatar.setImageResource(R.drawable.default_profile);
			if (!TextUtils.isEmpty(friendModel.avatar))
				Picasso.get().load(friendModel.avatar).into(holder.img_avatar);
			if (model.confirmUser.equals(AppGlobals.currentUser.getUid()))
				holder.img_unread.setVisibility(View.VISIBLE);

			holder.btn_delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					new AlertDialog.Builder(mActivity)
							.setTitle(R.string.delete)
							.setMessage(R.string.confirm_delete_message)
							.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if (DeviceUtil.isNetworkAvailable(mActivity))
										delete(mDataList.get(position));
								}
							})
							.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									openIndex = -1;
									adapter.notifyDataSetChanged();
								}
							}).show();
				}
			});
			holder.layout_container.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					openIndex = -1;
					adapter.notifyDataSetChanged();
					if (model.confirmUser.equals(AppGlobals.currentUser.getUid()))
						RoomModel.Update(model.documentId, model.lastMessage, "", null);
					ChatActivity.mFriendModel = friendModel;
					startActivity(new Intent(mActivity, ChatActivity.class));
					mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			});
			holder.slide_layer.setOnSlideStateChangeListener(new SlideMenu.OnSlideStateChangeListener() {
				@Override
				public void onSlideStateChange(int slideState) {
					if (slideState == SlideMenu.STATE_OPEN_RIGHT) {
						openIndex = position;
						adapter.notifyDataSetChanged();
					}
				}
				@Override
				public void onSlideOffsetChange(float offsetPercent) {}
			});
			return convertView;
		}
	}

	private void delete(RoomModel roomModel) {
		mActivity.dlg_progress.show();
		RoomModel.Delete(roomModel.documentId, new ExceptionListener() {
			@Override
			public void done(String error) {
				mActivity.dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(mActivity, R.string.Success);
					list_message.refresh();
				} else {
					MessageUtil.showToast(mActivity, error);
				}
			}
		});
	}

	public void refreshData() {
		if (getActivity() == null)
			return;
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				list_message.refresh();
			}
		});
	}

	@Override
	public void onRefresh() {}
}