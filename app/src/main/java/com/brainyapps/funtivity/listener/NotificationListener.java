package com.brainyapps.funtivity.listener;

import com.brainyapps.funtivity.model.NotificationModel;

public interface NotificationListener {
	public void done(NotificationModel notification, String error);
}
