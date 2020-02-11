package com.bokecc.video.api;

import com.bokecc.sdk.mobile.live.BaseCallback;
import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.DWLiveEngine;
import com.bokecc.sdk.mobile.live.DWLiveLoginListener;
import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.pojo.LiveInfo;
import com.bokecc.sdk.mobile.live.pojo.LoginInfo;
import com.bokecc.sdk.mobile.live.pojo.PublishInfo;
import com.bokecc.sdk.mobile.live.pojo.PunchCommitRespone;
import com.bokecc.sdk.mobile.live.pojo.RoomInfo;
import com.bokecc.sdk.mobile.live.pojo.TemplateInfo;
import com.bokecc.sdk.mobile.live.pojo.Viewer;
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay;
import com.bokecc.sdk.mobile.live.replay.DWLiveReplayLoginListener;
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayLoginInfo;
import com.bokecc.sdk.mobile.live.rtc.CCRTCRender;
import com.bokecc.sdk.mobile.live.widget.DocView;
import com.bokecc.video.msg.AnnounceMsg;
import com.bokecc.video.msg.BannedChatMsg;
import com.bokecc.video.route.OnVideoSwitchMsg;

import org.webrtc.EglBase;
import org.webrtc.SurfaceViewRenderer;

public class HDApi {
    private static final String TAG = "HDApi";


    public enum ApiType {
        LIVE, REPLAY
    }

    private static HDApi mApi = null;
    //当前使用的api类型
    private ApiType mApiType = null;

    //是否是专题课
    private boolean isSpecialCourse = false;

    private TemplateInfo templateInfo;

    public boolean isBanned = false;

    public static HDApi get() {
        if (mApi == null) {
            synchronized (HDApi.class) {
                if (mApi == null) {
                    mApi = new HDApi();
                }
            }
        }
        return mApi;
    }


    public void setCourseType(boolean isSpecialCourse) {
        this.isSpecialCourse = isSpecialCourse;
    }

    /**
     * 是否是专题课
     */
    public boolean isSpecialCourse() {
        return isSpecialCourse;
    }


    /**
     * 设置登录直播
     */
    public void setLiveLoginListener(LoginInfo loginInfo, final DWLiveLoginListener listener) {
        mApiType = ApiType.LIVE;
        DWLive.getInstance().setDWLiveLoginParams(new DWLiveLoginListener() {
            @Override
            public void onLogin(TemplateInfo template, Viewer viewer, RoomInfo roomInfo, PublishInfo publishInfo) {
                templateInfo = template;
                if (listener != null) {
                    listener.onLogin(template, viewer, roomInfo, publishInfo);
                }

                //发送视频切换的消息
                CCEventBus.getDefault().post(new OnVideoSwitchMsg(OnVideoSwitchMsg.START, "start_switch_video"));
            }

            @Override
            public void onException(DWLiveException e) {
                if (listener != null) {
                    listener.onException(e);
                }
            }
        }, loginInfo);
    }

    /**
     * 设置登录回放的参数
     */
    public void setReplayLoginListener(ReplayLoginInfo replayLoginInfo, final DWLiveReplayLoginListener listener) {
        mApiType = ApiType.REPLAY;
        DWLiveReplay.getInstance().setLoginParams(new DWLiveReplayLoginListener() {
            @Override
            public void onLogin(TemplateInfo template) {
                templateInfo = template;
                if (listener != null) {
                    listener.onLogin(templateInfo);
                }
                //发送视频切换的消息
                CCEventBus.getDefault().post(new OnVideoSwitchMsg(OnVideoSwitchMsg.START, ""));
            }

            @Override
            public void onException(DWLiveException e) {
                if (listener != null) {
                    listener.onException(e);
                }
            }
        }, replayLoginInfo);
    }

    public ApiType getApiType() {
        return mApiType;
    }

    public boolean hasDoc() {
        if (templateInfo != null) {
            return templateInfo.hasDoc();
        }
        return false;
    }

    /**
     * 开始登录
     */
    public void login() {
        if (mApiType == ApiType.LIVE) {
            DWLive.getInstance().startLogin();
        } else if (mApiType == ApiType.REPLAY) {
            DWLiveReplay.getInstance().startLogin();
        }
    }


    public String getSelfId() {
        if (mApiType == ApiType.LIVE) {
            return DWLive.getInstance().getViewer().getId();
        } else {
            return DWLiveReplay.getInstance().getViewer().getId();
        }
    }


    public LiveInfo getLiveInfo() {
        if (mApiType == ApiType.LIVE) {
            return DWLive.getInstance().getLiveInfo();
        } else {
            return null;
        }
    }

    /**
     * 发送礼物
     */
    public void sendGiftMsg(String content, String url, int num) {
        sendPublicChatMsg(content + "[cem_" + url + "]x" + num);
    }

    public void sendRewardMsg(String content, String url, int num) {
        sendPublicChatMsg(content + "[cem_" + url + "]¥" + num);
    }


    /**
     * 发送聊天
     */
    public void sendPublicChatMsg(String msg) {
        if (mApiType == ApiType.LIVE) {
            if ("".equals(msg)) return;
            if (isBanned) {
                CCEventBus.getDefault().post(new BannedChatMsg(BannedChatMsg.ALL_BANNED));
            }
            DWLive.getInstance().sendPublicChatMsg(msg);
        }
    }

