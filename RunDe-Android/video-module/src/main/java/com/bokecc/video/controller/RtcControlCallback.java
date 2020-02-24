package com.bokecc.video.controller;

public interface RtcControlCallback {

    /**
     * 申请连麦
     */
    void onRtcApplyBtnClick();

    /**
     * 挂断申请
     */
    void onHangUpRtcApply();

    /**
     * 挂断连麦
     */
    void onRtcHangUpBtnClick();

}
