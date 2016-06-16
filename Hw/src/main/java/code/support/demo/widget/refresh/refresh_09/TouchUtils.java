package code.support.demo.widget.refresh.refresh_09;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zpj on 2016/6/14.
 */
public class TouchUtils extends ViewGroup implements NestedScrollingChild {

    private final NestedScrollingChildHelper mScrollingChildHelper;

    /**
     * Used during scrolling to retrieve the new offset within the window.
     */
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedYOffset;
    private int mLastMotionY;

    public TouchUtils(Context context) {
        super(context);
        mScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(l, t, r, b);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        MotionEvent vtev = MotionEvent.obtain(event);
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }
        vtev.offsetLocation(0, mNestedYOffset);

        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = (int) event.getY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                stopNestedScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                final int y = (int) MotionEventCompat.getY(event, 0);
                int deltaY = mLastMotionY - y;
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    vtev.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }
                final int scrolledDeltaY = getScrollY();
                final int unconsumedY = deltaY - scrolledDeltaY;
                dispatchNestedScroll(0, scrolledDeltaY, 0, unconsumedY, mScrollOffset);
                break;
        }
        return true;
    }

    public static ViewGroup wrap(View view) {
        Context context = view.getContext();
        TouchUtils wrapper = new TouchUtils(context);
        wrapper.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        return wrapper;
    }

    public boolean canChildDragDown(PointF pointF) {
        return canChildDragDownTraversal(this, pointF.x, pointF.y);
    }

    private boolean canChildDragDownTraversal(View view, float x, float y) {
        if (!inside(view, x, y)) return false;
        if (ViewCompat.canScrollVertically(view, -1)) return true;
        boolean canDragDown;
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            int count = vp.getChildCount();
            float newX = x - view.getLeft();
            float newY = y - view.getTop();
            View sub;
            for (int i = 0; i < count; i++) {
                sub = vp.getChildAt(i);
                canDragDown = canChildDragDownTraversal(sub, newX, newY);
                if (canDragDown) return true;
            }
        }
        return false;
    }

    private boolean inside(View view, float x, float y) {
        return view.getLeft() <= x && view.getRight() >= x && view.getTop() <= y && view.getBottom() >= y;
    }

    // NestedScrollingChild
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mScrollingChildHelper.isNestedScrollingEnabled();

    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    public static class TouchManager {

        public static final int INVALID_POINTER = -1;

        private TouchCallback mTouchCallback;
        private int mTouchSlop;
        private int mTopOffset;
        private boolean mBeginDragging;

        private int mThreshold = (int) (Resources.getSystem().getDisplayMetrics().density * 120 + 0.5f);
        private final int sThreshold2 = (int) (Resources.getSystem().getDisplayMetrics().density * 400 + 0.5f);

        private boolean mInterceptEnabled = true;
        private PointF mTmpPoint = new PointF();

        private int mActivePointerId = INVALID_POINTER;
        private float mTouchDownActor;
        private int mMotionX;

        public TouchManager(TouchCallback mTouchHelper) {
            this.mTouchCallback = mTouchHelper;
        }

        public void setTouchSlop(int touchSlop) {
            this.mTouchSlop = touchSlop;
        }

        public void endDrag() {
            mBeginDragging = false;
        }

        public float calExpandProgress(int currentTop) {
            return currentTop * 1.0f / mThreshold;
        }

        public int calTargetTopOffset(int currentTop) {
            return calTargetTopOffset(currentTop, getTopOffset());
        }

        public int calTargetTopOffset(int currentTop, int offset) {
            int target;
            if (currentTop <= sThreshold2) {
                if (offset < 0) {
                    target = 0 - currentTop;
                } else if (offset < sThreshold2) {
                    target = offset - currentTop;
                } else {
                    target = sThreshold2 - currentTop;
                }
            } else {
                target = sThreshold2 - currentTop;
            }
            return target;
        }

        public int getTopOffset() {
            return mTopOffset;
        }

        public void setInterceptEnabled(boolean interceptEnabled) {
            this.mInterceptEnabled = interceptEnabled;
        }

        public PointF event2Point(MotionEvent event) {
            mTmpPoint.set(event.getX(), event.getY());
            return mTmpPoint;
        }

        public boolean onFeedInterceptEvent(MotionEvent event) {
            int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    setActivePointerId(event, 0);
                    if (mBeginDragging) {
                        return true;
                    }
                    final float initialDownY = getCurrentMotionEventY(event);
                    if (initialDownY == -1) return false;
                    mTouchDownActor = initialDownY;
                    mBeginDragging = false;
                    if (mTouchCallback != null) mTouchCallback.onActionDown();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    resetActivePointerId();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mActivePointerId == TouchManager.INVALID_POINTER) {
                        return false;
                    }
                    final float y = getCurrentMotionEventY(event);
                    if (y == -1) {
                        return false;
                    }
                    if (mInterceptEnabled && !mBeginDragging && y - mTouchDownActor > mTouchSlop) {
                        mBeginDragging = true;
                        if (mTouchCallback != null) mTouchCallback.onBeginDragging();
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    onSecondaryPointerUp(event);
                    break;
            }
            return mBeginDragging;
        }

        private void setActivePointerId(MotionEvent event, int defaultId) {
            mActivePointerId = MotionEventCompat.getPointerId(event, defaultId);
            final float initialDownY = getCurrentMotionEventY(event);
            if (initialDownY == -1) return;
            if (mBeginDragging) {
                mTouchDownActor = motionY2TouchDown(initialDownY);
            }
        }

        private float motionY2TouchDown(float y) {
            float diff;
            if (mTopOffset < 0) {
                diff = 0;
            } else if (mTopOffset > mThreshold) {
                diff = (mTopOffset - mThreshold) / 0.3f / 0.6f + mThreshold / 0.6f;
            } else {
                diff = mTopOffset / 0.6f;
            }
            return y - diff;
        }

        private float getCurrentMotionEventY(MotionEvent ev) {
            final int index = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
            if (index < 0) {
                return -1;
            }
            return MotionEventCompat.getY(ev, index);
        }

        private void resetActivePointerId() {
            mActivePointerId = INVALID_POINTER;
        }

        private void onSecondaryPointerUp(MotionEvent ev) {
            final int pointerIndex = MotionEventCompat.getActionIndex(ev);
            final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
            if (pointerId == mActivePointerId) {
                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                setActivePointerId(ev, newPointerIndex);
            }
        }

        public boolean onFeedTouchEvent(MotionEvent event) {
            final int action = MotionEventCompat.getActionMasked(event);
            int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
            if (pointerIndex < 0) {
                return false;
            }
            final float y = MotionEventCompat.getY(event, pointerIndex);
            setTopOffset(y);

            boolean isExpanded = mTopOffset >= mThreshold && mBeginDragging;

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    setActivePointerId(event, 0);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mTouchCallback != null) mTouchCallback.onActionCancel(isExpanded);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mTouchCallback != null) mTouchCallback.onActionUp(isExpanded);
                    resetActivePointerId();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mMotionX = (int) MotionEventCompat.getX(event, pointerIndex);
                    if (mTouchCallback != null) mTouchCallback.onActionMove(isExpanded, this);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    pointerIndex = MotionEventCompat.getActionIndex(event);
                    if (pointerIndex < 0) {
                        return false;
                    }
                    setActivePointerId(event, pointerIndex);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    onSecondaryPointerUp(event);
                    break;
            }
            return true;
        }

        private void setTopOffset(float y) {
            mTopOffset = motionY2TopOffset(y);
        }

        private int motionY2TopOffset(float y) {
            float original = y - mTouchDownActor;
            float basic = original * 0.6f;
            if (basic > mThreshold) {
                basic = mThreshold + (basic - mThreshold) * 0.3f;
            }
            return (int) basic;
        }

        public boolean isBeginDragging() {
            return mBeginDragging;
        }

        public void setMaxHeight(int maxHeight) {
            this.mThreshold = maxHeight;
        }

        public int getMotionX() {
            return mMotionX;
        }
    }

    public interface TouchCallback {
        void onActionDown();

        void onActionMove(boolean isExpanded, TouchManager touchManager);

        void onActionUp(boolean isExpanded);

        void onActionCancel(boolean isExpanded);

        void onBeginDragging();
    }
}
