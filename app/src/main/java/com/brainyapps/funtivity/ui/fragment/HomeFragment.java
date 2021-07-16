package com.brainyapps.funtivity.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.MeetupListListener;
import com.brainyapps.funtivity.listener.TextChangeListener;
import com.brainyapps.funtivity.listener.UserListListener;
import com.brainyapps.funtivity.model.MeetupModel;
import com.brainyapps.funtivity.model.NotificationModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.activity.MeetupDetailActivity;
import com.brainyapps.funtivity.ui.activity.MeetupInterestActivity;
import com.brainyapps.funtivity.ui.activity.MeetupMyActivity;
import com.brainyapps.funtivity.ui.activity.MeetupRegisterActivity;
import com.brainyapps.funtivity.ui.activity.MainActivity;
import com.brainyapps.funtivity.ui.view.DragListView;
import com.brainyapps.funtivity.ui.view.MySquareImageView;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DateTimeUtils;
import com.brainyapps.funtivity.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends BaseFragment implements DragListView.OnRefreshLoadingMoreListener {
	public static HomeFragment instance;
	LinearLayout layout_title;
	LinearLayout layout_line;
	TextView txt_all;
	TextView txt_private;
	TextView txt_my;
	View line_all;
	View line_private;
	View line_my;
	EditText edt_search;
	public DragListView list_meetup;
	LinearLayout layout_nodata;
	ImageView btn_add;

	ListAdapter adapter;
	ArrayList<MeetupModel> mServerDataList = new ArrayList<>();
	ArrayList<MeetupModel> mDataList = new ArrayList<>();
	MainActivity mActivity;
	int TYPE_ALL = 0;
	int TYPE_PRIVATE = 1;
	int TYPE_MY = 2;
	int type = TYPE_ALL;
	Boolean isLoading = false;

	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
		mActivity = MainActivity.instance;
		instance = this;
	}

	@Override
	public void onResume() {
		super.onResume();
		layout_title.setBackgroundColor(CommonUtil.getMainColor());
		layout_line.setBackgroundColor(CommonUtil.getMainColor());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_home, container, false);
		layout_title = mView.findViewById(R.id.layout_title);
		layout_line = mView.findViewById(R.id.layout_line);
		txt_all = mView.findViewById(R.id.txt_all);
		txt_private = mView.findViewById(R.id.txt_private);
		txt_my = mView.findViewById(R.id.txt_my);
		line_all = mView.findViewById(R.id.line_all);
		line_private = mView.findViewById(R.id.line_private);
		line_my = mView.findViewById(R.id.line_my);
		edt_search = mView.findViewById(R.id.edt_search);
		layout_nodata = mView.findViewById(R.id.layout_nodata);
		list_meetup = mView.findViewById(R.id.listView);
		btn_add = mView.findViewById(R.id.btn_add);
		adapter = new ListAdapter();
		list_meetup.setAdapter(adapter);
		list_meetup.setOnRefreshListener(this);

		edt_search.addTextChangedListener(new TextChangeListener() {
			@Override
			public void onTextChange(CharSequence s) {
				if (!isLoading)
					showData();
			}
		});
		mView.findViewById(R.id.txt_all).setOnClickListener(this);
		mView.findViewById(R.id.txt_private).setOnClickListener(this);
		mView.findViewById(R.id.txt_my).setOnClickListener(this);
		mView.findViewById(R.id.layout_nodata).setOnClickListener(this);
		mView.findViewById(R.id.btn_add).setOnClickListener(this);
		initialize();
		return mView;
	}

	private void initialize() {
		setType(TYPE_ALL);
		list_meetup.refresh();
	}

	@Override
	public void onClick(View view) {
		CommonUtil.hideKeyboard(mActivity, edt_search);
		switch (view.getId()) {
			case R.id.txt_all:
				edt_search.setText("");
				setType(TYPE_ALL);
				break;
			case R.id.txt_private:
				edt_search.setText("");
				setType(TYPE_PRIVATE);
				break;
			case R.id.txt_my:
				edt_search.setText("");
				setType(TYPE_MY);
				break;
			case R.id.btn_add:
				MeetupRegisterActivity.mMeetupModel = null;
				startActivity(new Intent(mActivity, MeetupRegisterActivity.class));
				mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				break;
			case R.id.layout_nodata:
				layout_nodata.setVisibility(View.GONE);
				list_meetup.refresh();
				break;
		}
	}

	private void setType(int _type) {
		type = _type;
		txt_all.setTextColor(getResources().getColor(R.color.gray));
		txt_private.setTextColor(getResources().getColor(R.color.gray));
		txt_my.setTextColor(getResources().getColor(R.color.gray));
		line_all.setBackgroundColor(getResources().getColor(R.color.transparent));
		line_private.setBackgroundColor(getResources().getColor(R.color.transparent));
		line_my.setBackgroundColor(getResources().getColor(R.color.transparent));
		btn_add.setVisibility(View.GONE);
		if (type == TYPE_ALL) {
			txt_all.setTextColor(getResources().getColor(R.color.white));
			line_all.setBackgroundColor(getResources().getColor(R.color.white));
			btn_add.setVisibility(View.VISIBLE);
		} else if (type == TYPE_PRIVATE) {
			txt_private.setTextColor(getResources().getColor(R.color.white));
			line_private.setBackgroundColor(getResources().getColor(R.color.white));
		} else {
			txt_my.setTextColor(getResources().getColor(R.color.white));
			line_my.setBackgroundColor(getResources().getColor(R.color.white));
		}
		showData();
	}

	@Override
	public void onDragRefresh() {
		layout_nodata.setVisibility(View.GONE);
		list_meetup.setVisibility(View.VISIBLE);
		if (DeviceUtil.isNetworkAvailable(mActivity)) {
			getUserList();
		} else {
			layout_nodata.setVisibility(View.VISIBLE);
			list_meetup.setVisibility(View.GONE);
			list_meetup.onRefreshComplete();
		}
	}

	@Override
	public void onDragLoadMore() {}

	private void getServerData() {
		isLoading = true;
		MeetupModel.GetMeetupList(new MeetupListListener() {
			@Override
			public void done(List<MeetupModel> meetups, String error) {
				mServerDataList.clear();
				if (error == null && meetups.size() > 0) {
					for (int i = meetups.size(); i > 0; i --)
						mServerDataList.add(meetups.get(i - 1));
				}
				isLoading = false;
				showData();
			}
		});
	}

	private void getUserList() {
		isLoading = true;
		UserModel.GetAllUserList(new UserListListener() {
			@Override
			public void done(List<UserModel> users, String error) {
				isLoading = false;
				getServerData();
			}
		});
	}

	private void showData() {
		mDataList.clear();
		String search_key = edt_search.getText().toString().trim().toLowerCase();
		for (int i = 0; i < mServerDataList.size(); i ++) {
			String key = mServerDataList.get(i).ownerModel.firstName + mServerDataList.get(i).ownerModel.lastName + mServerDataList.get(i).meetupName + mServerDataList.get(i).location;
			if (TextUtils.isEmpty(search_key) || key.toLowerCase().indexOf(search_key) > -1) {
				if (type == TYPE_MY) {
					if (mServerDataList.get(i).userId.equals(AppGlobals.currentUser.getUid()))
						mDataList.add(mServerDataList.get(i));
				} else if ((mServerDataList.get(i).startAge <= AppGlobals.mCurrentUserModel.age) && (mServerDataList.get(i).endAge >= AppGlobals.mCurrentUserModel.age)) {
					if (type == TYPE_PRIVATE) {
						if (!mServerDataList.get(i).userId.equals(AppGlobals.currentUser.getUid()) && mServerDataList.get(i).kind == 1)
							mDataList.add(mServerDataList.get(i));
					} else if (type == TYPE_ALL) {
						if (!mServerDataList.get(i).userId.equals(AppGlobals.currentUser.getUid()))
							mDataList.add(mServerDataList.get(i));
					}
				}
			}
		}
		if (mDataList.size() > 0) {
			layout_nodata.setVisibility(View.GONE);
			list_meetup.setVisibility(View.VISIBLE);
		} else {
			layout_nodata.setVisibility(View.VISIBLE);
			list_meetup.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
		list_meetup.onRefreshComplete();
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
			TextView txt_name;
			TextView txt_location;
			ImageView btn_edit;
			MySquareImageView img_photo_a;
			MySquareImageView img_photo_b;
			MySquareImageView img_photo_c;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ListAdapter.ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.item_meetup, null);
				holder = new ListAdapter.ViewHolder();
				holder.layout_container = convertView.findViewById(R.id.layout_container);
				holder.txt_name = convertView.findViewById(R.id.txt_name);
				holder.txt_location = convertView.findViewById(R.id.txt_location);
				holder.img_photo_a = convertView.findViewById(R.id.img_photo_a);
				holder.img_photo_b = convertView.findViewById(R.id.img_photo_b);
				holder.img_photo_c = convertView.findViewById(R.id.img_photo_c);
				holder.btn_edit = convertView.findViewById(R.id.btn_edit);
				convertView.setTag(holder);
			} else {
				holder = (ListAdapter.ViewHolder) convertView.getTag();
			}
			holder.btn_edit.setVisibility(View.GONE);
			MeetupModel model = mDataList.get(position);
			if (model.userId.equals(AppGlobals.currentUser.getUid()))
				holder.btn_edit.setVisibility(View.VISIBLE);
			holder.txt_name.setText(model.meetupName);
			holder.txt_location.setText(model.location + " - " + DateTimeUtils.dateToString(model.date, DateTimeUtils.DATE_TIME_STRING_FORMAT));
			holder.img_photo_a.showImage(model.photoA);
			holder.img_photo_b.showImage(model.photoB);
			holder.img_photo_c.showImage(model.photoC);
			holder.layout_container.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String joinState = MeetupModel.getJoinedString(mDataList.get(position).joinUsers, AppGlobals.currentUser.getUid());
					if (model.userId.equals(AppGlobals.currentUser.getUid())) {
						MeetupMyActivity.mMeetupModel = mDataList.get(position);
						startActivity(new Intent(mActivity, MeetupMyActivity.class));
						mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					} else if (TextUtils.isEmpty(joinState) || MeetupModel.getState(joinState) == MeetupModel.STATE_PENDING) {
						MeetupInterestActivity.mMeetupModel = mDataList.get(position);
						startActivity(new Intent(mActivity, MeetupInterestActivity.class));
						mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					} else if (MeetupModel.getState(joinState) == MeetupModel.STATE_ACCEPTED) {
						MeetupDetailActivity.mMeetupModel = mDataList.get(position);
						startActivity(new Intent(mActivity, MeetupDetailActivity.class));
						mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
				}
			});
			holder.btn_edit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MeetupRegisterActivity.mMeetupModel = mDataList.get(position);
					startActivity(new Intent(mActivity, MeetupRegisterActivity.class));
					mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			});
			return convertView;
		}
	}

	@Override
	public void onRefresh() {}
}
