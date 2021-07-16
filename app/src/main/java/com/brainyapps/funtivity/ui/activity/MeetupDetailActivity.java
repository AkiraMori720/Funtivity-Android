package com.brainyapps.funtivity.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brainyapps.funtivity.R;
import com.brainyapps.funtivity.listener.ReviewListListener;
import com.brainyapps.funtivity.model.MeetupModel;
import com.brainyapps.funtivity.model.NotificationModel;
import com.brainyapps.funtivity.model.ReviewModel;
import com.brainyapps.funtivity.model.UserModel;
import com.brainyapps.funtivity.ui.view.CircleImageView;
import com.brainyapps.funtivity.ui.view.MySquareImageView;
import com.brainyapps.funtivity.utils.CommonUtil;
import com.brainyapps.funtivity.utils.DateTimeUtils;
import com.hedgehog.ratingbar.RatingBar;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeetupDetailActivity extends BaseActionBarActivity {
	public static MeetupDetailActivity instance = null;
	// UI
	TextView txt_title;
	TextView txt_location;
	MySquareImageView img_photo_a;
	MySquareImageView img_photo_b;
	MySquareImageView img_photo_c;
	TextView txt_description;
	CircleImageView img_avatar;
	TextView txt_name;
	TextView txt_creator;
	LinearLayout layout_title;
	TextView txt_write;
	LinearLayout layout_reviews;

	public static MeetupModel mMeetupModel;
	List<ShowModel> mDataList = new ArrayList<>();

	class ShowModel {
		ReviewModel reviewModel;
		UserModel userModel;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		SetTitle(R.string.app_name, -1);
		ShowActionBarIcons(true, R.id.action_back, R.id.action_report);
		setContentView(R.layout.activity_meetup_detail);
		txt_title = findViewById(R.id.txt_title);
		txt_location = findViewById(R.id.txt_location);
		img_photo_a = findViewById(R.id.img_photo_a);
		img_photo_b = findViewById(R.id.img_photo_b);
		img_photo_c = findViewById(R.id.img_photo_c);
		txt_description = findViewById(R.id.txt_description);
		img_avatar = findViewById(R.id.img_avatar);
		txt_name = findViewById(R.id.txt_name);
		txt_creator = findViewById(R.id.txt_creator);
		layout_title = findViewById(R.id.layout_title);
		txt_write = findViewById(R.id.txt_write);
		layout_reviews = findViewById(R.id.layout_reviews);
		findViewById(R.id.txt_write).setOnClickListener(this);
		initialize();
	}

	@Override
	public void onResume() {
		super.onResume();
		layout_title.setBackgroundColor(CommonUtil.getMainColor());
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
			case R.id.action_report:
				ReportActivity.mUser = mMeetupModel.ownerModel;
				ReportActivity.mMeetupModel = mMeetupModel;
				startActivity(new Intent(instance, ReportActivity.class));
				break;
			case R.id.txt_write:
				ReviewActivity.mMeetupModel = mMeetupModel;
				startActivity(new Intent(instance, ReviewActivity.class));
				break;
		}
	}

	private void initialize() {
		txt_title.setText(mMeetupModel.meetupName);
		txt_location.setText(mMeetupModel.location + " - " + DateTimeUtils.dateToString(mMeetupModel.date, DateTimeUtils.DATE_TIME_STRING_FORMAT));
		img_photo_a.showImage(mMeetupModel.photoA);
		img_photo_b.showImage(mMeetupModel.photoB);
		img_photo_c.showImage(mMeetupModel.photoC);
		txt_description.setText(mMeetupModel.description);
		txt_name.setText(UserModel.GetFullName(mMeetupModel.ownerModel));
		txt_write.setText(Html.fromHtml(getString(R.string.write_review_)));
		img_avatar.setImageResource(R.drawable.default_profile);
		if (!TextUtils.isEmpty(mMeetupModel.ownerModel.avatar))
			Picasso.get().load(mMeetupModel.ownerModel.avatar).into(img_avatar);
		getServerData();
	}

	public void getServerData() {
		dlg_progress.show();
		ReviewModel.GetReviewList(mMeetupModel.documentId, new ReviewListListener() {
			@Override
			public void done(List<ReviewModel> reviews, String error) {
				dlg_progress.cancel();
				mDataList.clear();
				if (error == null && reviews.size() > 0) {
					for (int i = 0; i < reviews.size(); i ++) {
						ShowModel model = new ShowModel();
						model.reviewModel = reviews.get(i);
						model.userModel = UserModel.GetUserModel(model.reviewModel.userId);
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
				if (model1.reviewModel.date.getTime() > model2.reviewModel.date.getTime())
					return -1;
				else
					return 1;
			}
		});
		showReview();
	}

	private void showReview() {
		layout_reviews.removeAllViews();
		for (int i = 0; i < mDataList.size(); i ++) {
			final int position = i;
			LinearLayout layer = (LinearLayout) LayoutInflater.from(instance).inflate(R.layout.item_review, null);
			CircleImageView img_avatar = layer.findViewById(R.id.img_avatar);
			TextView txt_name = layer.findViewById(R.id.txt_name);
			TextView txt_rating = layer.findViewById(R.id.txt_rating);
			RatingBar rating_bar = layer.findViewById(R.id.rating_bar);
			TextView txt_message = layer.findViewById(R.id.txt_message);
			MySquareImageView img_photo = layer.findViewById(R.id.img_photo);

			img_photo.setVisibility(View.GONE);
			txt_rating.setText(mDataList.get(position).reviewModel.rating + ".0");
			rating_bar.setStar(mDataList.get(position).reviewModel.rating);
			txt_message.setText(mDataList.get(position).reviewModel.message);
			txt_name.setText(UserModel.GetFullName(mDataList.get(position).userModel));
			img_avatar.setImageResource(R.drawable.default_profile);
			if (!TextUtils.isEmpty(mDataList.get(position).userModel.avatar))
				Picasso.get().load(mDataList.get(position).userModel.avatar).into(img_avatar);
			if (!TextUtils.isEmpty(mDataList.get(position).reviewModel.photo)) {
				img_photo.setVisibility(View.VISIBLE);
				img_photo.showImage(mDataList.get(position).reviewModel.photo);
			}
			layout_reviews.addView(layer);
		}
	}
}
