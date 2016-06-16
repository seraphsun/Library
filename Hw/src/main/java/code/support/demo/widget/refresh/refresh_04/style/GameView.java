package code.support.demo.widget.refresh.refresh_04.style;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import code.support.demo.R;

/**
 * Created by zpj on 2016/6/14.
 */
public abstract class GameView extends View {

    public static final int STATUS_GAME_PREPAR = 0;
    public static final int STATUS_GAME_PLAY = 1;
    public static final int STATUS_GAME_FINISHED = 2;
    public static final int STATUS_GAME_OVER = 3;

    public static final String TEXT_GAME_OVER = "Game Over";
    public static final String TEXT_LOADING = "Loading...";
    public static final String TEXT_LOADING_FINISHED = "Loading Finished";

    /**
     * 分割线默认宽度大小
     */
    public static final float DIVIDING_LINE_SIZE = 1.f;

    /**
     * 控件高度占屏幕高度比率
     */
    static final float VIEW_HEIGHT_RATIO = .161f;

    protected Paint mPaint;
    protected TextPaint textPaint;

    protected float controllerPosition;
    protected int controllerSize;
    protected int screenWidth, screenHeight;
    protected int status = STATUS_GAME_PREPAR;
    protected int lModelColor, rModelColor, mModelColor;

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FunGame);
        lModelColor = typedArray.getColor(R.styleable.FunGame_left_model_color, Color.rgb(0, 0, 0));
        mModelColor = typedArray.getColor(R.styleable.FunGame_middle_model_color, Color.BLACK);
        rModelColor = typedArray.getColor(R.styleable.FunGame_right_model_color, Color.parseColor("#A5A5A5"));
        typedArray.recycle();

        initBaseTools();
        initBaseConfigParams(context);
        initConcreteView();
    }

    protected void initBaseTools() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#C1C2C2"));

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(1.f);
    }

    protected void initBaseConfigParams(Context context) {
        controllerPosition = DIVIDING_LINE_SIZE;

        screenWidth = getScreenMetrics(context).widthPixels;
        screenHeight = getScreenMetrics(context).heightPixels;
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context context
     * @return 手机屏幕尺寸
     */
    private DisplayMetrics getScreenMetrics(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(screenWidth, (int) (screenHeight * VIEW_HEIGHT_RATIO));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBoundary(canvas);
        drawText(canvas);
        drawGame(canvas);
    }

    /**
     * 绘制分割线
     *
     * @param canvas 默认画布
     */
    private void drawBoundary(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#606060"));
        canvas.drawLine(0, 0, screenWidth, 0, mPaint);
        canvas.drawLine(0, getMeasuredHeight(), screenWidth, getMeasuredHeight(), mPaint);
    }

    /**
     * 绘制文字内容
     *
     * @param canvas 默认画布
     */
    private void drawText(Canvas canvas) {
        switch (status) {
            case STATUS_GAME_PREPAR:
            case STATUS_GAME_PLAY:
                textPaint.setTextSize(60);
                promptText(canvas, TEXT_LOADING);
                break;
            case STATUS_GAME_FINISHED:
                textPaint.setTextSize(50);
                promptText(canvas, TEXT_LOADING_FINISHED);
                break;
            case STATUS_GAME_OVER:
                textPaint.setTextSize(60);
                promptText(canvas, TEXT_GAME_OVER);
                break;
        }
    }

    /**
     * 提示文字信息
     *
     * @param canvas 默认画布
     * @param text   相关文字字符串
     */
    private void promptText(Canvas canvas, String text) {
        float textX = (canvas.getWidth() - textPaint.measureText(text)) * .5f;
        float textY = canvas.getHeight() * .5f - (textPaint.ascent() + textPaint.descent()) * .5f;
        canvas.drawText(text, textX, textY, textPaint);
    }

    /**
     * 移动控制器（控制器对象为具体控件中的右边图像模型）
     *
     * @param distance 移动的距离
     */
    public void moveController(float distance) {
        float maxDistance = (getMeasuredHeight() - 2 * DIVIDING_LINE_SIZE - controllerSize);

        if (distance > maxDistance) {
            distance = maxDistance;
        }

        controllerPosition = distance;
        postInvalidate();
    }

    /**
     * 更新当前控件状态
     *
     * @param status 状态码
     */
    public void postStatus(int status) {
        this.status = status;

        if (status == STATUS_GAME_PREPAR) {
            resetConfigParams();
        }

        postInvalidate();
    }

    protected abstract void initConcreteView();

    protected abstract void drawGame(Canvas canvas);

    protected abstract void resetConfigParams();
}
