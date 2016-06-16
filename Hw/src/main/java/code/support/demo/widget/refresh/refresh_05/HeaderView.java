package code.support.demo.widget.refresh.refresh_05;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import code.support.demo.R;
import code.support.demo.util.UtilDensity;

public class HeaderView extends Drawable implements Drawable.Callback, Animatable {

    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final int ANIMATION_DURATION = 1000;
    private RefreshLayout mParent;
    private Matrix mMatrix;
    private Animation mAnimation;
    private float mRotate = 0.0f;

    private int mTop;
    private int mScreenWidth;

    private final static float SKY_RATIO = 0.65f;
    private static final float SKY_INITIAL_SCALE = 1.05f;

    private final static float TOWN_RATIO = 0.22f;
    private static final float TOWN_INITIAL_SCALE = 1.20f;
    private static final float TOWN_FINAL_SCALE = 1.30f;

    private static final float SUN_FINAL_SCALE = 0.75f;
    private static final float SUN_INITIAL_ROTATE_GROWTH = 1.2f;
    private static final float SUN_FINAL_ROTATE_GROWTH = 1.5f;

    private static final float SCALE_START_PERCENT = 0.5f;

    private int mSkyHeight;
    private float mSkyTopOffset;
    private float mSkyMoveOffset;

    private int mTownHeight;
    private float mTownInitialTopOffset;
    private float mTownFinalTopOffset;
    private float mTownMoveOffset;

    private int mSunSize = 100;
    private float mSunLeftOffset;
    private float mSunTopOffset;

    private Bitmap mSky;
    private Bitmap mSun;
    private Bitmap mTown;

    private boolean isRefreshing = false;
    private float mPercent = 0.0f;

    public HeaderView(Context context, RefreshLayout parent) {
        mParent = parent;
        mMatrix = new Matrix();

        setupAnimations();
        parent.post(new Runnable() {
            @Override
            public void run() {
                initiateDimens(parent.getWidth());
            }
        });
    }

