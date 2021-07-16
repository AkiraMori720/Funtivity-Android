package com.brainyapps.funtivity.listener;

import com.brainyapps.funtivity.model.ReviewModel;
import java.util.List;

public interface ReviewListListener {
	public void done(List<ReviewModel> reviews, String error);
}
