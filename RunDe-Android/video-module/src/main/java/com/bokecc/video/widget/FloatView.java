package com.bokecc.video.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bokecc.video.R;
import com.bokecc.video.ui.main.fragment.OnFloatViewMoveListener;
import com.bokecc.video.utils.CommonUtils;


@SuppressLint("ViewConstructor")
public class FloatView extends FrameLayout implements View.OnClickListener {
    private ImageView mDismissView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mContentView;
    private int mDownRawX, mDownRawY;//手指按下时相对于屏幕的坐标
    private int mDownX, mDownY;//手指按下时相对于悬浮窗的坐标

    private OnFloatViewMoveListener moveListener;
    private VelocityTracker mVelocityTracker;

    private boolean isSystemView = false;

    public FloatView(@NonNull Context context) {
        super(context);
        init(0, 0);
    }

    public FloatView(@NonNull Context context, int width, int height) {
        super(context);
        init(width, height);
    }

    private void init(int width, int height) {
        setBackgroundResource(R.drawable.shape_float_window_bg);
        mDismissView = new ImageView(getContext());
        mDismissView.setImageResource(R.drawable.live_screen_close);
        if (width == 0 || height == 0) {
            width = height = CommonUtils.dp2px(getContext(), 20);
        }
        LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        lp.gravity = Gravity.END;
        addView(mDismissView, lp);
        int padding = CommonUtils.dp2px(getContext(), 1);
        setPadding(padding, padding, padding, padding);
        initWindow();
        mDismissView.setOnClickListener(this);
    }


    public void addChildView(View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, 0, params);
        mContentView = view;
    }

    public View removeChildView() {
        if (mContentView != null) {
            removeView(mContentView);
        }
        return mContentView;
    }

    private void initWindow() {
        mWindowManager = CommonUtils.getWindowManager(getContext().getApplicationContext());
    }

    public void setMoveListener(OnFloatViewMoveListener listener) {
        moveListener = listener;
    }


    private void createActivityFloatLayoutParams() {
        mParams = new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        // 设置图片格式，效果为背景透明
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.windowAnimations = R.style.FloatWindowAnimation;
        mParams.gravity = Gravity.START | Gravity.TOP; // 调整悬浮窗口至右下角
        // 设置悬浮窗口长宽数据
        int width = CommonUtils.dp2px(getContext(), 150);
        mParams.width = width;
        mParams.height = width * 9 / 16;
        mParams.x = mDownX;
        mParams.y = mDownY;
    }


    private void createSystemFloatLayoutParams() {

        mParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // 设置图片格式，效果为背景透明
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mParams.windowAnimations = R.style.FloatWindowAnimation;
        mParams.gravity = Gravity.START | Gravity.TOP; // 调整悬浮窗口至右下角
        // 设置悬浮窗口长宽数据
        int width = CommonUtils.dp2px(getContext(), 150);
        mParams.width = width;
        mParams.height = width * 9 / 16;
        mParams.x = mDownX;
        mParams.y = mDownY;
    }

    public boolean isSystemView() {
        return isSystemView;
    }

    /**
     * 脱离Activity显示
     * 需要申请权限
     */
    public boolean addToWindow(int x, int y) {
        isSystemView = true;
        mDownX = x;
        mDownY = y;
        createSystemFloatLayoutParams();
        return addToWindow(null);
    }


    /**
     * 依赖activity显示,不需要申请权限，但是activity消失，则界面消失
     */
    public void addToActivity(View view, int x, int y) {
        isSystemView = false;
        mDownX = x;
        mDownY = y;
        createActivityFloatLayoutParams();
        addToWindow(view);
    }

    public void addContainer(ViewGroup mContentView, int x, int y) {
        isSystemView = false;
        mDownX = x;
        mDownY = y;
        createActivityFloatLayoutParams();

    }


    public boolean hasAddToWindow() {
        return getParent() != null;
    }

    /**
     * 添加至窗口
     */
    private boolean addToWindow(View view) {
        if (mWindowManager != null) {
            if (view != null)
                mParams.token = view.getWindowToken();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!isAttachedToWindow()) {
                    mWindowManager.addView(this, mParams);
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (getParent() == null) {
                        mWindowManager.addView(this, mParams);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * 从窗口移除
     */
    public boolean removeFromWindow() {
        if (mWindowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isAttachedToWindow()) {
                    mWindowManager.removeViewImmediate(this);
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (getParent() != null) {
                        mWindowManager.removeViewImmediate(this);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                mDownRawX = (int) ev.getRawX();
                mDownRawY = (int) ev.getRawY();
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getRawX() - mDownRawX);
                float absDeltaY = Math.abs(ev.getRawY() - mDownRawY);
                intercepted = absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop();
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.computeCurrentVelocity(200);
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                mParams.x = x - mDownX;
                mParams.y = y - mDownY;
                mWindowManager.updateViewLayout(this, mParams);
                if (moveListener != null) {
                    moveListener.onMove(this, mVelocityTracker);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                break;

            case MotionEvent.ACTION_UP:
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                if (moveListener != null) {
                    moveListener.onMoveFinished(this);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (v == mDismissView) {
            removeFromWindow();
            if (moveListener != null) {
                moveListener.onMoveOutWindow();
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }
        }
    }

    public void updatePosition(int x, int y) {
        if (!isShown()) return;
        if (mWindowManager != null) {
            mParams.x = x;
            mParams.y = y;
            mWindowManager.updateViewLayout(this, mParams);
        }
    }

    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener listener) {
        onDismissListener = listener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

}
