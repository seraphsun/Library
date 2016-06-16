package code.support.demo.widget.menu;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import code.support.demo.R;
import code.support.demo.util.anim.AnimHelper;

/**
 * Created by Ignacey 2016/1/18.
 */
public class ActionBarMenu extends DialogFragment {

    public static final String TAG = ActionBarMenu.class.getSimpleName();
    public static final String BUNDLE_MENU_PARAMS = "bundle menu params";

    private LinearLayout mWrapperButton;
    private LinearLayout mWrapperText;
    private MyFragmentAdapter mFragmentAdapter;
    private MenuObject.MenuParams mMenuParams;

    private OnMenuItemClickListener mItemClickListener;
    private OnMenuItemLongClickListener mItemLongClickListener;

    @Deprecated
    public static ActionBarMenu newInstance(int actionBarSize, List<MenuObject> menuObjects) {
        MenuObject.MenuParams params = new MenuObject.MenuParams();
        params.setActionBarSize(actionBarSize);
        params.setMenuObjects(menuObjects);

        return newInstance(params);
    }

    @Deprecated
    public static ActionBarMenu newInstance(int actionBarSize, List<MenuObject> menuObjects, int animationDelay) {
        MenuObject.MenuParams params = new MenuObject.MenuParams();
        params.setActionBarSize(actionBarSize);
        params.setMenuObjects(menuObjects);
        params.setAnimationDelay(animationDelay);

        return newInstance(params);
    }

    @Deprecated
    public static ActionBarMenu newInstance(int actionBarSize, List<MenuObject> menuObjects, int animationDelay, int animationDuration) {
        MenuObject.MenuParams params = new MenuObject.MenuParams();
        params.setActionBarSize(actionBarSize);
        params.setMenuObjects(menuObjects);
        params.setAnimationDelay(animationDelay);
        params.setAnimationDuration(animationDuration);

        return newInstance(params);
    }

    /**
     * @param actionBarSize     fragment尺寸
     * @param menuObjects       menu的item选项列表
     * @param animationDelay    延时多久开始动画
     * @param animationDuration 动画的持续时间
     * @param fitsSystemWindow  是否自适应屏幕
     * @param clipToPadding     是否修整子视图边距
     * @return 半透明的DialogFragment
     */
    @Deprecated
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static ActionBarMenu newInstance(int actionBarSize, List<MenuObject> menuObjects, int animationDelay, int animationDuration, boolean fitsSystemWindow, boolean clipToPadding) {
        MenuObject.MenuParams params = new MenuObject.MenuParams();
        params.setActionBarSize(actionBarSize);
        params.setMenuObjects(menuObjects);
        params.setAnimationDelay(animationDelay);
        params.setAnimationDuration(animationDuration);
        params.setFitsSystemWindow(fitsSystemWindow);
        params.setClipToPadding(clipToPadding);
        return newInstance(params);
    }

