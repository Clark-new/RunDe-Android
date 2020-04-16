package com.bokecc.video.ui.main.fragment;

import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.sdk.mobile.live.pojo.Marquee;
import com.bokecc.sdk.mobile.live.widget.DocView;
import com.bokecc.video.R;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.controller.OtherFunctionCallback;
import com.bokecc.video.controller.StandardVideoController;
import com.bokecc.video.msg.MarqueeAction;
import com.bokecc.video.msg.StreamState;
import com.bokecc.video.route.DanmuMessage;
import com.bokecc.video.route.NotificationPlayMsg;
import com.bokecc.video.route.NotificationReceiver;
import com.bokecc.video.route.OnVideoSwitchMsg;
import com.bokecc.video.ui.chat.KeyBoardFragment;
import com.bokecc.video.ui.main.activity.VideoCourseActivity;
import com.bokecc.video.utils.CommonUtils;
import com.bokecc.video.video.HDVideoView;
import com.bokecc.video.video.RTCController;
import com.bokecc.video.widget.DocWebView;
import com.bokecc.video.widget.FloatView;
import com.bokecc.video.widget.MarqueeView;
import com.bokecc.video.widget.MaxVideoContainer;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;
import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

public class VideoCourseFragment extends RTCControlFragment implements OtherFunctionCallback, OnFloatViewMoveListener, FloatView.OnDismissListener {
    private static final String TAG = "VideoCourseFragment";

    private static final String CHANNEL_ID = "HD_SDK_CHANNEL_ID";

    //TODO:这里更换当前课程的标题
    private static final String courseTitle = "药店大学直播课程标题";

    //由于系统的UI自动隐藏了状态栏，UI计算存在误差
    private int layoutOffsetHeight = 0;

    //视频播放控制器
    private StandardVideoController mVideoController;
    //存放视频或者文档视图
    private MaxVideoContainer mMaxContainer;
    //播放视频的view
    private HDVideoView mVideoView;
    private FloatView mFloatView;
    //文档视图
    private DocWebView mDocView;
    //存放floatView相对于屏幕的绝对坐标
    private int[] location = new int[2];

    private boolean isManualPause = false;
    private AudioManager mAudioManager;
    private boolean mFirstEnter = true;
    private RelativeLayout mNoStreamBg;
    private TextView tvNoStream;

