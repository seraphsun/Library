package code.support.demo.Material;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import code.support.demo.R;
import code.support.demo.widget.navigationtab.MainTabHost;

/**
 * This is Material Design Code.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String[] mTitle = {"首页", "刷新", "主题", "个人中心"};
    private Map<Integer, Fragment> mFragmentMap = new HashMap<>();

    private int[] mIconNormal = {
            R.mipmap.icon_main_tabhost_1_normal,
            R.mipmap.icon_main_tabhost_2_normal,
            R.mipmap.icon_main_tabhost_3_normal,
            R.mipmap.icon_main_tabhost_4_normal,
    };

    private int[] mIconSelect = {
            R.mipmap.icon_main_tabhost_1_selected,
            R.mipmap.icon_main_tabhost_2_selected,
            R.mipmap.icon_main_tabhost_3_selected,
            R.mipmap.icon_main_tabhost_4_selected,
    };

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_main_activity);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("Material");
        setSupportActionBar(toolbar);

        // FloatingActionButton悬浮按钮，底部弹出Material风格的Toast.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        // DrawerLayout的矢量开关，需要动态的添加到toolbar中且关联DrawerLayout.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.headerView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserInfo.class));
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));

        MainTabHost tabHost = (MainTabHost) findViewById(R.id.tabHost);
        tabHost.setViewPager(viewPager);
    }

    private Fragment getFragment(int position) {
        Fragment fragment = mFragmentMap.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new MainFragment_1();
                    break;
                case 1:
                    fragment = new MainFragment_2();
                    break;
                case 2:
                    fragment = new MainFragment_3();
                    break;
                case 3:
                    fragment = new MainFragment_4();
                    break;
            }
            mFragmentMap.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_camera:
                // Handle the camera action
                break;
            case R.id.nav_gallery:

                break;
            case R.id.nav_slideshow:

                break;
            case R.id.nav_manage:

                break;
            case R.id.nav_share:

                break;
            case R.id.nav_send:

                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 需要实现OnItemIconTextSelectListener接口
     */
    class PageAdapter extends FragmentPagerAdapter implements MainTabHost.OnItemIconTextSelectListener {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int[] onIconSelect(int position) {
            int icon[] = new int[2];
            icon[0] = mIconSelect[position];
            icon[1] = mIconNormal[position];
            return icon;
        }

        @Override
        public String onTextSelect(int position) {
            return mTitle[position];
        }

        @Override
        public int getCount() {
            return mTitle.length;
        }
    }
}
