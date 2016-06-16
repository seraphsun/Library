package code.support.demo.widget.navigationbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import code.support.demo.R;
import code.support.demo.util.UtilScreen;

/**
 * Created by Design on 2016/4/14.
 */
@CoordinatorLayout.DefaultBehavior(ScrollingBehavior.class)
public class BottomNavigation extends FrameLayout {

    public static final int MODE_DEFAULT = 0;
    public static final int MODE_CLASSIC = 1;
    public static final int MODE_SHIFTING = 2;

    @IntDef({MODE_DEFAULT, MODE_CLASSIC, MODE_SHIFTING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

//    public static final int BACKGROUND_STYLE_DEFAULT = 0;
//    public static final int BACKGROUND_STYLE_STATIC = 1;
//    public static final int BACKGROUND_STYLE_RIPPLE = 2;
//
//    @IntDef({BACKGROUND_STYLE_DEFAULT, BACKGROUND_STYLE_STATIC, BACKGROUND_STYLE_RIPPLE})
//    @Retention(RetentionPolicy.SOURCE)
//    public @interface BackgroundStyle {
//    }

    @Mode
    private int mMode = MODE_DEFAULT;
//    @BackgroundStyle
//    private int mBackgroundStyle = BACKGROUND_STYLE_DEFAULT;

    private static final Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
    private ViewPropertyAnimatorCompat mTranslationAnimator;

    private boolean mScrollable = false;

    private static final int MIN_SIZE = 3;
    private static final int MAX_SIZE = 5;

    ArrayList<BottomNavigationItem> mBottomNavigationItems = new ArrayList<>();
    ArrayList<Tab> mBottomNavigationTabs = new ArrayList<>();

    private static final int DEFAULT_SELECTED_POSITION = -1;
    private int mSelectedPosition = DEFAULT_SELECTED_POSITION;
    private int mFirstSelectedPosition = 0;
    private OnTabSelectedListener mTabSelectedListener;

    private int mActiveColor;
    private int mInActiveColor;
    private int mBackgroundColor;

    private FrameLayout mContainer;
    private FrameLayout mBackgroundOverlay;
    private LinearLayout mTabContainer;

    private int mAnimationDuration = 200;
    private int mRippleAnimationDuration = 500;

    public BottomNavigation(Context context) {
        super(context);
        init();
    }

    public BottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * This method initiates the bottom Navigation bar and assigns default values
     */
    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getContext().getResources().getDimension(R.dimen.dimen_56)));
        View parentView = inflater.inflate(R.layout.bottom_navigation_layout, this, true);
        mContainer = (FrameLayout) parentView.findViewById(R.id.bottom_navigation_bar);
        mBackgroundOverlay = (FrameLayout) parentView.findViewById(R.id.bottom_navigation_bar_overLay);
        mTabContainer = (LinearLayout) parentView.findViewById(R.id.bottom_navigation_bar_item_container);

        mActiveColor = UtilScreen.fetchContextColor(getContext(), R.attr.colorAccent);
        mBackgroundColor = Color.WHITE;
        mInActiveColor = Color.LTGRAY;

