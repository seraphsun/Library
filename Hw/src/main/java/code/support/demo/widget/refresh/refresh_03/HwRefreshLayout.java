package code.support.demo.widget.refresh.refresh_03;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import code.support.demo.R;

/**
 * Created by Design on 2016/5/13.
 */
public class HwRefreshLayout extends RefreshLayout {

    private String mLoadingText = "Loading...";
    private int mLoadingTextColor;
    private int mJellyColor;

    HwRefreshListener mHwRefreshListener;

    public HwRefreshLayout(Context context) {
        super(context);
        setupHeader();
    }

    public HwRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        setupHeader();
    }

    public HwRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(attrs);
        setupHeader();
    }

    public HwRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setAttributes(attrs);
        setupHeader();
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.refresh_03);
        try {
            Resources resources = getResources();
            mLoadingText = a.getString(R.styleable.refresh_03_android_text);
            mLoadingTextColor = a.getColor(R.styleable.refresh_03_android_textColor, resources.getColor(android.R.color.white));
            mJellyColor = a.getColor(R.styleable.refresh_03_jellyColor, resources.getColor(android.R.color.holo_blue_bright));
        } finally {
            a.recycle();
        }
    }

    private void setupHeader() {
        if (isInEditMode()) {
            return;
        }

        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.refresh_03_header, null);
        final HeaderView headerView = (HeaderView) view.findViewById(R.id.headerView);
        final TextView textLoading = (TextView) view.findViewById(R.id.text_loading);
        headerView.setColor(mJellyColor);
        textLoading.setText(mLoadingText);
        textLoading.setTextColor(mLoadingTextColor);

        final float headerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        final float pullHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
        setHeaderHeight(headerHeight);
        setPullHeight(pullHeight);
        setHeaderView(view);

        setRefreshListener(new RefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (mHwRefreshListener != null) {
                    mHwRefreshListener.onRefresh(HwRefreshLayout.this);
                }
                headerView.setMinimumHeight((int) (headerHeight));
                ValueAnimator animator = ValueAnimator.ofInt(headerView.getCurrHeight(), 0);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        headerView.setCurrHeight((int) animation.getAnimatedValue());
                        headerView.invalidate();
                    }
                });
                animator.setInterpolator(new OvershootInterpolator(3));
                animator.setDuration(200);
                animator.start();
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textLoading.setAlpha(0.01f);
                        textLoading.setVisibility(View.VISIBLE);
                        textLoading.animate().alpha(1f).setInterpolator(new AccelerateInterpolator()).setDuration(200);
                    }
                }, 120);
            }
        });

        setRefreshPullingListener(new RefreshPullingListener() {
            @Override
            public void onPulling(RefreshLayout refreshLayout, float fraction) {
                textLoading.setVisibility(View.GONE);
                headerView.setMinimumHeight((int) (headerHeight * constrains(0, 1, fraction)));
                headerView.setCurrHeight((int) (pullHeight * Math.max(0, fraction - 1)));
                headerView.invalidate();
            }

            @Override
            public void onReleasing(RefreshLayout refreshLayout, float fraction) {
                if (!refreshLayout.isRefreshing()) {
                    textLoading.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setRefreshListener(HwRefreshListener hwRefreshListener) {
        this.mHwRefreshListener = hwRefreshListener;
    }

    public interface HwRefreshListener {
        void onRefresh(HwRefreshLayout jellyRefreshLayout);
    }
}
