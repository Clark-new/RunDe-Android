package com.bokecc.video.ui.main.fragment;

import android.view.VelocityTracker;
import android.view.View;

import com.bokecc.video.widget.FloatView;

public interface OnFloatViewMoveListener {

    void onMoveInWindow();

    void onMove(FloatView view, VelocityTracker tracker);

    void onMoveFinished(FloatView view);

    void onMoveOutWindow();
}
