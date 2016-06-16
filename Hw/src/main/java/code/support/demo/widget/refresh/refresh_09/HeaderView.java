package code.support.demo.widget.refresh.refresh_09;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Transformation;

import java.util.List;

import code.support.demo.util.UtilDensity;

public class HeaderView extends ViewGroup implements RefreshLayout.OnExpandViewListener {

    private TouchHelper mTouchHelper;
    private Threshold mAnimCancelThreshold;

    private static final float sMagicNumber = 0.55228475f;
    private static final int sDefaultCircleColor = 0xFFFFCC11;
    private static final int sDefaultBackgroundColor = 0xFF333333;

    private static final float sThreshold = 0.5f;
    private static final float sAnimCancelThreshold = 0.75f;
    private int mRadius = UtilDensity.dp2px(getContext(), 40);
    private int mGap = UtilDensity.dp2px(getContext(), 15);

    private Paint mPaint;
    private Path mPath;
    private float mDegrees;
    private float mTranslate;
    private int mCurrentFlag;

    private static final float sFactorScaleCircle = 0.75f;
    private static final float sFactorScaleIcon = 0.3f;

    private OnRippleListener mRippleListener;
    private GummyAnimatorHelper mGummyAnimatorHelper = new GummyAnimatorHelper();
    private RippleAnimatorHelper mRippleAnimatorHelper = new RippleAnimatorHelper();

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchHelper = new TouchHelper(configuration.getScaledTouchSlop());
        mAnimCancelThreshold = new Threshold(getMovingThreshold() * sAnimCancelThreshold);

        setBackgroundColor(sDefaultBackgroundColor);

        mPaint = new Paint();
        mPaint.setColor(sDefaultCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mPath = new Path();
        reset();
        setWillNotDraw(false);
    }

    private float getMovingThreshold() {
        return getItemWidth() * sThreshold;
    }

    private int getItemWidth() {
        return mRadius * 2 + mGap;
    }

    private void reset() {
        onExpandView(0, false);
        updateAlpha(1);
        updateCurrentFlag((getChildCount() - 1) >> 1);
        mTranslate = flag2TargetTranslate();
    }

