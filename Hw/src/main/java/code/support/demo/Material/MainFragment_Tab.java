package code.support.demo.Material;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import code.support.demo.R;


/**
 * Created by Design on 2016/3/29.
 */
public class MainFragment_Tab extends Fragment {

    private AppCompatActivity mActivity;

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;

    private ArrayList<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.m_main_fragment_tab, null);

        initView(contentView);

        return contentView;
    }

    private void initView(View contentView) {
        list = new ArrayList<>();
        String[] args = {"000000000000", "111111111", "222222222222", "3333333333333", "444444444444444444", "555555555555555555", "6666666666666666", "77777777777777777"};
        Collections.addAll(list, args);

        mSwipeRefresh = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new MyAdapter(list));
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
