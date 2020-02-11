package com.bokecc.video.ui.chat;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.video.R;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.keyboard.GlobalOnItemClickManagerUtils;
import com.bokecc.video.keyboard.KeyboardHeightObserver;
import com.bokecc.video.keyboard.KeyboardHeightProvider;
import com.bokecc.video.route.ClickAction;
import com.bokecc.video.route.CloseInputMsg;
import com.bokecc.video.ui.base.BaseFragment;
import com.bokecc.video.ui.base.FragmentFactory;
import com.bokecc.video.utils.EmotionUtils;
import com.bokecc.video.widget.MultipleStateImageView;
import com.bokecc.video.widget.RadioView;
import com.bokecc.video.widget.heart.HeartView;

/**
 * 处理聊天键盘相关
 */
public abstract class KeyBoardFragment extends BaseFragment implements View.OnClickListener, KeyboardHeightObserver {
    //扣1和扣2消息
    public static final String Q_ONE_MSG = "[em2_q1]";
    public static final String Q_TWO_MSG = "[em2_q2]";


    private enum LeftBtnState implements MultipleStateImageView.SelectState {
        //软键盘打开 COURSE--->EM_HIDE
        //点击表情按钮  EMOTICON--->KEYBOARD  KEYBOARD--->EMOTICON
        COURSE, //此状态下点击按钮-->将显示课程列表
        EMOTICON,//此状态下点击按钮-->显示表情键盘(软键盘状态)
        KEYBOARD,//此状态下点击按钮-->显示软键盘(表情键盘状态)
    }

    private RelativeLayout mInputToolbar;
    //当前软键盘的高度
    private int softKeyHeight;

    private MultipleStateImageView mInputLeftBtn;
    private ImageView mInputRightBtn1;
    private ImageView mInputRightBtn2;
    private LinearLayout mPlusLayout;
    private boolean isPlusLayoutShow;
    private EditText mInputEdit;
    private LinearLayout mEmotionLayout;
    private Animation mShowAnim;
    private InputMethodManager mImm;
    private TextView mSendBtn;
    private View mRewardView;
    private View mEvaluationView;
    private View mConsultView;

    private RadioView mRadioView;
    private HeartView mHeartView;

    private KeyboardHeightProvider keyboardHeightProvider;

    protected Handler mHeartHandler = new Handler();
    private HeartRunnable mHeartRunnable;


