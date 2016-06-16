package code.support.demo.Test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import code.support.demo.R;
import code.support.demo.widget.navigationtab.NavigationTab;

/**
 * Created by zpj on 2016/6/13.
 */
public class TestActivity_4 extends AppCompatActivity {

    @Bind(R.id.vp)
    ViewPager mViewPager;

    @Bind(R.id.nts_top)
    NavigationTab mTopNavigationTab;
    @Bind(R.id.nts_center)
    NavigationTab mCenterNavigationTab;
    @Bind(R.id.nts_bottom)
    NavigationTab mBottomNavigationTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_04);

        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                final View view = new View(getBaseContext());
                container.addView(view);
                return view;
            }
        });

        mTopNavigationTab.setTabIndex(1, true);
        mCenterNavigationTab.setViewPager(mViewPager, 1);
        mBottomNavigationTab.setTabIndex(1, true);
    }
}
