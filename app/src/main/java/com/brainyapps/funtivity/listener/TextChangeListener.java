package com.brainyapps.funtivity.listener;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextChangeListener implements TextWatcher {

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		onTextChange(s);
	}
	@Override
	public void afterTextChanged(Editable s) {}
	public abstract void onTextChange(CharSequence s);
}