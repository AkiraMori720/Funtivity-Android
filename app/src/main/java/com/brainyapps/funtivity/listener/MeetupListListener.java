package com.brainyapps.funtivity.listener;

import com.brainyapps.funtivity.model.MeetupModel;
import java.util.List;

public interface MeetupListListener {
	public void done(List<MeetupModel> meetups, String error);
}
