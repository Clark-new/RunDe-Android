package com.bokecc.video.ui.chat;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.video.route.CloseInputMsg;

public class InputState {
    //记录当前键盘的打开还是关闭的状态
    public static boolean IS_INPUT_STATE = false;

    /**
     * 关闭键盘
     */
    public static void closeInputState() {
        if (InputState.IS_INPUT_STATE) {
            CCEventBus.getDefault().post(new CloseInputMsg());
        }
    }
}
