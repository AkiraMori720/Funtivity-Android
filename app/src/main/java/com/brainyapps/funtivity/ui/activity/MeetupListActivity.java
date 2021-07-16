package com.brainyapps.funtivity.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.brainyapps.funtivity.model.MeetupModel;
import com.brainyapps.funtivity.ui.view.DragListView;
import com.brainyapps.funtivity.ui.view.MySquareImageView;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DateTimeUtils;
import com.brainyapps.funtivity.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.List;

public class MeetupListActivity extends BaseActionBarActivity implements DragListView.OnRefreshLoadingMoreListener {
	public static MeetupListActivity instance;
	EditText edt_search;
	public DragListView list_meetup;
	LinearLayout layout_nodata;
	ImageView btn_add;

	ListAdapter adapter;
	ArrayList<MeetupModel> mServerDataList = new ArrayList<>();
	ArrayList<MeetupModel> mDataList = new ArrayList<>();
	Boolean isLoading = false;

	public static MeetupListActivity newInstance() {
		MeetupListActivity fragment = new MeetupListActivity();
		return fragment;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		SetTitle(R.string.app_name, -1);
		ShowActionBarIcons(true, R.id.action_back);
		setContentView(R.layout.activity_meetup_list);
		edt_search = findViewById(R.id.edt_search);
		layout_nodata = findViewById(R.id.layout_nodata);
		list_meetup = findViewById(R.id.listView);
		btn_add = findViewById(R.id.btn_add);
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
		list_meetup.refresh();
		findViewById(R.id.layout_nodata).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		CommonUtil.hideKeyboard(instance, edt_search);
		super.onClick(view);
		switch (view.getId()) {
			case R.id.layout_nodata:
				layout_nodata.setVisibility(View.GONE);
				list_meetup.refresh();
				break;
		}
	}

	@Override
	public void onDragRefresh() {
		layout_nodata.setVisibility(View.GONE);
		list_meetup.setVisibility(View.VISIBLE);
		if (DeviceUtil.isNetworkAvailable(instance)) {
			getServerData();
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
				if (error == null && meetups.size() > 0)
					mServerDataList.addAll(meetups);
				isLoading = false;
				showData();
			}
		});
	}

	private void showData() {
		mDataList.clear();
		String search_key = edt_search.getText().toString().trim().toLowerCase();
		for (int i = 0; i < mServerDataList.size(); i ++) {
			String key = mServerDataList.get(i).ownerModel.firstName + mServerDataList.get(i).ownerModel.lastName + mServerDataList.get(i).meetupName + mServerDataList.get(i).location;
			if (TextUtils.isEmpty(search_key) || key.toLowerCase().indexOf(search_key) > -1) {
				if (MeetupModel.isInterested(mServerDataList.get(i).joinUsers, AppGlobals.currentUser.getUid() + "-----" + MeetupModel.STATE_ACCEPTED))
					mDataList.add(mServerDataList.get(i));
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
			final ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(instance, R.layout.item_meetup, null);
				holder = new ViewHolder();
				holder.layout_container = convertView.findViewById(R.id.layout_container);
				holder.txt_name = convertView.findViewById(R.id.txt_name);
				holder.txt_location = convertView.findViewById(R.id.txt_location);
				holder.img_photo_a = convertView.findViewById(R.id.img_photo_a);
				holder.img_photo_b = convertView.findViewById(R.id.img_photo_b);
				holder.img_photo_c = convertView.findViewById(R.id.img_photo_c);
				holder.btn_edit = convertView.findViewById(R.id.btn_edit);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.btn_edit.setVisibility(View.GONE);
			MeetupModel model = mDataList.get(position);
			holder.txt_name.setText(model.meetupName);
			holder.txt_location.setText(model.location + " - " + DateTimeUtils.dateToString(model.date, DateTimeUtils.DATE_TIME_STRING_FORMAT));
			holder.img_photo_a.showImage(model.photoA);
			holder.img_photo_b.showImage(model.photoB);
			holder.img_photo_c.showImage(model.photoC);
			holder.layout_container.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MeetupDetailActivity.mMeetupModel = mDataList.get(position);
					startActivity(new Intent(instance, MeetupDetailActivity.class));
				}
			});
			return convertView;
		}
	}
}
