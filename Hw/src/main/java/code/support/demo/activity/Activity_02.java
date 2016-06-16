package code.support.demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import code.support.demo.R;
import code.support.demo.widget.refresh.refresh_02.RefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_02 extends AppCompatActivity {

    private RefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_02);

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);

        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {

            @Override
            public void refreshing() {
                // do something when refresh starts
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefreshing();
                    }
                }, 5000);
            }

            @Override
            public void completeRefresh() {
                // do something when refresh complete
            }
        });
    }
}
