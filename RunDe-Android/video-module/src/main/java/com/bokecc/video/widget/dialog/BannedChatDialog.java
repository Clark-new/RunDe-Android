package com.bokecc.video.widget.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bokecc.video.R;

/**
 * 禁言dialog
 */
public class BannedChatDialog extends DialogFragment {

    private boolean isBanned;

    private View mRootView;
    private ImageView imageView;
    private TextView mFirstTitleView;
    private TextView mSecondTitleView;
    private Handler mHandler;
    private CountRunnable countRunnable;
    private boolean isShow = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_banned_chat_dialog, container);
        imageView = mRootView.findViewById(R.id.id_img);
        mFirstTitleView = mRootView.findViewById(R.id.id_first_title);
        mSecondTitleView = mRootView.findViewById(R.id.id_second_title);

        return mRootView;
    }

    public void setBanned(boolean banned) {
        this.isBanned = banned;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBanned) {
            imageView.setImageResource(R.drawable.ic_forbidden);
            mFirstTitleView.setText("全体禁言");
            mSecondTitleView.setText("讲师已开启全体禁言");
        } else {
            imageView.setImageResource(R.drawable.ic_relieve);
            mFirstTitleView.setText("解除全体禁言");
            mSecondTitleView.setText("讲师已解除全体禁言");
        }
        mHandler = new Handler(Looper.getMainLooper());
        countRunnable = new CountRunnable();
        mHandler.postDelayed(countRunnable, 3000);
        isShow = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mHandler.removeCallbacks(countRunnable);
        isShow  = false;
    }

    private class CountRunnable implements Runnable {
        @Override
        public void run() {
            dismiss();
        }
    }

    public boolean isShow(){
        return isShow;
    }
}
