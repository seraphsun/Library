package code.support.demo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import code.support.demo.R;
import code.support.demo.widget.refresh.refresh_12.RefreshFooterListener;
import code.support.demo.widget.refresh.refresh_12.RefreshHeaderAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_12 extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<String> mList;

    private RefreshHeaderAdapter customAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_12);

        setData();
        initRefreshLayout();
    }

    private void setData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mList.add("第" + i + "个");
        }
    }

    private void initRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        RefreshAdapter refreshAdapter = new RefreshAdapter(mList, this);
        customAdapter = new RefreshHeaderAdapter(refreshAdapter);
        recyclerView.setAdapter(customAdapter);

        createLoadMoreView();

        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.addOnScrollListener(new RefreshFooterListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                simulateLoadMoreData();
            }
        });
    }

    private void createLoadMoreView() {
        View loadMoreView = LayoutInflater.from(Activity_12.this).inflate(R.layout.refresh_12_loadmore, recyclerView, false);
        customAdapter.addFooterView(loadMoreView);
    }

    private void simulateLoadMoreData() {
        Observable
                .timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        loadMoreData();
                        customAdapter.notifyDataSetChanged();
                        return null;
                    }
                }).subscribe();
    }

    private void loadMoreData() {
        List<String> moreList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            moreList.add("加载更多的数据");
        }
        mList.addAll(moreList);
    }

    @Override
    public void onRefresh() {
        Observable
                .timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        fetchingNewData();
                        swipeRefreshLayout.setRefreshing(false);
                        customAdapter.notifyDataSetChanged();
                        return null;
                    }
                }).subscribe();
    }

    private void fetchingNewData() {
        mList.add(0, "下拉刷新出来的数据");
    }

    static class RefreshAdapter extends RecyclerView.Adapter<RefreshAdapter.ViewHolder> {

        List<String> mList;
        Context context;

        public RefreshAdapter(List<String> mList, Context context) {
            this.mList = mList;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
