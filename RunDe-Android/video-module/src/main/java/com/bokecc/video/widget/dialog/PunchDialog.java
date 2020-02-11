package com.bokecc.video.widget.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bokecc.sdk.mobile.live.BaseCallback;
import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.sdk.mobile.live.pojo.PunchCommitRespone;
import com.bokecc.video.R;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.route.PunchResultMsg;
import com.bokecc.video.widget.SubmitButton;

/**
 * 签到Dialog
 */
public class PunchDialog extends DialogFragment {
    private View mRootView;
    private TextView countDownText;
    private int time;
    private SubmitButton submitButton;
    private Handler mHandler;
    private String punchId;
    private boolean submitting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_punch_dialog, container);
        countDownText = mRootView.findViewById(R.id.id_count_down_time);
        submitButton = mRootView.findViewById(R.id.id_submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPunch();
            }
        });
        return mRootView;
    }

    public void setPunchId(String punchId) {
        this.punchId = punchId;
    }

    /**
     * 开始打开
     */
    private void startPunch() {
        if (submitting) return;
        submitting = true;
        submitButton.startSubmitAnim();
        HDApi.get().punch(punchId, new BaseCallback<PunchCommitRespone>() {
            @Override
            public void onError(String s) {
                submitting = false;
                submitButton.reset();
                dismiss();
                CCEventBus.getDefault().post(new PunchResultMsg(PunchResultMsg.FAILED));
            }

            @Override
            public void onSuccess(PunchCommitRespone punchCommitRespone) {
                submitting = false;
                dismiss();
                CCEventBus.getDefault().post(new PunchResultMsg(PunchResultMsg.SUCCESS));
            }
        });

    }


    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public void onResume() {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().setCancelable(false);
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new CountRunnable());
        super.onResume();
    }


    private class CountRunnable implements Runnable {
        @Override
        public void run() {
            countDownText.setText(time + "s");
            ELog.i("Sivin", "time:" + time);
            if (time < 0) {
                dismiss();
                return;
            }
            time--;
            mHandler.postDelayed(this, 1000);
        }
    }



}
