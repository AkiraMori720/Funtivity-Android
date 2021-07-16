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
import com.brainyapps.funtivity.listener.TextChangeListener;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.activity.MainActivity;
import com.brainyapps.funtivity.ui.activity.ProfileActivity;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.ui.view.DragListView;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends BaseFragment implements DragListView.OnRefreshLoadingMoreListener {
	public static FriendsFragment instance;
	EditText edt_search;
	public DragListView list_users;
	LinearLayout layout_nodata;
	ListAdapter adapter;
	List<UserModel> mServerDataList = new ArrayList<>();
	List<UserModel> mDataList = new ArrayList<>();
	MainActivity mActivity;

	public static FriendsFragment newInstance() {
		FriendsFragment fragment = new FriendsFragment();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_friends, container, false);
		edt_search = mView.findViewById(R.id.edt_search);
		layout_nodata = mView.findViewById(R.id.layout_nodata);
		list_users = mView.findViewById(R.id.listView);
		adapter = new ListAdapter();
		list_users.setAdapter(adapter);
		list_users.setOnRefreshListener(this);
		list_users.refresh();
		edt_search.addTextChangedListener(new TextChangeListener() {
			@Override
			public void onTextChange(CharSequence s) {
				showData();
			}
		});
		layout_nodata.setOnClickListener(this);
		return mView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.layout_nodata:
				layout_nodata.setVisibility(View.GONE);
				list_users.refresh();
				break;
		}
	}

	@Override
	public void onDragRefresh() {
		layout_nodata.setVisibility(View.GONE);
		list_users.setVisibility(View.VISIBLE);
		if (DeviceUtil.isNetworkAvailable(mActivity)) {
			getServerData();
		} else {
			layout_nodata.setVisibility(View.VISIBLE);
			list_users.setVisibility(View.GONE);
			list_users.onRefreshComplete();
		}
	}

	@Override
	public void onDragLoadMore() {}

	private void getServerData() {
		List<String> friends = AppGlobals.mCurrentUserModel.friends;
		mServerDataList.clear();
		for (int i = 0; i < friends.size(); i ++) {
			UserModel model = UserModel.GetUserModel(friends.get(i));
			mServerDataList.add(model);
		}
		showData();
	}

	private void showData() {
		mDataList.clear();
		String search_key = edt_search.getText().toString().trim().toLowerCase();
		for (int i = 0; i < mServerDataList.size(); i ++) {
			String key = UserModel.GetFullName(mServerDataList.get(i)).trim().toLowerCase();
			if (TextUtils.isEmpty(search_key) || key.indexOf(search_key) > -1) {
				mDataList.add(mServerDataList.get(i));
			}
		}
		if (mDataList.size() > 0) {
			layout_nodata.setVisibility(View.GONE);
			list_users.setVisibility(View.VISIBLE);
		} else {
			layout_nodata.setVisibility(View.VISIBLE);
			list_users.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
		list_users.onRefreshComplete();
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
			ImageView btn_accept;
			ImageView btn_decline;
			ImageView btn_more;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ListAdapter.ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.item_users, null);
				holder = new ListAdapter.ViewHolder();
				holder.layout_container = convertView.findViewById(R.id.layout_container);
				holder.img_avatar = convertView.findViewById(R.id.img_avatar);
				holder.txt_name = convertView.findViewById(R.id.txt_name);
				holder.btn_accept = convertView.findViewById(R.id.btn_accept);
				holder.btn_decline = convertView.findViewById(R.id.btn_decline);
				holder.btn_more = convertView.findViewById(R.id.btn_more);
				convertView.setTag(holder);
			} else {
				holder = (ListAdapter.ViewHolder) convertView.getTag();
			}
			holder.btn_accept.setVisibility(View.GONE);
			holder.btn_decline.setVisibility(View.GONE);
			holder.txt_name.setText(UserModel.GetFullName(mDataList.get(position)));
			holder.img_avatar.setImageResource(R.drawable.default_profile);
			if (!TextUtils.isEmpty(mDataList.get(position).avatar))
				Picasso.get().load(mDataList.get(position).avatar).into(holder.img_avatar);

			holder.layout_container.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ProfileActivity.mUserModel = mDataList.get(position);
					startActivity(new Intent(mActivity, ProfileActivity.class));
					mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			});
			return convertView;
		}
	}

	@Override
	public void onRefresh() {}
}
