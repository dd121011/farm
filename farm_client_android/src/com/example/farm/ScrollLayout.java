package com.example.farm;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.view.View.OnClickListener;

;

public class ScrollLayout extends ViewGroup {
	private static final String TAG = "ScrollLayout";
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private int mCurScreen;
	private int mDefaultScreen = 0;

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;

	private static final int SNAP_VELOCITY = 600;

	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;

	public ScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
		// Ĭ����ʾ��һ��
		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();

			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
					// ������ 0 - 320 | 320 - 640 | 640 - 960 ...��������Ļ��320��
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.e(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		// Log.e(TAG, "moving to screen "+mCurScreen);
		// x���� y����
		// �ƶ����ڼ���
		scrollTo(mCurScreen * width, 0);
	}

	/**
	 * According to the position of current layout scroll to the destination
	 * page. �жϻ�����λ�� ������ڵ�ǰ���м��λ�� ���� ���� ��Ȼ������� getScrollX x�����ƫ����
	 */

	public void snapToDestination() {
		final int screenWidth = getWidth();
		// �ж�
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// get the valid layout page
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();
			// ��ʼ����
			// x��y��x�����ƶ�����y�����ƶ�������������ʱ�䣬���������
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout
		}
	}

	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	// ֻ�е�ǰLAYOUT�е�ĳ��CHILD����SCROLL�����������Ż���ʹ�Լ���COMPUTESCROLL������
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		Log.e(TAG, "computeScroll");
		// �������true����ʾ������û�н���
		// ��Ϊǰ��startScroll������ֻ����startScroll���ʱ �Ż�Ϊfalse
		if (mScroller.computeScrollOffset()) {
			Log.e(TAG, mScroller.getCurrX() + "======" + mScroller.getCurrY());
			// �����˶���Ч�� ÿ�ι���һ��
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onTouchEvent start");
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		View v = findViewById(R.id.button1);
		Log.e(TAG, "button on click" + x + "==" + y);
		Log.e(TAG, "button on click" + v.getX() + "==" + v.getY());
		if (x > v.getX() && x < v.getX() + v.getWidth() && y > v.getY()
				&& y < v.getY() + v.getHeight()) {
			Log.e(TAG, "button on click");
			v.onTouchEvent(event);
		}

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.e(TAG, "event down! " + mScroller.isFinished());
			// �����Ļ�Ĺ�������û�н��� ��Ͱ����� �ͽ�������
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			Log.e(TAG, "event move!");
			// ȡ��ƫ����
			int deltaX = (int) (mLastMotionX - x);
			Log.e(TAG, "detaX: " + deltaX);
			mLastMotionX = x;
			// x�����ƫ���� y�����ƫ����
			scrollBy(deltaX, 0);
			break;

		case MotionEvent.ACTION_UP:
			Log.e(TAG, "event : up");
			// if (mTouchState == TOUCH_STATE_SCROLLING) {
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			Log.e(TAG, "velocityX:" + velocityX);

			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				Log.e(TAG, "snap left");
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				Log.e(TAG, "snap right");
				snapToScreen(mCurScreen + 1);
			} else {
				// һ�㶼�������
				snapToDestination();
			}
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			// }
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	// ����о�ûʲô���� ����true����false ���ǻ�ִ��onTouchEvent�� ��Ϊ��view����onTouchEvent����false��
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onInterceptTouchEvent-slop:" + mTouchSlop);
		/*
		 * final int action = ev.getAction(); if ((action ==
		 * MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
		 * return true; }
		 * 
		 * final float x = ev.getX(); final float y = ev.getY();
		 * 
		 * switch (action) { case MotionEvent.ACTION_MOVE:
		 * Log.e(TAG,"onInterceptTouchEvent move"); final int xDiff =
		 * (int)Math.abs(mLastMotionX-x); if (xDiff>mTouchSlop) { mTouchState =
		 * TOUCH_STATE_SCROLLING;
		 * 
		 * } break;
		 * 
		 * case MotionEvent.ACTION_DOWN:
		 * Log.e(TAG,"onInterceptTouchEvent down"); mLastMotionX = x;
		 * mLastMotionY = y; Log.e(TAG,mScroller.isFinished()+""); mTouchState =
		 * mScroller.isFinished()? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
		 * break;
		 * 
		 * case MotionEvent.ACTION_CANCEL: case MotionEvent.ACTION_UP:
		 * Log.e(TAG,"onInterceptTouchEvent up or cancel"); mTouchState =
		 * TOUCH_STATE_REST; break; }
		 * Log.e(TAG,mTouchState+"===="+TOUCH_STATE_REST); return mTouchState !=
		 * TOUCH_STATE_REST;
		 */
		return true;
	}
}
