package com.bokecc.video.ui.base;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.bokecc.video.keyboard.EmoticonFragment;
import com.bokecc.video.ui.chat.ChatFragment;
import com.bokecc.video.ui.course.CourseListFragment;
import com.bokecc.video.ui.desc.TeacherDescFragment;

public class FragmentFactory {

    public static final String EMOTION_MAP_TYPE = "EMOTION_MAP_TYPE";

    public static Fragment getEmoticonFragment(int emotionType) {
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentFactory.EMOTION_MAP_TYPE, emotionType);
        EmoticonFragment fragment = EmoticonFragment.newInstance(EmoticonFragment.class, bundle);
        return fragment;
    }


    public static Fragment create(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ChatFragment();
                break;
            case 1:
                fragment = new CourseListFragment();
                break;
            case 2:
                fragment = new TeacherDescFragment();
                break;
        }
        return fragment;
    }
}