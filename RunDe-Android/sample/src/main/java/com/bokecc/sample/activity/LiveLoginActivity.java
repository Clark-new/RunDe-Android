package com.bokecc.sample.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import com.bokecc.sample.R;
import com.bokecc.sample.base.BaseActivity;
import com.bokecc.sample.popup.LoginPopupWindow;
import com.bokecc.sample.scan.qr_codescan.MipcaActivityCapture;
import com.bokecc.sample.widget.LoginLineLayout;
import com.bokecc.sdk.mobile.live.DWLiveLoginListener;
import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.sdk.mobile.live.pojo.LoginInfo;
import com.bokecc.sdk.mobile.live.pojo.PublishInfo;
import com.bokecc.sdk.mobile.live.pojo.RoomInfo;
import com.bokecc.sdk.mobile.live.pojo.TemplateInfo;
import com.bokecc.sdk.mobile.live.pojo.Viewer;
import com.bokecc.video.TestConstanst;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.ui.main.activity.VideoCourseActivity;

import java.util.HashMap;
import java.util.Map;

/***
 * 直播观看登录页面
 */
public class LiveLoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LiveLoginActivity";

    static final int MAX_NAME = 20;  // 用户昵称最多20字符

    View mRoot;
    LoginPopupWindow loginPopupWindow;   // 登录Loading控件
    LoginLineLayout lllLoginLiveUid;        // CC 账号ID
    LoginLineLayout lllLoginLiveRoomid;     // 直播间ID
    LoginLineLayout lllLoginLiveName;       // 用户昵称
    LoginLineLayout lllLoginLivePassword;   // 用户密码
    Button btnLoginLive; // 登录按钮
    private String mGroupId = ""; //聊天分组使用（选填）
    private boolean needAutoLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideActionBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_login);
        initViews();
        preferences = getSharedPreferences("live_login_info", Activity.MODE_PRIVATE);
        getSharePreference();
        if (map != null) {
            initEditTextInfo();
        }
        //解析网页端URL跳转直播
        parseUriIntent();
    }


    private void parseUriIntent() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String userId = uri.getQueryParameter("userid");
            String roomId = uri.getQueryParameter("roomid");
            String autoLogin = uri.getQueryParameter("autoLogin");
            String viewerName = uri.getQueryParameter("viewername");
            String viewerToken = uri.getQueryParameter("viewertoken");
            String groupId = uri.getQueryParameter("groupid");
            String qurey = uri.getQuery();

            ELog.d(TAG, "userId =" + userId + " roomId =" + roomId + " autoLogin =" + autoLogin
                    + " viewerName =" + viewerName + " viewerToken =" + viewerToken + " groupId =" + groupId
                    + " qurey:" + qurey
            );

            userId = userId == null ? "" : userId;
            roomId = roomId == null ? "" : roomId;
            viewerName = viewerName == null ? "" : viewerName;
            viewerToken = viewerToken == null ? "" : viewerToken;
            mGroupId = mGroupId == null ? "" : mGroupId;

            lllLoginLiveUid.setText(userId);
            lllLoginLiveRoomid.setText(roomId);
            lllLoginLiveName.setText(viewerName);
            lllLoginLivePassword.setText(viewerToken);

            if ("true".equals(autoLogin)) {
                needAutoLogin = true;
            }
        }
    }

    private void initViews() {


        mRoot = getWindow().getDecorView().findViewById(android.R.id.content);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_scan).setOnClickListener(this);

        btnLoginLive = findViewById(R.id.btn_login_live);
        lllLoginLiveUid = findViewById(R.id.lll_login_live_uid);
        lllLoginLiveRoomid = findViewById(R.id.lll_login_live_roomid);
        lllLoginLiveName = findViewById(R.id.lll_login_live_name);
        lllLoginLivePassword = findViewById(R.id.lll_login_live_password);

        lllLoginLiveUid.setHint(getResources().getString(R.string.login_uid_hint)).addOnTextChangeListener(myTextWatcher);
        lllLoginLiveRoomid.setHint(getResources().getString(R.string.login_roomid_hint)).addOnTextChangeListener(myTextWatcher);
        lllLoginLiveName.setHint(getResources().getString(R.string.login_name_hint)).addOnTextChangeListener(myTextWatcher);


        lllLoginLiveName.maxEditTextLength = MAX_NAME;


        lllLoginLivePassword.setHint(getResources().getString(R.string.login_s_password_hint)).addOnTextChangeListener(myTextWatcher)
                .setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);



        lllLoginLiveRoomid.setText("59261EA8EED5CD919C33DC5901307461");
        lllLoginLiveUid.setText("358B27E7B04F3B02");
        lllLoginLiveName.setText("tt");

        btnLoginLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLiveLogin();
            }
        });


        loginPopupWindow = new LoginPopupWindow(this);


        mRoot.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean hasFocus) {
                if (needAutoLogin) {
                    needAutoLogin = false;
                    doLiveLogin();
                }
                mRoot.getViewTreeObserver().removeOnWindowFocusChangeListener(this);
            }
        });


    }


    /**
     * 隐藏弹窗
     */
    private void dismissPopupWindow() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loginPopupWindow != null && loginPopupWindow.isShowing()) {
                    loginPopupWindow.dismiss();
                }
            }
        });
    }

    //———————————————————————————————————— 登录相关方法（核心方法）  —————————————————————————————————————————

    /**
     * 执行直播登录操作
     */
    private void doLiveLogin() {

        if (!loginCheck()) {
            return;
        }

        loginPopupWindow.show(mRoot);

        // 创建登录信息
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setRoomId(lllLoginLiveRoomid.getText());
        loginInfo.setUserId(lllLoginLiveUid.getText());
        loginInfo.setViewerName(lllLoginLiveName.getText());
        loginInfo.setViewerToken(lllLoginLivePassword.getText());
        if (!"".equals(mGroupId.trim())) {
            loginInfo.setGroupId(mGroupId);
        }

        TestConstanst.ROOMID = loginInfo.getRoomId();
        TestConstanst.USERID = loginInfo.getUserId();
        TestConstanst.USERNAME = loginInfo.getViewerName();
        TestConstanst.USERTOKEN = loginInfo.getViewerToken();

        // 设置登录参数
        HDApi.get().setLiveLoginListener(loginInfo, new DWLiveLoginListener() {
            @Override
            public void onLogin(TemplateInfo templateInfo, Viewer viewer, final RoomInfo roomInfo, PublishInfo publishInfo) {
                toastOnUiThread("登录成功");
                // 缓存登陆的参数
                writeSharePreference();
                dismissPopupWindow();
                VideoCourseActivity.go(LiveLoginActivity.this, true);
            }

            @Override
            public void onException(final DWLiveException e) {
                toastOnUiThread("登录失败" + e.getLocalizedMessage());
                dismissPopupWindow();
            }
        });
        // 执行登录操作
        HDApi.get().login();
    }

    private boolean loginCheck() {
        if (lllLoginLiveUid.getText().trim().equals("")) {
            toastOnUiThread("CC账号ID=null");
            return false;
        }
        if (lllLoginLiveRoomid.getText().trim().equals("")) {
            toastOnUiThread("直播间ID=null");
            return false;
        }
        if (lllLoginLiveName.getText().trim().equals("")) {
            toastOnUiThread("用户名=null");
            return false;
        }

        return true;
    }

    //------------------------------- 缓存数据相关方法-----------------------------------------

    SharedPreferences preferences;

    private void writeSharePreference() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("liveuid", lllLoginLiveUid.getText());
        editor.putString("liveroomid", lllLoginLiveRoomid.getText());
        editor.putString("liveusername", lllLoginLiveName.getText());
        editor.putString("livepassword", lllLoginLivePassword.getText());
        editor.apply();
    }

    private void getSharePreference() {
        lllLoginLiveUid.setText(preferences.getString("liveuid", ""));
        lllLoginLiveRoomid.setText(preferences.getString("liveroomid", ""));
        lllLoginLiveName.setText(preferences.getString("liveusername", ""));
        lllLoginLivePassword.setText(preferences.getString("livepassword", ""));
    }

    //—————————————————————————————————— 扫码相关逻辑 ——————————————————————————————————————

    private static final int QR_REQUEST_CODE = 111;

    String userIdStr = "userid";  // 用户id
    String roomIdStr = "roomid";  // 房间id

    Map<String, String> map;

    // 跳转到扫码页面
    private void showScan() {
        Intent intent = new Intent(this, MipcaActivityCapture.class);
        startActivityForResult(intent, QR_REQUEST_CODE);
    }

    // 接收并处理扫码页返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle == null) return;
                String result = bundle.getString("result");
                if (result == null) return;
                if (!result.contains("userid=")) {
                    Toast.makeText(getApplicationContext(), "扫描失败，请扫描正确的播放二维码", Toast.LENGTH_SHORT).show();
                    return;
                }
                map = parseUrl(result);
                if (lllLoginLiveUid != null) {
                    initEditTextInfo();
                }
            }
        }
    }

    private void initEditTextInfo() {
        if (map.containsKey(roomIdStr)) {
            lllLoginLiveRoomid.setText(map.get(roomIdStr));
        }

        if (map.containsKey(userIdStr)) {
            lllLoginLiveUid.setText(map.get(userIdStr));
        }
    }

    //------------------------------------- 工具方法 -------------------------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_scan:
                showScan();
                break;
        }
    }

    private TextWatcher myTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            boolean isLoginEnabled = isNewLoginButtonEnabled(lllLoginLiveName, lllLoginLiveRoomid, lllLoginLiveUid);
            btnLoginLive.setEnabled(isLoginEnabled);
            btnLoginLive.setTextColor(isLoginEnabled ? Color.parseColor("#ffffff") : Color.parseColor("#f7d8c8"));
        }
    };

    // 检测登录按钮是否应该可用
    public static boolean isNewLoginButtonEnabled(LoginLineLayout... views) {
        for (LoginLineLayout view : views) {
            if ("".equals(view.getText().trim())) {
                return false;
            }
        }
        return true;
    }


    // 解析扫码获取到的URL
    private Map<String, String> parseUrl(String url) {
        Map<String, String> map = new HashMap<>();
        String param = url.substring(url.indexOf("?") + 1);
        String[] params = param.split("&");

        if (params.length < 2) {
            return null;
        }
        for (String p : params) {
            String[] en = p.split("=");
            map.put(en[0], en[1]);
        }
        return map;
    }

}
