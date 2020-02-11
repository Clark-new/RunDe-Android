package com.bokecc.video.ui.main.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bokecc.video.api.HDApi;
import com.bokecc.video.utils.HandleBackUtil;
import com.bokecc.video.widget.dialog.TowBtnDialog;

public class BaseActivity extends AppCompatActivity {

    private TowBtnDialog mExitDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        if (ScreenAdapterUtil.hasNotchAtOPPO(this)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(0xff000000);
//        }else{
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        initExitDialog();
    }





    private void initExitDialog() {
        if (mExitDialog == null) {
            mExitDialog = TowBtnDialog.Builder(this)
                    .setTitle(HDApi.get().getApiType() == HDApi.ApiType.LIVE ? "退出直播" : "退出播放")
                    .setMessage("是否确定退出")
                    .setOnConfirmClickListener("退出", new TowBtnDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(View view) {
                            mExitDialog.dismiss();
                            finish();
                        }
                    })
                    .setOnCancelClickListener("再想想", new TowBtnDialog.OnCancelClickListener() {
                        @Override
                        public void onClick(View view) {
                            mExitDialog.dismiss();
                        }
                    })
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
            showExitDialog();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
//            if(ScreenAdapterUtil.hasNotchAtOPPO(this)){
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            }

        }
    }

    public void showExitDialog() {
        if (mExitDialog != null) {
            mExitDialog.updateTitle(HDApi.get().getApiType() == HDApi.ApiType.LIVE ? "退出直播" : "退出播放");
            mExitDialog.show();
        }
    }

}
