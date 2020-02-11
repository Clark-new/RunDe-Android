package com.bokecc.video.widget.dialog;

import android.content.DialogInterface;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bokecc.video.R;

/**
 * 广告dialog
 */
public class AnnounceDialog extends DialogFragment {
    private View mRootView;
    private TextView mContentView;
    private TextView mKnowView;
    private String announce = "";
    private boolean mIsShow = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_announce_dialog, container);
        mContentView = mRootView.findViewById(R.id.id_content);
        mKnowView = mRootView.findViewById(R.id.id_know);
        mKnowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return mRootView;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
        if(mContentView != null){
            mContentView.setText(announce);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsShow = true;
        mContentView.setText(announce);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mIsShow = false;
    }

    public boolean isShow(){
        return mIsShow;
    }
}
