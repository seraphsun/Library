package code.support.demo.widget.refresh.refresh_11;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by Design on 2016/5/12.
 */
public class RefreshLayout extends ListView implements AbsListView.OnScrollListener, HeaderView.StateChangedListener {

    private final static float OFFSET_RADIO = 1.8f;
    private final static int PULL_LOAD_MORE_DELTA = 50;
    private final static int SCROLL_DURATION = 400;

    private Scroller mScroller;
    private HeaderView mHeaderView;
    private FooterView mFooterView;

    private float mLastY = -1;
    private boolean mIsFooterReady = false;
    private boolean isTouchingScreen = false;// 手指是否触摸屏幕
    private boolean mEnablePullRefresh = true;

    private int mTotalItemCount;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;

    private ScrollBack mScrollBack;

    private enum ScrollBack {
        header,
        footer
    }

    private OnScrollListener mScrollListener;
    private RefreshListener mRefreshListener;

    public RefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);

        mHeaderView = new HeaderView(context);
        mHeaderView.setStateChangedListener(this);
        addHeaderView(mHeaderView);

        mFooterView = new FooterView(context);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!mIsFooterReady) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                isTouchingScreen = true;
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisibleHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1;
                isTouchingScreen = false;
                if (getFirstVisiblePosition() == 0) {
                    resetHeaderHeight();
                }
                if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void updateHeaderHeight(float delta) {
        int newHeight = (int) delta + mHeaderView.getVisibleHeight();
        updateHeaderHeight(newHeight);
    }

    private void updateHeaderHeight(int height) {
        if (mEnablePullRefresh) {
            if (mHeaderView.getCurrentState() == HeaderView.STATE.normal && height >= mHeaderView.getStretchHeight()) {
                // 由normal变成stretch的逻辑：1、当前状态是normal；2、下拉头达到了stretchHeight的高度
                mHeaderView.updateState(HeaderView.STATE.stretch);
            } else if (mHeaderView.getCurrentState() == HeaderView.STATE.stretch && height >= mHeaderView.getReadyHeight()) {
                // 由stretch变成ready的逻辑：1、当前状态是stretch；2、下拉头达到了readyHeight的高度
                mHeaderView.updateState(HeaderView.STATE.ready);
            } else if (mHeaderView.getCurrentState() == HeaderView.STATE.stretch && height < mHeaderView.getStretchHeight()) {
                // 由stretch变成normal的逻辑：1、当前状态是stretch；2、下拉头高度小于stretchHeight的高度
                mHeaderView.updateState(HeaderView.STATE.normal);
            } else if (mHeaderView.getCurrentState() == HeaderView.STATE.end && height < 2) {
                // 由end变成normal的逻辑：1、当前状态是end；2、下拉头高度小于一个极小值
                mHeaderView.updateState(HeaderView.STATE.normal);
            }
        }
        // 动态设置HeaderView的高度
        mHeaderView.setVisibleHeight(height);
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) {
                // more.
                mFooterView.setState(FooterView.STATE.ready);
            } else {
                mFooterView.setState(FooterView.STATE.normal);
            }
        }
        mFooterView.setBottomMargin(height);
    }

    /**
     * 重置headerHeight的高度
     * 逻辑：1、如果状态处于非refreshing，则回滚到height=0状态2；2、如果状态处于refreshing，则回滚到stretchHeight高度
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisibleHeight();
        if (height == 0) {
            // not visible.
            return;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mHeaderView.getCurrentState() == HeaderView.STATE.refreshing && height <= mHeaderView.getStretchHeight()) {
            return;
        }
        // default: scroll back to dismiss header.
        int finalHeight = 0;
        // is refreshing, just scroll back to show all the header.
        if ((mHeaderView.getCurrentState() == HeaderView.STATE.ready || mHeaderView.getCurrentState() == HeaderView.STATE.refreshing) && height > mHeaderView.getStretchHeight()) {
            finalHeight = mHeaderView.getStretchHeight();
        }

        mScrollBack = ScrollBack.header;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(FooterView.STATE.loading);
        if (mRefreshListener != null) {
            mRefreshListener.onLoadMore();
        }
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = ScrollBack.footer;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == ScrollBack.header) {
                updateHeaderHeight(mScroller.getCurrY());
                if (mScroller.getCurrY() < 2 && mHeaderView.getCurrentState() == HeaderView.STATE.end) {
                    // 停止滚动了
                    // 逻辑：如果header范围进入了一个极小值内，且当前的状态是end，就把状态置成normal
                    mHeaderView.updateState(HeaderView.STATE.normal);
                }
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void notifyStateChanged(HeaderView.STATE oldState, HeaderView.STATE newState) {
        if (newState == HeaderView.STATE.refreshing) {
            if (mRefreshListener != null) {
                mRefreshListener.onRefresh();
            }
        }
    }

    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(FooterView.STATE.normal);
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFooterView.setEnabled(false);
                    startLoadMore();
                }
            });
        }
    }

    public void stopRefresh() {
        if (mHeaderView.getCurrentState() == HeaderView.STATE.refreshing) {
            mHeaderView.updateState(HeaderView.STATE.end);
            if (!isTouchingScreen) {
                resetHeaderHeight();
            }
        } else {
            throw new IllegalStateException("can not stop refresh while it is not refreshing!");
        }
    }

    public void stopLoadMore() {
        if (mPullLoading) {
            mPullLoading = false;
            mFooterView.setState(FooterView.STATE.normal);
        }
        mFooterView.setEnabled(true);
    }

    public void setRefreshListener(RefreshListener l) {
        mRefreshListener = l;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    public interface OnXScrollListener extends OnScrollListener {
        void onXScrolling(View view);
    }

    public interface RefreshListener {
        void onRefresh();

        void onLoadMore();
    }
}
