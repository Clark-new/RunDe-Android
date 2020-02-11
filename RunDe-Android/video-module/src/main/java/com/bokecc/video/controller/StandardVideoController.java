package com.bokecc.video.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bokecc.video.R;
import com.bokecc.video.route.RTCMessage;
import com.bokecc.video.utils.CommonUtils;
import com.bokecc.video.video.HDVideoView;

/**
 * 直播/点播控制器
 */
public class StandardVideoController extends GestureVideoController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    protected TextView mTotalTime, mCurrTime;
    protected ImageView mFullScreenButton;
    protected View mBottomContainer;
    protected SeekBar mVideoSeekBar;
    protected ImageView mBackButton;
    private boolean mIsLive;
    private boolean mIsDragging;

    private ProgressBar mBottomProgress;
    private ImageView mStartPlayBtnB;
    private ImageView mStartPlayBtnS;
    private ProgressBar mLoadingProgress;

    private LinearLayout mOtherControlLayout;
    private ImageView mSwapBtn;
    private ImageView mShareBtn;
    //连麦按钮
    private ImageView mRtcBtn;

    private Animation mShowAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in);
    private Animation mHideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out);

    public StandardVideoController(@NonNull Context context) {
        this(context, null);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.layout_standard_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        mBottomContainer = mControllerView.findViewById(R.id.id_bottom_layout);
        mBackButton = mControllerView.findViewById(R.id.id_back);
        mBackButton.setOnClickListener(this);
        mFullScreenButton = mControllerView.findViewById(R.id.id_full_screen);
        mFullScreenButton.setOnClickListener(this);

        mVideoSeekBar = mControllerView.findViewById(R.id.id_seekBar);
        mVideoSeekBar.setOnSeekBarChangeListener(this);
        mTotalTime = mControllerView.findViewById(R.id.id_total_time);
        mCurrTime = mControllerView.findViewById(R.id.id_curr_time);

        mStartPlayBtnB = mControllerView.findViewById(R.id.id_play_b);
        mStartPlayBtnB.setOnClickListener(this);
        mStartPlayBtnS = mControllerView.findViewById(R.id.id_play_s);
        mLoadingProgress = mControllerView.findViewById(R.id.id_loading);
        mBottomProgress = mControllerView.findViewById(R.id.id_bottom_progress);

        mOtherControlLayout = mControllerView.findViewById(R.id.id_other_control);
        mSwapBtn = mControllerView.findViewById(R.id.id_switch_screen);
        mSwapBtn.setOnClickListener(this);
        mShareBtn = mControllerView.findViewById(R.id.id_share);
        mShareBtn.setOnClickListener(this);
        mRtcBtn = mControllerView.findViewById(R.id.id_apply_rtc);
        mRtcBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_full_screen) {
            doStartStopFullScreen();
        } else if (id == R.id.id_play_b || id == R.id.id_play_s) {
            doPauseResume();
        } else if (id == R.id.id_back) {
            doBack();
        } else if (id == R.id.id_switch_screen) {
            if (otherFunctionCallback != null) {
                otherFunctionCallback.onSwapBtnClick((ImageView) v);
            }
        } else if (id == R.id.id_share) {
            if (otherFunctionCallback != null) {
                otherFunctionCallback.onSharedBtnClick();
            }
        } else if (id == R.id.id_apply_rtc) {
            onRtcBtnClick();
        }
    }


    public enum SwapBtnState{
        INVISIBLE,
        SWAP_STATE,
        OPEN_SCREEN,
    }

    /**
     * 设置双屏状态栏的状态
     */
    public void setSwapBtnState(SwapBtnState state){
        if(state == SwapBtnState.INVISIBLE){
            mSwapBtn.setVisibility(INVISIBLE);
        }else{
            mSwapBtn.setVisibility(VISIBLE);
        }
        if(state == SwapBtnState.SWAP_STATE){
            mSwapBtn.setImageResource(R.drawable.live_switchscreen);
        }else if(state == SwapBtnState.OPEN_SCREEN){
            mSwapBtn.setImageResource(R.drawable.live_openscreen);
        }
    }


    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case HDVideoView.STATE_IDLE:
                show();
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoSeekBar.setProgress(0);
                mVideoSeekBar.setSecondaryProgress(0);
                mBottomProgress.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.GONE);
                mStartPlayBtnB.setVisibility(View.GONE);
                break;
            case HDVideoView.STATE_PLAYING:
                post(mShowProgress);
                mLoadingProgress.setVisibility(View.GONE);
                mStartPlayBtnS.setSelected(true);
                mStartPlayBtnB.setSelected(true);
                break;
            case HDVideoView.STATE_PAUSED:
                mStartPlayBtnB.setSelected(false);
                mStartPlayBtnS.setSelected(false);
                break;
            case HDVideoView.STATE_PREPARING:
                mStartPlayBtnS.setVisibility(View.GONE);
                mStartPlayBtnB.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.VISIBLE);
                break;
            case HDVideoView.STATE_PREPARED:
                if (mShowing) {
                    mStartPlayBtnB.setVisibility(View.VISIBLE);
                }
                if (!mIsLive && !mShowing) {
                    mVideoSeekBar.setVisibility(View.VISIBLE);
                }
                mBottomProgress.setVisibility(GONE);
                mLoadingProgress.setVisibility(GONE);
                mStartPlayBtnS.setVisibility(View.VISIBLE);
                break;
            case HDVideoView.STATE_ERROR:
                mStartPlayBtnS.setVisibility(View.GONE);
                mStartPlayBtnB.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.GONE);
                mBottomProgress.setVisibility(View.GONE);
                break;
            case HDVideoView.STATE_BUFFERING:
                mLoadingProgress.setVisibility(View.VISIBLE);
                mStartPlayBtnB.setVisibility(View.GONE);
                mStartPlayBtnB.setSelected(mPlayer.isPlaying());
                mStartPlayBtnS.setSelected(mPlayer.isPlaying());
                break;
            case HDVideoView.STATE_BUFFERED:
                mLoadingProgress.setVisibility(View.GONE);
                mStartPlayBtnS.setSelected(true);
                mStartPlayBtnB.setSelected(true);
                post(mShowProgress);
                break;
            case HDVideoView.STATE_PLAYBACK_COMPLETED:
                hide();
                removeCallbacks(mShowProgress);
                mStartPlayBtnS.setVisibility(View.GONE);
                mStartPlayBtnB.setVisibility(View.GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                break;
        }
    }

    @Override
    protected void onIntoOrStopFullScreen(boolean isFullScreen) {
        if (isFullScreen) {
            mFullScreenButton.setImageResource(R.drawable.live_small_screen);
        } else {
            mFullScreenButton.setImageResource(R.drawable.live_full_screen);
        }
    }

    /**
     * 设置是否为直播视频
     */
    @Override
    public void setLive(boolean isLive) {
        super.setLive(isLive);
        mIsLive = isLive;
        if (isLive) {
            mBottomProgress.setVisibility(View.GONE);
            mVideoSeekBar.setVisibility(View.INVISIBLE);
            mTotalTime.setVisibility(View.INVISIBLE);
            mCurrTime.setVisibility(View.INVISIBLE);
            mRtcBtn.setVisibility(View.GONE);
        } else {
            mBottomProgress.setVisibility(View.VISIBLE);
            mVideoSeekBar.setVisibility(View.VISIBLE);
            mTotalTime.setVisibility(View.VISIBLE);
            mCurrTime.setVisibility(View.VISIBLE);
            mRtcBtn.setVisibility(View.GONE);
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mPlayer.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / mVideoSeekBar.getMax();
        mPlayer.seekTo((int) newPosition);
        mIsDragging = false;
        post(mShowProgress);
        show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        long duration = mPlayer.getDuration();
        long newPosition = (duration * progress) / mVideoSeekBar.getMax();
        if (mCurrTime != null) {
            mCurrTime.setText(stringForTime((int) newPosition));
        }
    }

    @Override
    public void hide() {
        if (mShowing) {
            mBackButton.setVisibility(View.GONE);
            mOtherControlLayout.setVisibility(GONE);
            mBottomContainer.setVisibility(View.GONE);
            mStartPlayBtnB.setVisibility(View.GONE);
            mBackButton.startAnimation(mHideAnim);
            mOtherControlLayout.startAnimation(mHideAnim);
            mBottomContainer.startAnimation(mHideAnim);
            mStartPlayBtnB.startAnimation(mHideAnim);
            if (!mIsLive) {
                mBottomProgress.setVisibility(View.VISIBLE);
                mBottomProgress.startAnimation(mShowAnim);
            }
        }
        mShowing = false;
    }


    private void show(int timeout) {
        if (!mShowing) {
            mBackButton.setVisibility(View.VISIBLE);
            mOtherControlLayout.setVisibility(VISIBLE);
            mBottomContainer.setVisibility(View.VISIBLE);
            mStartPlayBtnB.setVisibility(View.VISIBLE);
            mBackButton.startAnimation(mShowAnim);
            mOtherControlLayout.startAnimation(mShowAnim);
            mBottomContainer.startAnimation(mShowAnim);
            mStartPlayBtnB.startAnimation(mShowAnim);
            if (!mIsLive) { //显示底部播放进度
                mBottomProgress.setVisibility(View.GONE);
                mBottomProgress.startAnimation(mHideAnim);
            }
        }
        mShowing = true;
        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }

    @Override
    public void show() {
        show(mDefaultTimeout);
    }

    @Override
    protected int setProgress() {
        if (mPlayer == null || mIsDragging) {
            return 0;
        }
        if (mIsLive) return 0;
        int position = (int) mPlayer.getCurrentPosition();
        int duration = (int) mPlayer.getDuration();
        if (mVideoSeekBar != null) {
            if (duration > 0) {
                mVideoSeekBar.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mVideoSeekBar.getMax());
                mVideoSeekBar.setProgress(pos);
                mBottomProgress.setProgress(pos);
            } else {
                mVideoSeekBar.setEnabled(false);
            }
            int percent = mPlayer.getBufferedPercentage();
            if (percent >= 95) { //解决缓冲进度不能100%问题
                mVideoSeekBar.setSecondaryProgress(mVideoSeekBar.getMax());
                mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
            } else {
                mVideoSeekBar.setSecondaryProgress(percent * 10);
                mBottomProgress.setSecondaryProgress(percent * 10);
            }
        }
        if (mTotalTime != null)
            mTotalTime.setText(String.format(" / %s", stringForTime(duration)));
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime(position));
        return position;
    }


    @Override
    public void setRtcState(int rtcState) {
        super.setRtcState(rtcState);
        switch (rtcState) {
            case RTCMessage.RTC_STATE_DISABLE:
                mRtcBtn.setVisibility(View.GONE);
                break;
            case RTCMessage.RTC_STATE_ENABLE:
                mRtcBtn.setVisibility(View.VISIBLE);
                mRtcBtn.setImageResource(R.drawable.live_put_up_hands);
                break;
            case RTCMessage.RTC_STATE_APPLYING:
                mRtcBtn.setImageResource(R.drawable.live_put_up_hands_on);
                break;
            case RTCMessage.RTC_STATE_CONNECTED:
                mRtcBtn.setImageResource(R.drawable.live_hang_up);
                break;
            case RTCMessage.RTC_STATE_DISCONNECTED:
                mRtcBtn.setImageResource(R.drawable.live_put_up_hands);
                break;
        }
    }


    private void onRtcBtnClick() {
        if (rtcControlCallback == null) return;
        if (mCurrentRTCState == RTCMessage.RTC_STATE_ENABLE || mCurrentRTCState == RTCMessage.RTC_STATE_DISCONNECTED) {
            rtcControlCallback.onRtcApplyBtnClick();
        } else if (mCurrentRTCState == RTCMessage.RTC_STATE_APPLYING) {
            rtcControlCallback.onHangUpRtcApply();
        } else if (mCurrentRTCState == RTCMessage.RTC_STATE_CONNECTED) {
            rtcControlCallback.onRtcHangUpBtnClick();
        }
    }


    @Override
    public boolean onBackPressed() {
        Activity activity = CommonUtils.scanForActivity(getContext());
        if (activity == null) return super.onBackPressed();
        if (mIsFullScreen) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            stopFullScreen();
            onIntoOrStopFullScreen(false);
            return true;
        }
        return super.onBackPressed();
    }


}
