package code.support.demo.widget.banner.indicator;

import android.support.v4.view.ViewPager;

/**
 * Created by Design on 2016/5/6.
 */
public interface BannerIndicator extends ViewPager.OnPageChangeListener {

    void setViewPager(ViewPager view);
    void setViewPager(ViewPager view, int initialPosition);
    void setCurrentItem(int item);
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
    void notifyDataSetChanged();

}
