package com.brainyapps.funtivity.ui.view.slidemenu;

import android.view.View;

public interface ScrollDetectorFactory {
	public ScrollDetectors.ScrollDetector newScrollDetector(View v);
}
