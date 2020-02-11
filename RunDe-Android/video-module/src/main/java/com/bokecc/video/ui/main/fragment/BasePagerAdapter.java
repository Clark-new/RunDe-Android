package com.bokecc.video.ui.main.fragment;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bokecc.video.ui.base.FragmentFactory;

import java.util.List;

public class BasePagerAdapter extends FragmentStatePagerAdapter {
    List<String> titles;

    public BasePagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentFactory.create(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}