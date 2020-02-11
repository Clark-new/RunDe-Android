package com.bokecc.video.keyboard;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class EmotionPagerAdapter extends PagerAdapter {

    private List<GridView> mGridViewList;

    public EmotionPagerAdapter(List<GridView> list) {
        this.mGridViewList = list;
    }

    @Override
    public int getCount() {
        return mGridViewList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView(mGridViewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        (container).addView(mGridViewList.get(position));
        return mGridViewList.get(position);
    }

}