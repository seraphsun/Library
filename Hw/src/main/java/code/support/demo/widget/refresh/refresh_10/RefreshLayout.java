package code.support.demo.widget.refresh.refresh_10;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RefreshLayout extends RecyclerView {

    private static final int TYPE_REFRESH_HEADER = -5;
    private static final int TYPE_HEADER = -4;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = -3;

    private Context mContext;
    private boolean pullRefreshEnabled = true;
    private boolean loadingMoreEnabled = true;

    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFootViews = new ArrayList<>();

    private HeaderView mRefreshHeader;

    // adapter没有数据的时候显示,类似于listView的emptyView
    private View mEmptyView;
    private Adapter mAdapter;
    private Adapter mWrapAdapter;

    private float mLastY = -1;
    private static final float DRAG_RATE = 3;

    private int mRefreshProgressStyle = ProgressStyle.SysProgress;
    private int mLoadingMoreProgressStyle = ProgressStyle.SysProgress;

    private boolean isLoadingData = false;
    private boolean isnomore = false;
    private LoadingListener mLoadingListener;

    private int previousTotal = 0;
    private int mPageCount = 0;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        if (pullRefreshEnabled) {
            HeaderView refreshHeader = new HeaderView(mContext);
            mHeaderViews.add(0, refreshHeader);
            mRefreshHeader = refreshHeader;
            mRefreshHeader.setProgressStyle(mRefreshProgressStyle);
        }
        FooterView footView = new FooterView(mContext);
        footView.setProgressStyle(mLoadingMoreProgressStyle);
        addFootView(footView);
        mFootViews.get(0).setVisibility(GONE);
    }

    public void addHeaderView(View view) {
        if (pullRefreshEnabled && !(mHeaderViews.get(0) instanceof HeaderView)) {
            HeaderView refreshHeader = new HeaderView(mContext);
            mHeaderViews.add(0, refreshHeader);
            mRefreshHeader = refreshHeader;
            mRefreshHeader.setProgressStyle(mRefreshProgressStyle);
        }
        mHeaderViews.add(view);
    }

    public void addFootView(final View view) {
        mFootViews.clear();
        mFootViews.add(view);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mWrapAdapter = new WrapAdapter(mHeaderViews, mFootViews, adapter);
        super.setAdapter(mWrapAdapter);
        mAdapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && pullRefreshEnabled) {
                    mRefreshHeader.onMove(deltaY / DRAG_RATE);
                    if (mRefreshHeader.getVisiableHeight() > 0 && mRefreshHeader.getState() < HeaderView.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled) {
                    if (mRefreshHeader.releaseAction()) {
                        if (mLoadingListener != null) {
                            mLoadingListener.onRefresh();
                            isnomore = false;
                            previousTotal = 0;
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private boolean isOnTop() {
        if (mHeaderViews == null || mHeaderViews.isEmpty()) {
            return false;
        }

        View view = mHeaderViews.get(0);
        return view.getParent() != null;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0 && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount() && !isnomore && mRefreshHeader.getState() < HeaderView.STATE_REFRESHING) {

                View footView = mFootViews.get(0);
                isLoadingData = true;
                if (footView instanceof FooterView) {
                    ((FooterView) footView).setState(FooterView.STATE_LOADING);
                } else {
                    footView.setVisibility(View.VISIBLE);
                }
                mLoadingListener.onLoadMore();
            }
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private int findMin(int[] firstPositions) {
        int min = firstPositions[0];
        for (int value : firstPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public void refreshComplete() {
        mRefreshHeader.refreshComplate();
    }

    public void setRefreshHeader(HeaderView refreshHeader) {
        mRefreshHeader = refreshHeader;
    }

    public void setArrowImageView(int resid) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setArrowImageView(resid);
        }
    }

    public void loadMoreComplete() {
        isLoadingData = false;
        View footView = mFootViews.get(0);

        Log.e("export", "previousTotal = " + previousTotal);
        Log.e("export", "ItemCount = " + getLayoutManager().getItemCount());

        if (previousTotal < getLayoutManager().getItemCount()) {


            if (footView instanceof FooterView) {
                ((FooterView) footView).setState(FooterView.STATE_COMPLETE);
            } else {
                footView.setVisibility(View.GONE);
            }
        } else {
            if (footView instanceof FooterView) {
                ((FooterView) footView).setState(FooterView.STATE_NOMORE);
            } else {
                footView.setVisibility(View.GONE);
            }
            isnomore = true;
        }
        previousTotal = getLayoutManager().getItemCount();
    }

    public void noMoreLoading() {
        isLoadingData = false;
        View footView = mFootViews.get(0);
        isnomore = true;
        if (footView instanceof FooterView) {
            ((FooterView) footView).setState(FooterView.STATE_NOMORE);
        } else {
            footView.setVisibility(View.GONE);
        }
    }

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public void setPullRefreshEnabled(boolean enabled) {
        pullRefreshEnabled = enabled;
    }

    public void setLoadingMoreEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
        if (!enabled) {
            if (mFootViews.size() > 0) {
                mFootViews.get(0).setVisibility(GONE);
            }
        }
    }

    public void setRefreshProgressStyle(int style) {
        mRefreshProgressStyle = style;
        if (mRefreshHeader != null) {
            mRefreshHeader.setProgressStyle(style);
        }
    }

    public void setLoadingMoreProgressStyle(int style) {
        mLoadingMoreProgressStyle = style;
        if (mFootViews.size() > 0 && mFootViews.get(0) instanceof FooterView) {
            ((FooterView) mFootViews.get(0)).setProgressStyle(style);
        }
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        mDataObserver.onChanged();
    }

    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                int emptyCount = 0;
                if (pullRefreshEnabled) {
                    emptyCount++;
                }
                if (loadingMoreEnabled) {
                    emptyCount++;
                }
                if (adapter.getItemCount() == emptyCount) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    RefreshLayout.this.setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    RefreshLayout.this.setVisibility(View.VISIBLE);
                }
            }
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter adapter;

        private ArrayList<View> mHeaderViews;

        private ArrayList<View> mFootViews;

        private int headerPosition = 1;

        public WrapAdapter(ArrayList<View> headerViews, ArrayList<View> footViews, RecyclerView.Adapter adapter) {
            this.adapter = adapter;
            this.mHeaderViews = headerViews;
            this.mFootViews = footViews;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        public int getFootersCount() {
            return mFootViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(mHeaderViews.get(0));
            } else if (viewType == TYPE_HEADER) {
                return new SimpleViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(mFootViews.get(0));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (isHeader(position)) {
                return;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                return getHeadersCount() + getFootersCount() + adapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                return TYPE_HEADER;
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return TYPE_NORMAL;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount()) {
                int adjPosition = position - getHeadersCount();
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(observer);
            }
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public class ProgressStyle {

        public static final int SysProgress = -1;
        public static final int BallPulse = 0;
        public static final int BallGridPulse = 1;
        public static final int BallClipRotate = 2;
        public static final int BallClipRotatePulse = 3;
        public static final int SquareSpin = 4;
        public static final int BallClipRotateMultiple = 5;
        public static final int BallPulseRise = 6;
        public static final int BallRotate = 7;
        public static final int CubeTransition = 8;
        public static final int BallZigZag = 9;
        public static final int BallZigZagDeflect = 10;
        public static final int BallTrianglePath = 11;
        public static final int BallScale = 12;
        public static final int LineScale = 13;
        public static final int LineScaleParty = 14;
        public static final int BallScaleMultiple = 15;
        public static final int BallPulseSync = 16;
        public static final int BallBeat = 17;
        public static final int LineScalePulseOut = 18;
        public static final int LineScalePulseOutRapid = 19;
        public static final int BallScaleRipple = 20;
        public static final int BallScaleRippleMultiple = 21;
        public static final int BallSpinFadeLoader = 22;
        public static final int LineSpinFadeLoader = 23;
        public static final int TriangleSkewSpin = 24;
        public static final int Pacman = 25;
        public static final int BallGridBeat = 26;
        public static final int SemiCircleSpin = 27;
    }

    public interface RefreshHeader {

        int STATE_NORMAL = 0;
        int STATE_RELEASE_TO_REFRESH = 1;
        int STATE_REFRESHING = 2;
        int STATE_DONE = 3;

        void onMove(float delta);

        boolean releaseAction();

        void refreshComplate();
    }

    public interface LoadingListener {

        void onRefresh();

        void onLoadMore();
    }
}
