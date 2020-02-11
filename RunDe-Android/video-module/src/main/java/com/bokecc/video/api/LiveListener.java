package com.bokecc.video.api;

import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.DWLiveListener;
import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.sdk.mobile.live.pojo.Answer;
import com.bokecc.sdk.mobile.live.pojo.BroadCastMsg;
import com.bokecc.sdk.mobile.live.pojo.ChatMessage;
import com.bokecc.sdk.mobile.live.pojo.LiveInfo;
import com.bokecc.sdk.mobile.live.pojo.PracticeInfo;
import com.bokecc.sdk.mobile.live.pojo.PracticeRankInfo;
import com.bokecc.sdk.mobile.live.pojo.PracticeStatisInfo;
import com.bokecc.sdk.mobile.live.pojo.PracticeSubmitResultInfo;
import com.bokecc.sdk.mobile.live.pojo.PrivateChatInfo;
import com.bokecc.sdk.mobile.live.pojo.PunchAction;
import com.bokecc.sdk.mobile.live.pojo.QualityInfo;
import com.bokecc.sdk.mobile.live.pojo.Question;
import com.bokecc.sdk.mobile.live.pojo.QuestionnaireInfo;
import com.bokecc.sdk.mobile.live.pojo.QuestionnaireStatisInfo;
import com.bokecc.sdk.mobile.live.pojo.SettingInfo;
import com.bokecc.sdk.mobile.live.pojo.TeacherInfo;
import com.bokecc.video.msg.AnnounceMsg;
import com.bokecc.video.msg.BannedChatMsg;
import com.bokecc.video.route.ChatMsgEntity;
import com.bokecc.video.route.QuestionnaireMsg;
import com.bokecc.video.route.StatusChangeMsg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LiveListener implements DWLiveListener {

    @Override
    public void onQuestion(Question question) {

    }

    @Override
    public void onPublishQuestion(String s) {

    }

    @Override
    public void onAnswer(Answer answer) {

    }

    @Override
    public void onLiveStatus(DWLive.PlayStatus playStatus) {

    }

    @Override
    public void onStreamEnd(boolean normal) {
        //断流，发送结束打开消息
        PunchAction action = new PunchAction();
        action.setType(PunchAction.Action.STOP_PUNCH);
        CCEventBus.getDefault().post(action);
    }

    @Override
    public void onHistoryChatMessage(ArrayList<ChatMessage> arrayList) {

        //获取到历史聊天消息
        CCEventBus.getDefault().post(new ChatMsgEntity(ChatMsgEntity.HISTORY_CHAT, arrayList));
    }


    /**
     * 计算两个时间的相对秒数
     *
     * @param start "2020-01-16 19:00:19"
     * @param end   "19:20:19"
     * @return 1200
     */
    private int calculateRelativeSecond(String start, String end) {
        String startTmp = start.substring(11);
        return calcPassTime(startTmp, end);
    }

    public int calcPassTime(String start, String end) {
        String[] startArray = start.split(":");
        String[] endArray = end.split(":");
        int hour = Integer.parseInt(endArray[0]) - Integer.parseInt(startArray[0]);
        int minute = Integer.parseInt(endArray[1]) - Integer.parseInt(startArray[1]);
        int second = Integer.parseInt(endArray[2]) - Integer.parseInt(startArray[2]);
        return hour * 3600 + minute * 60 + second;
    }


    @Override
    public void onPublicChatMessage(ChatMessage chatMessage) {
        /*
         * 直播过程中发送过来的时间是绝对时间"19:20:19"，但是历史聊天消息是相对直播开始时间的相对时间
         * 这里为了方便统一计算，我们将绝对时间统一计算成相对时间
         */

//        LiveInfo liveInfo = HDApi.get().getLiveInfo();
//
//        if (liveInfo != null) {
//            String liveStartTime = liveInfo.getLiveStartTime();
//            int second = calculateRelativeSecond(liveStartTime, chatMessage.getTime());
//            chatMessage.setTime(second + "");
//        }

        CCEventBus.getDefault().post(new ChatMsgEntity(ChatMsgEntity.PUBLIC_CHAT, chatMessage));
    }

    @Override
    public void onChatMessageStatus(String s) {
        try {
            if (s == null) {
                ELog.e("onChatMessageStatus", ":s is null str");
                return;
            }
            JSONObject jsonObject = new JSONObject(s);
            String status = jsonObject.getString("status");
            JSONArray chatIdJson = jsonObject.getJSONArray("chatIds");
            ArrayList<String> chatIds = new ArrayList<>();
            for (int i = 0; i < chatIdJson.length(); i++) {
                chatIds.add(chatIdJson.getString(i));
            }
            CCEventBus.getDefault().post(new StatusChangeMsg(chatIds, status));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSilenceUserChatMessage(ChatMessage chatMessage) {

    }

    /**
     * @param i = 1,个人禁言，2 ，全体禁言
     */
    @Override
    public void onBanChat(int i) {
        if (i == 2) {
            HDApi.get().isBanned = true;
        }
        CCEventBus.getDefault().post(new BannedChatMsg(i == 1 ? BannedChatMsg.PERSION_BANNED : BannedChatMsg.ALL_BANNED));
    }

    @Override
    public void onUnBanChat(int i) {
        if (i == 2) {
            HDApi.get().isBanned = false;
        }
        CCEventBus.getDefault().post(new BannedChatMsg(i == 1 ? BannedChatMsg.PERSION_UNBANNED : BannedChatMsg.ALL_UNBANNED));
    }

    @Override
    public void onPrivateChat(PrivateChatInfo privateChatInfo) {

    }

    @Override
    public void onPrivateChatSelf(PrivateChatInfo privateChatInfo) {

    }

    @Override
    public void onUserCountMessage(int i) {

    }

    @Override
    public void onOnlineTeachers(List<TeacherInfo> list) {

    }


    @Override
    public void onPageChange(String s, String s1, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onNotification(String s) {

    }

    @Override
    public void onSwitchSource(String s) {

    }

    @Override
    public void onSwitchVideoDoc(boolean b) {

    }

    @Override
    public void onRoomSettingInfo(SettingInfo settingInfo) {

    }

    @Override
    public void onHistoryBroadcastMsg(ArrayList<BroadCastMsg> arrayList) {

    }

    @Override
    public void onBroadcastMsg(String s) {

    }

    @Override
    public void onInformation(String s) {
        if (s.contains("讲师暂停了文字聊天")) {
            CCEventBus.getDefault().post(new BannedChatMsg(BannedChatMsg.ALL_BANNED));
        }
    }

    @Override
    public void onException(DWLiveException e) {

    }

    @Override
    public void onInitFinished(int i, List<QualityInfo> list) {

    }

    @Override
    public void onKickOut(int i) {

    }

    @Override
    public void onLivePlayedTime(int i) {

    }

    @Override
    public void onLivePlayedTimeException(Exception e) {

    }

    @Override
    public void isPlayedBack(boolean b) {

    }

    @Override
    public void onStatisticsParams(Map<String, String> map) {

    }

    @Override
    public void onCustomMessage(String s) {

    }

    @Override
    public void onBanStream(String s) {

    }

    @Override
    public void onUnbanStream() {

    }

    @Override
    public void onAnnouncement(boolean b, String s) {
        if (!b && s != null) {
            CCEventBus.getDefault().post(new AnnounceMsg(s));
        }
    }

    @Override
    public void onRollCall(int i) {

    }

    @Override
    public void onStartLottery(String s) {

    }

    @Override
    public void onLotteryResult(boolean b, String s, String s1, String s2) {

    }

    @Override
    public void onStopLottery(String s) {

    }

    @Override
    public void onVoteStart(int i, int i1) {

    }

    @Override
    public void onVoteStop() {

    }

    @Override
    public void onVoteResult(JSONObject jsonObject) {

    }

    @Override
    public void onPrizeSend(int i, String s, String s1) {

    }

    @Override
    public void onQuestionnairePublish(QuestionnaireInfo info) {
        CCEventBus.getDefault().post(new QuestionnaireMsg(QuestionnaireMsg.QUESTION, info));
    }

    @Override
    public void onQuestionnaireStop(String s) {
        CCEventBus.getDefault().post(new QuestionnaireMsg(QuestionnaireMsg.CLOSE, null));
    }

    @Override
    public void onQuestionnaireStatis(QuestionnaireStatisInfo questionnaireStatisInfo) {

    }

    @Override
    public void onExeternalQuestionnairePublish(String s, String s1) {

    }

    @Override
    public void onPracticePublish(PracticeInfo practiceInfo) {

    }

    @Override
    public void onPracticeSubmitResult(PracticeSubmitResultInfo practiceSubmitResultInfo) {

    }

    @Override
    public void onPracticStatis(PracticeStatisInfo practiceStatisInfo) {

    }

    @Override
    public void onPracticRanking(PracticeRankInfo practiceRankInfo) {

    }

    @Override
    public void onPracticeStop(String s) {

    }

    @Override
    public void onPracticeClose(String s) {

    }
}
