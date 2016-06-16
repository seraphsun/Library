package code.support.demo.widget.refresh.refresh_10.util;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import code.support.demo.R;
import code.support.demo.util.UtilDensity;
import code.support.demo.widget.refresh.refresh_10.style.BallBeat;
import code.support.demo.widget.refresh.refresh_10.style.BallClipRotate;
import code.support.demo.widget.refresh.refresh_10.style.BallClipRotateMultiple;
import code.support.demo.widget.refresh.refresh_10.style.BallClipRotatePulse;
import code.support.demo.widget.refresh.refresh_10.style.BallGridBeat;
import code.support.demo.widget.refresh.refresh_10.style.BallGridPulse;
import code.support.demo.widget.refresh.refresh_10.style.BallPulse;
import code.support.demo.widget.refresh.refresh_10.style.BallPulseRise;
import code.support.demo.widget.refresh.refresh_10.style.BallPulseSync;
import code.support.demo.widget.refresh.refresh_10.style.BallRotate;
import code.support.demo.widget.refresh.refresh_10.style.BallScale;
import code.support.demo.widget.refresh.refresh_10.style.BallScaleMultiple;
import code.support.demo.widget.refresh.refresh_10.style.BallScaleRipple;
import code.support.demo.widget.refresh.refresh_10.style.BallScaleRippleMultiple;
import code.support.demo.widget.refresh.refresh_10.style.BallSpinFadeLoader;
import code.support.demo.widget.refresh.refresh_10.style.BallTrianglePath;
import code.support.demo.widget.refresh.refresh_10.style.BallZigZagDeflect;
import code.support.demo.widget.refresh.refresh_10.style.BallZigZag;
import code.support.demo.widget.refresh.refresh_10.style.CubeTransition;
import code.support.demo.widget.refresh.refresh_10.style.LineScale;
import code.support.demo.widget.refresh.refresh_10.style.LineScaleParty;
import code.support.demo.widget.refresh.refresh_10.style.LineScalePulseOut;
import code.support.demo.widget.refresh.refresh_10.style.LineScalePulseOutRapid;
import code.support.demo.widget.refresh.refresh_10.style.LineSpinFadeLoader;
import code.support.demo.widget.refresh.refresh_10.style.Pacman;
import code.support.demo.widget.refresh.refresh_10.style.SemiCircleSpin;
import code.support.demo.widget.refresh.refresh_10.style.SquareSpin;
import code.support.demo.widget.refresh.refresh_10.style.TriangleSkewSpin;

public class ViewLoading extends View {

    public static final int BallPulse = 0;
    public static final int BallGridPulse = 1;
    public static final int BallClipRotate = 2;
    public static final int BallClipRotatePulse = 3;
    public static final int SquareSpin = 4;
    public static final int BallClipRotateMultiple = 5;
    public static final int BallPulseRise = 6;
    public static final int BallRotate = 7;
    public static final int CubeTransition = 8;
    public static final int BallZigZag = 9;
    public static final int BallZigZagDeflect = 10;
    public static final int BallTrianglePath = 11;
    public static final int BallScale = 12;
    public static final int LineScale = 13;
    public static final int LineScaleParty = 14;
    public static final int BallScaleMultiple = 15;
    public static final int BallPulseSync = 16;
    public static final int BallBeat = 17;
    public static final int LineScalePulseOut = 18;
    public static final int LineScalePulseOutRapid = 19;
    public static final int BallScaleRipple = 20;
    public static final int BallScaleRippleMultiple = 21;
    public static final int BallSpinFadeLoader = 22;
    public static final int LineSpinFadeLoader = 23;
    public static final int TriangleSkewSpin = 24;
    public static final int Pacman = 25;
    public static final int BallGridBeat = 26;
    public static final int SemiCircleSpin = 27;

    @IntDef(flag = true, value = {
            BallPulse,
            BallGridPulse,
            BallClipRotate,
            BallClipRotatePulse,
            SquareSpin,
            BallClipRotateMultiple,
            BallPulseRise,
            BallRotate,
            CubeTransition,
            BallZigZag,
            BallZigZagDeflect,
            BallTrianglePath,
            BallScale,
            LineScale,
            LineScaleParty,
            BallScaleMultiple,
            BallPulseSync,
            BallBeat,
            LineScalePulseOut,
            LineScalePulseOutRapid,
            BallScaleRipple,
            BallScaleRippleMultiple,
            BallSpinFadeLoader,
            LineSpinFadeLoader,
            TriangleSkewSpin,
            Pacman,
            BallGridBeat,
            SemiCircleSpin
    })
    public @interface Indicator {
    }

