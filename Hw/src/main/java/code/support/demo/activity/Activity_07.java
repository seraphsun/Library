package code.support.demo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Random;

import code.support.demo.R;
import code.support.demo.widget.refresh.refresh_07.RefreshLayout;

/**
 * Created by Design on 2016/5/11.
 */
public class Activity_07 extends AppCompatActivity implements RefreshLayout.OnRefreshListener {

    private ListView mListview;
    private RefreshLayout mWaveSwipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.refresh_07);
        initView();
        setSampleData();
    }

    private void initView() {
        mWaveSwipeRefreshLayout = (RefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        mWaveSwipeRefreshLayout.setWaveColor(Color.argb(100, 255, 0, 0));

        mListview = (ListView) findViewById(R.id.main_list);

        findViewById(R.id.button_of_wave_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWaveSwipeRefreshLayout.setWaveColor(0xFF000000 + new Random().nextInt(0xFFFFFF)); // Random assign
            }
        });

        ((SeekBar) findViewById(R.id.seekbar_of_drop_height)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float scale = (seekBar.getProgress() / 100f);
                mWaveSwipeRefreshLayout.setMaxDropHeight((int) (mWaveSwipeRefreshLayout.getHeight() * scale));
            }
        });
    }

    private void setSampleData() {
        ArrayList<String> sampleArrayStr = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            sampleArrayStr.add("");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, sampleArrayStr);
        mListview.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWaveSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }
}
