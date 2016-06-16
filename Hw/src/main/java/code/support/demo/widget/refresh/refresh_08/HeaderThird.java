package code.support.demo.widget.refresh.refresh_08;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

import code.support.demo.R;

public class HeaderThird extends View {

    private Bitmap endBitmap;

    public HeaderThird(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HeaderThird(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderThird(Context context) {
        super(context);
        init();
    }

    private void init() {
        endBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img_refresh_end_05));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureWidth(widthMeasureSpec) * endBitmap.getHeight() / endBitmap.getWidth());
    }

    private int measureWidth(int widthMeasureSpec) {
        int result;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = endBitmap.getWidth();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }
}
