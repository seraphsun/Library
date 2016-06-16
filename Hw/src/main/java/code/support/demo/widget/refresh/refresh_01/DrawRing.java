package code.support.demo.widget.refresh.refresh_01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;

import code.support.demo.util.UtilDensity;

/**
 * Created by Design on 2016/5/11.
 */
public class DrawRing extends RefreshDrawable {

    private static final int MAX_LEVEL = 200;

    private boolean isRunning;
    private RectF mBounds;
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private Path mPath;
    private float mAngle;
    private int[] mColorSchemeColors;
    private Handler mHandler = new Handler();
    private int mLevel;
    private float mDegress;

    DrawRing(Context context, RefreshLayout layout) {
        super(context, layout);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(UtilDensity.dp2px(getContext(), 3));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath = new Path();
    }

    @Override
    public void setPercent(float percent) {
        mPaint.setColor(evaluate(percent, mColorSchemeColors[0], mColorSchemeColors[1]));
        mAngle = 340 * percent;
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
    public void setColorSchemeColors(int[] colorSchemeColors) {
        mColorSchemeColors = colorSchemeColors;
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        invalidateSelf();
    }

    @Override
    public void start() {
        mLevel = 50;
        isRunning = true;
        invalidateSelf();
    }

    @Override
    public void stop() {
        isRunning = false;
        mDegress = 0;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDegress, mBounds.centerX(), mBounds.centerY());
        drawRing(canvas);
        canvas.restore();
        if (isRunning) {
            mLevel = mLevel >= MAX_LEVEL ? 0 : mLevel + 1;
            updateLevel(mLevel);
            invalidateSelf();
        }
    }

    private void drawRing(Canvas canvas) {
        mPath.reset();
        mPath.arcTo(mBounds, 270, mAngle, true);
        canvas.drawPath(mPath, mPaint);
    }

    private void updateLevel(int level) {
        int animationLevel = level == MAX_LEVEL ? 0 : level;
        int stateForLevel = (animationLevel / 50);

        float percent = level % 50 / 50f;
        int startColor = mColorSchemeColors[stateForLevel];
        int endColor = mColorSchemeColors[(stateForLevel + 1) % mColorSchemeColors.length];
        mPaint.setColor(evaluate(percent, startColor, endColor));

        mDegress = 360 * percent;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mWidth = getRefreshLayout().getFinalOffset();
        mHeight = mWidth;
        mBounds = new RectF(bounds.width() / 2 - mWidth / 2, bounds.top, bounds.width() / 2 + mWidth / 2, bounds.top + mHeight);
        mBounds.inset(UtilDensity.dp2px(getContext(), 15), UtilDensity.dp2px(getContext(), 15));
    }
}
