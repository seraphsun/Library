package code.support.demo.widget.refresh.refresh_10;


import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import code.support.demo.R;
import code.support.demo.widget.refresh.refresh_10.util.ViewLoading;
import code.support.demo.widget.refresh.refresh_10.util.ViewSwitch;

public class HeaderView extends LinearLayout implements RefreshLayout.RefreshHeader {

    private final int ROTATE_ANIM_DURATION = 180;

    private Context mContext;
    private LinearLayout mContainer;

    private ImageView mArrowImageView;
    private TextView mStatusTextView;
    private TextView mHeaderTimeView;
    private ViewSwitch mProgressBar;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    public int mMeasuredHeight;
    private int mState = STATE_NORMAL;

    public HeaderView(Context context) {
        super(context);
        initView(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        // 初始情况，设置下拉刷新view高度为0
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_10_header, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.listview_header_arrow);
        mStatusTextView = (TextView) findViewById(R.id.refresh_status_textview);

        // init the progress view
        mProgressBar = (ViewSwitch) findViewById(R.id.listview_header_progressbar);
        ViewLoading progressView = new ViewLoading(context);
        progressView.setIndicatorColor(0xffB5B5B5);
        progressView.setIndicatorId(RefreshLayout.ProgressStyle.BallSpinFadeLoader);
        mProgressBar.setView(progressView);


        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

        mHeaderTimeView = (TextView) findViewById(R.id.last_refresh_time);
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }

    @Override
    public void onMove(float delta) {
        if (getVisiableHeight() > 0 || delta > 0) {
            setVisiableHeight((int) delta + getVisiableHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) {
                // 未处于刷新状态，更新箭头
                if (getVisiableHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    public int getVisiableHeight() {
        int height;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        height = lp.height;
        return height;
    }

    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisiableHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if (getVisiableHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight) {
            // return;
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisiableHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisiableHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    @Override
    public void refreshComplate() {
        mHeaderTimeView.setText(friendlyTime(new Date()));
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
            }
        }, 500);
    }

    public static String friendlyTime(Date time) {
        // 获取time距离当前的秒数
        int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);

        if (ct == 0) {
            return "刚刚";
        }

        if (ct > 0 && ct < 60) {
            return ct + "秒前";
        }

        if (ct >= 60 && ct < 3600) {
            return Math.max(ct / 60, 1) + "分钟前";
        }
        if (ct >= 3600 && ct < 86400)
            return ct / 3600 + "小时前";
        if (ct >= 86400 && ct < 2592000) { //86400 * 30
            int day = ct / 86400;
            return day + "天前";
        }
        if (ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + "月前";
        }
        return ct / 31104000 + "年前";
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        if (state == mState) return;

        if (state == STATE_REFRESHING) {
            // 显示进度
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else if (state == STATE_DONE) {
            mArrowImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            // 显示箭头图片
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_RELEASE_TO_REFRESH) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    mArrowImageView.clearAnimation();
                }
                mStatusTextView.setText("pull to refresh");
                break;
            case STATE_RELEASE_TO_REFRESH:
                if (mState != STATE_RELEASE_TO_REFRESH) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mStatusTextView.setText("release to start refresh");
                }
                break;
            case STATE_REFRESHING:
                mStatusTextView.setText("refreshing...");
                break;
            case STATE_DONE:
                mStatusTextView.setText("refresh done");
                break;
            default:
        }

        mState = state;
    }

    public void reset() {
        smoothScrollTo(0);
        setState(STATE_NORMAL);
    }

    public void setArrowImageView(int resid) {
        mArrowImageView.setImageResource(resid);
    }

    public void setProgressStyle(int style) {
        if (style == RefreshLayout.ProgressStyle.SysProgress) {
            mProgressBar.setView(new ProgressBar(mContext, null, android.R.attr.progressBarStyle));
        } else {
            ViewLoading progressView = new ViewLoading(this.getContext());
            progressView.setIndicatorColor(0xffB5B5B5);
            progressView.setIndicatorId(style);
            mProgressBar.setView(progressView);
        }
    }

}
