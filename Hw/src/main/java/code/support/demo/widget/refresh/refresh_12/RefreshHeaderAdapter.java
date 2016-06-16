package code.support.demo.widget.refresh.refresh_12;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Design on 2016/5/12.
 */
public class RefreshHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADERS_START = Integer.MIN_VALUE;
    private static final int FOOTERS_START = Integer.MIN_VALUE + 10;
    private static final int ITEMS_START = Integer.MIN_VALUE + 20;
    private static final int ADAPTER_MAX_TYPES = 100;

    private RecyclerView.Adapter mWrappedAdapter;
    private List<View> mHeaderViews, mFooterViews;
    private Map<Class, Integer> mItemTypesOffset;

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
        }


        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
        }


        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
        }


        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int hCount = getHeaderCount();
            notifyItemRangeChanged(fromPosition + hCount, toPosition + hCount + itemCount);
        }
    };

    public RefreshHeaderAdapter(RecyclerView.Adapter adapter) {
        mHeaderViews = new ArrayList<>();
        mFooterViews = new ArrayList<>();
        mItemTypesOffset = new HashMap<>();
        setWrappedAdapter(adapter);
    }

    private void setWrappedAdapter(RecyclerView.Adapter adapter) {
        if (mWrappedAdapter != null) mWrappedAdapter.unregisterAdapterDataObserver(mDataObserver);
        mWrappedAdapter = adapter;
        Class adapterClass = mWrappedAdapter.getClass();
        if (!mItemTypesOffset.containsKey(adapterClass)) putAdapterTypeOffset(adapterClass);
        mWrappedAdapter.registerAdapterDataObserver(mDataObserver);
    }

    private int getAdapterTypeOffset() {
        return mItemTypesOffset.get(mWrappedAdapter.getClass());
    }

    private void putAdapterTypeOffset(Class adapterClass) {
        mItemTypesOffset.put(adapterClass, ITEMS_START + mItemTypesOffset.size() * ADAPTER_MAX_TYPES);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < HEADERS_START + getHeaderCount())
            return new ViewHolder(mHeaderViews.get(viewType - HEADERS_START));
        else if (viewType < FOOTERS_START + getFooterCount())
            return new ViewHolder(mFooterViews.get(viewType - FOOTERS_START));
        else {
            return mWrappedAdapter.onCreateViewHolder(parent, viewType - getAdapterTypeOffset());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int hCount = getHeaderCount();
        if (position >= hCount && position < hCount + mWrappedAdapter.getItemCount()) {
            mWrappedAdapter.onBindViewHolder(holder, position - hCount);
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getFooterCount() + getWrappedItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        int hCount = getHeaderCount();
        if (position < hCount) return HEADERS_START + position;
        else {
            int itemCount = mWrappedAdapter.getItemCount();
            if (position < hCount + itemCount) {
                return getAdapterTypeOffset() + mWrappedAdapter.getItemViewType(position - hCount);
            } else {
                return FOOTERS_START + position - hCount - itemCount;
            }
        }
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }

    public void addFooterView(View view) {
        mFooterViews.add(view);
    }

    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    public int getFooterCount() {
        return mFooterViews.size();
    }

    public int getWrappedItemCount() {
        return mWrappedAdapter.getItemCount();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mWrappedAdapter != null && mWrappedAdapter.getItemCount() > 0) {
            notifyItemRangeRemoved(getHeaderCount(), mWrappedAdapter.getItemCount());
        }
        setWrappedAdapter(adapter);
        notifyItemRangeInserted(getHeaderCount(), mWrappedAdapter.getItemCount());
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
