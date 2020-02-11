package com.bokecc.video.ui.main.fragment;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.video.R;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.ui.base.BaseFragment;
import com.bokecc.video.ui.base.HandleBackInterface;
import com.bokecc.video.utils.CommonUtils;
import com.bokecc.video.widget.TabPageIndicator;
import com.bokecc.video.widget.ViewPagerSlide;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseVideoFragment extends BaseFragment implements HandleBackInterface {

    public static String SPECIAL_KEY = "is_special_course";
    public static String SPECIAL_COURSE_UI = "special_course_ui";
    public static String OPEN_COURSE_UI = "open_course_ui";
    //小title状态
    protected static final int NARROW_STATE = 0;
    //宽title状态
    protected static final int BROAD_STATE = 1;

    private TabPageIndicator indicator;
    private ViewPagerSlide viewPager;
    private BasePagerAdapter adapter;
    private List<String> titleArray = new ArrayList<>();
    //是否是专题课
    protected boolean isSpecialCourse = true;

    protected View mCourseTile;
    /*
     * 当前title的状态，平铺和堆叠两种
     * 由于没有监听动画的执行过程，因此中间状态被忽略掉
     * 因此在动画开始转换时就将状态设置成目标状态
     */
    protected int mOpenTitleState = NARROW_STATE;
    private TextView mTeacherNameTv;
    private TextView mWatcherNumTv;
    private View mSendGiftBtn;

    protected int anchorX;
    protected int anchorY;
    protected int mBroadHeight;
    protected int mNarrowHeight;
    protected int statusBarHeight;
    protected int floatViewWidth;

    protected View mFloatAnchorView;
    protected FrameLayout mFloatViewContainer;

    @Override
    public int getRootResource() {
        return R.layout.layout_base_fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        isSpecialCourse = getActivity().getIntent().getBooleanExtra(SPECIAL_KEY, false);
        HDApi.get().setCourseType(isSpecialCourse);
        mNarrowHeight = CommonUtils.dip2px(getContext(), 60);
        mBroadHeight = CommonUtils.dip2px(getContext(), 85);
        floatViewWidth = CommonUtils.dip2px(getContext(), 150);
        statusBarHeight = (int) CommonUtils.getStatusBarHeight(getActivity());
    }

    @Override
    protected void initView() {
        super.initView();
        CCEventBus.getDefault().register(this);
        indicator = findViewById(R.id.id_indicator);
        viewPager = findViewById(R.id.id_view_pager);
        mCourseTile = findViewById(R.id.id_open_course_title);
        mFloatAnchorView = findViewById(R.id.id_float_view_anchor);
        mTeacherNameTv = findViewById(R.id.id_teacher_name_tv);
        mWatcherNumTv = findViewById(R.id.id_watch_user_num);
        mSendGiftBtn = findViewById(R.id.id_send_gift_btn);
        mFloatViewContainer = findViewById(R.id.id_float_view_container);
        if (isSpecialCourse) {
            createCourseTitle(SPECIAL_COURSE_UI);
        } else {
            createCourseTitle(OPEN_COURSE_UI);
        }
        adapter = new BasePagerAdapter(getActivity().getSupportFragmentManager(), titleArray);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        indicator.setViewPager(viewPager);
        setTabPagerIndicator();
        if (isSpecialCourse) {
            showSpecialCourseUi();
        } else {
            showOpenCourseUi();
        }
    }


    public void createCourseTitle(String extra) {
        if (extra.equals(SPECIAL_COURSE_UI)) {
            titleArray.clear();
            titleArray.add("聊天");
            titleArray.add("直播目录");
            titleArray.add("讲师介绍");
        } else {
            titleArray.clear();
            titleArray.add("聊天");
        }
    }

    private void setTabPagerIndicator() {
        indicator.setIndicatorMode(TabPageIndicator.IndicatorMode.MODE_WEIGHT_NOEXPAND_SAME);// 设置模式，一定要先设置模式
        indicator.setDividerColor(0x00000000);// 设置分割线的颜色
        indicator.setIndicatorColor(getResources().getColor(R.color.pink));// 设置底部导航线的颜色
        indicator.setTextColorSelected(getResources().getColor(R.color.pink));// 设置tab标题选中的颜色
        indicator.setTextColor(getResources().getColor(R.color.text_color));// 设置tab标题未被选中的颜色
        indicator.setTextSize(CommonUtils.sp2px(getContext(), 15));// 设置字体大小
    }

    /**
     * 显示专题课Ui
     */
    protected void showSpecialCourseUi() {
        createCourseTitle(SPECIAL_COURSE_UI);
        mCourseTile.setVisibility(View.GONE);
        indicator.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        viewPager.setSlide(true);
    }

    /**
     * 显示公开课Ui
     */
    protected void showOpenCourseUi() {
        createCourseTitle(OPEN_COURSE_UI);
        indicator.setVisibility(View.GONE);
        mCourseTile.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        viewPager.setSlide(false);
        viewPager.setCurrentItem(0);
    }


    protected void setTitleToNarrow() {
        if (mOpenTitleState == NARROW_STATE || isSpecialCourse) return;
        mOpenTitleState = NARROW_STATE;
        int ret = Math.abs(mCourseTile.getHeight() - mNarrowHeight);
        if (ret > 10) {
            CommonUtils.setChangeHeightAnim(mCourseTile, mBroadHeight, mNarrowHeight, 300);
            CommonUtils.setChangeWidthAnim(mFloatAnchorView, floatViewWidth, 0, 300);
        }
    }

    protected void setTitleToBroad() {
        if (mOpenTitleState == BROAD_STATE || isSpecialCourse) return;
        mOpenTitleState = BROAD_STATE;
        int ret = Math.abs(mCourseTile.getHeight() - mBroadHeight);
        if (ret > 10) {
            CommonUtils.setChangeHeightAnim(mCourseTile, mNarrowHeight, mBroadHeight, 300);
            CommonUtils.setChangeWidthAnim(mFloatAnchorView, 0, floatViewWidth, 300);
        }
    }


    protected void switchCourseUi(boolean isSpecialCourse) {
        this.isSpecialCourse = isSpecialCourse;
        setUiMeasuredListener();
        if (isSpecialCourse) {
            showSpecialCourseUi();
        } else {
            setTitleToBroad();
            showOpenCourseUi();
        }
        mRootView.requestLayout();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CCEventBus.getDefault().unregister(this);
    }


    protected abstract void setUiMeasuredListener();
}
