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
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.listener.TextChangeListener;
import com.brainyapps.funtivity.listener.UserListListener;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.activity.MainActivity;
import com.brainyapps.funtivity.ui.activity.ProfileActivity;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.ui.view.DragListView;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment implements DragListView.OnRefreshLoadingMoreListener {
	public static SearchFragment instance;
	EditText edt_search;
	public DragListView list_users;
	LinearLayout layout_nodata;
	ListAdapter adapter;
	List<ShowModel> mServerDataList = new ArrayList<>();
	List<ShowModel> mDataList = new ArrayList<>();
	MainActivity mActivity;

	class ShowModel {
		UserModel userModel;
		Boolean isFriend;
	}

	public static SearchFragment newInstance() {
		SearchFragment fragment = new SearchFragment();
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
		mView = inflater.inflate(R.layout.fragment_search, container, false);
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
		UserModel.GetAllUserList(new UserListListener() {
			@Override
			public void done(List<UserModel> users, String error) {
				mServerDataList.clear();
				if (error == null && users.size() > 0) {
					for (int i = 0; i < users.size(); i ++) {
						if (users.get(i).type == UserModel.TYPE_USER && !users.get(i).userId.equals(AppGlobals.currentUser.getUid())) {
							ShowModel model = new ShowModel();
							model.userModel = users.get(i);
							model.isFriend = UserModel.isFriend(users.get(i));
							mServerDataList.add(model);
						}
					}
				}
				showData();
			}
		});
	}

	private void showData() {
		mDataList.clear();
		String search_key = edt_search.getText().toString().trim().toLowerCase();
		for (int i = 0; i < mServerDataList.size(); i ++) {
			String key = UserModel.GetFullName(mServerDataList.get(i).userModel).trim().toLowerCase();
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
			ImageView btn_add;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ListAdapter.ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.item_search, null);
				holder = new ListAdapter.ViewHolder();
				holder.layout_container = convertView.findViewById(R.id.layout_container);
				holder.img_avatar = convertView.findViewById(R.id.img_avatar);
				holder.txt_name = convertView.findViewById(R.id.txt_name);
				holder.btn_add = convertView.findViewById(R.id.btn_add);
				convertView.setTag(holder);
			} else {
				holder = (ListAdapter.ViewHolder) convertView.getTag();
			}
			holder.btn_add.setVisibility(View.VISIBLE);
			UserModel model = mDataList.get(position).userModel;
			holder.txt_name.setText(UserModel.GetFullName(model));
			if (mDataList.get(position).isFriend)
				holder.btn_add.setVisibility(View.GONE);
			holder.img_avatar.setImageResource(R.drawable.default_profile);
			if (!TextUtils.isEmpty(model.avatar))
				Picasso.get().load(model.avatar).into(holder.img_avatar);

			holder.layout_container.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ProfileActivity.mUserModel = mDataList.get(position).userModel;
					startActivity(new Intent(mActivity, ProfileActivity.class));
					mActivity.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			});
			holder.btn_add.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (DeviceUtil.isNetworkAvailable(mActivity))
						updateFriends(mDataList.get(position).userModel, !mDataList.get(position).isFriend);
				}
			});
			return convertView;
		}
	}

	private void updateFriends(UserModel userModel, Boolean isAdd) {
		mActivity.dlg_progress.show();
		UserModel.UpdateFriend(userModel, isAdd, new ExceptionListener() {
			@Override
			public void done(String error) {
				mActivity.dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(mActivity, R.string.Success);
					list_users.refresh();
				} else {
					MessageUtil.showToast(mActivity, error);
				}
			}
		});
	}

	@Override
	public void onRefresh() {}
}