    /**
     * 私聊
     */
    public void sendPrivateChatMsg(String toUserId, String msg) {
        if (mApiType == ApiType.LIVE) {
            DWLive.getInstance().sendPrivateChatMsg(toUserId, msg);
        }
    }


    public void setDocView(DocView docView) {
        if (mApiType == ApiType.LIVE) {
            DWLive.getInstance().setDWLivePlayDocView(docView);
        } else if (mApiType == ApiType.REPLAY) {
            DWLiveReplay.getInstance().setReplayDocView(docView);
        }
    }


    public void setLiveParams() {
        if (mApiType != ApiType.LIVE) return;
        DWLive.getInstance().setDWLivePlayParams(new LiveListener(), DWLiveEngine.getInstance().getContext());
        DWLive.getInstance().setPunchCallback(new PunchStatusCallback());
        String announcement = DWLive.getInstance().getAnnouncement();
        if (announcement != null) {
            CCEventBus.getDefault().post(new AnnounceMsg(announcement));
        }
    }

    public void start() {
        if (mApiType == ApiType.LIVE) {
            DWLive.getInstance().start(null);
        } else if (mApiType == ApiType.REPLAY) {
            DWLiveReplay.getInstance().setReplayParams(new ReplayListener(), DWLiveEngine.getInstance().getContext());
            DWLiveReplay.getInstance().start();
        }
    }

    public void pause() {
        if (mApiType == ApiType.LIVE) {
        } else if (mApiType == ApiType.REPLAY) {
            DWLiveReplay.getInstance().pause();
        }
    }

    public void stop() {
        if (mApiType == ApiType.LIVE) {
            DWLive.getInstance().stop();
        } else if (mApiType == ApiType.REPLAY) {
            DWLiveReplay.getInstance().stop();
        }
    }

    public void release() {
        DWLive.getInstance().onDestroy();
        DWLiveReplay.getInstance().onDestroy();
    }


    /**
     * 设置连麦视图
     */
    public void setRtcRender(SurfaceViewRenderer localRender, CCRTCRender remoteRender) {
        EglBase rootEglBase = EglBase.create();
        remoteRender.init(rootEglBase.getEglBaseContext(), null);
        //不管是直播还是回放，先注册RTCRender
        DWLive.getInstance().setRtcClientParameters(new RTCListener(), localRender, remoteRender);
    }

    /**
     * 申请连麦
     */
    public void applyRtc(boolean isVideoRtc) {
        if (mApiType == ApiType.LIVE) {
            if (isVideoRtc) {
                DWLive.getInstance().startRtcConnect();
            } else {
                DWLive.getInstance().startVoiceRTCConnect();
            }
        }
    }

    /**
     * 挂断连麦
     */
    public void handUpRtc() {
        if (mApiType == ApiType.LIVE) {
            DWLive.getInstance().disConnectSpeak();
        }
    }

    /**
     * 挂断连麦申请
     */
    public void handUpApplyRtc() {
        if (mApiType == ApiType.LIVE) {
            DWLive.getInstance().disConnectApplySpeak();
        }
    }

    /**
     * 重新连接视频
     */
    public void reconnectVideo() {
        if (mApiType == ApiType.LIVE) {
            try {
                DWLive.getInstance().restartVideo(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 切换前调用
     */
    private void prepareSwitchVideo(String extra) {
        if (mApiType == ApiType.LIVE) {
            DWLive.getInstance().stop();
        } else if (mApiType == ApiType.REPLAY) {
            DWLiveReplay.getInstance().stop();
        }
        CCEventBus.getDefault().post(new OnVideoSwitchMsg(OnVideoSwitchMsg.PREPARE, extra));
    }

    private long lastSwitchTime = 0;


    /**
     * 视频信息切换
     */
    public void switchVideo(VideoInfo videoInfo, ApiType type) {
        if (System.currentTimeMillis() - lastSwitchTime < 1000) {
            lastSwitchTime = System.currentTimeMillis();
            return;
        }
        lastSwitchTime = System.currentTimeMillis();
        prepareSwitchVideo(videoInfo.getExtra());
        if (type == ApiType.LIVE) {
            LoginInfo info = new LoginInfo();
            info.setRoomId(videoInfo.getRoomId());
            info.setUserId(videoInfo.getUserId());
            info.setViewerToken(videoInfo.getViewerToken());
            info.setViewerName(videoInfo.getViewerName());
            info.setGroupId(videoInfo.getGroupId());
            setLiveLoginListener(info, null);
        } else {
            ReplayLoginInfo info = new ReplayLoginInfo();
            info.setRoomId(videoInfo.getRoomId());
            info.setUserId(videoInfo.getUserId());
            info.setLiveId(videoInfo.getLiveId());
            info.setRecordId(videoInfo.getRecordId());
            info.setViewerToken(videoInfo.getViewerToken());
            info.setViewerName(videoInfo.getViewerName());
            info.setGroupId(videoInfo.getGroupId());
            setReplayLoginListener(info, null);
        }
        //开始登录
        login();
//        this.isSpecialCourse = isSpecialCourse;
    }

    /**
     * 提交签到打卡
     */
    public void punch(String punchId, BaseCallback<PunchCommitRespone> callback) {
        if (mApiType == ApiType.LIVE) {
            DWLive.getInstance().commitPunch(punchId, callback);
        }
    }

}
