package com.liferay.mobile.screens.westerosemployees.Views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import com.liferay.mobile.screens.westerosemployees.utils.CardState;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Víctor Galán Grande
 */

public class Card extends FrameLayout {

	public static final int MARGIN = 30;
	public static final int DURATION_MILLIS = 300;
	public static final int CARD_SIZE = 250;

	public static int NORMAL_Y = MARGIN * 4;
	public static int BACKGROUND_Y = MARGIN;

	public Card(Context context) {
		super(context);
	}

	public Card(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Card(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public Card(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		initializeSize();

		arrows = getViewsByTag(this, "arrow");
	}

	private void initializeSize() {
		if (maxWidth != 0 && maxHeight != 0) {
			takeSubviewsOffScreen();
		} else {
			getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						removeObserver();
					} else {
						getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}

					maxWidth = getWidth();
					maxHeight = getHeight();

					takeSubviewsOffScreen();
				}

				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				private void removeObserver() {
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
			});
		}
	}

	private void takeSubviewsOffScreen() {
		for (int i = 1; i < getChildCount(); i++) {
			getChildAt(i).setX(maxWidth);
		}
	}

	public void initPosition(int minimizedPosition) {
		this.minimizedPosition = minimizedPosition;

		setY(minimizedPosition);
	}

	public void setState(CardState state) {
		switch (state) {
			case BACKGROUND:
				animate().setDuration(DURATION_MILLIS).scaleX(0.95f);
				animate().setDuration(DURATION_MILLIS).translationY(BACKGROUND_Y);
				break;
			case NORMAL:
				showArrows(true);
				animate().setDuration(DURATION_MILLIS).scaleX(1f);
				animate().setDuration(DURATION_MILLIS).translationY(NORMAL_Y);
				break;
			case MINIMIZED:
				showArrows(false);
				animate().setDuration(DURATION_MILLIS).translationY(minimizedPosition);
				break;
			case MAXIMIZED:
				animate().setDuration(DURATION_MILLIS).translationY(0);
				break;

			case HIDDEN:
				animate().setDuration(DURATION_MILLIS).translationY(maxHeight);
				break;
		}

		cardState = state;
	}

	public void goRight() {

		View current = getChildAt(index);
		View next = getChildAt(index + 1);

		if (next != null) {
			index++;
			current.animate().translationX(-maxWidth);
			next.animate().translationX(0);
		}
	}

	public void goLeft() {

		View current = getChildAt(index);
		View next = getChildAt(index - 1);

		if (next != null) {
			index--;
			next.animate().translationX(0);
			current.animate().translationX(maxWidth);
		}
	}

	protected void showArrows(boolean show) {
		int visibility = show ? VISIBLE : INVISIBLE;

		for (View arrow : arrows) {
			arrow.setVisibility(visibility);
		}
	}

	protected static List<View> getViewsByTag(ViewGroup root, String tag){
		List<View> views = new ArrayList<>();
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);
			if (child instanceof ViewGroup) {
				views.addAll(getViewsByTag((ViewGroup) child, tag));
			}

			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.equals(tag)) {
				views.add(child);
			}
		}

		return views;
	}

	public CardState getCardState() {
		return cardState;
	}


	protected List<View> arrows;
	protected int index;

	protected int maxWidth;
	protected int maxHeight;

	protected int minimizedPosition;

	protected CardState cardState;
}
