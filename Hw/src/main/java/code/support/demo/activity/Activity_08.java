package code.support.demo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import code.support.demo.R;
import code.support.demo.widget.refresh.refresh_08.RefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_08 extends AppCompatActivity implements RefreshLayout.OnRefreshListener {

    private final static int REFRESH_COMPLETE = 0;

    private RefreshLayout mListView;
    private List<String> mDatas;
    private ArrayAdapter<String> mAdapter;

    private InterHandler mInterHandler = new InterHandler(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_08);

        mListView = (RefreshLayout) findViewById(R.id.listview);
        String[] data = new String[]{
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
                "hello world",
        };
        mDatas = new ArrayList<>(Arrays.asList(data));
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setOnMeiTuanRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    mDatas.add(0, "new data");
                    mInterHandler.sendEmptyMessage(REFRESH_COMPLETE);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * InterHandler是一个私有静态内部类继承自Handler，内部持有MainActivity的弱引用，避免内存泄露
     */
    private static class InterHandler extends Handler {

        private WeakReference<Activity_08> mActivity;

        public InterHandler(Activity_08 activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity_08 activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case REFRESH_COMPLETE:
                        activity.mListView.setOnRefreshComplete();
                        activity.mAdapter.notifyDataSetChanged();
                        activity.mListView.setSelection(0);
                        break;
                }
            } else {
                super.handleMessage(msg);
            }
        }
    }
}
