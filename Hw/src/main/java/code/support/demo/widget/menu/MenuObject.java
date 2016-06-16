package code.support.demo.widget.menu;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.util.List;

public class MenuObject implements Parcelable {

    private String mTitle;
    // bg
    private Drawable mBgDrawable;
    private int mBgColor;
    private int mBgResource;
    // image
    private Drawable mDrawable;
    private int mColor;
    private Bitmap mBitmap;
    private int mResource;
    // image scale type
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.CENTER_INSIDE;
    // text
    private int mTextColor;
    // divider
    private int mDividerColor = Integer.MAX_VALUE;

    private int mTextAppearanceStyle;

    public MenuObject() {
        this.mTitle = "";
    }

    public MenuObject(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Drawable getBgDrawable() {
        return mBgDrawable;
    }

    public void setBgDrawable(Drawable mBgDrawable) {
        this.mBgDrawable = mBgDrawable;
        mBgColor = 0;
        mBgResource = 0;
    }

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        mBgResource = 0;
        mBgDrawable = null;
    }

    public int getBgResource() {
        return mBgResource;
    }

    public void setBgResource(int mBgResource) {
        this.mBgResource = mBgResource;
        mBgColor = 0;
        mBgDrawable = null;
    }

    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Use {@link #setMenuTextAppearanceStyle(int)} to set all text style params at one place
     */
    @Deprecated
    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
        mResource = 0;
        mBitmap = null;
        mDrawable = null;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        mColor = 0;
        mResource = 0;
        mDrawable = null;
    }

    public int getResource() {
        return mResource;
    }

    public void setResource(int mResource) {
        this.mResource = mResource;
        mColor = 0;
        mBitmap = null;
        mDrawable = null;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
        mColor = 0;
        mResource = 0;
        mBitmap = null;
    }

    public int getMenuTextAppearanceStyle() {
        return mTextAppearanceStyle;
    }

    /**
     * Set style resource id, it will be used for setting text appearance of menu item title.
     * For better effect your style should extend TextView.DefaultStyle
     */
    public void setMenuTextAppearanceStyle(int mMenuTextAppearanceStyle) {
        this.mTextAppearanceStyle = mMenuTextAppearanceStyle;
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(int mDividerColor) {
        this.mDividerColor = mDividerColor;
    }

    public ImageView.ScaleType getScaleType() {
        return mScaleType;
    }

    public void setScaleType(ImageView.ScaleType mScaleType) {
        this.mScaleType = mScaleType;
    }

    public static Creator<MenuObject> getCreator() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
        dest.writeParcelable(mBgDrawable == null ? null : ((BitmapDrawable) this.mBgDrawable).getBitmap(), flags);
        dest.writeInt(this.mBgColor);
        dest.writeInt(this.mBgResource);
        dest.writeParcelable(mDrawable == null ? null : ((BitmapDrawable) this.mDrawable).getBitmap(), flags);
        dest.writeInt(this.mColor);
        dest.writeParcelable(this.mBitmap, 0);
        dest.writeInt(this.mResource);
        dest.writeInt(this.mScaleType == null ? -1 : this.mScaleType.ordinal());
        dest.writeInt(this.mTextColor);
        dest.writeInt(this.mDividerColor);
        dest.writeInt(this.mTextAppearanceStyle);
    }

    private MenuObject(Parcel in) {
        this.mTitle = in.readString();
        Bitmap bitmapBgDrawable = in.readParcelable(Bitmap.class.getClassLoader());
        if (bitmapBgDrawable != null) {
            this.mBgDrawable = new BitmapDrawable(bitmapBgDrawable);
        }
        this.mBgColor = in.readInt();
        this.mBgResource = in.readInt();
        Bitmap bitmapDrawable = in.readParcelable(Bitmap.class.getClassLoader());
        if (bitmapDrawable != null) {
            this.mDrawable = new BitmapDrawable(bitmapDrawable);
        }
        this.mColor = in.readInt();
        this.mBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.mResource = in.readInt();
        int tmpMScaleType = in.readInt();
        this.mScaleType = tmpMScaleType == -1 ? null : ImageView.ScaleType.values()[tmpMScaleType];
        this.mTextColor = in.readInt();
        this.mDividerColor = in.readInt();
        this.mTextAppearanceStyle = in.readInt();
    }

