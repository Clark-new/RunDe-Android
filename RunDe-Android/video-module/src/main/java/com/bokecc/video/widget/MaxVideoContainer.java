package com.bokecc.video.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 根据视频的宽度，重写视频的高度
 */
public class MaxVideoContainer extends FrameLayout {

    private static final String TAG = "MaxVideoContainer";

    private View mContentView;

    public MaxVideoContainer(@NonNull Context context) {
        this(context, null);
    }

    public MaxVideoContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void addChildView(View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, 0, params);
        mContentView = view;
    }

    public View removeChildView(){
        if(mContentView != null){
            removeView(mContentView);
        }
        return mContentView;
    }

    public void addControlView(View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, params);
    }


    public boolean hasContent(){
        return mContentView != null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i(TAG, "onMeasure: ");
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
