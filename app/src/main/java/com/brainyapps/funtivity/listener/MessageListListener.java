package com.brainyapps.funtivity.listener;

import com.brainyapps.funtivity.model.MessageModel;
import java.util.List;

public interface MessageListListener {
	public void done(List<MessageModel> messages, String error);
}
