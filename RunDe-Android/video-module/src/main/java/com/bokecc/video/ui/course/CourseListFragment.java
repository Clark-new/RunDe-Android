package com.bokecc.video.ui.course;

import android.view.View;
import android.widget.Button;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.video.R;
import com.bokecc.video.TestConstanst;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.api.VideoInfo;
import com.bokecc.video.route.NotificationPlayMsg;
import com.bokecc.video.ui.base.BaseFragment;

public class CourseListFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "CourseListFragment";
    private Button mLiveBtn1;
    private Button mLiveBtn2;
    private Button mReplayBtn1;
    private Button mReplayBtn2;

    private VideoInfo mVideoInfo;

    /**
     * 通知栏点击上一个或者下一个时通知响应事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveNotificationPlayMsg(NotificationPlayMsg message) {
        if(message.code == NotificationPlayMsg.LAST){
            //TODO:切换到上一个课程
            ELog.i(TAG,"switch last course");
            mVideoInfo = new VideoInfo();
            mVideoInfo.setRoomId("D7123DC27A274C9C9C33DC5901307461");
            mVideoInfo.setUserId("358B27E7B04F3B02");
            mVideoInfo.setLiveId("9C211CAFB37E1E85");
            mVideoInfo.setRecordId("94C3C69E37642E1F");
            mVideoInfo.setViewerName("replay2");
            HDApi.get().switchVideo(mVideoInfo, HDApi.ApiType.REPLAY);
        }else if(message.code == NotificationPlayMsg.NEXT){
            ELog.i(TAG,"switch next course");
            mVideoInfo = new VideoInfo();
            mVideoInfo.setRoomId("78F140798BF0F0609C33DC5901307461");
            mVideoInfo.setUserId("27A28C5ABFA53BC1");
            mVideoInfo.setLiveId("03FBEA220C4A741D");
            mVideoInfo.setRecordId("8B04FD894E08A94E");
            mVideoInfo.setViewerToken("123");
            mVideoInfo.setViewerName("replay2");
            HDApi.get().switchVideo(mVideoInfo, HDApi.ApiType.REPLAY);
        }
    }

    @Override
    public int getRootResource() {
        return R.layout.layout_menu_list_fragment;
    }

    @Override
    protected void initView() {
        super.initView();
        if(!CCEventBus.getDefault().isRegistered(this)){
            CCEventBus.getDefault().register(this);
        }
        mLiveBtn1 = findViewById(R.id.id_live_1);
        mReplayBtn1 = findViewById(R.id.id_replay_1);
        mReplayBtn2 = findViewById(R.id.id_replay_2);

        mLiveBtn1.setOnClickListener(this);
        mReplayBtn1.setOnClickListener(this);
        mReplayBtn2.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(CCEventBus.getDefault().isRegistered(this)){
            CCEventBus.getDefault().unregister(this);
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_live_1) {

            mVideoInfo = new VideoInfo();
            mVideoInfo.setRoomId(TestConstanst.ROOMID);
            mVideoInfo.setUserId(TestConstanst.USERID);
            mVideoInfo.setViewerName(TestConstanst.USERNAME);
            mVideoInfo.setViewerToken(TestConstanst.USERTOKEN);
            HDApi.get().switchVideo(mVideoInfo, HDApi.ApiType.LIVE);

        } else if (id == R.id.id_replay_1) {
            mVideoInfo = new VideoInfo();
            mVideoInfo.setRoomId("D7123DC27A274C9C9C33DC5901307461");
            mVideoInfo.setUserId("358B27E7B04F3B02");
            mVideoInfo.setLiveId("9C211CAFB37E1E85");
            mVideoInfo.setRecordId("94C3C69E37642E1F");
            mVideoInfo.setViewerName("replay2");
            HDApi.get().switchVideo(mVideoInfo, HDApi.ApiType.REPLAY);
        } else if (id == R.id.id_replay_2) {
            mVideoInfo = new VideoInfo();
            mVideoInfo.setRoomId("78F140798BF0F0609C33DC5901307461");
            mVideoInfo.setUserId("27A28C5ABFA53BC1");
            mVideoInfo.setLiveId("03FBEA220C4A741D");
            mVideoInfo.setRecordId("8B04FD894E08A94E");
            mVideoInfo.setViewerToken("123");
            mVideoInfo.setViewerName("replay2");
            HDApi.get().switchVideo(mVideoInfo, HDApi.ApiType.REPLAY);
        }
    }





}
