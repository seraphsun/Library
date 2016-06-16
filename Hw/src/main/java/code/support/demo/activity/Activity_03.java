package code.support.demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import code.support.demo.R;
import code.support.demo.widget.refresh.refresh_03.HwRefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_03 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_03);

        final HwRefreshLayout refreshLayout = (HwRefreshLayout) findViewById(R.id.mRefreshLayout);
        refreshLayout.setRefreshListener(HwRefreshLayout -> HwRefreshLayout.postDelayed((Runnable) HwRefreshLayout::finishRefreshing, 3000));
    }
}
