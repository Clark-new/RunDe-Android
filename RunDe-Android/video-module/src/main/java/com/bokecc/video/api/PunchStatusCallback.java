package com.bokecc.video.api;

import com.bokecc.sdk.mobile.live.BaseCallback;
import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.sdk.mobile.live.pojo.PunchAction;
import com.bokecc.video.route.PunchMsg;

public class PunchStatusCallback implements BaseCallback<PunchAction> {

    private static final String TAG = "QueryPunchStatusCallback";

    @Override
    public void onError(String s) {
        ELog.e(TAG, "Error:s");
    }

    @Override
    public void onSuccess(PunchAction action) {
        PunchMsg msg = new PunchMsg(action);
        CCEventBus.getDefault().post(msg);
    }
}
