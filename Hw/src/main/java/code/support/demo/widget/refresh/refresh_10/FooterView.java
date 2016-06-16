package code.support.demo.widget.refresh.refresh_10;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import code.support.demo.util.UtilDensity;
import code.support.demo.widget.refresh.refresh_10.util.ViewLoading;
import code.support.demo.widget.refresh.refresh_10.util.ViewSwitch;

public class FooterView extends LinearLayout {

    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;

    private Context mContext;
    private ViewSwitch progressCon;
    private TextView mText;

    public FooterView(Context context) {
        super(context);
        initView(context);
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        mContext = context;
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressCon = new ViewSwitch(context);
        progressCon.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ViewLoading progressView = new ViewLoading(this.getContext());
        progressView.setIndicatorColor(0xffB5B5B5);
        progressView.setIndicatorId(RefreshLayout.ProgressStyle.BallSpinFadeLoader);
        progressCon.setView(progressView);

        addView(progressCon);
        mText = new TextView(context);
        mText.setText("正在加载...");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(UtilDensity.dp2px(mContext, 10), 0, 0, 0);

        mText.setLayoutParams(layoutParams);
        addView(mText);
    }

    public void setProgressStyle(int style) {
        if (style == RefreshLayout.ProgressStyle.SysProgress) {
            progressCon.setView(new ProgressBar(mContext, null, android.R.attr.progressBarStyle));
        } else {
            ViewLoading progressView = new ViewLoading(this.getContext());
            progressView.setIndicatorColor(0xffB5B5B5);
            progressView.setIndicatorId(style);
            progressCon.setView(progressView);
        }
    }

    public void setState(int state) {
        switch (state) {
            case STATE_LOADING:
                progressCon.setVisibility(View.VISIBLE);
                mText.setText("loading...");
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                mText.setText("loading...");
                this.setVisibility(View.GONE);
                break;
            case STATE_NOMORE:
                mText.setText("no more to be loaded");
                progressCon.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                break;
        }
    }
}
