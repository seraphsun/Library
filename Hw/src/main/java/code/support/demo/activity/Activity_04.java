package code.support.demo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import code.support.demo.R;
import code.support.demo.widget.refresh.refresh_04.RefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_04 extends AppCompatActivity {

    private RefreshLayout refreshView;

    private ListView listView;
    private List<String> dataList;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_04);

        refreshView = (RefreshLayout) findViewById(R.id.refresh_fun_game);
        listView = (ListView) findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, createDate());

        listView.setAdapter(arrayAdapter);
        refreshView.setOnRefreshListener(new RefreshLayout.RefreshListener() {
            @Override
            public void onRefreshing() {
                try {
                    // 模拟网络请求耗时动作
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    private List<String> createDate() {
        dataList = new ArrayList<>();
        dataList.add("A");
        dataList.add("B");
        dataList.add("C");
        dataList.add("D");
        dataList.add("E");
        dataList.add("F");
        dataList.add("G");
        return dataList;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dataList.add("X");
            arrayAdapter.notifyDataSetChanged();
            refreshView.finishRefreshing();
        }
    };
}
