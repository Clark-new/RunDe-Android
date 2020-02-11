package com.bokecc.video.ui.main.fragment;

import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.os.Build;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.sdk.mobile.live.widget.DocView;
import com.bokecc.video.R;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.controller.OtherFunctionCallback;
import com.bokecc.video.controller.StandardVideoController;
import com.bokecc.video.route.DanmuMessage;
import com.bokecc.video.route.OnVideoSwitchMsg;
import com.bokecc.video.ui.chat.KeyBoardFragment;
import com.bokecc.video.utils.CommonUtils;
import com.bokecc.video.video.HDVideoView;
import com.bokecc.video.video.RTCController;
import com.bokecc.video.widget.FloatView;
import com.bokecc.video.widget.MaxVideoContainer;

public class VideoCourseFragment extends RTCControlFragment implements OtherFunctionCallback, OnFloatViewMoveListener, FloatView.OnDismissListener {
    private static final String TAG = "VideoCourseFragment";

    //由于系统的UI自动隐藏了状态栏，到UI计算存在误差
    private int layoutOffsetHeight = 0;

    //视频播放控制器
    private StandardVideoController mVideoController;
    //存放视频或者文档视图
    private MaxVideoContainer mMaxContainer;
    //播放视频的view
    private HDVideoView mVideoView;
    private FloatView mFloatView;
    //文档视图
    private DocView mDocView;
    //存放floatView相对于屏幕的绝对坐标
    private int[] location = new int[2];

    @Override
    protected void initView() {
        super.initView();
        mMaxContainer = findViewById(R.id.id_max_container);
        mVideoController = new StandardVideoController(getActivity());
        mVideoController.setOtherFunctionCallback(this);
        mVideoController.setRtcControlCallback(this);
        mMaxContainer.addControlView(mVideoController);
        mVideoView = new HDVideoView(getActivity());
        mVideoView.setVideoController(mVideoController);
        mVideoView.init(getContext());
        setUiMeasuredListener();
    }

