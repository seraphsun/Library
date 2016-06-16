package code.support.demo.widget.navigationtab;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import code.support.demo.R;

/**
 * Created by Ignacey 2016/1/11.
 */
public class MainTabHost extends LinearLayout implements View.OnClickListener {

    private int mTextSize = 12;
    private int mTextColorSelect = 0xff45c01a;
    private int mTextColorNormal = 0xff777777;
    private int mPadding = 10;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mChildSize;
    private List<MainTabWidget> mTabItems;
    private OnItemIconTextSelectListener mListener;

    public MainTabHost(Context context) {
        this(context, null);
    }

    public MainTabHost(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainTabHost(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.MainTabHost);
        int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            switch (typedArray.getIndex(i)) {
                case R.styleable.MainTabHost_text_size:
                    mTextSize = (int) typedArray.getDimension(i, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.MainTabHost_text_normal_color:
                    mTextColorNormal = typedArray.getColor(i, mTextColorNormal);
                    break;
                case R.styleable.MainTabHost_text_select_color:
                    mTextColorSelect = typedArray.getColor(i, mTextColorSelect);
                    break;
                case R.styleable.MainTabHost_item_padding:
                    mPadding = (int) typedArray.getDimension(i, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPadding, getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();
        mTabItems = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setViewPager(final ViewPager mViewPager) {
        if (mViewPager == null) {
            return;
        }
        this.mViewPager = mViewPager;
        this.mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        PagerAdapter mPagerAdapter = mViewPager.getAdapter();
        if (mPagerAdapter == null) {
            throw new RuntimeException("Please set ViewPagerAdapter first");
        }
        this.mChildSize = mPagerAdapter.getCount();
        if (mPagerAdapter instanceof OnItemIconTextSelectListener) {
            mListener = (OnItemIconTextSelectListener) mPagerAdapter;
        } else {
            throw new RuntimeException("Please allow your adapter to implement the interface of OnItemIconTextSelectListener");
        }

        initItem();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener mOnPageChangeListener) {
        this.mOnPageChangeListener = mOnPageChangeListener;
    }

    private void initItem() {
        for (int i = 0; i < mChildSize; i++) {
            MainTabWidget tabItem = new MainTabWidget(getContext());
            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tabItem.setPadding(mPadding, mPadding, mPadding, mPadding);
            tabItem.setIconText(mListener.onIconSelect(i), mListener.onTextSelect(i));
            tabItem.setTextSize(mTextSize);
            tabItem.setTextColorNormal(mTextColorNormal);
            tabItem.setTextColorSelect(mTextColorSelect);
            tabItem.setLayoutParams(params);
            tabItem.setTag(i);
            tabItem.setOnClickListener(this);
            mTabItems.add(tabItem);
            addView(tabItem);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        if (mViewPager.getCurrentItem() == position) {
            return;
        }
        for (MainTabWidget tabItem : mTabItems) {
            tabItem.setTabAlpha(0);
        }
        mTabItems.get(position).setTabAlpha(1);
        mViewPager.setCurrentItem(position, false);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            View viewLeft;
            View viewRight;

            if (positionOffset > 0) {
//                viewLeft = mViewPager.getChildAt(position);
//                viewRight = mViewPager.getChildAt(position + 1);
//                viewLeft.setAlpha(1 - positionOffset);
//                viewRight.setAlpha(positionOffset);
                mTabItems.get(position).setTabAlpha(1 - positionOffset);
                mTabItems.get(position + 1).setTabAlpha(positionOffset);
            } else {
//                mViewPager.getChildAt(position).setAlpha(1);
                mTabItems.get(position).setTabAlpha(1 - positionOffset);
            }
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    }

    /**
     * Widget监听
     */
    public interface OnItemIconTextSelectListener {

        int[] onIconSelect(int position);

        String onTextSelect(int position);

    }
}
