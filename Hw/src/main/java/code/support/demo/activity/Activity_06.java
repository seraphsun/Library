package code.support.demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import code.support.demo.R;
import code.support.demo.widget.refresh.refresh_06.RefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_06 extends AppCompatActivity {

    public static final int REFRESH_DELAY = 2000;

    private RefreshLayout mRefreshLayout;
    private ArrayList<String> listData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_06);

        listData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            listData.add("item" + i);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(listData));

        mRefreshLayout = (RefreshLayout) findViewById(R.id.pull_to_refresh);
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        public ArrayList<String> datas = null;

        public MyAdapter(ArrayList<String> datas) {
            this.datas = datas;
        }

        // 创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.refresh_item, viewGroup, false);
            return new ViewHolder(view);
        }

        // 将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.mTextView.setText(datas.get(position));
        }

        // 获取数据的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }

        // 自定义的ViewHolder，持有每个Item的的所有界面元素
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mTextView = (TextView) view.findViewById(R.id.text1);
            }
        }
    }
}
