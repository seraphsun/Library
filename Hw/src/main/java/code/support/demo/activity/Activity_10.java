package code.support.demo.activity;

import android.os.Bundle;
import android.os.Handler;
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
import code.support.demo.widget.refresh.refresh_10.RefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_10 extends AppCompatActivity {

    private RefreshLayout mRecyclerView;
    private MyAdapter mAdapter;
    private ArrayList<String> listData;
    private int refreshTime = 0;
    private int times = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_10);

        mRecyclerView = (RefreshLayout) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRefreshProgressStyle(RefreshLayout.ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(RefreshLayout.ProgressStyle.Pacman);
        mRecyclerView.setArrowImageView(R.mipmap.ic_arrow_down);

        listData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            listData.add("item" + i);
        }
        mAdapter = new MyAdapter(listData);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLoadingListener(new RefreshLayout.LoadingListener() {

            @Override
            public void onRefresh() {
                refreshTime++;
                times = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        listData.clear();
                        for (int i = 0; i < 30; i++) {
                            listData.add("item" + i + "after " + refreshTime + " times of refresh");
                        }
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    }

                }, 1000);
            }

            @Override
            public void onLoadMore() {
                if (times < 3) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for (int i = 0; i < 15; i++) {
                                listData.add("item" + (i + listData.size()));
                            }

                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.loadMoreComplete();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.loadMoreComplete();
                        }
                    }, 1000);
                }
                times++;
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