        ViewCompat.setElevation(this, getContext().getResources().getDimension(R.dimen.dimen_8));
        setClipToPadding(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public BottomNavigation addItem(BottomNavigationItem item) {
        mBottomNavigationItems.add(item);
        return this;
    }

    public BottomNavigation removeItem(BottomNavigationItem item) {
        mBottomNavigationItems.remove(item);
        return this;
    }

    public BottomNavigation setMode(@Mode int mode) {
        this.mMode = mode;
        return this;
    }

//    public BottomNavigation setBackgroundStyle(@BackgroundStyle int backgroundStyle) {
//        this.mBackgroundStyle = backgroundStyle;
//        return this;
//    }

    public BottomNavigation setActiveColor(@ColorRes int activeColor) {
        this.mActiveColor = getContext().getResources().getColor(activeColor, null);
        return this;
    }

    public BottomNavigation setActiveColor(String activeColorCode) {
        this.mActiveColor = Color.parseColor(activeColorCode);
        return this;
    }

    public BottomNavigation setInActiveColor(@ColorRes int inActiveColor) {
        this.mInActiveColor = getContext().getResources().getColor(inActiveColor, null);
        return this;
    }

    public BottomNavigation setInActiveColor(String inActiveColorCode) {
        this.mInActiveColor = Color.parseColor(inActiveColorCode);
        return this;
    }

    public BottomNavigation setBarBackgroundColor(@ColorRes int backgroundColor) {
        this.mBackgroundColor = getContext().getResources().getColor(backgroundColor, null);
        return this;
    }

    public BottomNavigation setBarBackgroundColor(String backgroundColorCode) {
        this.mBackgroundColor = Color.parseColor(backgroundColorCode);
        return this;
    }

    public BottomNavigation setFirstSelectedPosition(int firstSelectedPosition) {
        this.mFirstSelectedPosition = firstSelectedPosition;
        return this;
    }

    public BottomNavigation setAnimationDuration(int animationDuration) {
        this.mAnimationDuration = animationDuration;
        this.mRippleAnimationDuration = (int) (animationDuration * 2.5);
        return this;
    }

    public BottomNavigation setTabSelectedListener(OnTabSelectedListener tabSelectedListener) {
        this.mTabSelectedListener = tabSelectedListener;
        return this;
    }

    public void initialise() {
        if (mBottomNavigationItems.size() > 0) {
            mTabContainer.removeAllViews();
            if (mMode == MODE_DEFAULT) {
                if (mBottomNavigationItems.size() <= MIN_SIZE) {
                    mMode = MODE_CLASSIC;
                } else {
                    mMode = MODE_SHIFTING;
                }
            }
//            if (mBackgroundStyle == BACKGROUND_STYLE_DEFAULT) {
//                if (mMode == MODE_CLASSIC) {
//                    mBackgroundStyle = BACKGROUND_STYLE_STATIC;
//                } else {
//                    mBackgroundStyle = BACKGROUND_STYLE_RIPPLE;
//                }
//            }

//            if (mBackgroundStyle == BACKGROUND_STYLE_STATIC) {
//                mBackgroundOverlay.setBackgroundColor(mBackgroundColor);
            mContainer.setBackgroundColor(mBackgroundColor);
//            }

            int screenWidth = UtilScreen.getScreenWidth(getContext());

            if (mMode == MODE_CLASSIC) {
                int widths[] = BottomNavigationHelper.getClassicMeasurements(getContext(), screenWidth, mBottomNavigationItems.size(), mScrollable);
                int itemWidth = widths[0];

                for (BottomNavigationItem currentItem : mBottomNavigationItems) {
                    经典标签 bottomNavigationTab = new 经典标签(getContext());
                    setUpTab(bottomNavigationTab, currentItem, itemWidth, itemWidth);
                }
            } else if (mMode == MODE_SHIFTING) {
                int widths[] = BottomNavigationHelper.getShiftingMeasurements(getContext(), screenWidth, mBottomNavigationItems.size(), mScrollable);
                int itemWidth = widths[0];
                int itemActiveWidth = widths[1];

                for (BottomNavigationItem currentItem : mBottomNavigationItems) {
                    位移标签 bottomNavigationTab = new 位移标签(getContext());
                    setUpTab(bottomNavigationTab, currentItem, itemWidth, itemActiveWidth);
                }
            }

            if (mBottomNavigationTabs.size() > mFirstSelectedPosition) {
                selectTabInternal(mFirstSelectedPosition, true, true);
            } else if (mBottomNavigationTabs.size() > 0) {
                selectTabInternal(0, true, true);
            }
        }
    }

    private void setUpTab(Tab bottomNavigationTab, BottomNavigationItem currentItem, int itemWidth, int itemActiveWidth) {
        bottomNavigationTab.setInactiveWidth(itemWidth);
        bottomNavigationTab.setActiveWidth(itemActiveWidth);
        bottomNavigationTab.setPosition(mBottomNavigationItems.indexOf(currentItem));

        bottomNavigationTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab bottomNavigationTabView = (Tab) v;
                selectTabInternal(bottomNavigationTabView.getPosition(), false, true);
            }
        });

        mBottomNavigationTabs.add(bottomNavigationTab);
        BottomNavigationHelper.bindTabWithData(currentItem, bottomNavigationTab, this);
