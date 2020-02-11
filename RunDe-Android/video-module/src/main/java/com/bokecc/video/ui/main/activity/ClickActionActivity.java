package com.bokecc.video.ui.main.activity;

import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.route.ClickAction;

/**
 * 该类由客户实现
 */
public class ClickActionActivity extends BaseActivity {

    private static final String TAG = ClickActionActivity.class.getSimpleName();

    private String giftUrl2 = "http://static.csslcloud.net/img/em2/15.png";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveViewClickAction(ClickAction clickAction) {
        switch (clickAction.action) {
            case ClickAction.ON_CLICK_TOOLBAR_COURSE:
                //TODO:点击聊天课程按钮触发
                ELog.i(TAG, "ON_CLICK_TOOLBAR_COURSE");
                break;
            case ClickAction.ON_CLICK_TOOLBAR_GIFT:
                //TODO:点击礼物界面触发
                ELog.i(TAG, "ON_CLICK_TOOLBAR_GIFT");
                //TODO:实例代码发送打赏礼物消息
                HDApi.get().sendGiftMsg("送出 老师心", giftUrl2, 20);
                break;
            case ClickAction.ON_CLICK_REWARD:
                ELog.i(TAG, "ON_CLICK_REWARD");
                //TODO:点击打赏按钮触发
                break;
            case ClickAction.ON_CLICK_EVALUATE:
                ELog.i(TAG, "ON_CLICK_EVALUATE");
                //TODO:点击评价按钮触发
                break;
            case ClickAction.ON_CLICK_COUNSULT:
                ELog.i(TAG, "ON_CLICK_COUNSULT");
                //TODO:点击咨询按钮触发
                break;
        }
    }


}
