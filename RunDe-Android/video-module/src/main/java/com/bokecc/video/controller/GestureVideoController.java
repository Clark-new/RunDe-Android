package com.bokecc.video.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bokecc.video.ui.chat.InputState;

/**
 * 包含手势操作的VideoController
 */

public abstract class GestureVideoController extends DanmakuController {

    protected GestureDetector mGestureDetector;

    public GestureVideoController(@NonNull Context context) {
        super(context);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        mGestureDetector = new GestureDetector(getContext(), new MyGestureListener());
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });


    }

    protected class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mShowing) {
                hide();
            } else {
                show();
            }
            InputState.closeInputState();
            return true;
        }
    }
}
