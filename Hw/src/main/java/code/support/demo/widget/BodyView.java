package code.support.demo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import code.support.demo.R;

/**
 * Created by Design on 2016/4/20.
 */
public class BodyView extends View {

    private Context mContext;
    private boolean mEnableFlag = true;
    private BodyViewListener mListener;

    private int mManFrontHeight;
    private int mManFrontWidth;
    private float mScaleRatio;

    private int mHeadHeight;
    private int mHeadWidth;
    private float mHeadScaleRatio = 1.0F;

    private Paint mPaint;
    private Crowd mCrowd = Crowd.MAN;
    private InitAreaRunnable mInitAreaRunnable;
    private boolean mIsBack;
    private boolean mIsHead;

    private int mHeight;
    private int mWidth;

    private float mXDown;
    private float mYDown;
    private int mXHeadOffset;
    private int mYHeadOffset;
    private int mXOffset;
    private int mYOffset;

    private List<BodyArea> mManHeadAreaList = new ArrayList();
    private List<BodyArea> mManBackAreaList = new ArrayList();
    private List<BodyArea> mManFrontAreaList = new ArrayList();

    private List<BodyArea> mWomanHeadAreaList = new ArrayList();
    private List<BodyArea> mWomanBackAreaList = new ArrayList();
    private List<BodyArea> mWomanFrontAreaList = new ArrayList();

    private List<BodyArea> mKidHeadAreaList = new ArrayList();
    private List<BodyArea> mKidBackAreaList = new ArrayList();
    private List<BodyArea> mKidFrontAreaList = new ArrayList();

    private BodyArea mCurrentBodyArea;
    private List<BodyArea> mCurrentAreaList;

    public BodyView(Context context) {
        this(context, null);
    }

    public BodyView(Context context, AttributeSet paramAttributeSet) {
        this(context, paramAttributeSet, 0);
    }