    /**
     * 构造器
     */
    public static ActionBarMenu newInstance(MenuObject.MenuParams menuParams) {
        ActionBarMenu fragment = new ActionBarMenu();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_MENU_PARAMS, menuParams);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.ActionBarMenu);
        if (getArguments() != null) {
            mMenuParams = getArguments().getParcelable(BUNDLE_MENU_PARAMS);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.actionbar_menu_layout, container, false);
        view.setFitsSystemWindows(mMenuParams.isFitsSystemWindow());
        view.setClipToPadding(mMenuParams.isClipToPadding());

        initView(view);
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        initViewAdapter();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFragmentAdapter.menuToggle();
            }
        }, mMenuParams.getAnimationDelay());

        if (mMenuParams.isClosableOutside()) {
            view.findViewById(R.id.rl_wrapper_root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAdded()) {
                        dismiss();
                    }
                }
            });
        }
        return view;
    }

    private void initView(View view) {
        mWrapperButton = (LinearLayout) view.findViewById(R.id.ll_wrapper_button);
        mWrapperText = (LinearLayout) view.findViewById(R.id.ll_wrapper_text);
    }

    private void initViewAdapter() {
        mFragmentAdapter = new MyFragmentAdapter(getActivity(), mWrapperButton, mWrapperText, mMenuParams.getMenuObjects(), mMenuParams.getActionBarSize());
        mFragmentAdapter.setOnItemClickListener(mItemClickListener);
        mFragmentAdapter.setOnItemLongClickListener(mItemLongClickListener);
        mFragmentAdapter.setAnimationDuration(mMenuParams.getAnimationDuration());
    }

    public void setItemClickListener(OnMenuItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnMenuItemLongClickListener itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    private void close() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, mMenuParams.getAnimationDelay());
    }

    public class MyFragmentAdapter {

        public static final int ANIMATION_DURATION_MILLIS = 100;

        private OnMenuItemClickListener mOnItemClickListener;
        private OnMenuItemLongClickListener mOnItemLongClickListener;

        private Context mContext;
        private LinearLayout mMenuWrapper;
        private LinearLayout mTextWrapper;
        private View mClickedView;

        private List<MenuObject> mMenuObjects;
        private AnimatorSet mAnimatorSetShowMenu;
        private AnimatorSet mAnimatorSetHideMenu;
        private boolean mIsMenuOpen = false;
        private boolean mIsAnimationRun = false;
        private int mMenuItemSize;
        private int mAnimationDurationMilis = ANIMATION_DURATION_MILLIS;

        private View.OnClickListener clickItem = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewClicked(v);
            }
        };

        private View.OnLongClickListener longClickItem = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                viewClicked(v);
                return true;
            }
        };

        private Animator.AnimatorListener mCloseOpenAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                toggleIsAnimationRun();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };

        private Animator.AnimatorListener mChosenItemFinishAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                toggleIsAnimationRun();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onMenuItemClick(mClickedView, mWrapperButton.indexOfChild(mClickedView));
                    close();
                } else if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onMenuItemLongClick(mClickedView, mWrapperButton.indexOfChild(mClickedView));
                    close();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };

        public MyFragmentAdapter(Context context, LinearLayout menuWrapper, LinearLayout textWrapper, List<MenuObject> menuObjects, int actionBarHeight) {
            this.mContext = context;
            this.mMenuWrapper = menuWrapper;
            this.mTextWrapper = textWrapper;
            this.mMenuObjects = menuObjects;

            /**
             * Make menu looks better by setting toolbar height as itemSize.
             */
            this.mMenuItemSize = actionBarHeight;
            setViews();
            resetAnimations();
            mAnimatorSetShowMenu = setOpenCloseAnimation(false);
            mAnimatorSetHideMenu = setOpenCloseAnimation(true);
        }

        /**
         * Creating views and filling to wrappers
         */
        private void setViews() {
            for (int i = 0; i < mMenuObjects.size(); i++) {
                MenuObject menuObject = mMenuObjects.get(i);
                mTextWrapper.addView(MenuUtil.getItemTextView(mContext, menuObject, mMenuItemSize));
                mMenuWrapper.addView(MenuUtil.getImageWrapper(mContext, menuObject, mMenuItemSize, clickItem, longClickItem, i != mMenuObjects.size() - 1));
            }
        }

        /**
         * Set starting params to all animations
         */
        private void resetAnimations() {
            for (int i = 0; i < getItemCount(); i++) {
                resetTextAnimation(mTextWrapper.getChildAt(i));
                if (i == 0) {
                    resetSideAnimation(mMenuWrapper.getChildAt(i));
                } else {
                    resetVerticalAnimation(mMenuWrapper.getChildAt(i), false);
                }
            }
        }

        /**
         * get Item count
         */
        private int getItemCount() {
            return mMenuObjects.size();
        }

        /**
         * Set starting params to text animations
         */
        private void resetTextAnimation(View v) {
            AnimHelper.setAlpha(v, !mIsMenuOpen ? 0 : 1);
            AnimHelper.setTranslationX(v, !mIsMenuOpen ? mMenuItemSize : 0);
        }

        /**
         * Set starting params to side animations
         */
        private void resetSideAnimation(View view) {
            if (!mIsMenuOpen) {
                AnimHelper.setRotation(view, 0);
                AnimHelper.setRotationY(view, -90);
                AnimHelper.setRotationX(view, 0);
            }
            AnimHelper.setPivotX(view, mMenuItemSize);
            AnimHelper.setPivotY(view, mMenuItemSize / 2);
        }

        /**
         * Set starting params to vertical animations
         */
        private void resetVerticalAnimation(View view, boolean toTop) {
            if (!mIsMenuOpen) {
                AnimHelper.setRotation(view, 0);
                AnimHelper.setRotationY(view, 0);
                AnimHelper.setRotationX(view, -90);
            }
            AnimHelper.setPivotX(view, mMenuItemSize / 2);
            AnimHelper.setPivotY(view, !toTop ? 0 : mMenuItemSize);
        }

        /**
         * Creates Open / Close AnimatorSet
         */
        private AnimatorSet setOpenCloseAnimation(boolean isCloseAnimation) {
            List<Animator> textAnimations = new ArrayList<>();
            List<Animator> imageAnimations = new ArrayList<>();

            if (isCloseAnimation) {
                for (int i = getItemCount() - 1; i >= 0; i--) {
                    fillOpenClosingAnimations(true, textAnimations, imageAnimations, i);
                }
            } else {
                for (int i = 0; i < getItemCount(); i++) {
                    fillOpenClosingAnimations(false, textAnimations, imageAnimations, i);
                }
            }

            AnimatorSet textCloseAnimatorSet = new AnimatorSet();
            textCloseAnimatorSet.playSequentially(textAnimations);

            AnimatorSet imageCloseAnimatorSet = new AnimatorSet();
            imageCloseAnimatorSet.playSequentially(imageAnimations);

            AnimatorSet animatorFullSet = new AnimatorSet();
            animatorFullSet.playTogether(imageCloseAnimatorSet, textCloseAnimatorSet);
            animatorFullSet.setDuration(mAnimationDurationMilis);
            animatorFullSet.addListener(mCloseOpenAnimatorListener);
            animatorFullSet.setStartDelay(0);
            animatorFullSet.setInterpolator(new MenuUtil.HesitateInterpolator());
            return animatorFullSet;
        }

        /**
         * Filling arrays of animations to build Set of Closing / Opening animations
         */
        private void fillOpenClosingAnimations(boolean isCloseAnimation, List<Animator> textAnimations, List<Animator> imageAnimations, int wrapperPosition) {
            AnimatorSet textAnimatorSet = new AnimatorSet();
            Animator textAppearance = isCloseAnimation ? MenuUtil.alphaDisappear(mTextWrapper.getChildAt(wrapperPosition)) : MenuUtil.alphaAppear(mTextWrapper.getChildAt(wrapperPosition));

            Animator textTranslation = isCloseAnimation ? MenuUtil.translationRight(mTextWrapper.getChildAt(wrapperPosition), mContext.getResources().getDimension(R.dimen.dimen_8)) : MenuUtil.translationLeft(mTextWrapper.getChildAt(wrapperPosition), mContext.getResources().getDimension(R.dimen.dimen_8));

            textAnimatorSet.playTogether(textAppearance, textTranslation);
            textAnimations.add(textAnimatorSet);

            Animator imageRotation = isCloseAnimation ? wrapperPosition == 0 ? MenuUtil.rotationCloseToRight(mMenuWrapper.getChildAt(wrapperPosition)) : MenuUtil.rotationCloseVertical(mMenuWrapper.getChildAt(wrapperPosition)) : wrapperPosition == 0 ? MenuUtil.rotationOpenFromRight(mMenuWrapper.getChildAt(wrapperPosition)) : MenuUtil.rotationOpenVertical(mMenuWrapper.getChildAt(wrapperPosition));
            imageAnimations.add(imageRotation);
        }

        private void viewClicked(View v) {
            if (mIsMenuOpen && !mIsAnimationRun) {
                mClickedView = v;
                int childIndex = mMenuWrapper.indexOfChild(v);
                if (childIndex == -1) {
                    return;
                }
                toggleIsAnimationRun();
                buildChosenAnimation(childIndex);
                toggleIsMenuOpen();
            }
        }

        private void toggleIsAnimationRun() {
            mIsAnimationRun = !mIsAnimationRun;
        }

        /**
         * Builds and runs chosen item and menu closing animation
         */
        private void buildChosenAnimation(int childIndex) {
            List<Animator> fadeOutTextTopAnimatorList = new ArrayList<>();
            List<Animator> closeToBottomImageAnimatorList = new ArrayList<>();
            for (int i = 0; i < childIndex; i++) {
                View view = mMenuWrapper.getChildAt(i);
                resetVerticalAnimation(view, true);
                closeToBottomImageAnimatorList.add(MenuUtil.rotationCloseVertical(view));
                fadeOutTextTopAnimatorList.add(MenuUtil.fadeOutSet(mTextWrapper.getChildAt(i), mContext.getResources().getDimension(R.dimen.dimen_8)));
            }
            AnimatorSet closeToBottom = new AnimatorSet();
            closeToBottom.playSequentially(closeToBottomImageAnimatorList);
            AnimatorSet fadeOutTop = new AnimatorSet();
            fadeOutTop.playSequentially(fadeOutTextTopAnimatorList);

            List<Animator> fadeOutTextBottomAnimatorList = new ArrayList<>();
            List<Animator> closeToTopAnimatorObjects = new ArrayList<>();
            for (int i = getItemCount() - 1; i > childIndex; i--) {
                View view = mMenuWrapper.getChildAt(i);
                resetVerticalAnimation(view, false);
                closeToTopAnimatorObjects.add(MenuUtil.rotationCloseVertical(view));
                fadeOutTextBottomAnimatorList.add(MenuUtil.fadeOutSet(mTextWrapper.getChildAt(i), mContext.getResources().getDimension(R.dimen.dimen_8)));
            }
            AnimatorSet closeToTop = new AnimatorSet();
            closeToTop.playSequentially(closeToTopAnimatorObjects);
            AnimatorSet fadeOutBottom = new AnimatorSet();
            fadeOutBottom.playSequentially(fadeOutTextBottomAnimatorList);

            resetSideAnimation(mMenuWrapper.getChildAt(childIndex));
            ObjectAnimator closeToRight = MenuUtil.rotationCloseToRight(mMenuWrapper.getChildAt(childIndex));
            closeToRight.addListener(mChosenItemFinishAnimatorListener);
            AnimatorSet fadeOutChosenText = MenuUtil.fadeOutSet(mTextWrapper.getChildAt(childIndex), mContext.getResources().getDimension(R.dimen.dimen_8));

            AnimatorSet imageFullAnimatorSet = new AnimatorSet();
            imageFullAnimatorSet.play(closeToBottom).with(closeToTop);
            AnimatorSet textFullAnimatorSet = new AnimatorSet();
            textFullAnimatorSet.play(fadeOutTop).with(fadeOutBottom);
            if (closeToBottomImageAnimatorList.size() >= closeToTopAnimatorObjects.size()) {
                imageFullAnimatorSet.play(closeToBottom).before(closeToRight);
                textFullAnimatorSet.play(fadeOutTop).before(fadeOutChosenText);
            } else {
                imageFullAnimatorSet.play(closeToTop).before(closeToRight);
                textFullAnimatorSet.play(fadeOutBottom).before(fadeOutChosenText);
            }

            AnimatorSet fullAnimatorSet = new AnimatorSet();
            fullAnimatorSet.playTogether(imageFullAnimatorSet, textFullAnimatorSet);
            fullAnimatorSet.setDuration(mAnimationDurationMilis);
            fullAnimatorSet.setInterpolator(new MenuUtil.HesitateInterpolator());
            fullAnimatorSet.start();
        }

        private void toggleIsMenuOpen() {
            mIsMenuOpen = !mIsMenuOpen;
        }

        public void menuToggle() {
            if (!mIsAnimationRun) {
                resetAnimations();
                mIsAnimationRun = true;
                if (mIsMenuOpen) {
                    mAnimatorSetHideMenu.start();
                } else {
                    mAnimatorSetShowMenu.start();
                }
                toggleIsMenuOpen();
            }
        }

        public void setOnItemClickListener(OnMenuItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        public void setOnItemLongClickListener(OnMenuItemLongClickListener listener) {
            mOnItemLongClickListener = listener;
        }

        public void setAnimationDuration(int durationMillis) {
            mAnimationDurationMilis = durationMillis;
            mAnimatorSetShowMenu.setDuration(mAnimationDurationMilis);
            mAnimatorSetHideMenu.setDuration(mAnimationDurationMilis);
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(View clickedView, int position);
    }

    public interface OnMenuItemLongClickListener {
        void onMenuItemLongClick(View clickedView, int position);
    }
}