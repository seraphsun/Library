package code.support.demo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import code.support.demo.R;
import code.support.demo.widget.refresh.refresh_11.RefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_11 extends AppCompatActivity implements RefreshLayout.RefreshListener {

    private RefreshLayout mRefreshLayout;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mRefreshLayout.stopRefresh();
                    break;
                case 2:
                    mRefreshLayout.stopLoadMore();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_11);

        mRefreshLayout = (RefreshLayout) findViewById(R.id.mRefreshLayout);
        mRefreshLayout.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, getData()));
        mRefreshLayout.setRefreshListener(this);
        mRefreshLayout.setPullLoadEnable(true);
    }

    private List<String> getData() {

        List<String> data = new ArrayList<>();
        data.add("To see a world in a grain of sand,");
        data.add("And a heaven in a wild flower,");
        data.add("Hold infinity in the palm of your hand,");
        data.add("And eternity in an hour.");

        return data;
    }


    @Override
    public void onRefresh() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