    public BodyView(Context context, AttributeSet paramAttributeSet, int paramInt) {
        super(context, paramAttributeSet, paramInt);
        this.mContext = context;
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.man_front, localOptions);
        mManFrontWidth = ((int) (density * localOptions.outWidth / 2.0F));
        mManFrontHeight = ((int) (density * localOptions.outHeight / 2.0F));
        BitmapFactory.decodeResource(getResources(), R.mipmap.man_bigface, localOptions);
        mHeadWidth = ((int) (density * localOptions.outWidth / 2.0F));
        mHeadHeight = ((int) (density * localOptions.outHeight / 2.0F));
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(5.0F);
        mInitAreaRunnable = new InitAreaRunnable(this);
    }

    private List getBodyAreaFromFile(String paramString, boolean isHead) {
        ArrayList bodyAreaList = new ArrayList();
        try {
            JSONArray localJSONArray1 = new JSONArray(new FileUtil().readFromAssets(paramString));
            for (int i = 0; i < localJSONArray1.length(); i++) {
                JSONObject localJSONObject1 = localJSONArray1.optJSONObject(i);
                BodyArea bodyArea = new BodyArea();
                if (localJSONObject1 != null) {
                    bodyArea.bodyId = localJSONObject1.optInt("bid");
                    bodyArea.mipmapId = getResources().getIdentifier("intelligence_highlight_0" + String.valueOf(bodyArea.bodyId), "mipmap", getContext().getPackageName());
                    bodyArea.bodyPart = BodyPart.values()[(bodyArea.bodyId % 100)];
                }
                JSONObject localJSONObject2 = localJSONObject1.optJSONObject("offset");
                if (localJSONObject2 != null) {
                    bodyArea.areaPoint = new AreaPoint(this, localJSONObject2.optInt("x"), localJSONObject2.optInt("y"), isHead);
                }
                JSONArray localJSONArray2 = localJSONObject1.optJSONArray("polygon");
                for (int j = 0; j < localJSONArray2.length(); j++) {
                    JSONObject localJSONObject3 = localJSONArray2.optJSONObject(j);
                    if (localJSONObject3 != null) {
                        AreaPoint areaPoint = new AreaPoint(this, localJSONObject3.optInt("x"), localJSONObject3.optInt("y"), isHead);
                        bodyArea.partPolygon.add(areaPoint);
                    }
                }
                bodyAreaList.add(bodyArea);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bodyAreaList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        this.mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(this.mWidth, this.mHeight);
        calcRatioAndOffset();
    }

    /**
     * 计算缩放比例和偏移量
     */
    private void calcRatioAndOffset() {
        float f = 1.0F;
        if (Build.MODEL.equals("MI NOTE Pro")) {
            f = 0.85F;
        }
        mScaleRatio = Math.min(f * mHeight / mManFrontHeight, f * mWidth / mManFrontWidth);
        mXOffset = ((int) ((mWidth - mManFrontWidth * mScaleRatio) / 2.0F));
        mYOffset = ((int) ((mHeight - mManFrontHeight * mScaleRatio) / 2.0F));
        mHeadScaleRatio = Math.min(mHeight / mHeadHeight, mWidth / mHeadWidth);
        mXHeadOffset = ((int) ((mWidth - mHeadWidth * mHeadScaleRatio) / 2.0F));
        mYHeadOffset = ((int) ((mHeight - mHeadHeight * mHeadScaleRatio) / 2.0F));
        setCurrentAreaList(true);
    }

    private void setCurrentAreaList(boolean paramBoolean) {
        if (mInitAreaRunnable != null) {
            new Handler(Looper.getMainLooper()).removeCallbacks(mInitAreaRunnable);
            mInitAreaRunnable.setCache(paramBoolean);
            new Handler(Looper.getMainLooper()).postDelayed(mInitAreaRunnable, 100L);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        calcRatioAndOffset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        Bitmap bodyBackground;

        if (mCrowd == Crowd.MAN) {
            if (mIsHead) {
                bodyBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.man_bigface);
                canvas.drawBitmap(bodyBackground, new Rect(0, 0, bodyBackground.getWidth(), bodyBackground.getHeight()), new Rect(mXHeadOffset, mYHeadOffset, (int) (mXHeadOffset + mHeadScaleRatio * bodyBackground.getWidth()), (int) (mYHeadOffset + mHeadScaleRatio * bodyBackground.getHeight())), mPaint);
            } else if (!mIsBack) {
                bodyBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.man_front);
                canvas.drawBitmap(bodyBackground, new Rect(0, 0, bodyBackground.getWidth(), bodyBackground.getHeight()), new Rect(mXOffset, mYOffset, (int) (mXOffset + mScaleRatio * bodyBackground.getWidth()), (int) (mYOffset + mScaleRatio * bodyBackground.getHeight())), mPaint);
            } else {
                bodyBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.man_back_view);
                canvas.drawBitmap(bodyBackground, new Rect(0, 0, bodyBackground.getWidth(), bodyBackground.getHeight()), new Rect(mXOffset, mYOffset, (int) (mXOffset + mScaleRatio * bodyBackground.getWidth()), (int) (mYOffset + mScaleRatio * bodyBackground.getHeight())), mPaint);
            }
        } else if (mCrowd == Crowd.WOMAN) {
            if (mIsHead) {
                bodyBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.woman_bigface);
                canvas.drawBitmap(bodyBackground, new Rect(0, 0, bodyBackground.getWidth(), bodyBackground.getHeight()), new Rect(mXHeadOffset, mYHeadOffset, (int) (mXHeadOffset + mHeadScaleRatio * bodyBackground.getWidth()), (int) (mYHeadOffset + mHeadScaleRatio * bodyBackground.getHeight())), mPaint);
            } else if (!mIsBack) {
                bodyBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.woman_front);
                canvas.drawBitmap(bodyBackground, new Rect(0, 0, bodyBackground.getWidth(), bodyBackground.getHeight()), new Rect(mXOffset, mYOffset, (int) (mXOffset + mScaleRatio * bodyBackground.getWidth()), (int) (mYOffset + mScaleRatio * bodyBackground.getHeight())), mPaint);
            } else {
                bodyBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.woman_back_view);
                canvas.drawBitmap(bodyBackground, new Rect(0, 0, bodyBackground.getWidth(), bodyBackground.getHeight()), new Rect(mXOffset, mYOffset, (int) (mXOffset + mScaleRatio * bodyBackground.getWidth()), (int) (mYOffset + mScaleRatio * bodyBackground.getHeight())), mPaint);
            }
        } else {
            if (mIsHead) {
                bodyBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.kid_bigface);
                canvas.drawBitmap(bodyBackground, new Rect(0, 0, bodyBackground.getWidth(), bodyBackground.getHeight()), new Rect(mXHeadOffset, mYHeadOffset, (int) (mXHeadOffset + mHeadScaleRatio * bodyBackground.getWidth()), (int) (mYHeadOffset + mHeadScaleRatio * bodyBackground.getHeight())), mPaint);
            } else if (!mIsBack) {
                bodyBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.kid_front);
                canvas.drawBitmap(bodyBackground, new Rect(0, 0, bodyBackground.getWidth(), bodyBackground.getHeight()), new Rect(mXOffset, mYOffset, (int) (mXOffset + mScaleRatio * bodyBackground.getWidth()), (int) (mYOffset + mScaleRatio * bodyBackground.getHeight())), mPaint);
            } else {
                bodyBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.kid_back_view);
                canvas.drawBitmap(bodyBackground, new Rect(0, 0, bodyBackground.getWidth(), bodyBackground.getHeight()), new Rect(mXOffset, mYOffset, (int) (mXOffset + mScaleRatio * bodyBackground.getWidth()), (int) (mYOffset + mScaleRatio * bodyBackground.getHeight())), mPaint);
            }
        }
        recycleBitmap(bodyBackground);

        if (mCurrentBodyArea != null) {
            Bitmap bodyPart = BitmapFactory.decodeResource(getResources(), mCurrentBodyArea.mipmapId);
            if (bodyPart != null) {
                canvas.drawBitmap(bodyPart, new Rect(0, 0, bodyPart.getWidth(), bodyPart.getHeight()), new BodyView.PaintRect(this, mCurrentBodyArea.areaPoint, bodyPart.getWidth(), bodyPart.getHeight(), mIsHead), mPaint);
                recycleBitmap(bodyPart);
            }
        }
        canvas.restore();
    }

    private void recycleBitmap(Bitmap bitmap) {
        if ((bitmap != null) && (!bitmap.isRecycled())) {
            bitmap.recycle();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mEnableFlag) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mXDown = event.getX();
                mYDown = event.getY();
                parseTouchEvent(new Point((int) mXDown, (int) mYDown));
                break;
            case MotionEvent.ACTION_UP:
                onActionUp();
                break;
        }
        return true;
    }

    private void parseTouchEvent(Point point) {
        if (mCurrentAreaList != null) {
            Iterator<BodyArea> iterator = mCurrentAreaList.iterator();

            while (iterator.hasNext()) {
                BodyArea bodyArea = iterator.next();
                if (bodyArea.partPolygon != null && isPointInPolygon(point, bodyArea.partPolygon)) {
                    mCurrentBodyArea = bodyArea;
                    invalidate();
                    break;
                } else {
                    mCurrentBodyArea = null;
                    invalidate();
                }
            }
        }
    }

    private boolean isPointInPolygon(Point point, List list) {
        int mCount = list.size();
        int nCross = 0;
        for (int i = 0; i < mCount; ++i) {
            AreaPoint p1 = (AreaPoint) list.get(i);
            AreaPoint p2 = (AreaPoint) list.get((i + 1) % mCount);

            // 求解 y=p.y 与 p1 p2 的交点
            if (p1.y == p2.y) {   // p1p2 与 y=p0.y平行
                continue;
            }
            if (point.y < Math.min(p1.y, p2.y)) { // 交点在p1p2延长线上
                continue;
            }
            if (point.y >= Math.max(p1.y, p2.y)) { // 交点在p1p2延长线上
                continue;
            }
            // 求交点的 X 坐标
            float x = (point.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;
            if (x > point.x) { // 只统计单边交点
                nCross++;
            }
        }
        // 单边交点为偶数，点在多边形之外
        return (nCross % 2 == 1);
    }

    private void onActionUp() {
        if ((mCurrentBodyArea != null) && (mCurrentBodyArea.bodyPart == BodyPart.UNKNOWN)) {
            setHead(true);
        }
        if ((mCurrentBodyArea != null) && (mListener != null)) {
            BodyPart bodyPart = mCurrentBodyArea.bodyPart;
            mListener.onClickBodyPart(bodyPart);
        }
        mCurrentBodyArea = null;
        invalidate();
    }

    public boolean getEnableFlag() {
        return this.mEnableFlag;
    }

    public void setEnableFlag(boolean b) {
        this.mEnableFlag = b;
    }

    public void setCrowd(Crowd param) {
        this.mCrowd = param;
        setCurrentAreaList(false);
        invalidate();
    }

    public void setHead(boolean paramBoolean) {
        this.mIsHead = paramBoolean;
        setCurrentAreaList(false);
        invalidate();
    }

    public void setBack(boolean paramBoolean) {
        this.mIsBack = paramBoolean;
        setCurrentAreaList(false);
        invalidate();
    }

    public void setBodySelectedListener(BodyViewListener listener) {
        this.mListener = listener;
    }

    public void removeHintAreaCallBack() {
        this.mCurrentBodyArea = null;
        invalidate();
    }

    class InitAreaRunnable implements Runnable {
        boolean isCache = true;

        private InitAreaRunnable(BodyView paramBodyView) {
        }

        public void setCache(boolean paramBoolean) {
            this.isCache = paramBoolean;
        }

        public void run() {
            switch (mCrowd.ordinal()) {
                case 1:
                    if (mIsHead) {
                        if (mManHeadAreaList.size() == 0 || isCache) {
                            mCurrentAreaList = getBodyAreaFromFile("guideBody/man_bigface", true);
                        }
                    } else if (mIsBack) {
                        if (mManBackAreaList.size() == 0 || isCache) {
                            mCurrentAreaList = getBodyAreaFromFile("guideBody/man_back", false);
                        }
                    } else {
                        if (mManFrontAreaList.size() == 0 || isCache) {
                            mCurrentAreaList = getBodyAreaFromFile("guideBody/man_front", false);
                        }
                    }
                    break;
                case 2:
                    if (mIsHead) {
                        if (mWomanHeadAreaList.size() == 0 || isCache) {
                            mCurrentAreaList = getBodyAreaFromFile("guideBody/woman_bigface", true);
                        }
                    } else if (mIsBack) {
                        if (mWomanBackAreaList.size() == 0 || isCache) {
                            mCurrentAreaList = getBodyAreaFromFile("guideBody/woman_back", false);
                        }
                    } else {
                        if (mWomanFrontAreaList.size() == 0 || isCache) {
                            mCurrentAreaList = getBodyAreaFromFile("guideBody/woman_front", false);
                        }
                    }
                    break;
                case 3:
                    if (mIsHead) {
                        if (mKidHeadAreaList.size() == 0 || isCache) {
                            mCurrentAreaList = getBodyAreaFromFile("guideBody/kid_bigface", true);
                        }

                    } else if (mIsBack) {
                        if (mKidBackAreaList.size() == 0 || isCache) {
                            mCurrentAreaList = getBodyAreaFromFile("guideBody/kid_back", false);
                        }

                    } else {
                        if (mKidFrontAreaList.size() == 0 || isCache) {
                            mCurrentAreaList = getBodyAreaFromFile("guideBody/kid_front", false);
                        }
                    }
            }
        }
    }

    /**
     * 热区对象
     */
    class BodyArea {
        public int bodyId = 0;
        public int mipmapId = R.mipmap.google;
        public BodyPart bodyPart = BodyPart.HEAD;
        public AreaPoint areaPoint = new AreaPoint();
        public List<AreaPoint> partPolygon = new ArrayList();

        public String toString() {
            return "{bodyId=" + bodyId + ",mipmapId=" + mipmapId + ",bodyPart=" + bodyPart.toString() + "}";
        }
    }

    /**
     * 热区坐标点
     */
    class AreaPoint extends Point {

        private AreaPoint() {
        }

        public AreaPoint(BodyView bodyView, int x, int y, boolean isHead) {
            if (isHead) {
                this.x = ((int) (mXHeadOffset + mHeadScaleRatio * (x / 2 * getResources().getDisplayMetrics().density)));
                this.y = ((int) (mYHeadOffset + mHeadScaleRatio * (y / 2 * getResources().getDisplayMetrics().density)));
            } else {
                this.x = ((int) (mXOffset + mScaleRatio * (x / 2 * getResources().getDisplayMetrics().density)));
                this.y = ((int) (mYOffset + mScaleRatio * (y / 2 * getResources().getDisplayMetrics().density)));
            }
        }
    }

    /**
     * 热区位置
     */
    class PaintRect extends RectF {

        public PaintRect(BodyView bodyView, AreaPoint areaPoint, int width, int height, boolean isHead) {
            // areaPoint is 矩阵的偏移量，对应视图进行缩放处理
            left = (float) areaPoint.x;
            top = (float) areaPoint.y;
            if (isHead) {
                right = ((mHeadScaleRatio * (float) width) + left);
                bottom = ((mHeadScaleRatio * (float) height) + top);
            } else {
                right = ((mScaleRatio * (float) width) + left);
                bottom = ((mScaleRatio * (float) height) + top);
            }

        }
    }

    public enum Crowd {
        UNKNOWN,
        MAN,
        WOMAN,
        KID,
    }

    public enum BodyPart {
        UNKNOWN,
        HEAD,
        EYE,
        NOSE,
        MOUTH,
        EAR,
        FACE,
        NECK,
        ARM,
        HAND,
        CHEST,
        ABDOMEN,
        BACK,
        WAIST,
        HIPS,
        GENITAL,
        LEG,
        FOOT,
        BLOOD,
        MENTAL,
        SKIN,
        OTHERS,
        BIGFACE,
    }

    public interface BodyViewListener {
        void onClickBodyPart(BodyPart bodyPart);
    }

    public class FileUtil {
        public String readFromAssets(String paramString) {
            String str = "";
            try {
                InputStream localInputStream = getContext().getResources().getAssets().open(paramString);
                byte[] arrayOfByte = new byte[localInputStream.available()];
                int len;
                while ((len = localInputStream.read(arrayOfByte)) != -1) {
                    str = new String(arrayOfByte, Charset.defaultCharset());
                }
                localInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
    }
}
