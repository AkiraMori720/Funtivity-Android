package com.brainyapps.funtivity.listener;

import com.brainyapps.funtivity.model.RoomModel;
import java.util.List;

public interface RoomListListener {
	public void done(List<RoomModel> rooms, String error);
}
