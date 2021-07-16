package com.brainyapps.funtivity.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.brainyapps.funtivity.AppConstant;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ExceptionListener;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.fragment.ProfileFragment;
import com.brainyapps.funtivity.ui.view.DragListView;
import com.brainyapps.funtivity.utils.DeviceUtil;
import com.brainyapps.funtivity.utils.MessageUtil;
import java.util.ArrayList;
import java.util.List;

public class ActivitesActivity extends BaseActionBarActivity implements DragListView.OnRefreshLoadingMoreListener {
	public static ActivitesActivity instance = null;
	// UI
	public DragListView list_activites;
	ListAdapter adapter;

	List<ShowModel> mDataList = new ArrayList<>();
	public static boolean isOutdoor = false;

	class ShowModel {
		int index = 0;
		String name = "";
		Boolean isSelected = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		if (!isOutdoor)
			SetTitle(R.string.activies, -1);
		else
			SetTitle(R.string.outdoor_activities, -1);
		ShowActionBarIcons(true, R.id.action_back);
		setContentView(R.layout.activity_activites);
		list_activites = findViewById(R.id.listView);
		adapter = new ListAdapter();
		list_activites.setAdapter(adapter);
		list_activites.setOnRefreshListener(this);
		list_activites.refresh();
		findViewById(R.id.btn_save).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
			case R.id.btn_save:
				if (DeviceUtil.isNetworkAvailable(instance))
					save();
				break;
		}
	}

	@Override
	public void onDragRefresh() {
		if (DeviceUtil.isNetworkAvailable(instance))
			showData();
		else
			list_activites.onRefreshComplete();
	}

	@Override
	public void onDragLoadMore() {}

	private void showData() {
		mDataList.clear();
		if (!isOutdoor) {
			for (int i = 0; i < AppConstant.CATEGORY_ARRAY.length; i ++) {
				ShowModel model = new ShowModel();
				model.index = i;
				model.name = AppConstant.CATEGORY_ARRAY[i];
				model.isSelected = isSelected(AppGlobals.mCurrentUserModel.activities, i);
				mDataList.add(model);
			}
		} else {
			for (int i = 0; i < AppConstant.OUTDOOR_ARRAY.length; i ++) {
				ShowModel model = new ShowModel();
				model.index = i;
				model.name = AppConstant.OUTDOOR_ARRAY[i];
				model.isSelected = isSelected(AppGlobals.mCurrentUserModel.outdoor, i);
				mDataList.add(model);
			}
		}
		adapter.notifyDataSetChanged();
		list_activites.onRefreshComplete();
	}

	private Boolean isSelected(List<Integer> activities, int position) {
		for (int i = 0; i < activities.size(); i ++) {
			int index = Integer.parseInt(String.valueOf(activities.get(i)));
			if (index == position)
				return true;
		}
		return false;
	}

	private void save() {
		List<Integer> activities = new ArrayList<>();
		for (int i = 0; i < mDataList.size(); i ++) {
			if (mDataList.get(i).isSelected)
				activities.add(i);
		}
		dlg_progress.show();
		UserModel.UpdateActivites(activities, isOutdoor, new ExceptionListener() {
			@Override
			public void done(String error) {
				dlg_progress.cancel();
				if (error == null) {
					MessageUtil.showToast(instance, R.string.Success);
					if (ProfileFragment.instance != null)
						ProfileFragment.instance.setActivities();
					myBack();
				} else {
					MessageUtil.showToast(instance, error);
				}
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
			View layout_container;
			TextView txt_name;
			CheckBox check_select;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ListAdapter.ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(instance, R.layout.item_activites, null);
				holder = new ListAdapter.ViewHolder();
				holder.layout_container = convertView.findViewById(R.id.layout_container);
				holder.txt_name = convertView.findViewById(R.id.txt_name);
				holder.check_select = convertView.findViewById(R.id.check_select);
				convertView.setTag(holder);
			} else {
				holder = (ListAdapter.ViewHolder) convertView.getTag();
			}
			holder.txt_name.setText(mDataList.get(position).name);
			holder.check_select.setChecked(mDataList.get(position).isSelected);
			holder.check_select.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mDataList.get(position).isSelected = holder.check_select.isChecked();
				}
			});
			return convertView;
		}
	}
}
