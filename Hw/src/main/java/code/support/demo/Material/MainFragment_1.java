package code.support.demo.Material;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import code.support.demo.R;

/**
 * Created by Design on 2016/3/29.
 */
public class MainFragment_1 extends Fragment {

    private AppCompatActivity mActivity;
    private View mContentView;

    // 导航栏，用于tab菜单切换
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.m_main_fragment_1, null);

        initView(contentView);

        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化布局视图
     */
    private void initView(View contentView) {
        mTabLayout = (TabLayout) contentView.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) contentView.findViewById(R.id.tabPager);

        // 初始化TabLayout的title数据集
        List<String> titles = new ArrayList<>();
        titles.add("message");
        titles.add("profile");
        titles.add("friends");
        titles.add("favorites");
        titles.add("user");

        // 初始化ViewPager的数据集
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainFragment_Tab());
        fragments.add(new MainFragment_Tab());
        fragments.add(new MainFragment_Tab());
        fragments.add(new MainFragment_Tab());
        fragments.add(new MainFragment_Tab());

        // 初始化TabLayout的title
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(3)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(4)));

        // 创建ViewPager的adapter
        MyFragmentAdapter adapter = new MyFragmentAdapter(getChildFragmentManager(), fragments, titles);
        mViewPager.setAdapter(adapter);

        // 关联TabLayout与ViewPager，同时也要覆写PagerAdapter的getPageTitle方法，否则Tab没有title
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
    }

    public class MyFragmentAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments;
        private List<String> mTitles;

        public MyFragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            mFragments = fragments;
            mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            // 相当于Fragment的onResume

        } else {
            // 相当于Fragment的onPause

        }
    }
}
