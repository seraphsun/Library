package code.support.demo.widget.refresh.refresh_11;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import code.support.demo.R;

/**
 * Created by Design on 2016/5/12.
 */
public class HeaderView extends FrameLayout {

    private static final int DISTANCE_BETWEEN_STRETCH_READY = 250;

    private LinearLayout mContainer;
    private ProgressBar mProgressBar;
    private DrawWaterDrop mDrawWaterDrop;

    private int stretchHeight;
    private int readyHeight;

    private STATE mState = STATE.normal;

    public enum STATE {
        normal,//正常
        stretch,//准备进行拉伸
        ready,//拉伸到最大位置
        refreshing,//刷新
        end//刷新结束，回滚
    }

    private StateChangedListener mStateChangedListener;

    public HeaderView(Context context) {
        super(context);
        init(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_11_header, null);
        mDrawWaterDrop = (DrawWaterDrop) mContainer.findViewById(R.id.waterDrop);
        mProgressBar = (ProgressBar) mContainer.findViewById(R.id.progressbar);

        // Set View Height 0
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        addView(mContainer, lp);
        initHeight();
    }

    private void initHeight() {
        mContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                stretchHeight = mDrawWaterDrop.getHeight();
                readyHeight = stretchHeight + DISTANCE_BETWEEN_STRETCH_READY;
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 修改状态。注：状态的改变与前一个状态以及下拉头高度有关
     */
    public void updateState(STATE state) {
        if (state == mState) return;
        STATE oldState = mState;
        mState = state;
        if (mStateChangedListener != null) {
            mStateChangedListener.notifyStateChanged(oldState, mState);
        }
        switch (mState) {
            case normal:
                handleStateNormal();
                break;
            case stretch:
                handleStateStretch();
                break;
            case ready:
                handleStateReady();
                break;
            case refreshing:
                handleStateRefreshing();
                break;
            case end:
                handleStateEnd();
                break;
            default:
        }
    }

    /**
     * 处理处于normal状态的值
     */
    private void handleStateNormal() {
        mDrawWaterDrop.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mContainer.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    /**
     * 处理水滴拉伸状态
     */
    private void handleStateStretch() {
        mDrawWaterDrop.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mContainer.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    }

    /**
     * 处理水滴ready状态，回弹效果
     */
    private void handleStateReady() {
        mDrawWaterDrop.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        Animator shrinkAnimator = mDrawWaterDrop.createAnimator();
        shrinkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 回弹结束后即进入refreshing状态
                updateState(STATE.refreshing);
            }
        });
        // 开始回弹
        shrinkAnimator.start();
    }

    /**
     * 处理正在进行刷新状态
     */
    private void handleStateRefreshing() {
        mDrawWaterDrop.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 处理刷新完毕状态
     */
    private void handleStateEnd() {
        mDrawWaterDrop.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    public int getVisibleHeight() {
        return mContainer.getHeight();
    }

    public void setVisibleHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
        // 通知水滴进行更新
        if (mState == STATE.stretch) {
            float pullOffset = (float) mapValueFromRangeToRange(height, stretchHeight, readyHeight, 0, 1);
            if (pullOffset < 0 || pullOffset > 1) {
                throw new IllegalArgumentException("pullOffset should between 0 and 1!" + mState + " " + height);
            }
            mDrawWaterDrop.updateCompleteState(pullOffset);
        }
    }

    private double mapValueFromRangeToRange(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        double fromRangeSize = fromHigh - fromLow;
        double toRangeSize = toHigh - toLow;
        double valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (valueScale * toRangeSize);
    }


    public STATE getCurrentState() {
        return mState;
    }

    public int getStretchHeight() {
        return stretchHeight;
    }

    public int getReadyHeight() {
        return readyHeight;
    }

    public void setStateChangedListener(StateChangedListener l) {
        mStateChangedListener = l;
    }

    public interface StateChangedListener {
        void notifyStateChanged(STATE oldState, STATE newState);
    }
}
