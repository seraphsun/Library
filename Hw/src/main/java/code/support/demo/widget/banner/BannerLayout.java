package code.support.demo.widget.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import code.support.demo.widget.banner.helper.PositionHelper;
import code.support.demo.widget.banner.helper.BannerAdapter;

/**
 * Created by Design on 2016/5/6.
 */
public class BannerLayout extends ViewPager {

    private static final long DEFAULT_AUTO_SCROLL_INTERVAL = 3000;// 3s
    private static final int MSG_AUTO_SCROLL = 1;
    private static final int MSG_SET_PAGE = 2;

    private Handler mHandler;
    private boolean mIsAutoScroll;
    private boolean mIsTouched;
    private boolean mIsBannerAdapter;

    private OnPageChangeListener mOnPageChangeListener;
    private long mDelay = DEFAULT_AUTO_SCROLL_INTERVAL;

    public BannerLayout(Context context) {
        this(context, null);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        setOffscreenPageLimit(1);
        // set listeners
        super.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(PositionHelper.getFakeFromReal(BannerLayout.this, position), positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position < PositionHelper.getStartPosition(BannerLayout.this) || position > PositionHelper.getEndPosition(BannerLayout.this)) {
                    mHandler.removeMessages(MSG_SET_PAGE);
                    Message msg = mHandler.obtainMessage(MSG_SET_PAGE);
                    msg.arg1 = position;
                    mHandler.sendMessageDelayed(msg, 500);
                    return;
                }

                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(PositionHelper.getFakeFromReal(BannerLayout.this, position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
        mHandler = new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                switch (msg.what) {
                    case MSG_AUTO_SCROLL:
                        setItemToNext();
                        sendDelayMessage();
                        break;
                    case MSG_SET_PAGE:
                        setFakeCurrentItem(PositionHelper.getRealPosition(BannerLayout.this, msg.arg1), false);
                        break;
                }
            }
        };
    }

    private void setItemToNext() {
        PagerAdapter adapter = getAdapter();
        if (adapter == null || adapter.getCount() == 0) {
            stopAutoScroll();
            return;
        }
        int totalCount = isBannerAdapter() ? PositionHelper.getRealAdapterSize(this) : adapter.getCount();
        if (totalCount <= 1) {
            return;
        }
        int nextItem = getFakeCurrentItem() + 1;
        if (isBannerAdapter()) {
            setFakeCurrentItem(nextItem);
        } else {
            if (nextItem == totalCount) {
                setFakeCurrentItem(0);
            }
        }
    }

    public void startAutoScroll() {
        startAutoScroll(mDelay);
    }

    public void startAutoScroll(long delayTime) {
        if (getAdapter() == null || getAdapter().getCount() == 0) {
            return;
        }
        mDelay = delayTime;
        mIsAutoScroll = true;
        sendDelayMessage();
    }

    private void sendDelayMessage() {
        mHandler.removeMessages(MSG_AUTO_SCROLL);
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_SCROLL, mDelay);
    }

    public void stopAutoScroll() {
        mIsAutoScroll = false;
        mHandler.removeMessages(MSG_AUTO_SCROLL);
    }

    public void setAutoScrollTime(long autoScrollTime) {
        mDelay = autoScrollTime;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        if (mIsAutoScroll || mIsTouched) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mIsTouched = true;
                    stopAutoScroll();
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.mIsAutoScroll || mIsTouched) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    mIsTouched = false;
                    startAutoScroll();
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        mIsBannerAdapter = getAdapter() instanceof BannerAdapter;
        if (!mIsBannerAdapter) {
            throw new IllegalArgumentException("Currently, only InfinitePagerAdapter is supported");
        }
        setFakeCurrentItem(PositionHelper.getRealPosition(BannerLayout.this, 0), false);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    private void setFakeCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    private void setFakeCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    public int getFakeCurrentItem() {
        return super.getCurrentItem();
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(PositionHelper.getRealFromFake(this, item));
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(PositionHelper.getRealFromFake(this, item), smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        return PositionHelper.getFakeFromReal(this, getFakeCurrentItem());
    }

    public boolean isBannerAdapter() {
        return mIsBannerAdapter;
    }

    public int getAdapterSize() {
        return getAdapter() == null ? 0 : getAdapter().getCount();
    }
}
