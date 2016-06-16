package code.support.demo.widget.banner.helper;

import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

/**
 * Created by Design on 2016/5/6.
 */
public abstract class BannerAdapter extends PagerAdapter {

    static final int IGNORE_ITEM_VIEW_TYPE = AdapterView.ITEM_VIEW_TYPE_IGNORE;

    private final RecycleBin recycleBin;

    public BannerAdapter() {
        this(new RecycleBin());
    }

    BannerAdapter(RecycleBin recycleBin) {
        this.recycleBin = recycleBin;
        recycleBin.setViewTypeCount(getViewTypeCount());
    }

    @Override
    public final int getCount() {
        return getItemCount() * PositionHelper.MULTIPLIER;
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        int viewType = getItemViewType(position);
        View view = null;
        if (viewType != IGNORE_ITEM_VIEW_TYPE) {
            view = recycleBin.getScrapView(position, viewType);
        }
        view = getViewInternal(position, view, container);
        container.addView(view);
        return view;
    }

    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        int viewType = getItemViewType(position);
        if (viewType != IGNORE_ITEM_VIEW_TYPE) {
            recycleBin.addScrapView(view, position, viewType);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        recycleBin.scrapActiveViews();
        super.notifyDataSetChanged();
    }

    @Deprecated
    public View getViewInternal(int position, View convertView, ViewGroup container) {
        if (getItemCount() == 0) {
            return null;
        }
        return getView(position % getItemCount(), convertView, container);
    }

    public View getView(int position, View convertView, ViewGroup container) {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    @SuppressWarnings("UnusedParameters")
    public int getItemViewType(int position) {
        return 0;
    }

    public abstract int getItemCount();


    public static class RecycleBin {

        private View[] activeViews = new View[0];
        private int[] activeViewTypes = new int[0];

        private int viewTypeCount;

        private SparseArray<View>[] scrapViews;
        private SparseArray<View> currentScrapViews;

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            SparseArray<View>[] scrapViews = new SparseArray[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new SparseArray<>();
            }
            this.viewTypeCount = viewTypeCount;
            currentScrapViews = scrapViews[0];
            this.scrapViews = scrapViews;
        }

        View getScrapView(int position, int viewType) {
            if (viewTypeCount == 1) {
                return retrieveFromScrap(currentScrapViews, position);
            } else if (viewType >= 0 && viewType < scrapViews.length) {
                return retrieveFromScrap(scrapViews[viewType], position);
            }
            return null;
        }

        private View retrieveFromScrap(SparseArray<View> scrapViews, int position) {
            int size = scrapViews.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    int fromPosition = scrapViews.keyAt(i);
                    View view = scrapViews.get(fromPosition);
                    if (fromPosition == position) {
                        scrapViews.remove(fromPosition);
                        return view;
                    }
                }
                int index = size - 1;
                View r = scrapViews.valueAt(index);
                scrapViews.remove(scrapViews.keyAt(index));
                return r;
            } else {
                return null;
            }
        }

        public void addScrapView(View scrap, int position, int viewType) {
            if (viewTypeCount == 1) {
                currentScrapViews.put(position, scrap);
            } else {
                scrapViews[viewType].put(position, scrap);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                scrap.setAccessibilityDelegate(null);
            }
        }

        public void scrapActiveViews() {
            final View[] activeViews = this.activeViews;
            final int[] activeViewTypes = this.activeViewTypes;
            final boolean multipleScraps = viewTypeCount > 1;

            SparseArray<View> scrapViews = currentScrapViews;
            final int count = activeViews.length;
            for (int i = count - 1; i >= 0; i--) {
                final View victim = activeViews[i];
                if (victim != null) {
                    int whichScrap = activeViewTypes[i];
                    activeViews[i] = null;
                    activeViewTypes[i] = -1;
                    if (!shouldRecycleViewType(whichScrap)) {
                        continue;
                    }
                    if (multipleScraps) {
                        scrapViews = this.scrapViews[whichScrap];
                    }
                    scrapViews.put(i, victim);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        victim.setAccessibilityDelegate(null);
                    }
                }
            }
            pruneScrapViews();
        }

        private boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0;
        }

        private void pruneScrapViews() {
            final int maxViews = activeViews.length;
            final int viewTypeCount = this.viewTypeCount;
            final SparseArray<View>[] scrapViews = this.scrapViews;
            for (int i = 0; i < viewTypeCount; ++i) {
                final SparseArray<View> scrapPile = scrapViews[i];
                int size = scrapPile.size();
                final int extras = size - maxViews;
                size--;
                for (int j = 0; j < extras; j++) {
                    scrapPile.remove(scrapPile.keyAt(size--));
                }
            }
        }
    }
}