    @Override
    protected void initEvent() {
        super.initEvent();
        mShowAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_bottom_in);
    }

    @Override
    protected void initData() {
        super.initData();
        mImm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboardHeightProvider = new KeyboardHeightProvider(getActivity());
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                keyboardHeightProvider.start();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        mHeartView = findViewById(R.id.id_heart_btn);
        mRadioView = findViewById(R.id.id_radio_view);
        mRadioView.setOnClickListener(this);
        mInputToolbar = findViewById(R.id.id_input_toolbar);
        mInputEdit = findViewById(R.id.id_chat_edit);
        mEmotionLayout = findViewById(R.id.id_emotion_panel);
        mInputLeftBtn = findViewById(R.id.id_input_left_btn);
        mInputLeftBtn.addState(LeftBtnState.COURSE, R.drawable.tool_bar_course);
        mInputLeftBtn.addState(LeftBtnState.EMOTICON, R.drawable.input_box_icon_s);
        mInputLeftBtn.addState(LeftBtnState.KEYBOARD, R.drawable.input_keyboard);
        mInputLeftBtn.select(LeftBtnState.COURSE);
        mInputLeftBtn.setOnClickListener(this);


        mInputRightBtn1 = findViewById(R.id.id_input_right_btn1);
        mInputRightBtn2 = findViewById(R.id.id_input_right_btn2);
        mPlusLayout = findViewById(R.id.id_plus_bottom);

        mRewardView = findViewById(R.id.id_toolbar_reward);
        mRewardView.setOnClickListener(this);
        mEvaluationView = findViewById(R.id.id_toolbar_evaluation);
        mEvaluationView.setOnClickListener(this);
        mConsultView = findViewById(R.id.id_toolbar_consult);
        mConsultView.setOnClickListener(this);


        mSendBtn = findViewById(R.id.id_send_btn);
        mSendBtn.setOnClickListener(this);
        mInputRightBtn1.setOnClickListener(this);
        mInputRightBtn2.setOnClickListener(this);
        mInputRightBtn2.setOnClickListener(this);
        initKeyBoardFragment();
        mInputEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    sendMsg(v.getText().toString().trim());
                    v.setText("");
                    closeInputPlane();
                }
                return handled;
            }
        });
        setToolbarCloseState();
        if (HDApi.get().getApiType() == HDApi.ApiType.LIVE) {
            startOrStopHeart();
        }
    }


    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height > 10) {
//            if(ScreenAdapterUtil.hasNotchAtOPPO(getActivity())){
//                softKeyHeight = height;
//            }else{
//                boolean hasNotch = ScreenAdapterUtil.hasNotchScreen(getActivity());
//                if (hasNotch) {
//                    softKeyHeight = (int) (height + CommonUtils.getStatusBarHeight(getContext()));
//                } else {
//                    softKeyHeight = height;
//                }
//            }
//            ELog.e("sivin","height:"+height);
            softKeyHeight = height;
            mInputLeftBtn.select(LeftBtnState.EMOTICON);
            InputState.IS_INPUT_STATE = true;
            setToolbarInputState();
            mInputToolbar.setTranslationY(-softKeyHeight);
        } else {
            LeftBtnState state = (LeftBtnState) mInputLeftBtn.getState();
            //当前显示的是软键盘状态
            if (state == LeftBtnState.EMOTICON) {
                closeInputPlane();
                InputState.IS_INPUT_STATE = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.addKeyboardHeightObserver(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LeftBtnState state = (LeftBtnState) mInputLeftBtn.getState();
        if (state == LeftBtnState.EMOTICON) {
            mImm.hideSoftInputFromWindow(mInputEdit.getWindowToken(), 0);
            closeInputPlane();
        } else if (state == LeftBtnState.KEYBOARD) {
            closeInputPlane();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.removeKeyboardHeightObserver(this);
        keyboardHeightProvider.close();
    }

    /**
     * 当软键盘弹起
     */
    private void setToolbarInputState() {
        if (isPlusLayoutShow) {
            closePlusLayout();
        }
        mInputRightBtn1.setImageResource(R.drawable.input_box_one);
        mInputRightBtn2.setImageResource(R.drawable.input_box_two);
    }


    /**
     * 还原输入栏初始状态
     */
    private void setToolbarCloseState() {
        if (HDApi.get().getApiType() == HDApi.ApiType.REPLAY) {
            mInputEdit.setEnabled(false);
            if (!HDApi.get().isSpecialCourse()) {
                mInputRightBtn1.setImageResource(R.drawable.tool_bar_gift_disabled);
                mInputRightBtn1.setEnabled(false);
            }
        } else {
            mInputEdit.setEnabled(true);
            mInputRightBtn1.setImageResource(R.drawable.tool_bar_gift);
            mInputRightBtn1.setEnabled(true);
        }
        mInputRightBtn2.setImageResource(R.drawable.tool_bar_plus);
    }

    /**
     * 是否是公开课回放
     */
    private boolean isOpenCourseReplay() {
        return (HDApi.get().getApiType() == HDApi.ApiType.REPLAY);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_input_right_btn2) {
            onInputRightBtn2Click();
        } else if (id == R.id.id_input_right_btn1) {
            onInputRightBtn1Click();
        } else if (id == R.id.id_input_left_btn) {
            onInputLeftBtnClick();
        } else if (id == R.id.id_send_btn) {
            sendInputMsg();
        } else if (id == R.id.id_toolbar_reward) {
            CCEventBus.getDefault().post(new ClickAction(ClickAction.ON_CLICK_REWARD));
        } else if (id == R.id.id_toolbar_evaluation) {
            CCEventBus.getDefault().post(new ClickAction(ClickAction.ON_CLICK_EVALUATE));
        } else if (id == R.id.id_toolbar_consult) {
            CCEventBus.getDefault().post(new ClickAction(ClickAction.ON_CLICK_COUNSULT));
        } else if (id == R.id.id_radio_view) {
            onClickRadioView();
        }
    }

    private void onClickRadioView() {
        if (mRadioView.isSelect()) {
            mRadioView.select(false);
            onlyShowTeacher(false);
        } else {
            mRadioView.select(true);
            onlyShowTeacher(true);
        }
    }

    private void sendInputMsg() {
        sendMsg(mInputEdit.getText().toString().trim());
        mInputEdit.setText("");
        closeInputPlane();
    }


    private void onInputLeftBtnClick() {
        LeftBtnState state = (LeftBtnState) mInputLeftBtn.getState();
        switch (state) {
            case COURSE:
                CCEventBus.getDefault().post(new ClickAction(ClickAction.ON_CLICK_TOOLBAR_COURSE));
                break;
            case EMOTICON:
                showEmoticon();
                mInputLeftBtn.select(LeftBtnState.KEYBOARD);
                break;
            case KEYBOARD:
                showInputKeyBoard();
                mInputLeftBtn.select(LeftBtnState.EMOTICON);
                break;
        }
    }

    /**
     * 主动显示软键盘输入
     */
    private void showInputKeyBoard() {
        mImm.showSoftInput(mInputEdit, 0);
    }

    /**
     * 显示表情键盘
     */
    private void showEmoticon() {
        if (mEmotionLayout.getHeight() != softKeyHeight && softKeyHeight != 0) {
            ViewGroup.LayoutParams lp = mEmotionLayout.getLayoutParams();
            lp.height = softKeyHeight;
            mEmotionLayout.setLayoutParams(lp);
        }
        mEmotionLayout.setVisibility(View.VISIBLE);
        mImm.hideSoftInputFromWindow(mInputEdit.getWindowToken(), 0);
    }


    private void onInputRightBtn1Click() {
        if (mInputLeftBtn.getState() == LeftBtnState.COURSE) {
            CCEventBus.getDefault().post(new ClickAction(ClickAction.ON_CLICK_TOOLBAR_GIFT));
        } else {
            sendMsg(Q_ONE_MSG);
            closeInputPlane();
        }
    }

    private void onInputRightBtn2Click() {
        if (mInputLeftBtn.getState() == LeftBtnState.COURSE) {
            if (isPlusLayoutShow) {
                closePlusLayout();
            } else {
                showPlusLayout();
            }
        } else {
            sendMsg(Q_TWO_MSG);
            closeInputPlane();
        }
    }

    private void showPlusLayout() {
        isPlusLayoutShow = true;
        if (isOpenCourseReplay()) {
            mRewardView.setVisibility(View.GONE);
        } else {
            mRewardView.setVisibility(View.VISIBLE);
        }
        mInputRightBtn2.setImageResource(R.drawable.tool_bar_close);
        mPlusLayout.setVisibility(View.VISIBLE);
        mPlusLayout.startAnimation(mShowAnim);
    }

    private void closePlusLayout() {
        isPlusLayoutShow = false;
        mInputRightBtn2.setImageResource(R.drawable.tool_bar_plus);
        mPlusLayout.setVisibility(View.GONE);
    }

    void initKeyBoardFragment() {
        GlobalOnItemClickManagerUtils.getInstance(getContext()).attachToEditText(mInputEdit);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentById(R.id.id_emotion_container);
        if (fragment == null) {
            fragment = FragmentFactory.getEmoticonFragment(EmotionUtils.EMOTION_CLASSIC_TYPE);
        } else {
            ft.remove(fragment);
            fm.popBackStack();
            ft.commit();
            ft = fm.beginTransaction();
        }
        ft.add(R.id.id_emotion_container, fragment);
        ft.commit();
    }


    @Override
    public boolean onBackPressed() {
        LeftBtnState state = (LeftBtnState) mInputLeftBtn.getState();
        if (state == LeftBtnState.KEYBOARD) {
            closeInputPlane();
            return true;
        } else if (isPlusLayoutShow) {
            closePlusLayout();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    protected void closeInputPlane() {
        mEmotionLayout.setVisibility(View.GONE);
        mInputToolbar.setTranslationY(0);
        setToolbarCloseState();
        mInputLeftBtn.select(LeftBtnState.COURSE);
        mImm.hideSoftInputFromWindow(mInputEdit.getWindowToken(), 0);
        InputState.IS_INPUT_STATE = false;
    }


    private void startOrStopHeart() {
        if (HDApi.get().getApiType() == HDApi.ApiType.LIVE) {
            mHeartRunnable = new HeartRunnable();
            mHeartHandler.postDelayed(mHeartRunnable, 3000);
        } else {
            mHeartHandler.removeCallbacks(mHeartRunnable);
        }
    }

    class HeartRunnable implements Runnable {

        @Override
        public void run() {
            if (HDApi.get().getApiType() == HDApi.ApiType.LIVE) {
                if (mHeartView != null) {
                    mHeartView.addLoveIcon();
                }
                int delay = (int) (Math.random() * 3 + 4) * 1000;
                mHeartRunnable = this;
                mHeartHandler.postDelayed(mHeartRunnable, delay);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveCloseInputMsg(CloseInputMsg msg) {
        closeInputPlane();
    }

    protected abstract void sendMsg(String msg);

    protected abstract void onlyShowTeacher(boolean onlyShowTeacher);

    protected void updateChatUi() {
        setToolbarCloseState();
        startOrStopHeart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHeartHandler.removeCallbacks(mHeartRunnable);
    }


}
