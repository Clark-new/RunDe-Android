package com.bokecc.video.api;

import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.replay.DWLiveReplayListener;
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayBroadCastMsg;
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayChatMsg;
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayPageInfo;
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayQAMsg;

import java.util.ArrayList;
import java.util.TreeSet;

public class ReplayListener implements DWLiveReplayListener {


    @Override
    public void onQuestionAnswer(TreeSet<ReplayQAMsg> treeSet) {

    }

    /**
     * 不展示聊天，占时不实现
     */
    @Override
    public void onChatMessage(TreeSet<ReplayChatMsg> treeSet) {
        //暂时不实现
    }

    @Override
    public void onBroadCastMessage(ArrayList<ReplayBroadCastMsg> arrayList) {

    }

    @Override
    public void onPageInfoList(ArrayList<ReplayPageInfo> arrayList) {

    }

    @Override
    public void onPageChange(String s, String s1, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onException(DWLiveException e) {

    }

    @Override
    public void onInitFinished() {

    }
}