    @Override
    protected void initData() {
        super.initData();
        mFirstEnter = true;
        mAudioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void initView() {
        super.initView();
        mMaxContainer = findViewById(R.id.id_max_container);
        mNoStreamBg = findViewById(R.id.no_stream_root);
        tvNoStream = findViewById(R.id.tv_no_stream);
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
                    mDocView = new DocWebView(getContext().getApplicationContext());
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
                    mDocView = new DocWebView(getContext().getApplicationContext());
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
        mAudioManager.requestAudioFocus(mAudioFocusChangeListener , AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        //必须在start之前先设置docview 给api
        if (HDApi.get().hasDoc()) {
            if (mDocView == null) {
                mDocView = new DocWebView(getContext().getApplicationContext());
                mDocView.changeBackgroundColor("#888888");
            }
            HDApi.get().setDocView(mDocView);
        }
        if (mFloatView != null && !mFloatView.hasAddToWindow()) {
            mFloatView.addToActivity(mRootView, anchorX, anchorY);
        }
        if(!isManualPause){
            mVideoView.videoStart();
            mVideoController.resume();
        }
        //设置跑马灯
        if (getActivity() instanceof VideoCourseActivity){
            VideoCourseActivity videoCourseActivity = (VideoCourseActivity) getActivity();
            mVideoView.setMarquee(getActivity(),videoCourseActivity.getMarquee());
            if (mDocView!=null)
                mDocView.setMarquee(getActivity(),videoCourseActivity.getMarquee());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //如果不开启后台播放，则将此处放开
//        mVideoView.videoPause();
        if (mFloatView != null) {
            mFloatView.removeFromWindow();
        }
        mVideoController.pause();
        if(mVideoView.isPlaying()){
            createNotification(courseTitle,R.drawable.icon_pause);
        }else{
            createNotification(courseTitle,R.drawable.icon_play);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mVideoView!=null)
        mVideoView.videoDestroy();
        if (mFloatView!=null)
            mFloatView.removeFromWindow();
        if (mVideoView!=null)
        mVideoView.release();
        mVideoController.release();
        clearNotification();
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
        if(mFloatView==null){
            return;
        }
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
            //设置跑马灯
            if (mVideoView!=null&&getActivity() instanceof VideoCourseActivity){
                VideoCourseActivity videoCourseActivity = (VideoCourseActivity) getActivity();
                if (mVideoView!=null)
                    mVideoView.setMarquee(getActivity(),videoCourseActivity.getMarquee());
                if (mDocView!=null)
                    mDocView.setMarquee(getActivity(),videoCourseActivity.getMarquee());
            }
        }
    }

    public HDVideoView getmVideoView() {
        return mVideoView;
    }

    public DocWebView getmDocView() {
        return mDocView;
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
        mNoStreamBg.setVisibility(View.GONE);
        if (message.getType() == OnVideoSwitchMsg.PREPARE) {

        } else if (message.getType() == OnVideoSwitchMsg.START) {
            switchCourseUi(HDApi.get().isSpecialCourse());
            updateController();
            startReloadCourse();
        }
    }

    /**
     * 不同的课程发生切换的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStreamStatus(StreamState streamState) {
        Log.e("###","onStreamStatus streamState ="+streamState.status);
        switch (streamState.status){
            case StreamState.STREAM_END:
                mNoStreamBg.setVisibility(VISIBLE);
                tvNoStream.setText("直播已结束");
                break;
            case StreamState.STREAM_START:
                mNoStreamBg.setVisibility(View.GONE);
                break;
            case StreamState.STREAM_NOT_START:
                mNoStreamBg.setVisibility(VISIBLE);
                tvNoStream.setText("直播未开始");
                break;
        }



    }

    /**
     * 接收到通知栏的暂停或者播放消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveNotificationPlayMsg(NotificationPlayMsg message) {
        if(message.code == NotificationPlayMsg.PLAY_PAUSE){
            if(mVideoView.isPlaying()){
                mVideoView.videoPause();
                mVideoController.pause();
                isManualPause = true;
                createNotification(courseTitle,R.drawable.icon_play);
            }else{
                mVideoView.videoStart();
                mVideoController.resume();
                isManualPause = false;
                createNotification(courseTitle,R.drawable.icon_pause);
            }
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
                mDocView = new DocWebView(getContext().getApplicationContext());
            }
            if (mFloatView == null) {
                mFloatView = new FloatView(getContext(), 0, 0);
                mFloatView.setOnDismissListener(this);
            }
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

        //TODO:
        if (mFloatView.hasAddToWindow()) {
            mFloatView.updatePosition(anchorX, anchorY);
        } else {
            mFloatView.addToActivity(mRootView, anchorX, anchorY);
        }
        mMaxContainer.removeChildView();
        mFloatView.removeChildView();
        createNotification("这是当前课程标题",R.drawable.icon_pause);
        //重新获取跑马灯
        if (mVideoView!=null)
            mVideoView.setMarquee(getActivity(),HDApi.get().getMarquee());
        if(mDocView!=null)
            mDocView.setMarquee(getActivity(),HDApi.get().getMarquee());
        if (getActivity() instanceof VideoCourseActivity){
            VideoCourseActivity videoCourseActivity = (VideoCourseActivity) getActivity();
            videoCourseActivity.setMarquee(HDApi.get().getMarquee());
        }

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
            if (mFloatView!=null)
                mFloatView.updatePosition(0, 0);
        } else {
            if (mFloatView!=null)
                mFloatView.updatePosition(anchorX, anchorY);
            setTitleToBroad();
        }
    }

    /**
     * 接收到聊天消息，添加到弹幕容器中
     */
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

    /**
     * 创建并更新通知
     */
    private void createNotification(String title,int playResId){
        if(getContext() == null) return;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(getContext(), VideoCourseActivity.class));
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.user_head_icon)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        RemoteViews remoteViews = new RemoteViews(getContext().getPackageName(), R.layout.item_notification);
        remoteViews.setTextViewText(R.id.id_content, title);
        remoteViews.setImageViewResource(R.id.id_play_btn,playResId);
        if(HDApi.get().getApiType() == HDApi.ApiType.LIVE){
            remoteViews.setViewVisibility(R.id.id_last_one,View.GONE);
            remoteViews.setViewVisibility(R.id.id_next_one,View.GONE);
        }else{
            remoteViews.setViewVisibility(R.id.id_last_one, VISIBLE);
            remoteViews.setViewVisibility(R.id.id_next_one, VISIBLE);
        }


        //上一个
        Intent lastAction = new Intent(getContext(), NotificationReceiver.class);
        lastAction.setAction(NotificationReceiver.ACTION_LAST);
        PendingIntent pendingLastAction = PendingIntent.getBroadcast(getContext(), -1,
                lastAction, PendingIntent.FLAG_UPDATE_CURRENT);

        //暂停播放
        Intent pauseAction = new Intent(getContext(), NotificationReceiver.class);
        pauseAction.setAction(NotificationReceiver.ACTION_PLAY_PAUSE);
        PendingIntent pendingPauseAction = PendingIntent.getBroadcast(getContext(), -1,
                pauseAction, PendingIntent.FLAG_UPDATE_CURRENT);

        //下一个
        Intent nextAction = new Intent(getContext(), NotificationReceiver.class);
        nextAction.setAction(NotificationReceiver.ACTION_NEXT);
        PendingIntent pendingNextAction = PendingIntent.getBroadcast(getContext(), -1,
                nextAction, PendingIntent.FLAG_UPDATE_CURRENT);

        //结束播放
        Intent destroyAction = new Intent(getContext(), NotificationReceiver.class);
        destroyAction.setAction(NotificationReceiver.ACTION_DESTROY);
        PendingIntent pendingDestroyAction = PendingIntent.getBroadcast(getContext(), -1,
                destroyAction, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.id_last_one, pendingLastAction);
        remoteViews.setOnClickPendingIntent(R.id.id_play_btn, pendingPauseAction);
        remoteViews.setOnClickPendingIntent(R.id.id_next_one, pendingNextAction);
        remoteViews.setOnClickPendingIntent(R.id.id_close_play, pendingDestroyAction);

        builder.setCustomContentView(remoteViews);
        createNotificationChannel();
        Notification build = builder.build();
        build.flags = Notification.FLAG_NO_CLEAR;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(1, build);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //TODO：
            CharSequence name = "药店大学直播";
            String description = "description";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,  NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            //锁屏显示通知
            channel.setLockscreenVisibility(VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void clearNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.cancel(1);
    }

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
           ELog.e("Sivin","focusChange:"+focusChange);
            switch (focusChange){
               case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
               case AudioManager.AUDIOFOCUS_LOSS:
                   if(mFirstEnter) {
                       mFirstEnter = false;
                       return;
                   }
                   mVideoView.videoPause();
                   mVideoController.pause();
                   isManualPause = true;
                   createNotification(courseTitle,R.drawable.icon_play);

           }
        }
    };

}

