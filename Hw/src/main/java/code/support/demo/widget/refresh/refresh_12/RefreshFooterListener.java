package code.support.demo.widget.refresh.refresh_12;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Design on 2016/5/12.
 */
public abstract class RefreshFooterListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager mLinearLayoutManager;

    private int mTotalItemCount;
    private int mVisibleItemCount;
    private int mLastVisibleItemPosition;

    private boolean loading = true;
    private int previousTotal = 0;
    private int currentPage = 1;

    public RefreshFooterListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        mTotalItemCount = mLinearLayoutManager.getItemCount();
        mVisibleItemCount = recyclerView.getChildCount();
        mLastVisibleItemPosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

        if (loading) {
            if (mTotalItemCount > previousTotal) {
                loading = false;
                previousTotal = mTotalItemCount;
            }
        }

        if (!loading && (mVisibleItemCount > 0) && (mLastVisibleItemPosition >= mTotalItemCount - 1)) {
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    public abstract void onLoadMore(int currentPage);
}
