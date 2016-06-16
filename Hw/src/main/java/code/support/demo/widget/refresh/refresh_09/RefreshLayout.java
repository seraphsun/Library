package code.support.demo.widget.refresh.refresh_09;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ScrollView;

import java.util.LinkedList;
import java.util.List;

import code.support.demo.R;

public class RefreshLayout extends ViewGroup implements TouchUtils.TouchCallback, NestedScrollingParent {

    private final TouchUtils.TouchManager mTouchManager = new TouchUtils.TouchManager(this);

    private View mTarget; // the target of the gesture
    private HeaderView mChromeLikeLayout;

    private final NestedScrollingChildHelper mScrollingChildHelper;
    private final NestedScrollingParentHelper mScrollingParentHelper;
    private final StatusManager mStatusManager = new StatusManager();

    private boolean mAnimationStarted;
    private int mCollapseDuration = 300;
    int[] offsets = new int[2];

    private boolean mEnabled = true;

    private OnItemSelectedListener mOnItemSelectedListener;
    private LinkedList<OnExpandViewListener> mExpandListeners = new LinkedList<>();

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        Config config = makeConfig();
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.refresh_9, defStyleAttr, 0);
        if (ta != null) {
            if (ta.hasValue(R.styleable.refresh_9_clwl_circleColor))
                config.circleColor(ta.getColor(R.styleable.refresh_9_clwl_circleColor, Config.DEFAULT));
            if (ta.hasValue(R.styleable.refresh_9_clwl_gap))
                config.gap(ta.getDimensionPixelOffset(R.styleable.refresh_9_clwl_gap, Config.DEFAULT));
            if (ta.hasValue(R.styleable.refresh_9_clwl_radius))
                config.radius(ta.getDimensionPixelOffset(R.styleable.refresh_9_clwl_radius, Config.DEFAULT));
            if (ta.hasValue(R.styleable.refresh_9_clwl_collapseDuration))
                config.collapseDuration(ta.getInt(R.styleable.refresh_9_clwl_collapseDuration, Config.DEFAULT));
            if (ta.hasValue(R.styleable.refresh_9_clwl_rippleDuration))
                config.rippleDuration(ta.getInt(R.styleable.refresh_9_clwl_rippleDuration, Config.DEFAULT));
            if (ta.hasValue(R.styleable.refresh_9_clwl_gummyDuration))
                config.gummyDuration(ta.getInt(R.styleable.refresh_9_clwl_gummyDuration, Config.DEFAULT));
            if (ta.hasValue(R.styleable.refresh_9_clwl_maxHeight))
                config.setMaxHeight(ta.getDimensionPixelOffset(R.styleable.refresh_9_clwl_maxHeight, Config.DEFAULT));
            ta.recycle();
        }
        config.setTo(this);

        // init NestedScrollingChildHelper
        mScrollingChildHelper = new NestedScrollingChildHelper(this);
        mScrollingParentHelper = new NestedScrollingParentHelper(this);
        setNestedScrollingEnabled(true);
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchManager.setTouchSlop((int) (configuration.getScaledTouchSlop() * 1.1f));

        mChromeLikeLayout = new HeaderView(getContext());
        mChromeLikeLayout.setRippleListener(new HeaderView.OnRippleListener() {
            @Override
            public void onRippleAnimFinished(int index) {
                mStatusManager.toRestore();
                if (!mAnimationStarted) launchResetAnim();
                mTouchManager.endDrag();
                if (mOnItemSelectedListener != null) mOnItemSelectedListener.onItemSelected(index);
            }
        });
        addOnExpandViewListener(mChromeLikeLayout);
        addView(mChromeLikeLayout);
    }

    private void launchResetAnim() {
        boolean isFromCancel = !mStatusManager.isRestoring();
        launchResetAnim(isFromCancel);
    }

    private void launchResetAnim(final boolean isFromCancel) {
        ensureTarget();

        final int from = mTarget.getTop();
        final int to = 0;
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float step = (to - from) * interpolatedTime + from;
                int top = mTarget.getTop();
                notifyOnExpandListeners(mTouchManager.calExpandProgress(top), isFromCancel);
                childOffsetTopAndBottom(mTouchManager.calTargetTopOffset(top, Math.round(step)));
            }
        };
        animation.setDuration(mCollapseDuration);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mAnimationStarted = false;
                mStatusManager.toIdle();
            }
        });
        this.clearAnimation();
        this.startAnimation(animation);
        mAnimationStarted = true;
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mChromeLikeLayout)) {
                    mTarget = child;
                    mChromeLikeLayout.bringToFront();
                    break;
                }
            }
        }
    }

    public void notifyOnExpandListeners(float fraction, boolean isFromCancel) {
        fraction = fraction < 1 ? fraction : 1;
        for (OnExpandViewListener listener : mExpandListeners)
            listener.onExpandView(fraction, isFromCancel);
    }

    private void childOffsetTopAndBottom(int target) {
        mTarget.offsetTopAndBottom(target);
        mChromeLikeLayout.offsetTopAndBottom(target);
        requestLayout();
    }

    public void addOnExpandViewListener(OnExpandViewListener listener) {
        mExpandListeners.add(listener);
    }

    public static Config makeConfig() {
        return new Config();
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

    // mScrollingParentHelper
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public int getNestedScrollAxes() {
        return mScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mScrollingParentHelper.onStopNestedScroll(target);
    }

    // do nothing now
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        boolean result = this.startNestedScroll(nestedScrollAxes);
        if (result) mTouchManager.setInterceptEnabled(false);
        return true;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        boolean result = this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsets);
        if (result) {
            boolean consumed = (offsets[1] + dyUnconsumed) == 0 && dyUnconsumed != 0;
            mTouchManager.setInterceptEnabled(!consumed);
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        this.dispatchNestedPreScroll(dx, dy, consumed, offsets);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        final int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mTarget.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        mChromeLikeLayout.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mTarget.getTop(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        View child = mTarget;
        int childLeft = getPaddingLeft();
        int childTop = child.getTop();
        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

        child = mChromeLikeLayout;
        childLeft = getPaddingLeft();
        childTop = mTarget.getTop() - child.getMeasuredHeight();
        childWidth = width - getPaddingLeft() - getPaddingRight();
        childHeight = child.getMeasuredHeight();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        boolean touchAlwaysTrue = child instanceof ScrollView || child instanceof AbsListView || child instanceof ScrollingView || child instanceof TouchUtils || child instanceof HeaderView;

        if (!touchAlwaysTrue) child = TouchUtils.wrap(child);
        super.addView(child, index, params);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        super.requestDisallowInterceptTouchEvent(b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mEnabled && !mAnimationStarted && !canChildDragDown(mTouchManager.event2Point(event)) && mTouchManager.onFeedInterceptEvent(event);
    }

    private boolean canChildDragDown(PointF pointF) {
        ensureTarget();
        if (mTarget instanceof TouchUtils)
            return ((TouchUtils) mTarget).canChildDragDown(pointF);
        else return ViewCompat.canScrollVertically(mTarget, -1);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mTouchManager.onFeedTouchEvent(event);
    }

    @Override
    public void onActionDown() {
        mChromeLikeLayout.onActionDown();
    }

    @Override
    public void onActionMove(boolean isExpanded, TouchUtils.TouchManager touchManager) {
        mChromeLikeLayout.onActionMove(isExpanded, touchManager);
        ensureTarget();
        View child = mTarget;
        int currentTop = child.getTop();
        if (mTouchManager.isBeginDragging()) {
            if (!isExpanded)
                notifyOnExpandListeners(mTouchManager.calExpandProgress(currentTop), true);
            childOffsetTopAndBottom(mTouchManager.calTargetTopOffset(currentTop));
        }
    }

    @Override
    public void onActionUp(boolean isExpanded) {
        executeAction(isExpanded);
        mChromeLikeLayout.onActionUpOrCancel(isExpanded);
    }

    private void executeAction(boolean isExpanded) {
        if (isExpanded) {
            mStatusManager.toBusy();
        } else {
            if (mStatusManager.isBusying()) return;
            if (mAnimationStarted) return;
            launchResetAnim();
            mTouchManager.endDrag();
        }
    }

    @Override
    public void onActionCancel(boolean isExpanded) {
        mChromeLikeLayout.onActionUpOrCancel(isExpanded);
    }

    @Override
    public void onBeginDragging() {
        mStatusManager.toChanged();
    }

    private void setConfig(Config config) {
        if (config.mIcons != null) mChromeLikeLayout.setIcons(config.mIcons);
        if (config.mBackgroundResId != Config.DEFAULT)
            mChromeLikeLayout.setBackgroundResource(config.mBackgroundResId);
        if (config.mBackgroundColor != Config.DEFAULT)
            mChromeLikeLayout.setBackgroundColor(config.mBackgroundColor);
        if (config.mCircleColor != Config.DEFAULT)
            mChromeLikeLayout.setCircleColor(config.mCircleColor);
        if (config.mRadius != Config.DEFAULT) mChromeLikeLayout.setRadius(config.mRadius);
        if (config.mRadius != Config.DEFAULT) mChromeLikeLayout.setGap(config.mGap);
        if (config.mRippleDuration != Config.DEFAULT)
            mChromeLikeLayout.setRippleDuration(config.mRippleDuration);
        if (config.mGummyDuration != Config.DEFAULT)
            mChromeLikeLayout.setGummyDuration(config.mGummyDuration);
        if (config.mCollapseDuration != Config.DEFAULT)
            setCollapseDuration(config.mCollapseDuration);
        if (config.maxHeight != Config.DEFAULT) mTouchManager.setMaxHeight(config.maxHeight);

        mOnItemSelectedListener = config.mOnItemSelectedListener;
    }

    private void setCollapseDuration(int collapseDuration) {
        this.mCollapseDuration = collapseDuration;
    }

    public void removeOnExpandViewListener(OnExpandViewListener listener) {
        mExpandListeners.remove(listener);
    }

    public void removeAllOnExpandViewListener() {
        mExpandListeners.clear();
    }

    /**
     * Config Builder
     */
    public static class Config {

        public List<Integer> mIcons;
        private OnItemSelectedListener mOnItemSelectedListener;
        private int mCircleColor = DEFAULT;
        private int mBackgroundResId = DEFAULT;
        private int mBackgroundColor = DEFAULT;
        private int mRadius = DEFAULT;
        private int mGap = DEFAULT;
        private int mCollapseDuration = DEFAULT;
        private int mRippleDuration = DEFAULT;
        private int mGummyDuration = DEFAULT;
        private int maxHeight = DEFAULT;
        private static final int DEFAULT = -1;

        private Config() {
        }

        public Config addIcon(@DrawableRes int drawableResId) {
            if (mIcons == null) mIcons = new LinkedList<>();
            mIcons.add(drawableResId);
            return this;
        }

        public Config background(@DrawableRes int backgroundResId) {
            this.mBackgroundResId = backgroundResId;
            return this;
        }

        public Config backgroundColor(@ColorInt int color) {
            this.mBackgroundColor = color;
            return this;
        }

        public Config circleColor(@ColorInt int color) {
            this.mCircleColor = color;
            return this;
        }

        public Config listenItemSelected(OnItemSelectedListener listener) {
            this.mOnItemSelectedListener = listener;
            return this;
        }

        public Config radius(int radius) {
            this.mRadius = radius;
            return this;
        }

        public Config gap(int gap) {
            this.mGap = gap;
            return this;
        }

        public Config collapseDuration(int duration) {
            this.mCollapseDuration = duration;
            return this;
        }

        public Config rippleDuration(int duration) {
            this.mRippleDuration = duration;
            return this;
        }

        public Config gummyDuration(int duration) {
            this.mGummyDuration = duration;
            return this;
        }

        public Config setMaxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public void setTo(RefreshLayout refreshLayout) {
            refreshLayout.setConfig(this);
        }
    }

    public static class AnimationListenerAdapter implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int index);
    }

    public interface OnExpandViewListener {
        void onExpandView(float fraction, boolean isFromCancel);
    }

    /**
     * Response for managing dropdown status
     */
    public static class StatusManager {

        private int mStatus = STATUS_IDLE;
        private static final int STATUS_IDLE = 0;
        private static final int STATUS_CHANGED = 1;
        private static final int STATUS_BUSY = 2;
        private static final int STATUS_RESTORE = 3;

        public void toIdle() {
            mStatus = STATUS_IDLE;
        }

        public void toBusy() {
            mStatus = STATUS_BUSY;
        }

        public void toRestore() {
            mStatus = STATUS_RESTORE;
        }

        public void toChanged() {
            mStatus = STATUS_CHANGED;
        }

        public boolean isChanged() {
            return mStatus == STATUS_CHANGED;
        }

        public boolean isBusying() {
            return mStatus == STATUS_BUSY;
        }

        public boolean isRestoring() {
            return mStatus == STATUS_RESTORE;
        }

        public boolean isIdle() {
            return mStatus == STATUS_IDLE;
        }
    }


}
