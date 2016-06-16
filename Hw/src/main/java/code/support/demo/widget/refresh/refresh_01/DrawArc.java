package code.support.demo.widget.refresh.refresh_01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;

import code.support.demo.util.UtilDensity;

/**
 * Created by Design on 2016/5/11.
 */
public class DrawArc extends RefreshDrawable {

    private static final int MAX_LEVEL = 200;

    private boolean isRunning;
    private RectF mBounds;
    private int mWidth;
    private int mHeight;
    private int mTop;
    private int mOffsetTop;
    private Paint mPaint;
    private float mAngle;
    private int[] mColorSchemeColors;
    private Handler mHandler = new Handler();
    private int mLevel;

    private Runnable mAnimationTask = new Runnable() {
        @Override
        public void run() {
            if (isRunning()) {
                mLevel++;
                if (mLevel > MAX_LEVEL) {
                    mLevel = 0;
                }
                updateLevel(mLevel);
                invalidateSelf();
                mHandler.postDelayed(this, 20);
            }
        }
    };

    DrawArc(Context context, RefreshLayout layout) {
        super(context, layout);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
    }

    @Override
    public void setPercent(float percent) {
        mPaint.setColor(evaluate(percent, mColorSchemeColors[3], mColorSchemeColors[0]));
    }

    @Override
    public void setColorSchemeColors(int[] colorSchemeColors) {
        mColorSchemeColors = colorSchemeColors;
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        mTop += offset;
        mOffsetTop += offset;
        float offsetTop = mOffsetTop;
        if (mOffsetTop > getRefreshLayout().getFinalOffset()) {
            offsetTop = getRefreshLayout().getFinalOffset();
        }
        mAngle = 360 * (offsetTop / getRefreshLayout().getFinalOffset());
        invalidateSelf();
    }

    private void updateLevel(int level) {
        int animationLevel = level == MAX_LEVEL ? 0 : level;
        int stateForLevel = (animationLevel / 50);
        float percent = level % 50 / 50f;
        int startColor = mColorSchemeColors[stateForLevel];
        int endColor = mColorSchemeColors[(stateForLevel + 1) % mColorSchemeColors.length];
        mPaint.setColor(evaluate(percent, startColor, endColor));
    }

    private int evaluate(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24) | ((startR + (int) (fraction * (endR - startR))) << 16) | ((startG + (int) (fraction * (endG - startG))) << 8) | ((startB + (int) (fraction * (endB - startB))));
    }

    @Override
    public void start() {
        isRunning = true;
        mHandler.post(mAnimationTask);
    }

    @Override
    public void stop() {
        isRunning = false;
        mHandler.removeCallbacks(mAnimationTask);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        drawRing(canvas);
        canvas.restore();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mWidth = UtilDensity.dp2px(getContext(), 40);
        mHeight = mWidth;
        mBounds = new RectF(bounds.width() / 2 - mWidth / 2, bounds.top, bounds.width() / 2 + mWidth / 2, bounds.top + mHeight);
    }

    private void drawRing(Canvas canvas) {
        canvas.drawArc(mBounds, 270, mAngle, true, mPaint);
    }
}