    protected void setUiMeasuredListener() {
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mRootView.getLocationOnScreen(location);
                ELog.i("Sivin", "mRootView y=" + location[1]);
                layoutOffsetHeight = location[1];
                mCourseTile.getLocationOnScreen(location);
                ELog.i("Sivin", "mCourseTile y=" + location[1]);
                anchorX = 0;
                anchorY = location[1];
//                if(ScreenAdapterUtil.hasNotchScreen(getActivity())){
//                    anchorY = location[1]-layoutOffsetHeight;
//                }
                onUiMeasuredFinish();
            }
        });
    }


    private void onUiMeasuredFinish() {
        if (isSpecialCourse) {
            if (HDApi.get().hasDoc()) {
                //文档视图
                if (mDocView == null) {
                    mDocView = new DocView(getContext().getApplicationContext());
                }
                if (mFloatView == null) {
                    mFloatView = new FloatView(getContext(), 150, 85);
                    mFloatView.setOnDismissListener(this);
                }

                mMaxContainer.removeChildView();
                mFloatView.removeChildView();

                mMaxContainer.addChildView(mDocView);
                mFloatView.addChildView(mVideoView);

                if (mFloatView.hasAddToWindow()) {
                    mFloatView.updatePosition(anchorX, anchorY);
                } else {
                    mFloatView.addToActivity(mRootView, anchorX, anchorY);
                }

                mFloatView.setMoveListener(this);
                HDApi.get().setDocView(mDocView);
            } else {
                mMaxContainer.addChildView(mVideoView);
            }
        } else {
            if (HDApi.get().hasDoc()) {
                //文档视图
                if (mDocView == null) {
                    mDocView = new DocView(getContext().getApplicationContext());
                }
                if (mFloatView == null) {
                    mFloatView = new FloatView(getContext(), 150, 85);
                    mFloatView.setOnDismissListener(this);
                }

                mMaxContainer.removeChildView();
                mFloatView.removeChildView();

                mOpenTitleState = BROAD_STATE;
                ViewGroup.LayoutParams lp = mFloatAnchorView.getLayoutParams();
                lp.width = floatViewWidth;
                lp.height = mBroadHeight;
                mFloatAnchorView.setLayoutParams(lp);
                mFloatAnchorView.requestLayout();

                mMaxContainer.addChildView(mDocView);
                mFloatView.addChildView(mVideoView);

                if (mFloatView.hasAddToWindow()) {
                    mFloatView.updatePosition(anchorX, anchorY);
                } else {
                    mFloatView.addToActivity(mRootView, anchorX, anchorY);
                }

                mFloatView.setMoveListener(this);
                HDApi.get().setDocView(mDocView);
            } else {
                mMaxContainer.addChildView(mVideoView);
            }
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        //必须在start之前先设置docview 给api
        if (HDApi.get().hasDoc()) {
            if (mDocView == null) {
                mDocView = new DocView(getContext().getApplicationContext());
            }
            HDApi.get().setDocView(mDocView);
        }

        if (mFloatView != null && !mFloatView.hasAddToWindow()) {
            mFloatView.addToActivity(mRootView, anchorX, anchorY);
        }
        mVideoView.videoStart();
        mVideoController.resume();
    }

    @Override
    public void onStop() {
        super.onStop();
        //如果不开启后台播放，则将此处放开
        //mVideoView.pause();
        if (mFloatView != null) {
            mFloatView.removeFromWindow();
        }
        mVideoController.pause();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mVideoView.videoDestroy();
        mFloatView.removeFromWindow();
        mVideoView.release();
        mVideoController.release();
    }

    @Override
    public boolean onBackPressed() {
        if (mVideoController != null) {
            return mVideoController.onBackPressed();
        } else {
            return super.onBackPressed();
        }
    }

    @Override
    public void onSwapBtnClick(ImageView view) {
        if (!mFloatView.hasAddToWindow() && HDApi.get().hasDoc()) {
            mFloatView.addToActivity(mRootView, anchorX, anchorY);
            setTitleToBroad();
            mVideoController.setSwapBtnState(StandardVideoController.SwapBtnState.SWAP_STATE);
            return;
        }
        if (mFloatView != null) {
            View view1 = mFloatView.removeChildView();
            View view2 = mMaxContainer.removeChildView();
            mMaxContainer.addChildView(view1);
            mFloatView.addChildView(view2);
        }
    }


    @Override
    public RTCController getRTCController() {
        return mVideoView;
    }

    /**
     * 点击分享
     */
    @Override
    public void onSharedBtnClick() {

    }


    /**
     * 不同的课程发生切换的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoSwitch(OnVideoSwitchMsg message) {
        if (message.getType() == OnVideoSwitchMsg.PREPARE) {

        } else if (message.getType() == OnVideoSwitchMsg.START) {
            switchCourseUi(HDApi.get().isSpecialCourse());
            updateController();
            startReloadCourse();
        }
    }


    /**
     * 准备重新加载Ui
     * 做专题和和公开课UI切换
     */
    private void updateController() {
        if (HDApi.get().getApiType() == HDApi.ApiType.REPLAY) {
            mVideoController.setLive(false);
        } else {
            mVideoController.setLive(true);
        }
    }

    /**
     * 课程切换课程
     */
    private void startReloadCourse() {
        if (HDApi.get().hasDoc()) {
            //文档视图
            if (mDocView == null) {
                mDocView = new DocView(getContext().getApplicationContext());
            }
            if (mFloatView == null) {
                mFloatView = new FloatView(getContext(), 0, 0);
                mFloatView.setOnDismissListener(this);
            }
            if (mFloatView.hasAddToWindow()) {
                mFloatView.updatePosition(anchorX, anchorY);
            } else {
                mFloatView.addToActivity(mRootView, anchorX, anchorY);
            }
            mMaxContainer.removeChildView();
            mFloatView.removeChildView();
            mMaxContainer.addChildView(mDocView);
            mFloatView.addChildView(mVideoView);
            HDApi.get().setDocView(mDocView);
        } else {
            mMaxContainer.removeChildView();
            if (mFloatView != null) {
                mFloatView.removeChildView();
                mFloatView.removeChildView();
                mFloatView.removeFromWindow();
            }
            mMaxContainer.addChildView(mVideoView);
        }
        mVideoView.videoStart();
    }


    @Override
    public void onMoveInWindow() {
    }

    @Override
    public void onMove(FloatView view, VelocityTracker tracker) {
        //这都是在竖屏模式下移动，横屏模式下，公开课模式下
        if (orientation == Configuration.ORIENTATION_LANDSCAPE || isSpecialCourse) {
            return;
        }
        //补偿高度
        int repairOffset = 0;
        //6.0以下的系统浮动窗口，无法扩展到状态栏，因此计算时需要适配补偿
        if (view.isSystemView() && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            repairOffset = statusBarHeight;
        }
        view.getLocationOnScreen(location);
        int floatViewTop = location[1] - repairOffset;
        int floatViewBottom = floatViewTop + view.getHeight();

        int titleTop = anchorY + repairOffset;
        int titleBottom = titleTop + mCourseTile.getHeight();

        if (floatViewTop > titleBottom || floatViewBottom < titleTop) {
            setTitleToNarrow();
        } else {
            setTitleToBroad();
        }
    }

    @Override
    public void onMoveFinished(FloatView view) {
        view.getLocationOnScreen(location);
        //修正高度
        int repairOffset = 0;
        if (view.isSystemView() && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            repairOffset = statusBarHeight;
        }
        int floatViewLeft = location[0];
        int floatViewTop = location[1];
        int floatViewBottom = floatViewTop + view.getHeight();
        int floatViewRight = floatViewLeft + view.getWidth();

        int titleTop = anchorY + repairOffset;
        int titleBottom = titleTop + mCourseTile.getHeight();

        if (isSpecialCourse || orientation == Configuration.ORIENTATION_LANDSCAPE) {
            edgeCorrect(view, floatViewLeft, floatViewTop, floatViewBottom, floatViewRight);
            return;
        }

        //公开课，并且是在竖屏模式下执行
        if (floatViewTop > titleBottom || floatViewBottom < titleTop) {
            edgeCorrect(view, floatViewLeft, floatViewTop, floatViewBottom, floatViewRight);
        } else {
            animAnchorFloatView(true, floatViewLeft, floatViewTop, anchorX, anchorY);
        }
    }


    @Override
    public void onMoveOutWindow() {
        setTitleToNarrow();
    }


    /**
     * 边界修正
     */
    private void edgeCorrect(FloatView view, int floatViewLeft, int floatViewTop, int floatViewBottom, int floatViewRight) {
        if (view.isSystemView()) return;
        int screenWidth = CommonUtils.getScreenWidthPixels(getActivity());
        int screenHeight = CommonUtils.getScreenHeightPixels(getActivity());
        int animX = floatViewLeft;
        int animY = floatViewTop;
        if (floatViewLeft < 0) {
            animX = 0;
        } else if (floatViewRight > screenWidth) {
            animX = screenWidth - view.getWidth();
        }
        if (floatViewTop < 0) {
            animY = 0;
        } else if (floatViewBottom > screenHeight) {
            animY = screenHeight - view.getHeight();
        }
        if (animX != floatViewLeft || animY != floatViewTop) {
            animAnchorFloatView(false, floatViewLeft, floatViewTop, animX, animY);
        }
    }

    private void animAnchorFloatView(final boolean isAnchorTitle, final int curX, final int curY, final int animX, final int animY) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                int newX = (int) ((animX - curX) * currentValue + curX);
                int newY = (int) ((animY - curY) * currentValue + curY);
                mFloatView.updatePosition(newX, newY);
                if (currentValue == 1.0f && isAnchorTitle) {
                    setTitleToBroad();
                }
            }
        });
        anim.start();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mFloatView.updatePosition(0, 0);
        } else {
            mFloatView.updatePosition(anchorX, anchorY);
            setTitleToBroad();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveDanmuMsg(DanmuMessage msg) {
        if (mVideoController != null) {
            String message = msg.extra.getMessage();
            if (message.equals(KeyBoardFragment.Q_ONE_MSG)) {
                message = "1";
            } else if (message.equals(KeyBoardFragment.Q_TWO_MSG)) {
                message = "2";
            }
            mVideoController.addDanmaku(message, true);
        }
    }

    @Override
    public void onDismiss() {
        if (mVideoController != null) {
            mVideoController.setSwapBtnState(StandardVideoController.SwapBtnState.OPEN_SCREEN);
        }
    }
}

