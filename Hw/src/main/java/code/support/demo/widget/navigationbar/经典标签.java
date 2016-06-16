package code.support.demo.widget.navigationbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import code.support.demo.R;

class 经典标签 extends Tab {

    float labelScale;

    public 经典标签(Context context) {
        super(context);
    }

    public 经典标签(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public 经典标签(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public 经典标签(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init() {
        paddingTopActive = (int) getResources().getDimension(R.dimen.dimen_6);
        paddingTopInActive = (int) getResources().getDimension(R.dimen.dimen_8);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.bottom_tab_classic, this, true);
        containerView = view.findViewById(R.id.classic_bottom_navigation_container);
        labelView = (TextView) view.findViewById(R.id.classic_bottom_navigation_title);
        iconView = (ImageView) view.findViewById(R.id.classic_bottom_navigation_icon);

        labelScale = getResources().getDimension(R.dimen.dimen_14) / getResources().getDimension(R.dimen.dimen_12);

//        super.init();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void select(boolean setActiveColor, int animationDuration) {

        labelView.animate().scaleX(labelScale).scaleY(labelScale).setDuration(animationDuration).start();
        super.select(setActiveColor, animationDuration);
    }

    @Override
    public void unSelect(boolean setActiveColor, int animationDuration) {
        labelView.animate().scaleX(1).scaleY(1).setDuration(animationDuration).start();
        super.unSelect(setActiveColor, animationDuration);
    }

    @Override
    public void initialise(boolean setActiveColor) {
        super.initialise(setActiveColor);
    }
}
