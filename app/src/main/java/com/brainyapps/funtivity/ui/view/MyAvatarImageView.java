package com.brainyapps.funtivity.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.brainyapps.funtivity.R;
import com.squareup.picasso.Picasso;

public class MyAvatarImageView extends RelativeLayout {
	Context mContext;

	CircleImageView img_avatar;
	ProgressBar pb_loading;
	String mImageUrl = null;

	public MyAvatarImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		if(!isInEditMode())
			init(context);
	}

	public MyAvatarImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		if(!isInEditMode())
			init(context);
	}

	public MyAvatarImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		if(!isInEditMode())
			init(context);
	}

	private void init(Context context) {
		mContext = context;

		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_avatar_imageview, this);

		img_avatar = findViewById(R.id.img_avatar);
		pb_loading = findViewById(R.id.pb_loading);
		pb_loading.setVisibility(View.INVISIBLE);
	}

	public ImageView getImageView(){
		return img_avatar;
	}

	public void showImage(String path) {
		if (path.equals(mImageUrl))
			return;

		mImageUrl = path;
		if (!TextUtils.isEmpty(mImageUrl)) {
			Picasso.get().load(mImageUrl).into(img_avatar);
		} else {
			pb_loading.setVisibility(View.GONE);
			img_avatar.setImageResource(R.drawable.default_profile);
		}
	}

	public void setImageDrawable(Drawable d) {
		if (img_avatar != null)
			img_avatar.setImageDrawable(d);
	}

	public void setImageResource(int resId) {
		if (img_avatar != null)
			img_avatar.setImageResource(resId);
	}

	public void setImageBitmap(Bitmap bm) {
		if (img_avatar != null)
			img_avatar.setImageBitmap(bm);
	}
}