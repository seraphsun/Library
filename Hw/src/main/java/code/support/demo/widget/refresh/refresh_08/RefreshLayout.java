package code.support.demo.widget.refresh.refresh_08;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import code.support.demo.R;

public class RefreshLayout extends ListView implements AbsListView.OnScrollListener {

    private static final int DONE = 0;
    private static final int PULL_TO_REFRESH = 1;
    private static final int RELEASE_TO_REFRESH = 2;
    private static final int REFRESHING = 3;
    private static final int RATIO = 3;

    private LinearLayout headerView;
    private HeaderFirst mFirstView;
    private HeaderSecond mSecondView;
    private HeaderThird mThirdView;

    private TextView mTVrefresh;
    private AnimationDrawable secondAnim;
    private AnimationDrawable thirdAnim;

    private int headerViewHeight;
    private float startY;
    private float offsetY;

    private int state;
    private boolean isEnd;
    private boolean isRefreable;
    private boolean isRecord;
    private int mFirstVisibleItem;

    private OnRefreshListener mOnRefreshListener;

    public RefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setOnScrollListener(this);

        headerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_08_header, null, false);
        mTVrefresh = (TextView) headerView.findViewById(R.id.TVrefresh);

        mFirstView = (HeaderFirst) headerView.findViewById(R.id.first_view);

        mSecondView = (HeaderSecond) headerView.findViewById(R.id.second_view);
        mSecondView.setBackgroundResource(R.drawable.anim_list_second);
        secondAnim = (AnimationDrawable) mSecondView.getBackground();

        mThirdView = (HeaderThird) headerView.findViewById(R.id.third_view);
        mThirdView.setBackgroundResource(R.drawable.anim_list_third);
        thirdAnim = (AnimationDrawable) mThirdView.getBackground();

        measureView(headerView);
        addHeaderView(headerView);
        headerViewHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerViewHeight, 0, 0);

        state = DONE;
        isEnd = true;
        isRefreable = false;
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 如果现在时结束的状态，即刷新完毕了，可以再次刷新了，在onRefreshComplete中设置
        if (isEnd) {
            // 如果现在是可刷新状态   在setOnMeiTuanListener中设置为true
            if (isRefreable) {
                switch (ev.getAction()) {
                    // 用户按下
                    case MotionEvent.ACTION_DOWN:
                        // 如果当前是在listview顶部并且没有记录y坐标
                        if (mFirstVisibleItem == 0 && !isRecord) {
                            // 将isRecord置为true，说明现在已记录y坐标
                            isRecord = true;
                            // 将当前y坐标赋值给startY起始y坐标
                            startY = ev.getY();
                        }
                        break;
                    // 用户滑动
                    case MotionEvent.ACTION_MOVE:
                        // 再次得到y坐标，用来和startY相减来计算offsetY位移值
                        float tempY = ev.getY();
                        // 再起判断一下是否为listview顶部并且没有记录y坐标
                        if (mFirstVisibleItem == 0 && !isRecord) {
                            isRecord = true;
                            startY = tempY;
                        }
                        // 如果当前状态不是正在刷新的状态，并且已经记录了y坐标
                        if (state != REFRESHING && isRecord) {
                            // 计算y的偏移量
                            offsetY = tempY - startY;
                            // 计算当前滑动的高度
                            float currentHeight = (-headerViewHeight + offsetY / 3);
                            // 用当前滑动的高度和头部headerView的总高度进行比 计算出当前滑动的百分比 0到1
                            float currentProgress = 1 + currentHeight / headerViewHeight;
                            // 如果当前百分比大于1了，将其设置为1，目的是让第一个状态的椭圆不再继续变大
                            if (currentProgress >= 1) {
                                currentProgress = 1;
                            }
                            // 如果当前的状态是放开刷新，并且已经记录y坐标
                            if (state == RELEASE_TO_REFRESH && isRecord) {
                                setSelection(0);
                                // 如果当前滑动的距离小于headerView的总高度
                                if (-headerViewHeight + offsetY / RATIO < 0) {
                                    // 将状态置为下拉刷新状态
                                    state = PULL_TO_REFRESH;
                                    // 根据状态改变headerView，主要是更新动画和文字等信息
                                    changeHeaderByState(state);
                                    // 如果当前y的位移值小于0，即为headerView隐藏了
                                } else if (offsetY <= 0) {
                                    // 将状态变为done
                                    state = DONE;
                                    // 根据状态改变headerView，主要是更新动画和文字等信息
                                    changeHeaderByState(state);
                                }
                            }
                            // 如果当前状态为下拉刷新并且已经记录y坐标
                            if (state == PULL_TO_REFRESH && isRecord) {
                                setSelection(0);
                                // 如果下拉距离大于等于headerView的总高度
                                if (-headerViewHeight + offsetY / RATIO >= 0) {
                                    // 将状态变为放开刷新
                                    state = RELEASE_TO_REFRESH;
                                    // 根据状态改变headerView，主要是更新动画和文字等信息
                                    changeHeaderByState(state);
                                    // 如果当前y的位移值小于0，即为headerView隐藏了
                                } else if (offsetY <= 0) {
                                    //将状态变为done
                                    state = DONE;
                                    // 根据状态改变headerView，主要是更新动画和文字等信息
                                    changeHeaderByState(state);
                                }
                            }
                            // 如果当前状态为done并且已经记录y坐标
                            if (state == DONE && isRecord) {
                                // 如果位移值大于0
                                if (offsetY >= 0) {
                                    // 将状态改为下拉刷新状态
                                    state = PULL_TO_REFRESH;
                                }
                            }
                            // 如果为下拉刷新状态
                            if (state == PULL_TO_REFRESH) {
                                // 则改变headerView的padding来实现下拉的效果
                                headerView.setPadding(0, (int) (-headerViewHeight + offsetY / RATIO), 0, 0);
                                // 给第一个状态的View设置当前进度值
                                mFirstView.setCurrentProgress(currentProgress);
                                // 重画
                                mFirstView.postInvalidate();
                            }
                            // 如果为放开刷新状态
                            if (state == RELEASE_TO_REFRESH) {
                                // 改变headerView的padding值
                                headerView.setPadding(0, (int) (-headerViewHeight + offsetY / RATIO), 0, 0);
                                // 给第一个状态的View设置当前进度值
                                mFirstView.setCurrentProgress(currentProgress);
                                // 重画
                                mFirstView.postInvalidate();
                            }
                        }
                        break;
                    // 当用户手指抬起时
                    case MotionEvent.ACTION_UP:
                        // 如果当前状态为下拉刷新状态
                        if (state == PULL_TO_REFRESH) {
                            //平滑的隐藏headerView
                            this.smoothScrollBy((int) (-headerViewHeight + offsetY / RATIO) + headerViewHeight, 500);
                            // 根据状态改变headerView
                            changeHeaderByState(state);
                        }
                        // 如果当前状态为放开刷新
                        if (state == RELEASE_TO_REFRESH) {
                            // 平滑的滑到正好显示headerView
                            this.smoothScrollBy((int) (-headerViewHeight + offsetY / RATIO), 500);
                            // 将当前状态设置为正在刷新
                            state = REFRESHING;
                            // 回调接口的onRefresh方法
                            mOnRefreshListener.onRefresh();
                            // 根据状态改变headerView
                            changeHeaderByState(state);
                        }
                        // 这一套手势执行完，一定别忘了将记录y坐标的isRecord改为false，以便于下一次手势的执行
                        isRecord = false;
                        break;
                }
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据状态改变headerView的动画和文字显示
     */
    private void changeHeaderByState(int state) {
        switch (state) {
            // 如果的隐藏的状态
            case DONE:
                // 设置headerView的padding为隐藏
                headerView.setPadding(0, -headerViewHeight, 0, 0);
                // 第一状态的view显示出来
                mFirstView.setVisibility(View.VISIBLE);
                // 第二状态的view隐藏起来
                mSecondView.setVisibility(View.GONE);
                // 停止第二状态的动画
                secondAnim.stop();
                // 第三状态的view隐藏起来
                mThirdView.setVisibility(View.GONE);
                // 停止第三状态的动画
                thirdAnim.stop();
                break;
            // 当前状态为放开刷新
            case RELEASE_TO_REFRESH:
                // 文字显示为放开刷新
                mTVrefresh.setText("放开刷新");
                // 第一状态view隐藏起来
                mFirstView.setVisibility(View.GONE);
                // 第二状态view显示出来
                mSecondView.setVisibility(View.VISIBLE);
                // 播放第二状态的动画
                secondAnim.start();
                // 第三状态view隐藏起来
                mThirdView.setVisibility(View.GONE);
                // 停止第三状态的动画
                thirdAnim.stop();
                break;
            // 当前状态为下拉刷新
            case PULL_TO_REFRESH:
                // 设置文字为下拉刷新
                mTVrefresh.setText("下拉刷新");
                // 第一状态view显示出来
                mFirstView.setVisibility(View.VISIBLE);
                // 第二状态view隐藏起来
                mSecondView.setVisibility(View.GONE);
                // 第二状态动画停止
                secondAnim.stop();
                // 第三状态view隐藏起来
                mThirdView.setVisibility(View.GONE);
                // 第三状态动画停止
                thirdAnim.stop();
                break;
            // 当前状态为正在刷新
            case REFRESHING:
                // 文字设置为正在刷新
                mTVrefresh.setText("正在刷新");
                // 第一状态view隐藏起来
                mFirstView.setVisibility(View.GONE);
                // 第三状态view显示出来
                mThirdView.setVisibility(View.VISIBLE);
                // 第二状态view隐藏起来
                mSecondView.setVisibility(View.GONE);
                // 停止第二状态动画
                secondAnim.stop();
                // 启动第三状态view
                thirdAnim.start();
                break;
            default:
                break;
        }
    }

    /**
     * 刷新完毕，从主线程发送过来，并且改变headerView的状态和文字动画信息
     */
    public void setOnRefreshComplete() {
        // 一定要将isEnd设置为true，以便于下次的下拉刷新
        isEnd = true;
        state = DONE;

        changeHeaderByState(state);
    }

    /**
     * 回调接口，想实现下拉刷新的listview实现此接口
     */
    public void setOnMeiTuanRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
        isRefreable = true;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
