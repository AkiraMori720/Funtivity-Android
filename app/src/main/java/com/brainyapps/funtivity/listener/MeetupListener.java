package com.brainyapps.funtivity.listener;

import com.brainyapps.funtivity.model.MeetupModel;

public interface MeetupListener {
	public void done(MeetupModel meetup, String error);
}
