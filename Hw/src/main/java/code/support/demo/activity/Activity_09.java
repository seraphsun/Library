package code.support.demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import code.support.demo.R;
import code.support.demo.util.UtilDensity;
import code.support.demo.widget.refresh.refresh_09.RefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_09 extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.refresh_09);

        RefreshLayout chromeLikeSwipeLayout = (RefreshLayout) findViewById(R.id.chrome_like_swipe_layout);
        RefreshLayout.makeConfig()
                .addIcon(R.drawable.icon_refresh_9)
                .addIcon(R.drawable.icon_refresh_9_add)
                .addIcon(R.drawable.icon_refresh_9_close)
//                .radius(UtilDensity.dp2px(this, 35))
//                .gap(UtilDensity.dp2px(this, 5))
                .circleColor(0xFF11CCFF)
                .listenItemSelected(new RefreshLayout.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        Toast.makeText(Activity_09.this, "onItemSelected:" + index, Toast.LENGTH_SHORT).show();
                    }
                })
                .setTo(chromeLikeSwipeLayout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends RecyclerView.Adapter<VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View v = layoutInflater.inflate(R.layout.refresh_item, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return 40;
        }
    }

    private class VH extends RecyclerView.ViewHolder {
        public VH(View itemView) {
            super(itemView);
        }

        public void bind(int position) {
            ((TextView) itemView).setText("item:" + position);
        }
    }
}
