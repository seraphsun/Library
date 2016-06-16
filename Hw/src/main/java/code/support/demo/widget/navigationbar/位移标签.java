package code.support.demo.widget.navigationbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import code.support.demo.R;

class 位移标签 extends Tab {

    public 位移标签(Context context) {
        super(context);
    }

    public 位移标签(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public 位移标签(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public 位移标签(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init() {
        paddingTopActive = (int) getResources().getDimension(R.dimen.dimen_6);
        paddingTopInActive = (int) getResources().getDimension(R.dimen.dimen_16);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.bottom_tab_shifting, this, true);
        containerView = view.findViewById(R.id.shifting_bottom_navigation_container);
        labelView = (TextView) view.findViewById(R.id.shifting_bottom_navigation_title);
        iconView = (ImageView) view.findViewById(R.id.shifting_bottom_navigation_icon);

//        super.init();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void select(boolean setActiveColor, int animationDuration) {
        super.select(setActiveColor, animationDuration);

        ResizeWidthAnimation anim = new ResizeWidthAnimation(this, mActiveWidth);
        anim.setDuration(animationDuration);
        this.startAnimation(anim);

        labelView.animate().scaleY(1).scaleX(1).setDuration(animationDuration).start();
    }

    @Override
    public void unSelect(boolean setActiveColor, int animationDuration) {
        super.unSelect(setActiveColor, animationDuration);

        ResizeWidthAnimation anim = new ResizeWidthAnimation(this, mInActiveWidth);
        anim.setDuration(animationDuration);
        this.startAnimation(anim);

        labelView.setScaleY(0);
        labelView.setScaleX(0);
    }

    @Override
    public void initialise(boolean setActiveColor) {
        super.initialise(setActiveColor);
    }

    public class ResizeWidthAnimation extends Animation {
        private int mWidth;
        private int mStartWidth;
        private View mView;

        public ResizeWidthAnimation(View view, int width) {
            mView = view;
            mWidth = width;
            mStartWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mView.getLayoutParams().width = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);
            mView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}
