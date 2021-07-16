package com.brainyapps.funtivity.ui.view.slidemenu;

import android.view.View;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class ScrollDetectors {
	private static final WeakHashMap<Class<? extends View>, ScrollDetector> IMPLES = new WeakHashMap<Class<? extends View>, ScrollDetector>();
	private static ScrollDetectorFactory mFactory;

	public static boolean canScrollHorizontal(View v, int direction) {
		ScrollDetector imples = getImplements(v);
		if (null == imples) {
			return false;
		}
		return imples.canScrollHorizontal(v, direction);
	}

	public static boolean canScrollVertical(View v, int direction) {
		ScrollDetector imples = getImplements(v);
		if (null == imples) {
			return false;
		}
		return imples.canScrollVertical(v, direction);
	}

	private static ScrollDetector getImplements(View v) {
		Class<? extends View> clazz = v.getClass();
		ScrollDetector imple = IMPLES.get(clazz);

		if (null != imple) {
			return imple;
		}

		if (v instanceof ViewPager) {
			imple = new ViewPagerScrollDetector();
		} else if (v instanceof HorizontalScrollView) {
			imple = new HorizontalScrollViewScrollDetector();
		} else if (v instanceof WebView) {
			imple = new WebViewScrollDetector();
		} else if (v instanceof SwipeRefreshLayout) {
			imple = new SwipeRefreshLayoutScrollDetector();
		} else if (null != mFactory) {
			imple = mFactory.newScrollDetector(v);
		} else {
			return null;
		}

		IMPLES.put(clazz, imple);
		return imple;
	}

	private static class ViewPagerScrollDetector implements ScrollDetector {
		@Override
		public boolean canScrollHorizontal(View v, int direction) {
			ViewPager viewPager = (ViewPager) v;
			PagerAdapter pagerAdapter = viewPager.getAdapter();
			if (null == pagerAdapter || 0 == pagerAdapter.getCount()) {
				return false;
			}

			final int currentItem = viewPager.getCurrentItem();
			return (direction < 0 && currentItem < pagerAdapter.getCount() - 1)
					|| (direction > 0 && currentItem > 0);
		}

		@Override
		public boolean canScrollVertical(View v, int direction) {
			// TODO
			return false;
		}
	}

	private static class WebViewScrollDetector implements ScrollDetector {

		@Override
		public boolean canScrollHorizontal(View v, int direction) {
			try {
				Method computeHorizontalScrollOffsetMethod = WebView.class
						.getDeclaredMethod("computeHorizontalScrollOffset");
				Method computeHorizontalScrollRangeMethod = WebView.class
						.getDeclaredMethod("computeHorizontalScrollRange");
				computeHorizontalScrollOffsetMethod.setAccessible(true);
				computeHorizontalScrollRangeMethod.setAccessible(true);

				final int horizontalScrollOffset = (Integer) computeHorizontalScrollOffsetMethod
						.invoke(v);
				final int horizontalScrollRange = (Integer) computeHorizontalScrollRangeMethod
						.invoke(v);

				return (direction > 0 && v.getScrollX() > 0)
						|| (direction < 0 && horizontalScrollOffset < horizontalScrollRange
								- v.getWidth());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public boolean canScrollVertical(View v, int direction) {
			// TODO
			return false;
		}

	}

	private static class HorizontalScrollViewScrollDetector implements
			ScrollDetector {

		@Override
		public boolean canScrollHorizontal(View v, int direction) {
			HorizontalScrollView horizontalScrollView = (HorizontalScrollView) v;
			final int scrollX = horizontalScrollView.getScrollX();
			if (0 == horizontalScrollView.getChildCount()) {
				return false;
			}
			return (direction < 0 && scrollX < horizontalScrollView.getChildAt(
					0).getWidth()
					- horizontalScrollView.getWidth())
					|| (direction > 0 && scrollX > 0);
		}

		@Override
		public boolean canScrollVertical(View v, int direction) {
			// TODO
			return false;
		}
	}

	private static class SwipeRefreshLayoutScrollDetector implements
			ScrollDetector {

		@Override
		public boolean canScrollHorizontal(View v, int direction) {
			return false;
		}

		@Override
		public boolean canScrollVertical(View v, int direction) {
			return true;
		}

	}

	public static void setScrollDetectorFactory(ScrollDetectorFactory factory) {
		mFactory = factory;
	}

	public interface ScrollDetector {
		public boolean canScrollHorizontal(View v, int direction);

		public boolean canScrollVertical(View v, int direction);
	}
}
