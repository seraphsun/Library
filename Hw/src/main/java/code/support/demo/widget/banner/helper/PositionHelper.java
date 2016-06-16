package code.support.demo.widget.banner.helper;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import code.support.demo.widget.banner.BannerLayout;

/**
 * Created by Design on 2016/5/6.
 */
public class PositionHelper {

    // Can not be less than 3
    public final static int MULTIPLIER = 5;

    public static int getRealFromFake(BannerLayout viewPager, int fake) {
        int realAdapterSize = viewPager.getAdapterSize() / MULTIPLIER;
        if (realAdapterSize == 0) {
            return 0;
        }
        fake = fake % realAdapterSize;
        int currentReal = viewPager.getFakeCurrentItem();
        return fake + (currentReal - currentReal % realAdapterSize);
    }

    public static int getFakeFromReal(BannerLayout viewPager, int real) {
        int realAdapterSize = viewPager.getAdapterSize() / MULTIPLIER;
        if (realAdapterSize == 0) {
            return 0;
        }
        return real % realAdapterSize;
    }

    public static int getStartPosition(BannerLayout viewPager) {
        return viewPager.getAdapterSize() / MULTIPLIER;
    }

    public static int getEndPosition(BannerLayout viewPager) {
        int realAdapterSize = viewPager.getAdapterSize() / MULTIPLIER;
        return realAdapterSize * (MULTIPLIER - 1) - 1;
    }

    public static int getAdapterSize(ViewPager viewPager) {
        if (viewPager instanceof BannerLayout) {
            BannerLayout infiniteViewPager = (BannerLayout) viewPager;
            return getRealAdapterSize(infiniteViewPager);
        }
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter instanceof BannerAdapter) {
            BannerAdapter infinitePagerAdapter = (BannerAdapter) viewPager.getAdapter();
            return infinitePagerAdapter.getItemCount();
        }
        return adapter == null ? 0 : adapter.getCount();
    }

    public static int getRealAdapterSize(BannerLayout viewPager) {
        return viewPager.isBannerAdapter() ? viewPager.getAdapterSize() / MULTIPLIER : viewPager.getAdapterSize();
    }

    public static int getRealPosition(BannerLayout viewPager, int position) {
        int realAdapterSize = getRealAdapterSize(viewPager);
        if (realAdapterSize == 0) {
            return 0;
        }
        int startPosition = getStartPosition(viewPager);
        int endPosition = getEndPosition(viewPager);
        if (position < startPosition) {
            return endPosition + 1 - realAdapterSize + position % realAdapterSize;
        }
        if (position > endPosition) {
            return startPosition + position % realAdapterSize;
        }
        return position;
    }

    public static boolean isOutOfRange(BannerLayout viewPager, int position) {
        return position < getStartPosition(viewPager) || position > getEndPosition(viewPager);
    }
}