    public static final Creator<MenuObject> CREATOR = new Creator<MenuObject>() {
        public MenuObject createFromParcel(Parcel source) {
            return new MenuObject(source);
        }

        public MenuObject[] newArray(int size) {
            return new MenuObject[size];
        }
    };

    public static class MenuParams implements Parcelable {

        private int mActionBarSize = 0;
        private List<MenuObject> mMenuObjects;
        /**
         * Delay after opening and before closing {@link ActionBarMenu}
         */
        private int mAnimationDelay = 0;
        private int mAnimationDuration = ActionBarMenu.MyFragmentAdapter.ANIMATION_DURATION_MILLIS;
        private boolean isFitsSystemWindow = false;
        private boolean isClipToPadding = true;
        /**
         * If option menu can be closed on touch to non-button area
         */
        private boolean isClosableOutside = false;

        public void setActionBarSize(int mActionBarSize) {
            this.mActionBarSize = mActionBarSize;
        }

        public void setMenuObjects(List<MenuObject> mMenuObjects) {
            this.mMenuObjects = mMenuObjects;
        }

        public void setAnimationDelay(int mAnimationDelay) {
            this.mAnimationDelay = mAnimationDelay;
        }

        public void setAnimationDuration(int mAnimationDuration) {
            this.mAnimationDuration = mAnimationDuration;
        }

        public void setFitsSystemWindow(boolean mFitsSystemWindow) {
            this.isFitsSystemWindow = mFitsSystemWindow;
        }

        public void setClipToPadding(boolean mClipToPadding) {
            this.isClipToPadding = mClipToPadding;
        }

        /**
         * Set option menu can be closed on touch to non-button area
         *
         * @param isClosableOutside true if can
         */
        public void setClosableOutside(boolean isClosableOutside) {
            this.isClosableOutside = isClosableOutside;
        }

        public int getActionBarSize() {
            return mActionBarSize;
        }

        public List<MenuObject> getMenuObjects() {
            return mMenuObjects;
        }

        public int getAnimationDelay() {
            return mAnimationDelay;
        }

        public int getAnimationDuration() {
            return mAnimationDuration;
        }

        public boolean isFitsSystemWindow() {
            return isFitsSystemWindow;
        }

        public boolean isClipToPadding() {
            return isClipToPadding;
        }

        public boolean isClosableOutside() {
            return isClosableOutside;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mActionBarSize);
            dest.writeTypedList(mMenuObjects);
            dest.writeInt(this.mAnimationDelay);
            dest.writeInt(this.mAnimationDuration);
            dest.writeByte(isFitsSystemWindow ? (byte) 1 : (byte) 0);
            dest.writeByte(isClipToPadding ? (byte) 1 : (byte) 0);
            dest.writeByte(isClosableOutside ? (byte) 1 : (byte) 0);
        }

        public MenuParams() {
        }

        private MenuParams(Parcel in) {
            this.mActionBarSize = in.readInt();
            in.readTypedList(mMenuObjects, MenuObject.CREATOR);
            this.mAnimationDelay = in.readInt();
            this.mAnimationDuration = in.readInt();
            this.isFitsSystemWindow = in.readByte() != 0;
            this.isClipToPadding = in.readByte() != 0;
            this.isClosableOutside = in.readByte() != 0;
        }

        public final Creator<MenuParams> CREATOR = new Creator<MenuParams>() {
            public MenuParams createFromParcel(Parcel source) {
                return new MenuParams(source);
            }

            public MenuParams[] newArray(int size) {
                return new MenuParams[size];
            }
        };
    }
}
