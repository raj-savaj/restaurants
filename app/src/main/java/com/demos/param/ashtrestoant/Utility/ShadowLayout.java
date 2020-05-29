package com.demos.param.ashtrestoant.Utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.demos.param.ashtrestoant.R;

public class ShadowLayout extends FrameLayout {
    private float mCornerRadius;
    private float mDx;
    private float mDy;
    private boolean mForceInvalidateShadow = false;
    private boolean mInvalidateShadowOnSizeChanged = true;
    private int mShadowColor;
    private float mShadowRadius;

    public ShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            if (getBackground() == null || this.mInvalidateShadowOnSizeChanged || this.mForceInvalidateShadow) {
                this.mForceInvalidateShadow = false;
                setBackgroundCompat(w, h);
            }
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mForceInvalidateShadow) {
            this.mForceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        this.mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
    }

    public void invalidateShadow() {
        this.mForceInvalidateShadow = true;
        requestLayout();
        invalidate();
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);
        setPadding(0, 0, (int) (this.mShadowRadius + Math.abs(this.mDx)), (int) (this.mShadowRadius + Math.abs(this.mDy)));
    }

    private void setBackgroundCompat(int w, int h) {
        BitmapDrawable drawable = new BitmapDrawable(getResources(), createShadowBitmap(w, h, this.mCornerRadius, this.mShadowRadius, this.mDx, this.mDy, this.mShadowColor, 0));
        if (VERSION.SDK_INT <= 16) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    @SuppressLint("ResourceType")
    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.BottomSheetBehavior_Layout);
        if (attr != null) {
            try {
                this.mCornerRadius = attr.getDimension(0, getResources().getDimension(R.dimen.default_corner_radius));
                this.mShadowRadius = attr.getDimension(4, getResources().getDimension(R.dimen.default_shadow_radius));
                this.mDx = attr.getDimension(1, 0.0f);
                this.mDy = attr.getDimension(2, 0.0f);
                this.mShadowColor = attr.getColor(3, getResources().getColor(R.color.default_shadow_color));
            } finally {
                attr.recycle();
            }
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius, float dx, float dy, int shadowColor, int fillColor) {
        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        RectF shadowRect = new RectF(shadowRadius, shadowRadius, ((float) shadowWidth) - shadowRadius, ((float) shadowHeight) - shadowRadius);
        if (dy > 0.0f) {
            shadowRect.top += dy;
            shadowRect.bottom -= dy;
        } else if (dy < 0.0f) {
            shadowRect.top += Math.abs(dy);
            shadowRect.bottom -= Math.abs(dy);
        }
        if (dx > 0.0f) {
            shadowRect.left += dx;
            shadowRect.right -= dx;
        } else if (dx < 0.0f) {
            shadowRect.left += Math.abs(dx);
            shadowRect.right -= Math.abs(dx);
        }
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(fillColor);
        shadowPaint.setStyle(Style.FILL);
        if (!isInEditMode()) {
            shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        }
        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);
        return output;
    }
}
