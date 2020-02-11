package com.bokecc.video.widget.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.video.R;
import com.bokecc.video.route.QuestionnaireMsg;

public class ResultSuccessDialog extends DialogFragment {
    private View mRootView;
    private TextView msgText;
    private Object extra;
    private int code;
    private String msg;
    private Handler mHandler = new Handler();
    private CloseRunnable mCloseRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.item_submit_ok, container);
        msgText = mRootView.findViewById(R.id.id_text_msg);
        return mRootView;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void onResume() {
        msgText.setText(msg);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        super.onResume();
        mCloseRunnable = new CloseRunnable();
        mHandler.postDelayed(mCloseRunnable, 3000);
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    /**
     * code = 1,消失后显示答案
     */
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mHandler.removeCallbacks(mCloseRunnable);
        if (code == 1) {
            CCEventBus.getDefault().post(new QuestionnaireMsg(QuestionnaireMsg.ANSWER, extra));
        }
    }


    class CloseRunnable implements Runnable {

        @Override
        public void run() {
            dismiss();
        }
    }
}
