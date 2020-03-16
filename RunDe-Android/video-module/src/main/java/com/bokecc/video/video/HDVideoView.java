package com.bokecc.video.video;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.sdk.mobile.live.pojo.Marquee;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.msg.MarqueeAction;
import com.bokecc.video.route.OnVideoSwitchMsg;
import com.bokecc.video.widget.MarqueeView;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频播放视图
 */
public class HDVideoView extends RTCVideoView {

    private static final String TAG = "HDVideoView";
    private MarqueeView marqueeView;

    public HDVideoView(@NonNull Context context) {
        this(context, null);
    }

    public HDVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HDVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    public void removeMarquee(){
        if (marqueeView!=null){
            removeView(marqueeView);
        }
    }
    public void setMarquee(Activity activity,Marquee marquee){
        if (marqueeView!=null){
            removeView(marqueeView);
        }
        if (marquee != null && marquee.getAction() != null) {
            marqueeView = new MarqueeView(activity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            marqueeView.setVisibility(GONE);
            addView(marqueeView, params);
            marqueeView.setVisibility(VISIBLE);
            List<MarqueeAction> marqueeActions = new ArrayList<>();
            for (int x = 0; x < marquee.getAction().size(); x++) {
                com.bokecc.sdk.mobile.live.pojo.MarqueeAction marqueeAction1 = marquee.getAction().get(x);
                MarqueeAction marqueeAction = new MarqueeAction();
                marqueeAction.setIndex(x);
                marqueeAction.setDuration(marqueeAction1.getDuration()*1000);
                marqueeAction.setStartXpos((float) marqueeAction1.getStart().getXpos());
                marqueeAction.setStartYpos((float) marqueeAction1.getStart().getYpos());
                marqueeAction.setStartAlpha((float) marqueeAction1.getStart().getAlpha());
                marqueeAction.setEndXpos((float) marqueeAction1.getEnd().getXpos());
                marqueeAction.setEndYpos((float) marqueeAction1.getEnd().getYpos());
                marqueeAction.setEndAlpha((float) marqueeAction1.getEnd().getAlpha());
                marqueeActions.add(marqueeAction);
            }
            marqueeView.setLoop(marquee.getLoop());
            marqueeView.setMarqueeActions(marqueeActions);
            if (marquee.getType().equals("text")) {
                marqueeView.setTextContent(marquee.getText().getContent());
                marqueeView.setTextColor(marquee.getText().getColor());
                marqueeView.setTextFontSize((int) (marquee.getText().getFont_size() * activity.getResources().getDisplayMetrics().density + 0.5f));
                marqueeView.setType(1);
            } else {
                marqueeView.setMarqueeImage(activity, marquee.getImage().getImage_url(), marquee.getImage().getWidth(), marquee.getImage().getHeight());
                marqueeView.setType(2);
            }

            marqueeView.start();
        }
    }
    @Override
    public void videoStart() {
        if (checkNetwork()) return;
        if (mVideoController != null) {
            if (mCurrentPlayState == STATE_PLAYING || mCurrentPlayState == STATE_BUFFERED) {
                mVideoController.setPlayState(STATE_PLAYING);
            } else if (mCurrentPlayState != STATE_PAUSED) {
                mVideoController.setPlayState(STATE_PREPARING);
            } else {
                mVideoController.setPlayState(STATE_PLAYING);
            }
        }
        if(mCurrentPlayState == STATE_IDLE){
            HDApi.get().setLiveParams();
        }
        HDApi.get().start();
    }


    public void videoPause() {
        mCurrentPlayState = STATE_PAUSED;
        HDApi.get().pause();
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_PAUSED);
        }
    }

    public void videoDestroy() {
        HDApi.get().stop();
        mCurrentPlayState = STATE_IDLE;
    }


    /**
     * 视频发生切换
     */
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 10)
    public void onVideoSwitch(OnVideoSwitchMsg message) {
        if (message.getType() == OnVideoSwitchMsg.START) {
            init(getContext());
        }
    }

    @Override
    public void mobileStart() {
        videoStart();
    }
}
