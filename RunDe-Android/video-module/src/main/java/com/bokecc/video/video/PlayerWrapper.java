package com.bokecc.video.video;

import android.content.Context;
import android.view.Surface;

import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.DWLivePlayer;
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay;
import com.bokecc.sdk.mobile.live.replay.DWReplayPlayer;
import com.bokecc.video.api.HDApi;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class PlayerWrapper {
    private Surface mSurface = null;
    private int bufferPercent = 0;

    public void release() {
        if(livePlayer != null){
            livePlayer.release();
            livePlayer = null;
        }
        if(replayPlayer != null){
            replayPlayer.release();
            replayPlayer = null;
        }
    }

    public enum Type {
        LIVE, REPLAY, LOCAL
    }

    private Type mCurrentType = null;
    private DWLivePlayer livePlayer;
    private DWReplayPlayer replayPlayer;

    private PlayerCallback playerCallback;

    public void setPlayCallback(PlayerCallback callback) {
        playerCallback = callback;
    }

    /**
     * 初始化播放器
     */
    public void init(Context ctx) {
        mCurrentType = HDApi.get().getApiType() == HDApi.ApiType.LIVE ? Type.LIVE : Type.REPLAY;
        if (mCurrentType == Type.LIVE) {
            if (livePlayer == null) {
                livePlayer = new DWLivePlayer(ctx.getApplicationContext());
                livePlayer.setOnPreparedListener(livePreparedListener);
                livePlayer.setOnInfoListener(LiveInfoListener);
                livePlayer.setOnVideoSizeChangedListener(liveVideoSizeChangedListener);
            }
            DWLive.getInstance().setDWLivePlayer(livePlayer);
        } else {
            if (replayPlayer == null) {
                replayPlayer = new DWReplayPlayer(ctx.getApplicationContext());
                replayPlayer.setOnPreparedListener(replayPrepareListener);
                replayPlayer.setOnInfoListener(replayInfoListener);
                replayPlayer.setOnBufferingUpdateListener(replayBufferUpdateListener);
                replayPlayer.setOnVideoSizeChangedListener(replayVideoSizeChangedListener);
                replayPlayer.setOnCompletionListener(replayCompletionListener);
                replayPlayer.setOnErrorListener(replayError);
            }
            bufferPercent = 0;
            DWLiveReplay.getInstance().setReplayPlayer(replayPlayer);
        }
    }

    /**
     * 设置surface
     */
    public void setSurface(Surface surface) {
        mSurface = surface;
        if (mCurrentType == Type.LIVE) {
            livePlayer.setSurface(surface);
        } else {
            replayPlayer.updateSurface(surface);
        }
    }


    public void start() {
        if (mCurrentType == Type.LIVE) {
            livePlayer.start();
        } else {
            replayPlayer.start();
        }
    }

    public void pause() {
        if (mCurrentType == Type.LIVE) {
            livePlayer.pause();
        } else {
            //回放的暂停必须调用这个api
            DWLiveReplay.getInstance().pause();
        }
    }


    public void stop() {
        if (mCurrentType == Type.LIVE) {
            livePlayer.stop();
        } else {
            replayPlayer.stop();
        }
    }


    public long getDuration() {
        if (mCurrentType == Type.LIVE) {
            return 0;
        } else {
            return replayPlayer.getDuration();
        }
    }

    public long getCurrentPosition() {
        if (mCurrentType == Type.LIVE) {
            return 0;
        } else {
            return replayPlayer.getCurrentPosition();
        }
    }


    public void seeTo(long pos) {
        if (mCurrentType == Type.REPLAY) {
            replayPlayer.seekTo(pos);
        }
    }

    public boolean isPlaying() {
        if (mCurrentType == Type.LIVE) {
            return livePlayer.isPlaying();
        } else {
            return replayPlayer.isPlaying();
        }
    }


    /**
     * 该API占时无效
     */
    public int getBufferedPercentage() {
        if (mCurrentType == Type.LIVE) {
            return 0;
        } else {
            return bufferPercent;
        }
    }


    public void setSpeed(float speed) {
        if (mCurrentType == Type.REPLAY) {
            replayPlayer.setSpeed(speed);
        }
    }

    /**
     * 直播准备监听器
     */
    IMediaPlayer.OnPreparedListener livePreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            if (mCurrentType != Type.LIVE) return;
            if (livePlayer != null) {
                livePlayer.setSurface(mSurface);
                livePlayer.start();
            }
        }
    };


    IMediaPlayer.OnInfoListener LiveInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (mCurrentType != Type.LIVE) return false;
            switch (what) {
                // 缓冲开始
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    if (playerCallback != null) {
                        playerCallback.onBufferStart();
                    }
                    break;
                // 缓冲结束
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    if (playerCallback != null) {
                        playerCallback.onBufferEnd();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    IMediaPlayer.OnVideoSizeChangedListener liveVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            if (mCurrentType != Type.LIVE) return;
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0 && playerCallback != null) {
                playerCallback.onVideoSizeChanged(videoWidth, videoHeight);
            }
        }
    };


    /**
     * -------------------------------在线回放相关---------------------------------------------------------------------
     */

    IMediaPlayer.OnPreparedListener replayPrepareListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            if (mCurrentType != Type.REPLAY) return;
            if (replayPlayer != null) {
                replayPlayer.updateSurface(mSurface);
            }
            if (playerCallback != null) {
                playerCallback.onPrepared();
            }
        }
    };


    private IMediaPlayer.OnInfoListener replayInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (mCurrentType != Type.REPLAY) return false;
            switch (what) {
                // 缓冲开始
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    if (playerCallback != null) {
                        playerCallback.onBufferStart();
                    }
                    break;
                // 缓冲结束
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    if (playerCallback != null) {
                        playerCallback.onBufferEnd();
                    }
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    if (playerCallback != null) {
                        playerCallback.onStartRender();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    };


    private IMediaPlayer.OnBufferingUpdateListener replayBufferUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            if (mCurrentType != Type.REPLAY) return;
            bufferPercent = percent;
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener replayVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            if (mCurrentType != Type.REPLAY) return;
            if (width != 0 && height != 0 && playerCallback != null) {
                playerCallback.onVideoSizeChanged(width, height);
            }
        }
    };

    private IMediaPlayer.OnCompletionListener replayCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            if (mCurrentType != Type.REPLAY) return;

        }
    };

    private IMediaPlayer.OnErrorListener replayError = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if (mCurrentType != Type.REPLAY) return false;
            return false;
        }
    };


}
