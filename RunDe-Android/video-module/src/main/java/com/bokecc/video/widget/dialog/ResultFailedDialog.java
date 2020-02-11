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

import com.bokecc.video.R;

public class ResultFailedDialog extends DialogFragment {
    private View mRootView;
    private TextView msgTxtView;
    private TextView extraTxtView;
    private String msg;
    private Handler mHandler = new Handler();
    private CloseRunnable mCloseRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.item_submit_fail, container);
        msgTxtView = mRootView.findViewById(R.id.id_result_msg);
        extraTxtView = mRootView.findViewById(R.id.id_extra_msg);
        return mRootView;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void onResume() {
        msgTxtView.setText(msg);
        mCloseRunnable = new CloseRunnable();
        mHandler.postDelayed(mCloseRunnable, 3000);
        super.onResume();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mHandler.removeCallbacks(mCloseRunnable);
    }

    public void disableExtra() {
        extraTxtView.setVisibility(View.GONE);
    }


    class CloseRunnable implements Runnable {
        @Override
        public void run() {
            dismiss();
        }
    }
}
