package com.bokecc.video.ui.main.fragment;


import android.Manifest;
import android.content.pm.PackageManager;

import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bokecc.video.controller.RtcControlCallback;
import com.bokecc.video.video.RTCController;
import com.bokecc.video.widget.dialog.TowBtnDialog;

/**
 * 处理Rtc相关的逻辑
 * 申请权限，显示提示框，
 */
public abstract class RTCControlFragment extends BaseVideoFragment implements RtcControlCallback {

    private final static int REQUEST = 0x100;

    //所有权限全部获取
    private static final int ALL_PERMISSION = 0x00;
    //需要获取Camera权限
    private static final int NEED_CAMERA_PERMISSION = 0x01;
    //需要获取麦克风权限
    private static final int NEED_RECORDER_PERMISSION = 0x10;

    private TowBtnDialog mApplyPermissionDialog;
    private TowBtnDialog mHangUpRTCDialog;


    @Override
    public void onRtcApplyBtnClick() {
        int ret = checkPermission();
        if (ret == ALL_PERMISSION) {
            applyVideoRtc();
            return;
        }
        showApplyPermissionTips(ret);

    }

    @Override
    public void onHangUpRtcApply() {
        showHangUpRtcApplyTips("温馨提示", "同学,是否取消连麦申请");
    }

    @Override
    public void onRtcHangUpBtnClick() {
        showHangUpRtcTips("温馨提示", "同学,是否挂断语音连接");
    }


    /**
     * 申请视频Rtc权限框
     */
    private void showApplyPermissionTips(int code) {
        if (code == ALL_PERMISSION) return;
        if (code == (NEED_CAMERA_PERMISSION | NEED_RECORDER_PERMISSION)) {
            //显示同时申请Camera和麦克风权限
            showApplyRtcTips("与老师连麦", "连麦需要获取您的麦克风和相机权限");
        } else if (code == NEED_CAMERA_PERMISSION) {
            //显示申请Camera权限
            showApplyRtcTips("与老师连麦", "连麦需要获取您的相机权限");
        } else if (code == NEED_RECORDER_PERMISSION) {
            //
            showApplyRtcTips("与老师连麦", "连麦需要获取您的麦克风权限");
        }
    }

    /**
     * 显示申请RTC权限提示框
     */
    private void showApplyRtcTips(String title, String msg) {
        if (mApplyPermissionDialog == null) {
            mApplyPermissionDialog = TowBtnDialog.Builder(getContext())
                    .setTitle(title)
                    .setMessage(msg)
                    .setOnConfirmClickListener("好", new TowBtnDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(View view) {
                            mApplyPermissionDialog.dismiss();
                            applyPermission();
                        }
                    })
                    .setOnCancelClickListener("下次吧", new TowBtnDialog.OnCancelClickListener() {
                        @Override
                        public void onClick(View view) {
                            mApplyPermissionDialog.dismiss();
                        }
                    })
                    .build();
        }
        mApplyPermissionDialog.show();
    }


    private void showHangUpRtcTips(String title, String msg) {
        if (mHangUpRTCDialog == null) {
            mHangUpRTCDialog = TowBtnDialog.Builder(getContext())
                    .setTitle(title)
                    .setMessage(msg)
                    .setOnConfirmClickListener("挂断", new TowBtnDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(View view) {
                            mHangUpRTCDialog.dismiss();
                            hangUpRtc();
                        }
                    })
                    .setOnCancelClickListener("不挂断", new TowBtnDialog.OnCancelClickListener() {
                        @Override
                        public void onClick(View view) {
                            mHangUpRTCDialog.dismiss();
                        }
                    })
                    .build();
        }
        mHangUpRTCDialog.show();
    }


    private void showHangUpRtcApplyTips(String title, String msg) {
        if (mHangUpRTCDialog == null) {
            mHangUpRTCDialog = TowBtnDialog.Builder(getContext())
                    .setTitle(title)
                    .setMessage(msg)
                    .setOnConfirmClickListener("是", new TowBtnDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(View view) {
                            mHangUpRTCDialog.dismiss();
                            hangUpRtcApply();
                        }
                    })
                    .setOnCancelClickListener("否", new TowBtnDialog.OnCancelClickListener() {
                        @Override
                        public void onClick(View view) {
                            mHangUpRTCDialog.dismiss();
                        }
                    })
                    .build();
        }
        mHangUpRTCDialog.show();
    }


    /**
     * 进行权限检测
     */
    private int checkPermission() {
        int result = ALL_PERMISSION;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            result = result | NEED_CAMERA_PERMISSION;
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            result = result | NEED_RECORDER_PERMISSION;
        }
        return result;
    }

    /**
     * 申请权限
     */
    private void applyPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST) {
            boolean isAllGranted = true;
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                applyVideoRtc();
            } else {
                showApplyPermissionTips(checkPermission());
            }

            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void applyVideoRtc() {
        RTCController rtcController = getRTCController();
        if (rtcController != null) {
            rtcController.applyVideoRtc();
        }
    }

    private void hangUpRtc() {
        RTCController rtcController = getRTCController();
        if (rtcController != null) {
            rtcController.handUpRtc();
        }
    }


    private void hangUpRtcApply() {
        RTCController rtcController = getRTCController();
        if (rtcController != null) {
            rtcController.handUpApplyRtc();
        }
    }


    public abstract RTCController getRTCController();
}