    private void updateCurrentFlag(int flag) {
        mCurrentFlag = flag;
        boolean isPressed;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            isPressed = i == mCurrentFlag;
            view.setPressed(isPressed);
        }
    }

    private void updateAlpha(float alpha) {
        mPaint.setAlpha(Math.round(255 * alpha));
    }

    private int flag2TargetTranslate() {
        int startXOffset = getCircleStartX();
        return startXOffset + getItemWidth() * mCurrentFlag;
    }

    private int getCircleStartX() {
        int contentWidth = getItemWidth();
        int totalWidth = getMeasuredWidth();
        int totalContextWidth = contentWidth * (getChildCount() - 1);
        return (totalWidth - totalContextWidth) >> 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int startXOffset = getCircleStartX();
        int startYOffset = (b - t);

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            final int left = startXOffset + i * getItemWidth() - view.getMeasuredWidth() / 2;
            final int right = left + view.getMeasuredWidth();
            final int top = (startYOffset - view.getMeasuredHeight()) >> 1;
            final int bottom = top + view.getMeasuredHeight();
            view.layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getChildCount() == 0) return;
        int centerY = getMeasuredHeight() >> 1;

        canvas.save();
        canvas.translate(mTranslate, centerY);
        canvas.rotate(mDegrees);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    @Override
    public void onExpandView(float fraction, boolean isFromCancel) {
        float circleFraction = circleOffsetFraction(fraction);
        if (isFromCancel) updateAlpha(circleFraction);
        updatePath(0, 0, Math.round(mRadius * circleFraction), true);
        updateIconScale(fraction);
    }

    private float circleOffsetFraction(float fraction) {
        return offsetFraction(fraction, sFactorScaleCircle);
    }

    private float offsetFraction(float fraction, float factor) {
        float result = (fraction - factor) / (1 - factor);
        result = result > 0 ? result : 0;
        return result;
    }

    private void updatePath(float currentX, float prevX, int radius, boolean animate) {
        updatePath(currentX, 0, prevX, 0, radius, animate);
    }

    private void updatePath(float currentX, float currentY, float prevX, float prevY, int radius, boolean animate) {
        float distance = distance(prevX, prevY, currentX, currentY);
        float tempDegree = points2Degrees(prevX, prevY, currentX, currentY);
        if (animate) {
            if (Math.abs(mDegrees - tempDegree) > 5) distance = -distance;
        } else {
            // if ( distance < mTouchSlop ) distance = 0;
            mDegrees = tempDegree;
        }
        float longRadius = radius + distance;
        float shortRadius = radius - distance * 0.1f;

        mPath.reset();

        mPath.lineTo(0, -radius);
        mPath.cubicTo(radius * sMagicNumber, -radius, longRadius, -radius * sMagicNumber, longRadius, 0);
        mPath.lineTo(0, 0);

        mPath.lineTo(0, radius);
        mPath.cubicTo(radius * sMagicNumber, radius, longRadius, radius * sMagicNumber, longRadius, 0);
        mPath.lineTo(0, 0);

        mPath.lineTo(0, -radius);
        mPath.cubicTo(-radius * sMagicNumber, -radius, -shortRadius, -radius * sMagicNumber, -shortRadius, 0);
        mPath.lineTo(0, 0);

        mPath.lineTo(0, radius);
        mPath.cubicTo(-radius * sMagicNumber, radius, -shortRadius, radius * sMagicNumber, -shortRadius, 0);
        mPath.lineTo(0, 0);

        invalidate();
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private static float points2Degrees(float x1, float y1, float x2, float y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        return (float) Math.toDegrees(angle);
    }

    private void updateIconScale(float fraction) {
        float iconFraction = iconOffsetFraction(fraction);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            ViewCompat.setScaleX(v, iconFraction);
            ViewCompat.setScaleY(v, iconFraction);
        }
    }

    private float iconOffsetFraction(float fraction) {
        return offsetFraction(fraction, sFactorScaleIcon);
    }

    public void setRippleListener(OnRippleListener mRippleListener) {
        this.mRippleListener = mRippleListener;
    }

    public void setIcons(List<Integer> drawables) {
        this.removeAllViews();
        for (int res : drawables) {
            View v = new View(getContext());
            v.setBackgroundResource(res);
            addView(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
    }

    public void setCircleColor(int circleColor) {
        mPaint.setColor(circleColor);
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    public void setGap(int gap) {
        this.mGap = gap;
    }

    public void setRippleDuration(int duration) {
        mRippleAnimatorHelper.setDuration(duration);
    }

    public void setGummyDuration(int duration) {
        mGummyAnimatorHelper.setDuration(duration);
    }

    public void onActionDown() {
        reset();
    }

    public void onActionMove(boolean isExpanded, TouchUtils.TouchManager touchManager) {
        int motionX = touchManager.getMotionX();
        if (!mTouchHelper.isExpanded() && isExpanded) {
            mTouchHelper.feed(motionX);
            return;
        }

        if (!isExpanded) {
            mTouchHelper.reset();
            return;
        }

        mTouchHelper.feed(motionX);

        if (!mTouchHelper.isMoving()) {
            updateAlpha(1);
            updatePath(0, 0, mRadius, false);
            updateIconScale(1);
            return;
        }

        if (mGummyAnimatorHelper.isAnimationStarted()) {
            float currentX = mTouchHelper.getCurrentX();
            if (mAnimCancelThreshold.absOverflow(currentX)) {
                mGummyAnimatorHelper.end();
                mTouchHelper.resetToReady(currentX);
            }
            return;
        }

        if (mCurrentFlag == prevOfCurrentFlag()) mTouchHelper.testLeftEdge();
        if (mCurrentFlag == nextOfCurrentFlag()) mTouchHelper.testRightEdge();

        float currentX = mTouchHelper.getCurrentX();
        float prevX = mTouchHelper.getPrevX();

        updateAlpha(1);
        updatePath(currentX, prevX, mRadius, false);
        updateIconScale(1);

        if (Math.abs(currentX - prevX) > getMovingThreshold()) {
            if (currentX > prevX) updateCurrentFlag(nextOfCurrentFlag());
            else updateCurrentFlag(prevOfCurrentFlag());
            mAnimCancelThreshold.reset();
            mGummyAnimatorHelper.launchAnim(currentX, prevX, mTranslate, flag2TargetTranslate());
        }
    }

    private int prevOfCurrentFlag() {
        int tmp = mCurrentFlag;
        tmp--;
        return Math.max(tmp, 0);
    }

    private int nextOfCurrentFlag() {
        int tmp = mCurrentFlag;
        tmp++;
        return Math.min(tmp, getChildCount() - 1);
    }

    public void onActionUpOrCancel(boolean isExpanded) {
        if (getChildCount() == 0) return;
        if (!mTouchHelper.isExpanded()) return;
        mTouchHelper.reset();

        if (isExpanded) {
            boolean isRippleAnimEnabled = getChildCount() > 0;
            if (isRippleAnimEnabled) {
                if (mRippleAnimatorHelper.isAnimationStarted()) return;
                if (mGummyAnimatorHelper.isAnimationStarted()) {
                    mGummyAnimatorHelper.end();
                }
                mRippleAnimatorHelper.launchAnim(mRadius, getMeasuredWidth());
            } else {
                if (mRippleListener != null) mRippleListener.onRippleAnimFinished(-1);
            }
        }
    }

    private class GummyAnimatorHelper extends RefreshLayout.AnimationListenerAdapter {

        private float mAnimFromX;
        private float mAnimToX;
        private float mAnimFromTranslate;
        private float mAnimToTranslate;
        private boolean mAnimationStarted;
        private int mDuration = 300;

        public void onAnimationUpdate(float interpolation) {
            Float currentX = FloatEvaluator.evaluate(interpolation, mAnimFromX, mAnimToX);
            mTranslate = FloatEvaluator.evaluate(interpolation, mAnimFromTranslate, mAnimToTranslate);
            updatePath(currentX, mAnimToX, mRadius, true);
        }

        public void launchAnim(float fromX, float toX, float fromTranslate, float toTranslate) {
            mAnimFromX = fromX;
            mAnimToX = toX;
            mAnimFromTranslate = fromTranslate;
            mAnimToTranslate = toTranslate;

            Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    onAnimationUpdate(interpolatedTime);
                }
            };
            animation.setDuration(mDuration);
            animation.setInterpolator(new BounceInterpolator());
            animation.setAnimationListener(this);
            HeaderView.this.clearAnimation();
            HeaderView.this.startAnimation(animation);

            mAnimationStarted = true;
        }

        public boolean isAnimationStarted() {
            return mAnimationStarted;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mAnimationStarted) {
                mTouchHelper.resetToReady(mAnimFromX);
                mAnimationStarted = false;
            }
        }

        public void setDuration(int duration) {
            this.mDuration = duration;
        }

        public void end() {
            // false immediately
            mAnimationStarted = false;

            // clear animation
            HeaderView.this.clearAnimation();

            // anim to end immediately
            onAnimationUpdate(1);
        }
    }

    private static class FloatEvaluator {
        public static Float evaluate(float fraction, Number startValue, Number endValue) {
            float startFloat = startValue.floatValue();
            return startFloat + fraction * (endValue.floatValue() - startFloat);
        }
    }

    private class RippleAnimatorHelper extends RefreshLayout.AnimationListenerAdapter {

        private float mAnimFromRadius;
        private float mAnimToRadius;
        private boolean mAnimationStarted;
        private boolean mEventDispatched;
        private int mDuration = 300;

        public void onAnimationUpdate(float interpolation) {
            int currentRadius = FloatEvaluator.evaluate(interpolation, mAnimFromRadius, mAnimToRadius).intValue();
            updatePath(0, 0, currentRadius, true);
            updateAlpha(1 - interpolation);
        }

        public void launchAnim(float fromRadius, float toRadius) {

            mAnimFromRadius = fromRadius;
            mAnimToRadius = toRadius;
            Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    onAnimationUpdate(interpolatedTime);
                }
            };
            animation.setDuration(mDuration);
            animation.setInterpolator(new FastOutSlowInInterpolator());
            animation.setAnimationListener(this);

            View target = HeaderView.this.getChildAt(mCurrentFlag);
            if (target == null) return;
            target.clearAnimation();
            target.startAnimation(animation);
            mAnimationStarted = true;
            mEventDispatched = false;
        }

        public boolean isAnimationStarted() {
            return mAnimationStarted;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mAnimationStarted = false;
            if (!mEventDispatched && mRippleListener != null) {
                mRippleListener.onRippleAnimFinished(mCurrentFlag);
                mEventDispatched = true;
            }
        }

        public void setDuration(int duration) {
            this.mDuration = duration;
        }
    }

    public static class TouchHelper {

        private final int mTouchSlop;
        private int mStatus;
        private final int STATUS_NONE = 0;
        private final int STATUS_EXPANDED = 1;
        private final int STATUS_READY = 2;
        private final int STATUS_MOVING = 3;
        private float mReadyPrevX;
        private float mMovingPrevX;
        private float mMovingCurrentX;

        public TouchHelper(int mTouchSlop) {
            this.mTouchSlop = mTouchSlop;
        }

        public boolean isMoving() {
            return mStatus == STATUS_MOVING;
        }

        public boolean isExpanded() {
            return mStatus > STATUS_NONE;
        }

        public void feed(float motionX) {
            int status = mStatus;
            // float tmpX = MotionEventCompat.getX(event,pointerIndex);
            switch (status) {
                case STATUS_NONE:
                case STATUS_EXPANDED:
                    mReadyPrevX = motionX;
                    mStatus = STATUS_READY;
                    break;
                case STATUS_READY:
                    if (Math.abs(motionX - mReadyPrevX) > mTouchSlop) {
                        mMovingPrevX = motionX;
                        mMovingCurrentX = motionX;
                        mStatus = STATUS_MOVING;
                    }
                    break;
                case STATUS_MOVING:
                    mMovingCurrentX = motionX;
                    break;
            }
        }

        public float getPrevX() {
            return mMovingPrevX;
        }

        public float getCurrentX() {
            return mMovingCurrentX;
        }

        public void resetToReady(float animFromX) {
            mStatus = STATUS_READY;
            mReadyPrevX = animFromX;
        }

        public void reset() {
            mStatus = STATUS_NONE;
            mReadyPrevX = 0;
            mMovingPrevX = 0;
        }

        public void testLeftEdge() {
            if (mMovingCurrentX < mMovingPrevX) mMovingPrevX = mMovingCurrentX;
        }

        public void testRightEdge() {
            if (mMovingCurrentX > mMovingPrevX) mMovingPrevX = mMovingCurrentX;
        }
    }

    public static class Threshold {

        private boolean mInit;
        private float mPrev;
        private float mThreshold;

        public Threshold(float threshold) {
            this.mThreshold = threshold;
        }

        private boolean checkAbsOverflow(float now) {
            return Math.abs(now - mPrev) > mThreshold;
        }

        public boolean absOverflow(float value) {
            if (!mInit) {
                mPrev = value;
                mInit = true;
                return false;
            }
            return checkAbsOverflow(value);
        }

        public void reset() {
            mInit = false;
            mPrev = 0;
        }
    }

    public interface OnRippleListener {
        void onRippleAnimFinished(int index);
    }
}
