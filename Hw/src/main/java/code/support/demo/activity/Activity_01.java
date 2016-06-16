package code.support.demo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import code.support.demo.R;
import code.support.demo.widget.menu.ActionBarMenu;
import code.support.demo.widget.menu.MenuObject;
import code.support.demo.widget.refresh.refresh_01.RefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_01 extends AppCompatActivity implements ActionBarMenu.OnMenuItemClickListener, ActionBarMenu.OnMenuItemLongClickListener {

    private FragmentManager fragmentManager;
    private ActionBarMenu mMenu;
    private List<MenuObject> mMenuObjects;

    @Bind(R.id.mRefreshLayout)
    RefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_01);

        fragmentManager = getSupportFragmentManager();
        initToolbar();

        ButterKnife.bind(this);
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        mRefreshLayout.setRefreshStyle(RefreshLayout.STYLE_CIRCLES);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initMenuFragment();
    }

    private void initMenuFragment() {
        MenuObject.MenuParams menuParams = new MenuObject.MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.dimen_56));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenu = ActionBarMenu.newInstance(menuParams);
        mMenu.setItemClickListener(this);
        mMenu.setItemLongClickListener(this);
    }

    public List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)

        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)

        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.mipmap.icon_toolbar_right);

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.mipmap.icon_toolbar_right_1);

        MenuObject like = new MenuObject("Like profile");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_toolbar_right_2);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.mipmap.icon_toolbar_right_3));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.mipmap.icon_toolbar_right_4);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.mipmap.icon_toolbar_right_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);

        return menuObjects;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_menu_right:
                if (fragmentManager.findFragmentByTag(ActionBarMenu.TAG) == null) {
                    mMenu.show(fragmentManager, ActionBarMenu.TAG);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 1:
                mRefreshLayout.setRefreshStyle(RefreshLayout.STYLE_ARC);
                break;
            case 2:
                mRefreshLayout.setRefreshStyle(RefreshLayout.STYLE_CIRCLES);
                break;
            case 3:
                mRefreshLayout.setRefreshStyle(RefreshLayout.STYLE_RING);
                break;
            case 4:
                mRefreshLayout.setRefreshStyle(RefreshLayout.STYLE_SMARTISAN);
                break;
            case 5:
                mRefreshLayout.setRefreshStyle(RefreshLayout.STYLE_WATER_DROP);
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {

    }
}
