package code.support.demo.widget.refresh.refresh_01;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

/**
 * Created by Design on 2016/5/11.
 */
public abstract class RefreshDrawable extends Drawable implements Drawable.Callback, Animatable {

    private RefreshLayout mRefreshLayout;

    public RefreshDrawable(Context context, RefreshLayout layout) {
        mRefreshLayout = layout;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
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

    public Context getContext() {
        return mRefreshLayout != null ? mRefreshLayout.getContext() : null;
    }

    public RefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    public abstract void setPercent(float percent);

    public abstract void setColorSchemeColors(int[] colorSchemeColors);

    public abstract void offsetTopAndBottom(int offset);
}
