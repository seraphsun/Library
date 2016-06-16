package code.support.demo.widget.refresh.refresh_03;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Design on 2016/5/12.
 */
public class HeaderView extends FrameLayout {

    Path path;
    Paint paint;
    private int minimumHeight;
    private int currHeight;

    public HeaderView(Context context) {
        this(context, null, 0);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        if (isInEditMode()) {
            return;
        }
        path = new Path();
        paint = new Paint();
        paint.setColor(getContext().getResources().getColor(android.R.color.holo_blue_bright));
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = getMeasuredWidth();
        path.reset();
        path.lineTo(0, minimumHeight);
        path.quadTo(measuredWidth / 2, minimumHeight + currHeight, measuredWidth, minimumHeight);
        path.lineTo(measuredWidth, 0);
        canvas.drawPath(path, paint);
    }

    @Override
    public int getMinimumHeight() {
        return minimumHeight;
    }

    @Override
    public void setMinimumHeight(int minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    public int getCurrHeight() {
        return currHeight;
    }

    public void setCurrHeight(int ribbonHeight) {
        this.currHeight = ribbonHeight;
    }

    public void setColor(int jellyColor) {
        paint.setColor(jellyColor);
    }
}
