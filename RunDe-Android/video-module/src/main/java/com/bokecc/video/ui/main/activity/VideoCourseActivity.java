package com.bokecc.video.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.sdk.mobile.live.pojo.PunchAction;
import com.bokecc.sdk.mobile.live.pojo.QuestionnaireInfo;
import com.bokecc.video.R;
import com.bokecc.video.msg.AnnounceMsg;
import com.bokecc.video.msg.BannedChatMsg;
import com.bokecc.video.route.PunchMsg;
import com.bokecc.video.route.PunchResultMsg;
import com.bokecc.video.route.QuestionnaireMsg;
import com.bokecc.video.route.ResultMessage;
import com.bokecc.video.ui.main.fragment.VideoCourseFragment;
import com.bokecc.video.ui.questionnaire.QuestionnaireView;
import com.bokecc.video.widget.dialog.AnnounceDialog;
import com.bokecc.video.widget.dialog.BannedChatDialog;
import com.bokecc.video.widget.dialog.PunchDialog;
import com.bokecc.video.widget.dialog.ResultFailedDialog;
import com.bokecc.video.widget.dialog.ResultSuccessDialog;

public class VideoCourseActivity extends ClickActionActivity {
    private Fragment videoFragment;
    //问卷视图
    private QuestionnaireView mQuestionnaire;

    //执行成功和失败的dialog
    private ResultFailedDialog mResultFailedDialog;
    private ResultSuccessDialog mResultSuccessDialog;
    private BannedChatDialog mBannedChatDialog;
    private BannedChatDialog mUNBannedChatDialog;
    //打卡dialog
    private PunchDialog mPunchDialog;
    private AnnounceDialog mAnnounceDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CCEventBus.getDefault().isRegistered(this)) {
            CCEventBus.getDefault().register(this);
        }
        setContentView(R.layout.layout_video_class_activity);
        addDetailFragment(savedInstanceState);
        initData();
        initView();
        initEvent();
    }

    private void initData() {

    }

    private void initView() {
        mQuestionnaire = new QuestionnaireView();
        mResultSuccessDialog = new ResultSuccessDialog();
        mResultFailedDialog = new ResultFailedDialog();
        mBannedChatDialog = new BannedChatDialog();
        mUNBannedChatDialog = new BannedChatDialog();
        mAnnounceDialog = new AnnounceDialog();
    }

    private void initEvent() {
    }


    private void addDetailFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            videoFragment = fm.findFragmentById(R.id.id_fragment_container);
            if (videoFragment == null) {
                videoFragment = new VideoCourseFragment();
            } else {
                ft.remove(videoFragment);
                fm.popBackStack();
                ft.commit();
                ft = fm.beginTransaction();
            }
            Bundle bundle = new Bundle();
            videoFragment.setArguments(bundle);
            ft.add(R.id.id_fragment_container, videoFragment);
            ft.commit();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (videoFragment != null) {
            videoFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CCEventBus.getDefault().isRegistered(this)) {
            CCEventBus.getDefault().unregister(this);
        }
    }


    /**
     * 接收到问卷消息，显示问卷视图
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionnaireAction(QuestionnaireMsg msg) {
        if (mQuestionnaire.isAdded()) {
            mQuestionnaire.dismiss();
        }
        if (msg.getCode() == QuestionnaireMsg.QUESTION) {
            mQuestionnaire.setQuestionInfo((QuestionnaireInfo) msg.extra);
            mQuestionnaire.setAnswerFlag(false);

            mQuestionnaire.show(getSupportFragmentManager(), "questionnaire");
        } else if (msg.getCode() == QuestionnaireMsg.ANSWER) {
            mQuestionnaire.setQuestionInfo((QuestionnaireInfo) msg.extra);
            mQuestionnaire.setAnswerFlag(true);
            mQuestionnaire.show(getSupportFragmentManager(), "questionnaire");
        } else if (msg.getCode() == QuestionnaireMsg.CLOSE) {
            if (mQuestionnaire != null && mQuestionnaire.isAdded()) {
                mQuestionnaire.dismiss();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowResultDialog(ResultMessage msg) {
        if (msg.getType() == ResultMessage.SUCCESS) {
            mResultSuccessDialog.setMsg(msg.getMsg());
            mResultSuccessDialog.setExtra(msg.extra);
            mResultSuccessDialog.setCode(msg.getCode());
            mResultSuccessDialog.show(getSupportFragmentManager(), "success_dialog");
        } else if (msg.getType() == ResultMessage.FAILED) {
            mResultFailedDialog.setMsg(msg.getMsg());
            mResultFailedDialog.show(getSupportFragmentManager(), "failed_dialog");
        }
    }


    /**
     * 收到签到消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePunch(PunchMsg msg) {
        PunchAction action = (PunchAction) msg.extra;
        if (action.getType() == PunchAction.Action.START_PUNCH) {
            mPunchDialog = new PunchDialog();
            int time = action.getRemainDuration() - 2;
            if (time <= 0) {
                return;
            }
            mPunchDialog.setPunchId(action.getId());
            mPunchDialog.setTime(time);
            mPunchDialog.show(getSupportFragmentManager(), "mPunchDialog");
        } else if (action.getType() == PunchAction.Action.STOP_PUNCH) {
            if (mPunchDialog != null) {
                mPunchDialog.dismiss();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePunchResult(PunchResultMsg msg) {
        if (msg.getCode() == PunchResultMsg.SUCCESS) {
            mResultSuccessDialog.setMsg("恭喜您，签到成功");
            mResultSuccessDialog.setCode(0);
            mResultSuccessDialog.show(getSupportFragmentManager(), "success_dialog");
        } else if (msg.getCode() == PunchResultMsg.FAILED) {
            mResultFailedDialog.setMsg("抱歉，签到失败");
            mResultFailedDialog.show(getSupportFragmentManager(), "failed_dialog");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveBannedChatMsg(BannedChatMsg msg) {
        if (msg.msgType == BannedChatMsg.ALL_BANNED) {
            if (mUNBannedChatDialog.isShow()) {
                mUNBannedChatDialog.dismiss();
            }
            if (mBannedChatDialog.isShow()) return;
            mBannedChatDialog.setBanned(true);
            mBannedChatDialog.show(getSupportFragmentManager(), "all_banned_dialog");

        } else if (msg.msgType == BannedChatMsg.ALL_UNBANNED) {
            if (mBannedChatDialog.isShow()) {
                mBannedChatDialog.dismiss();
            }
            if (mUNBannedChatDialog.isShow()) return;
            mUNBannedChatDialog.setBanned(false);
            mUNBannedChatDialog.show(getSupportFragmentManager(), "all_unbanned_dialog");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveAnnounceChatMsg(AnnounceMsg msg) {
        mAnnounceDialog.setAnnounce(msg.msg);
        if (!mAnnounceDialog.isShow()) {
            mAnnounceDialog.show(getSupportFragmentManager(), "announce_dialog");
        }
    }


    public static void go(Activity activity, boolean isSpecial) {
        Intent intent = new Intent(activity, VideoCourseActivity.class);
        intent.putExtra(VideoCourseFragment.SPECIAL_KEY, isSpecial);
        activity.startActivity(intent);
    }
}
