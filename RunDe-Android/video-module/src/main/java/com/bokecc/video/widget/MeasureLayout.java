package com.bokecc.video.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class MeasureLayout extends RelativeLayout {
    public MeasureLayout(Context context) {
        super(context);
    }

    public MeasureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * 9 / 16;
            setMeasuredDimension(width, height);
            int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