    // Sizes (with defaults in DP)
    public static final int DEFAULT_SIZE = 30;

    // attrs
    int mIndicatorId;
    int mIndicatorColor;

    Paint mPaint;

    ViewController mIndicatorController;

    private boolean mHasAnimation;

    public ViewLoading(Context context) {
        super(context);
        init(null, 0);
    }

    public ViewLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ViewLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewLoading(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.refresh_10);
        mIndicatorId = a.getInt(R.styleable.refresh_10_indicator, BallPulse);
        mIndicatorColor = a.getColor(R.styleable.refresh_10_indicator_color, Color.WHITE);
        a.recycle();
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        applyIndicator();
    }

    private void applyIndicator() {
        switch (mIndicatorId) {
            case BallPulse:
                mIndicatorController = new BallPulse();
                break;
            case BallGridPulse:
                mIndicatorController = new BallGridPulse();
                break;
            case BallClipRotate:
                mIndicatorController = new BallClipRotate();
                break;
            case BallClipRotatePulse:
                mIndicatorController = new BallClipRotatePulse();
                break;
            case SquareSpin:
                mIndicatorController = new SquareSpin();
                break;
            case BallClipRotateMultiple:
                mIndicatorController = new BallClipRotateMultiple();
                break;
            case BallPulseRise:
                mIndicatorController = new BallPulseRise();
                break;
            case BallRotate:
                mIndicatorController = new BallRotate();
                break;
            case CubeTransition:
                mIndicatorController = new CubeTransition();
                break;
            case BallZigZag:
                mIndicatorController = new BallZigZag();
                break;
            case BallZigZagDeflect:
                mIndicatorController = new BallZigZagDeflect();
                break;
            case BallTrianglePath:
                mIndicatorController = new BallTrianglePath();
                break;
            case BallScale:
                mIndicatorController = new BallScale();
                break;
            case LineScale:
                mIndicatorController = new LineScale();
                break;
            case LineScaleParty:
                mIndicatorController = new LineScaleParty();
                break;
            case BallScaleMultiple:
                mIndicatorController = new BallScaleMultiple();
                break;
            case BallPulseSync:
                mIndicatorController = new BallPulseSync();
                break;
            case BallBeat:
                mIndicatorController = new BallBeat();
                break;
            case LineScalePulseOut:
                mIndicatorController = new LineScalePulseOut();
                break;
            case LineScalePulseOutRapid:
                mIndicatorController = new LineScalePulseOutRapid();
                break;
            case BallScaleRipple:
                mIndicatorController = new BallScaleRipple();
                break;
            case BallScaleRippleMultiple:
                mIndicatorController = new BallScaleRippleMultiple();
                break;
            case BallSpinFadeLoader:
                mIndicatorController = new BallSpinFadeLoader();
                break;
            case LineSpinFadeLoader:
                mIndicatorController = new LineSpinFadeLoader();
                break;
            case TriangleSkewSpin:
                mIndicatorController = new TriangleSkewSpin();
                break;
            case Pacman:
                mIndicatorController = new Pacman();
                break;
            case BallGridBeat:
                mIndicatorController = new BallGridBeat();
                break;
            case SemiCircleSpin:
                mIndicatorController = new SemiCircleSpin();
                break;
        }
        mIndicatorController.setTarget(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(UtilDensity.dp2px(getContext(), DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(UtilDensity.dp2px(getContext(), DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation) {
            mHasAnimation = true;
            applyAnimation();
        }
    }

    void applyAnimation() {
        mIndicatorController.initAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas);
    }

    void drawIndicator(Canvas canvas) {
        mIndicatorController.draw(canvas, mPaint);
    }

    public void setIndicatorId(int indicatorId) {
        mIndicatorId = indicatorId;
        applyIndicator();
    }

    public void setIndicatorColor(int color) {
        mIndicatorColor = color;
        mPaint.setColor(mIndicatorColor);
        this.invalidate();
    }

    @Override
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (v == GONE || v == INVISIBLE) {
                mIndicatorController.setAnimationStatus(ViewController.AnimStatus.END);
            } else {
                mIndicatorController.setAnimationStatus(ViewController.AnimStatus.START);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIndicatorController.setAnimationStatus(ViewController.AnimStatus.START);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIndicatorController.setAnimationStatus(ViewController.AnimStatus.CANCEL);
    }
}
