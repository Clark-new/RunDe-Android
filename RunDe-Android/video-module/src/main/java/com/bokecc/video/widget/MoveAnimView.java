package com.bokecc.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.bokecc.video.utils.CommonUtils;

public class MoveAnimView extends FrameLayout {
    public static final int STATE_STACK = 1; //堆叠状态
    public static final int STATE_TILE = 2; //平放状态

    //平放状态下ViewGroup的默认高度
    public int narrowHeight;//(dp)
    //堆叠状态下ViewGroup的默认高度
    public int broadHeight = 85;//(dp)
    private int verticalSpace;
    private int horizontalSpace;

    //当前ViewGroup的状态
    private int mState = STATE_STACK;

    public MoveAnimView(Context context) {
        this(context, null);
    }

    public MoveAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoveAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        narrowHeight = CommonUtils.dip2px(context, 60);
        broadHeight = CommonUtils.dip2px(context, 85);
        verticalSpace = CommonUtils.dip2px(context, 8);
        horizontalSpace = CommonUtils.dip2px(context, 19);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        if (count != 2) return;
        int height = getHeight();
        View view1 = getChildAt(0);
        View view2 = getChildAt(1);

        int v1Width = view1.getMeasuredWidth();
        int v1Height = view1.getMeasuredHeight();
        int v2Width = view2.getMeasuredWidth();
        int v2Height = view2.getMeasuredHeight();

        int v1MaxTop = (height - v1Height - v2Height - verticalSpace) >> 1;
        int v2MinTop = (height - v2Height) >> 1;
        int v2MaxLeft = v1Width + horizontalSpace;
        int v2MaxTop = v1MaxTop + v1Height + verticalSpace;

        if (Math.abs(height - narrowHeight) == 0) {
            int v1Top = (height - v1Height) >> 1;
            int v2Left = v1Width + horizontalSpace;
            view1.layout(0, v1Top, v1Width, v1Top + v1Height);
            view2.layout(v2Left, v2MinTop, v2Left + v2Width, v2MinTop + v2Height);
        } else if (Math.abs(height - broadHeight) == 0) {
            view1.layout(0, v1MaxTop, v1Width, v1MaxTop + v1Height);
            view2.layout(0, v2MaxTop, v2Width, v2MaxTop + v2Height);
        } else {
            float fraction = (height - narrowHeight) * 1.0f / (broadHeight - narrowHeight);//0%->100%;
            onAnimTrans(view2, v2Width, v2Height, fraction, 0, v2MaxLeft, v2MinTop, v2MaxTop);
        }
    }

    private void onAnimTrans(View view, int width, int height,
                             float fraction, int minLeft, int maxLeft,
                             int minTop, int maxTop) {
        int l = (int) ((1.0f - fraction) * (maxLeft - minLeft)) + minLeft;
        int t = (int) (fraction * (maxTop - minTop) + minTop);
        view.layout(l, t, l + width, t + height);
    }
}
