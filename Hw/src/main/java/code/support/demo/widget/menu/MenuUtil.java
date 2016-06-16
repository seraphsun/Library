package code.support.demo.widget.menu;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import code.support.demo.R;

public class MenuUtil {

    public static int getDefaultActionBarSize(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarSize;
    }

    public static RelativeLayout getImageWrapper(Context context, MenuObject menuItem, int menuItemSize, View.OnClickListener onCLick, View.OnLongClickListener onLongClick, boolean showDivider) {
        RelativeLayout imageWrapper = new RelativeLayout(context);
        LinearLayout.LayoutParams imageWrapperLayoutParams = new LinearLayout.LayoutParams(menuItemSize, menuItemSize);
        imageWrapper.setLayoutParams(imageWrapperLayoutParams);
        imageWrapper.setOnClickListener(onCLick);
        imageWrapper.setOnLongClickListener(onLongClick);
        imageWrapper.addView(MenuUtil.getItemImageButton(context, menuItem));
        if (showDivider) {
            imageWrapper.addView(getDivider(context, menuItem));
        }

        if (menuItem.getBgColor() != 0) {
            imageWrapper.setBackgroundColor(menuItem.getBgColor());
        } else if (menuItem.getBgDrawable() != null) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                imageWrapper.setBackgroundDrawable(menuItem.getBgDrawable());
            } else {
                imageWrapper.setBackground(menuItem.getBgDrawable());
            }
        } else if (menuItem.getBgResource() != 0) {
            imageWrapper.setBackgroundResource(menuItem.getBgResource());
        } else {
            imageWrapper.setBackgroundColor(context.getResources().getColor(R.color.color_f2f2f2));
        }
        return imageWrapper;
    }

    public static ImageView getItemImageButton(Context context, MenuObject item) {
        ImageView imageView = new ImageButton(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(lp);
        imageView.setPadding((int) context.getResources().getDimension(R.dimen.dimen_2), (int) context.getResources().getDimension(R.dimen.dimen_2), (int) context.getResources().getDimension(R.dimen.dimen_2), (int) context.getResources().getDimension(R.dimen.dimen_2));
        imageView.setClickable(false);
        imageView.setFocusable(false);
        imageView.setBackgroundColor(Color.TRANSPARENT);

        if (item.getColor() != 0) {
            Drawable color = new ColorDrawable(item.getColor());
            imageView.setImageDrawable(color);
        } else if (item.getResource() != 0) {
            imageView.setImageResource(item.getResource());
        } else if (item.getBitmap() != null) {
            imageView.setImageBitmap(item.getBitmap());
        } else if (item.getDrawable() != null) {
            imageView.setImageDrawable(item.getDrawable());
        }
        imageView.setScaleType(item.getScaleType());

        return imageView;
    }

    public static TextView getItemTextView(Context context, MenuObject menuItem, int menuItemSize) {
        TextView itemTextView = new TextView(context);
        RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, menuItemSize);
        itemTextView.setLayoutParams(textLayoutParams);
        itemTextView.setText(menuItem.getTitle());
        itemTextView.setPadding(0, 0, (int) context.getResources().getDimension(R.dimen.dimen_28), 0);
        itemTextView.setGravity(Gravity.CENTER_VERTICAL);
        int textColor = menuItem.getTextColor() == 0 ? context.getResources().getColor(android.R.color.white) : menuItem.getTextColor();
        itemTextView.setTextColor(textColor);
        itemTextView.setTextAppearance(context, menuItem.getMenuTextAppearanceStyle() > 0 ? menuItem.getMenuTextAppearanceStyle() : R.style.DefaultTextStyle);
        return itemTextView;
    }

    public static View getDivider(Context context, MenuObject menuItem) {
        View dividerView = new View(context);
        RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.dimen_1));
        viewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        dividerView.setLayoutParams(viewLayoutParams);
        dividerView.setClickable(true);
        int dividerColor = menuItem.getDividerColor() == Integer.MAX_VALUE ? context.getResources().getColor(R.color.color_b5b5b5) : menuItem.getDividerColor();
        dividerView.setBackgroundColor(dividerColor);
        return dividerView;
    }

    public static ObjectAnimator rotationCloseToRight(View v) {
        return ObjectAnimator.ofFloat(v, "rotationY", 0, -90);
    }

    public static ObjectAnimator rotationOpenFromRight(View v) {
        return ObjectAnimator.ofFloat(v, "rotationY", -90, 0);
    }

    public static ObjectAnimator rotationCloseVertical(View v) {
        return ObjectAnimator.ofFloat(v, "rotationX", 0, -90);
    }

    public static ObjectAnimator rotationOpenVertical(View v) {
        return ObjectAnimator.ofFloat(v, "rotationX", -90, 0);
    }

    public static ObjectAnimator alphaDisappear(View v) {
        return ObjectAnimator.ofFloat(v, "alpha", 1, 0);
    }

    public static ObjectAnimator alphaAppear(View v) {
        return ObjectAnimator.ofFloat(v, "alpha", 0, 1);
    }

    public static ObjectAnimator translationRight(View v, float x) {
        return ObjectAnimator.ofFloat(v, "translationX", 0, x);
    }
    public static ObjectAnimator translationLeft(View v, float x) {
        return ObjectAnimator.ofFloat(v, "translationX", x, 0);
    }

    public static AnimatorSet fadeOutSet(View v, float x){
        AnimatorSet fadeOutSet = new AnimatorSet();
        fadeOutSet.playTogether(alphaDisappear(v), translationRight(v, x));
        return fadeOutSet;
    }

    public static class HesitateInterpolator implements Interpolator {

        public HesitateInterpolator() {
        }

        public float getInterpolation(float t) {
            float x = 2.0f * t - 1.0f;
            return 0.5f * (x * x * x + 1.0f);
        }
    }
}