//        bottomNavigationTab.initialise(mBackgroundStyle == BACKGROUND_STYLE_STATIC);
        bottomNavigationTab.initialise(true);
        mTabContainer.addView(bottomNavigationTab);
    }

    private void selectTabInternal(int newPosition, boolean firstTab, boolean callListener) {
        if (callListener)
            sendListenerCall(mSelectedPosition, newPosition);
        if (mSelectedPosition != newPosition) {
//            if (mBackgroundStyle == BACKGROUND_STYLE_STATIC) {
            if (mSelectedPosition != -1)
                mBottomNavigationTabs.get(mSelectedPosition).unSelect(true, mAnimationDuration);
            mBottomNavigationTabs.get(newPosition).select(true, mAnimationDuration);
//            } else if (mBackgroundStyle == BACKGROUND_STYLE_RIPPLE) {
//                if (mSelectedPosition != -1)
//                    mBottomNavigationTabs.get(mSelectedPosition).unSelect(false, mAnimationDuration);
//                mBottomNavigationTabs.get(newPosition).select(false, mAnimationDuration);
//
//                BottomNavigationTab clickedView = mBottomNavigationTabs.get(newPosition);
//                if (firstTab) {
//                    mContainer.setBackgroundColor(clickedView.getActiveColor());
//                } else {
//                    BottomNavigationHelper.setBackgroundWithRipple(clickedView, mContainer, mBackgroundOverlay, clickedView.getActiveColor(), mRippleAnimationDuration);
//                }
//            }
            mSelectedPosition = newPosition;
        }
    }

    private void sendListenerCall(int oldPosition, int newPosition) {
        if (mTabSelectedListener != null && oldPosition != -1) {
            if (oldPosition == newPosition) {
                mTabSelectedListener.onTabReselected(newPosition);
            } else {
                mTabSelectedListener.onTabSelected(newPosition);
                mTabSelectedListener.onTabUnselected(newPosition);
            }
        }
    }

    public void hide() {
        hide(true);
    }

    public void hide(boolean animate) {
        setTranslationY(this.getHeight(), animate);
    }

    public void unHide() {
        unHide(true);
    }

    public void unHide(boolean animate) {
        setTranslationY(0, animate);
    }

    private void setTranslationY(int offset, boolean animate) {
        if (animate) {
            animateOffset(offset);
        } else {
            if (mTranslationAnimator != null) {
                mTranslationAnimator.cancel();
            }
            this.setTranslationY(offset);
        }
    }

    private void animateOffset(final int offset) {
        if (mTranslationAnimator == null) {
            mTranslationAnimator = ViewCompat.animate(this);
            mTranslationAnimator.setDuration(mRippleAnimationDuration);
            mTranslationAnimator.setInterpolator(INTERPOLATOR);
        } else {
            mTranslationAnimator.cancel();
        }
        mTranslationAnimator.translationY(offset).start();
    }

    public void selectTab(int newPosition) {
        selectTab(newPosition, true);
    }

    public void selectTab(int newPosition, boolean callListener) {
        selectTabInternal(newPosition, false, callListener);
    }

    public void clearAll() {
        mTabContainer.removeAllViews();
        mBottomNavigationTabs.clear();
        mBottomNavigationItems.clear();
        mBackgroundOverlay.setBackgroundColor(Color.TRANSPARENT);
        mContainer.setBackgroundColor(Color.TRANSPARENT);
        mSelectedPosition = DEFAULT_SELECTED_POSITION;
    }

    public int getActiveColor() {
        return mActiveColor;
    }

    public int getInActiveColor() {
        return mInActiveColor;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * Callback interface invoked when a tab's selection state changes.
     */
    public interface OnTabSelectedListener {

        /**
         * Called when a tab enters the selected state.
         *
         * @param position The position of the tab that was selected
         */
        void onTabSelected(int position);

        /**
         * Called when a tab exits the selected state.
         *
         * @param position The position of the tab that was unselected
         */
        void onTabUnselected(int position);

        /**
         * Called when a tab that is already selected is chosen again by the user. Some applications
         * may use this action to return to the top level of a category.
         *
         * @param position The position of the tab that was reselected.
         */
        void onTabReselected(int position);
    }

    public static class BottomNavigationItem {

        protected int mIconResource;
        protected Drawable mIcon;
        protected int mTitleResource;
        protected String mTitle;
        protected int mActiveColorResource;
        protected String mActiveColorCode;
        protected int mInActiveColorResource;
        protected String mInActiveColorCode;

        /**
         * @param mIconResource resource for the Tab icon.
         * @param mTitle        title for the Tab.
         */
        public BottomNavigationItem(@DrawableRes int mIconResource, @NonNull String mTitle) {
            this.mIconResource = mIconResource;
            this.mTitle = mTitle;
        }

        /**
         * @param mIcon  drawable icon for the Tab.
         * @param mTitle title for the Tab.
         */
        public BottomNavigationItem(Drawable mIcon, @NonNull String mTitle) {
            this.mIcon = mIcon;
            this.mTitle = mTitle;
        }

        /**
         * @param mIcon          drawable icon for the Tab.
         * @param mTitleResource resource for the title.
         */
        public BottomNavigationItem(Drawable mIcon, @StringRes int mTitleResource) {
            this.mIcon = mIcon;
            this.mTitleResource = mTitleResource;
        }

        /**
         * @param mIconResource  resource for the Tab icon.
         * @param mTitleResource resource for the title.
         */
        public BottomNavigationItem(@DrawableRes int mIconResource, @StringRes int mTitleResource) {
            this.mIconResource = mIconResource;
            this.mTitleResource = mTitleResource;
        }

        /**
         * @param colorResource resource for active color
         * @return this, to allow builder pattern
         */
        public BottomNavigationItem setActiveColor(@ColorRes int colorResource) {
            this.mActiveColorResource = colorResource;
            return this;
        }

        /**
         * @param colorCode color code for active color
         * @return this, to allow builder pattern
         */
        public BottomNavigationItem setActiveColor(String colorCode) {
            this.mActiveColorCode = colorCode;
            return this;
        }

        /**
         * @param colorResource resource for in-active color
         * @return this, to allow builder pattern
         */
        public BottomNavigationItem setInActiveColor(@ColorRes int colorResource) {
            this.mInActiveColorResource = colorResource;
            return this;
        }

        /**
         * @param colorCode color code for in-active color
         * @return this, to allow builder pattern
         */
        public BottomNavigationItem setInActiveColor(String colorCode) {
            this.mInActiveColorCode = colorCode;
            return this;
        }

        /**
         * @param context to fetch drawable
         * @return icon drawable
         */
        protected Drawable getIcon(Context context) {
            if (this.mIconResource != 0) {
                return ContextCompat.getDrawable(context, this.mIconResource);
            } else {
                return this.mIcon;
            }
        }

        /**
         * @param context to fetch resource
         * @return title string
         */
        protected String getTitle(Context context) {
            if (this.mTitleResource != 0) {
                return context.getString(this.mTitleResource);
            } else {
                return this.mTitle;
            }
        }

        /**
         * @param context to fetch color
         * @return active color (or) -1 if no color is specified
         */
        protected int getActiveColor(Context context) {
            if (this.mActiveColorResource != 0) {
                return context.getResources().getColor(mActiveColorResource, null);
            } else if (this.mActiveColorCode != null && !TextUtils.isEmpty(mActiveColorCode)) {
                return Color.parseColor(mActiveColorCode);
            } else {
                return -1;
            }
        }

        /**
         * @param context to fetch color
         * @return in-active color (or) -1 if no color is specified
         */
        protected int getInActiveColor(Context context) {
            if (this.mInActiveColorResource != 0) {
                return context.getResources().getColor(mInActiveColorResource, null);
            } else if (this.mInActiveColorCode != null && !TextUtils.isEmpty(mInActiveColorCode)) {
                return Color.parseColor(mInActiveColorCode);
            } else {
                return -1;
            }
        }
    }

    public static class BottomNavigationHelper {

        public static int[] getClassicMeasurements(Context context, int screenWidth, int noOfTabs, boolean scrollable) {
            int result[] = new int[2];
            int minWidth = (int) context.getResources().getDimension(R.dimen.dimen_104);
            int maxWidth = (int) context.getResources().getDimension(R.dimen.dimen_120);
            int itemWidth = screenWidth / noOfTabs;
            if (itemWidth < minWidth && scrollable) {
                itemWidth = (int) context.getResources().getDimension(R.dimen.dimen_120);
            } else if (itemWidth > maxWidth) {
                itemWidth = maxWidth;
            }
            result[0] = itemWidth;
            return result;
        }

        public static int[] getShiftingMeasurements(Context context, int screenWidth, int noOfTabs, boolean scrollable) {
            int result[] = new int[2];
            int minWidth = (int) context.getResources().getDimension(R.dimen.dimen_64);
            int maxWidth = (int) context.getResources().getDimension(R.dimen.dimen_96);
            double minPossibleWidth = minWidth * (noOfTabs + 0.5);
            double maxPossibleWidth = maxWidth * (noOfTabs + 0.75);
            int itemWidth;
            int itemActiveWidth;
            if (screenWidth < minPossibleWidth) {
                if (scrollable) {
                    itemWidth = minWidth;
                    itemActiveWidth = (int) (minWidth * 1.5);
                } else {
                    itemWidth = (int) (screenWidth / (noOfTabs + 0.5));
                    itemActiveWidth = (int) (itemWidth * 1.5);
                }
            } else if (screenWidth > maxPossibleWidth) {
                itemWidth = maxWidth;
                itemActiveWidth = (int) (itemWidth * 1.75);
            } else {
                double minPossibleWidth1 = minWidth * (noOfTabs + 0.625);
                double minPossibleWidth2 = minWidth * (noOfTabs + 0.75);
                itemWidth = (int) (screenWidth / (noOfTabs + 0.5));
                itemActiveWidth = (int) (itemWidth * 1.5);
                if (screenWidth > minPossibleWidth1) {
                    itemWidth = (int) (screenWidth / (noOfTabs + 0.625));
                    itemActiveWidth = (int) (itemWidth * 1.625);
                    if (screenWidth > minPossibleWidth2) {
                        itemWidth = (int) (screenWidth / (noOfTabs + 0.75));
                        itemActiveWidth = (int) (itemWidth * 1.75);
                    }
                }
            }
            result[0] = itemWidth;
            result[1] = itemActiveWidth;
            return result;
        }

        public static void bindTabWithData(BottomNavigationItem bottomNavigationItem, Tab bottomNavigationTab, BottomNavigation bottomNavigation) {
            Context context = bottomNavigation.getContext();
            bottomNavigationTab.setLabel(bottomNavigationItem.getTitle(context));
            bottomNavigationTab.setIcon(bottomNavigationItem.getIcon(context));
            int activeColor = bottomNavigationItem.getActiveColor(context);
            int inActiveColor = bottomNavigationItem.getInActiveColor(context);
            if (activeColor != -1) {
                bottomNavigationTab.setActiveColor(activeColor);
            } else {
                bottomNavigationTab.setActiveColor(bottomNavigation.getActiveColor());
            }
            if (inActiveColor != -1) {
                bottomNavigationTab.setInactiveColor(inActiveColor);
            } else {
                bottomNavigationTab.setInactiveColor(bottomNavigation.getInActiveColor());
            }
            bottomNavigationTab.setItemBackgroundColor(bottomNavigation.getBackgroundColor());
        }

        public static void setBackgroundWithRipple(View clickedView, final View backgroundView, final View bgOverlay, final int newColor, int animationDuration) {
            int centerX = (int) (clickedView.getX() + (clickedView.getMeasuredWidth() / 2));
            int centerY = clickedView.getMeasuredHeight() / 2;
            int finalRadius = backgroundView.getWidth();
            backgroundView.clearAnimation();
            bgOverlay.clearAnimation();
            Animator circularReveal;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circularReveal = ViewAnimationUtils.createCircularReveal(bgOverlay, centerX, centerY, 0, finalRadius);
            } else {
                bgOverlay.setAlpha(0);
                circularReveal = ObjectAnimator.ofFloat(bgOverlay, "alpha", 0, 1);
            }
            circularReveal.setDuration(animationDuration);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    onCancel();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    onCancel();
                }

                private void onCancel() {
                    backgroundView.setBackgroundColor(newColor);
                    bgOverlay.setVisibility(View.GONE);
                }
            });

            bgOverlay.setBackgroundColor(newColor);
            bgOverlay.setVisibility(View.VISIBLE);
            circularReveal.start();
        }
    }
}
