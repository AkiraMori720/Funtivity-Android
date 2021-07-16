package com.brainyapps.funtivity.listener;

import com.brainyapps.funtivity.model.UserModel;
import java.util.List;

public interface UserListListener {
	public void done(List<UserModel> users, String error);
}
