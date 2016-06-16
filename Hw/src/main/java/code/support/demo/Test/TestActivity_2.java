package code.support.demo.Test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import code.support.demo.R;
import code.support.demo.bean.PagerItem;
import code.support.demo.widget.banner.BannerLayout;
import code.support.demo.widget.banner.helper.BannerAdapter;
import code.support.demo.widget.banner.indicator.IndicatorCircle;
import code.support.demo.widget.banner.indicator.IndicatorLine;

/**
 * Created by Design on 2016/5/10.
 */
public class TestActivity_2 extends AppCompatActivity {

    @Bind(R.id.viewpager_1)
    BannerLayout bannerLayout_1;

    @Bind(R.id.indicator_1)
    IndicatorLine indicatorLine;

    @Bind(R.id.viewpager_2)
    BannerLayout bannerLayout_2;

    @Bind(R.id.indicator_2)
    IndicatorCircle indicatorCircle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_02);

        ButterKnife.bind(this);
        init();
    }

    private void init() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this);
        pagerAdapter.setDataList(getDataForNet(0));
        bannerLayout_1.setAdapter(pagerAdapter);
        bannerLayout_1.setAutoScrollTime(2000);
        bannerLayout_1.startAutoScroll();
        indicatorLine.setViewPager(bannerLayout_1);

        MyPagerAdapter pagerAdapter2 = new MyPagerAdapter(this);
        pagerAdapter2.setDataList(getDataForNet(1));
        bannerLayout_2.setAdapter(pagerAdapter2);
        bannerLayout_2.setAutoScrollTime(2000);
        bannerLayout_2.startAutoScroll();
        indicatorCircle.setViewPager(bannerLayout_2);
    }

    private List getDataForNet(int type) {
        List<PagerItem> list = new ArrayList<>();
        PagerItem item;
        if (type == 0) {
            for (int i = 0; i < 3; i++) {
                item = new PagerItem();
                item.setDesc("Description:" + i);
                item.setName("Name:" + i);
                item.setPosition(i);
                int index = i % 3;
                if (index == 1) {
                    item.setImageUrl("http://p3.123.sogoucdn.com/imgu/2015/09/20150924151622_720.jpg");
                } else if (index == 2) {
                    item.setImageUrl("http://p8.123.sogoucdn.com/imgu/2015/09/20150924151358_518.png");
                } else {
                    item.setImageUrl("http://p6.123.sogoucdn.com/imgu/2015/09/20150921184131_208.png");
                }
                list.add(item);
            }
            return list;
        } else {
            for (int i = 0; i < 5; i++) {
                item = new PagerItem();
                item.setDesc("Description:" + i);
                item.setName("Name:" + i);
                item.setPosition(i);
                int index = i % 3;
                if (index == 1) {
                    item.setImageUrl("http://p3.123.sogoucdn.com/imgu/2015/09/20150924151622_720.jpg");
                } else if (index == 2) {
                    item.setImageUrl("http://p8.123.sogoucdn.com/imgu/2015/09/20150924151358_518.png");
                } else {
                    item.setImageUrl("http://p6.123.sogoucdn.com/imgu/2015/09/20150921184131_208.png");
                }
                list.add(item);
            }
            return list;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (bannerLayout_1 != null)
            bannerLayout_1.startAutoScroll();
        if (bannerLayout_2 != null)
            bannerLayout_2.startAutoScroll();
    }

    @Override
    protected void onStop() {
        if (bannerLayout_1 != null)
            bannerLayout_1.stopAutoScroll();
        if (bannerLayout_2 != null)
            bannerLayout_2.stopAutoScroll();
        super.onStop();
    }

    class MyPagerAdapter extends BannerAdapter {

        private final LayoutInflater mInflater;
        private final Context mContext;

        private List<PagerItem> mList;

        public MyPagerAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public void setDataList(List<PagerItem> list) {
            if (list == null || list.size() == 0)
                throw new IllegalArgumentException("list can not be null or has an empty size");
            this.mList = list;
            this.notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public View getView(final int position, View view, ViewGroup container) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.banner_layout, container, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            PagerItem item = mList.get(position);
            holder.position = position;
            holder.name.setText(item.getName());
            holder.description.setText(item.getDesc() + "position:" + position);
            Glide.with(mContext).load(item.getImageUrl()).placeholder(R.mipmap.banner_normal_bg).into(holder.image);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TestActivity_2.this, "position = " + position, Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }

        private class ViewHolder {
            public int position;
            TextView name;
            TextView description;
            ImageView image;
            Button downloadButton;

            public ViewHolder(View view) {
                name = (TextView) view.findViewById(R.id.item_name);
                description = (TextView) view.findViewById(R.id.item_desc);
                image = (ImageView) view.findViewById(R.id.item_image);
                downloadButton = (Button) view.findViewById(R.id.item_button);
            }
        }
    }
}
