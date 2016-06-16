package code.support.demo.widget.navigationbar;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Design on 2016/4/14.
 */
abstract class Tab extends FrameLayout {

    protected int paddingTopActive;
    protected int paddingTopInActive;

    protected int mPosition;
    protected int mActiveColor;
    protected int mInActiveColor;
    protected int mBackgroundColor;
    protected int mActiveWidth;
    protected int mInActiveWidth;
    protected Drawable mCompactIcon;
    protected String mLabel;

    boolean isActive = false;

    View containerView;
    TextView labelView;
    ImageView iconView;

    public Tab(Context context) {
        super(context);
        init();
    }

    public Tab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Tab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Tab(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected abstract void init();

//    void init() {
//        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void select(boolean setActiveColor, int animationDuration) {
        isActive = true;

        ValueAnimator animator = ValueAnimator.ofInt(containerView.getPaddingTop(), paddingTopActive);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                containerView.setPadding(containerView.getPaddingLeft(), (Integer) valueAnimator.getAnimatedValue(), containerView.getPaddingRight(), containerView.getPaddingBottom());
            }
        });
        animator.setDuration(animationDuration);
        animator.start();

        iconView.setSelected(true);
        if (setActiveColor) {
            labelView.setTextColor(mActiveColor);
        } else {
            labelView.setTextColor(mBackgroundColor);
        }
    }

    public void unSelect(boolean setActiveColor, int animationDuration) {
        isActive = false;

        ValueAnimator animator = ValueAnimator.ofInt(containerView.getPaddingTop(), paddingTopInActive);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                containerView.setPadding(containerView.getPaddingLeft(), (Integer) valueAnimator.getAnimatedValue(), containerView.getPaddingRight(), containerView.getPaddingBottom());
            }
        });
        animator.setDuration(animationDuration);
        animator.start();

        labelView.setTextColor(mInActiveColor);
        iconView.setSelected(false);
    }

    public void initialise(boolean setActiveColor) {
        iconView.setSelected(false);
//        if (setActiveColor) {
            DrawableCompat.setTintList(mCompactIcon, new ColorStateList(new int[][]{new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}, new int[]{}}, new int[]{mActiveColor, mInActiveColor, mInActiveColor}));
//        } else {
//            DrawableCompat.setTintList(mCompactIcon, new ColorStateList(new int[][]{new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}, new int[]{}}, new int[]{mBackgroundColor, mInActiveColor, mInActiveColor}));
//        }
        iconView.setImageDrawable(mCompactIcon);
    }

    public void setIcon(Drawable icon) {
        mCompactIcon = DrawableCompat.wrap(icon);
    }

    public void setLabel(String label) {
        mLabel = label;
        labelView.setText(label);
    }

    public void setItemBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public void setActiveWidth(int activeWidth) {
        mActiveWidth = activeWidth;
    }

    public int getActiveColor() {
        return mActiveColor;
    }

    public void setActiveColor(int activeColor) {
        mActiveColor = activeColor;
    }

    public void setInactiveWidth(int inactiveWidth) {
        mInActiveWidth = inactiveWidth;
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = mInActiveWidth;
        setLayoutParams(params);
    }



    public void setInactiveColor(int inActiveColor) {
        mInActiveColor = inActiveColor;
        labelView.setTextColor(inActiveColor);
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }
}
