package com.bokecc.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import com.bokecc.video.ui.chat.InputState;

public class ViewPagerSlide extends ViewPager {
    //是否可以进行滑动
    private boolean isSlide = false;

    public void setSlide(boolean slide) {
        this.isSlide = slide;
    }

    public ViewPagerSlide(Context context) {
        this(context, null);

    }

    public ViewPagerSlide(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSlide && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //触摸ViewPage关闭输入模式
        InputState.closeInputState();
        return isSlide && super.onTouchEvent(ev);
    }

    private void addPageChangeListener(){
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}