    private void setupAnimations() {
        mAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setRotate(interpolatedTime);
            }
        };
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        mAnimation.setDuration(ANIMATION_DURATION);
    }

    public void setRotate(float rotate) {
        mRotate = rotate;
        invalidateSelf();
    }

    public void initiateDimens(int viewWidth) {
        if (viewWidth <= 0 || viewWidth == mScreenWidth) return;

        mScreenWidth = viewWidth;
        mSkyHeight = (int) (SKY_RATIO * mScreenWidth);
        mSkyTopOffset = (mSkyHeight * 0.38f);
        mSkyMoveOffset = UtilDensity.dp2px(getContext(), 15);

        mTownHeight = (int) (TOWN_RATIO * mScreenWidth);
        mTownInitialTopOffset = (mParent.getTotalDragDistance() - mTownHeight * TOWN_INITIAL_SCALE);
        mTownFinalTopOffset = (mParent.getTotalDragDistance() - mTownHeight * TOWN_FINAL_SCALE);
        mTownMoveOffset = UtilDensity.dp2px(getContext(), 10);

        mSunLeftOffset = 0.3f * (float) mScreenWidth;
        mSunTopOffset = (mParent.getTotalDragDistance() * 0.1f);

        mTop = -mParent.getTotalDragDistance();

        createBitmaps();
    }

    private Context getContext() {
        return mParent != null ? mParent.getContext() : null;
    }

    private void createBitmaps() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        mSky = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.refresh_sky, options);
        mSky = Bitmap.createScaledBitmap(mSky, mScreenWidth, mSkyHeight, true);
        mTown = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.refresh_buildings, options);
        mTown = Bitmap.createScaledBitmap(mTown, mScreenWidth, (int) (mScreenWidth * TOWN_RATIO), true);
        mSun = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.refresh_sun, options);
        mSun = Bitmap.createScaledBitmap(mSun, mSunSize, mSunSize, true);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, mSkyHeight + top);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mScreenWidth <= 0) return;

        final int saveCount = canvas.save();

        canvas.translate(0, mTop);
        canvas.clipRect(0, -mTop, mScreenWidth, mParent.getTotalDragDistance());

        drawSky(canvas);
        drawSun(canvas);
        drawTown(canvas);

        canvas.restoreToCount(saveCount);
    }

    private void drawSky(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        float dragPercent = Math.min(1f, Math.abs(mPercent));

        float skyScale;
        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            skyScale = SKY_INITIAL_SCALE - (SKY_INITIAL_SCALE - 1.0f) * scalePercent;
        } else {
            skyScale = SKY_INITIAL_SCALE;
        }

        float offsetX = -(mScreenWidth * skyScale - mScreenWidth) / 2.0f;
        float offsetY = (1.0f - dragPercent) * mParent.getTotalDragDistance() - mSkyTopOffset - mSkyHeight * (skyScale - 1.0f) / 2 + mSkyMoveOffset * dragPercent;

        matrix.postScale(skyScale, skyScale);
        matrix.postTranslate(offsetX, offsetY);
        canvas.drawBitmap(mSky, matrix, null);
    }

    private void drawSun(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        float dragPercent = mPercent;
        if (dragPercent > 1.0f) {
            dragPercent = (dragPercent + 9.0f) / 10;
        }

        float sunRadius = (float) mSunSize / 2.0f;
        float sunRotateGrowth = SUN_INITIAL_ROTATE_GROWTH;

        float offsetX = mSunLeftOffset;
        float offsetY = mSunTopOffset + (mParent.getTotalDragDistance() / 2) * (1.0f - dragPercent) - mTop;

        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            float sunScale = 1.0f - (1.0f - SUN_FINAL_SCALE) * scalePercent;
            sunRotateGrowth += (SUN_FINAL_ROTATE_GROWTH - SUN_INITIAL_ROTATE_GROWTH) * scalePercent;

            matrix.preTranslate(offsetX + (sunRadius - sunRadius * sunScale), offsetY * (2.0f - sunScale));
            matrix.preScale(sunScale, sunScale);

            offsetX += sunRadius;
            offsetY = offsetY * (2.0f - sunScale) + sunRadius * sunScale;
        } else {
            matrix.postTranslate(offsetX, offsetY);

            offsetX += sunRadius;
            offsetY += sunRadius;
        }
        matrix.postRotate((isRefreshing ? -360 : 360) * mRotate * (isRefreshing ? 1 : sunRotateGrowth), offsetX, offsetY);

        canvas.drawBitmap(mSun, matrix, null);
    }

    private void drawTown(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        float dragPercent = Math.min(1f, Math.abs(mPercent));

        float townScale;
        float townTopOffset;
        float townMoveOffset;
        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            townScale = TOWN_INITIAL_SCALE + (TOWN_FINAL_SCALE - TOWN_INITIAL_SCALE) * scalePercent;
            townTopOffset = mTownInitialTopOffset - (mTownFinalTopOffset - mTownInitialTopOffset) * scalePercent;
            townMoveOffset = mTownMoveOffset * (1.0f - scalePercent);
        } else {
            float scalePercent = dragPercent / SCALE_START_PERCENT;
            townScale = TOWN_INITIAL_SCALE;
            townTopOffset = mTownInitialTopOffset;
            townMoveOffset = mTownMoveOffset * scalePercent;
        }

        float offsetX = -(mScreenWidth * townScale - mScreenWidth) / 2.0f;
        float offsetY = (1.0f - dragPercent) * mParent.getTotalDragDistance() + townTopOffset - mTownHeight * (townScale - 1.0f) / 2 + townMoveOffset;

        matrix.postScale(townScale, townScale);
        matrix.postTranslate(offsetX, offsetY);

        canvas.drawBitmap(mTown, matrix, null);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    @Override
    public void start() {
        mAnimation.reset();
        isRefreshing = true;
        mParent.startAnimation(mAnimation);
    }

    @Override
    public void stop() {
        mParent.clearAnimation();
        isRefreshing = false;
        resetOriginals();
    }

    private void resetOriginals() {
        setPercent(0);
        setRotate(0);
    }

    private void setPercent(float percent) {
        mPercent = percent;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    public void setPercent(float percent, boolean invalidate) {
        setPercent(percent);
        if (invalidate) setRotate(percent);
    }

    public void offsetTopAndBottom(int offset) {
        mTop += offset;
        invalidateSelf();
    }
}
