package com.brainyapps.funtivity.listener;

import com.brainyapps.funtivity.model.NotificationModel;
import java.util.List;

public interface NotificationListListener {
	public void done(List<NotificationModel> notifications, String error);
}
