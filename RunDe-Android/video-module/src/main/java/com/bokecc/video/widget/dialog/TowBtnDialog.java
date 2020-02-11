package com.bokecc.video.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bokecc.video.R;

/**
 * 两个按钮的dialog
 */
public class TowBtnDialog extends Dialog {

    private String title;
    private final String message;
    private final String confirmTxt;
    private final String cancelTxt;
    private final OnConfirmClickListener confirmClickListener;
    private final OnCancelClickListener cancelClickListener;
    private TextView tvTitle;

    public interface OnConfirmClickListener {
        void onClick(View view);
    }

    public interface OnCancelClickListener {
        void onClick(View view);
    }

    private TowBtnDialog(@NonNull Context context, String title, String message, String confirmText, String cancelText,
                         OnConfirmClickListener confirmClickListener, OnCancelClickListener cancelClickListener) {
        super(context, R.style.TwoBtnDialog);
        this.title = title;
        this.message = message;
        this.confirmTxt = confirmText;
        this.cancelTxt = cancelText;
        this.confirmClickListener = confirmClickListener;
        this.cancelClickListener = cancelClickListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_dialog);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        Button confirmBtn = findViewById(R.id.id_btn_confirm);
        Button cancelBtn = findViewById(R.id.id_btn_cancel);
        tvTitle = findViewById(R.id.id_dialog_title);
        TextView tvMessage = findViewById(R.id.id_dialog_msg);

        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
        if (!TextUtils.isEmpty(confirmTxt)) {
            confirmBtn.setText(confirmTxt);
        }
        if (!TextUtils.isEmpty(cancelTxt)) {
            cancelBtn.setText(cancelTxt);
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmClickListener != null) {
                    confirmClickListener.onClick(v);
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelClickListener != null) {
                    cancelClickListener.onClick(v);
                }
            }
        });

    }

    @Override
    public void show() {
        if (tvTitle != null && !TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        super.show();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public static Builder Builder(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private String mTitle;
        private String mMessage;
        private String mConfirmText;
        private String mCancelText;
        private OnConfirmClickListener mConfirmClickListener;
        private OnCancelClickListener mCancelClickListener;
        private Context mContext;

        private Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder setOnConfirmClickListener(String confirmText, OnConfirmClickListener confirmclickListener) {
            this.mConfirmText = confirmText;
            this.mConfirmClickListener = confirmclickListener;
            return this;
        }

        public Builder setOnCancelClickListener(String cancelText, OnCancelClickListener onCancelclickListener) {
            this.mCancelText = cancelText;
            this.mCancelClickListener = onCancelclickListener;
            return this;
        }

        public TowBtnDialog build() {
            return new TowBtnDialog(mContext, mTitle, mMessage, mConfirmText, mCancelText,
                    mConfirmClickListener, mCancelClickListener);
        }
    }
}
