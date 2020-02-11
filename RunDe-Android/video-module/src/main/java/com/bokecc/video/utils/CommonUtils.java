package com.bokecc.video.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.provider.Settings;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;

import android.view.WindowManager;

import com.bokecc.sdk.mobile.live.logging.ELog;

import java.lang.reflect.Method;

public class CommonUtils {
    private static final String TAG = "CommonUtils";

    public static int layoutOffsetSideHeight = 0;

    /**
     * 获取Activity
     */
    public static Activity scanForActivity(Context context) {
        return context == null ? null :
                (context instanceof Activity ?
                        (Activity) context :
                        (context instanceof ContextWrapper ?
                                scanForActivity(((ContextWrapper) context).getBaseContext()) : null));
    }


    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }


    /**
     * dip转化成px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转化成dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * px转化成sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转化成px
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }


    /**
     * dp转为px
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * 获取状态栏高度
     */
    public static double getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }


    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidthPixels(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeightPixels(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static void setChangeHeightAnim(View view, int startPx, int endPx, int time) {
        ViewAnimFactory viewAnimFactory = new ViewAnimFactory();
        viewAnimFactory.setView(view);
        ObjectAnimator animator = ObjectAnimator.ofInt(viewAnimFactory, "height", startPx, endPx);
        animator.setDuration(time);
        animator.setRepeatCount(0);
        animator.start();
    }

    public static void setChangeWidthAnim(View view, int startPx, int endPx, int time) {
        ViewAnimFactory viewAnimFactory = new ViewAnimFactory();
        viewAnimFactory.setView(view);
        ObjectAnimator animator = ObjectAnimator.ofInt(viewAnimFactory, "width", startPx, endPx);
        animator.setDuration(time);
        animator.setRepeatCount(0);
        animator.start();
    }

    public int getVirtualBarHeigh(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    /**
     * 获取虚拟按键的高度
     * 1. 全面屏下
     * 1.1 开启全面屏开关-返回0
     * 1.2 关闭全面屏开关-执行非全面屏下处理方式
     * 2. 非全面屏下
     * 2.1 没有虚拟键-返回0
     * 2.1 虚拟键隐藏-返回0
     * 2.2 虚拟键存在且未隐藏-返回虚拟键实际高度
     */
    public static int getNavigationBarHeightIfRoom(Context context) {
        if (navigationGestureEnabled(context)) {
            return 0;
        }
        return getCurrentNavigationBarHeight(((Activity) context));
    }

    /**
     * 全面屏（是否开启全面屏开关 0 关闭  1 开启）
     *
     * @param context
     * @return
     */
    public static boolean navigationGestureEnabled(Context context) {
        int val = Settings.Global.getInt(context.getContentResolver(), getDeviceInfo(), 0);
        return val != 0;
    }

    /**
     * 获取设备信息（目前支持几大主流的全面屏手机，亲测华为、小米、oppo、魅族、vivo都可以）
     */
    public static String getDeviceInfo() {
        String brand = Build.BRAND;
        if (TextUtils.isEmpty(brand)) return "navigationbar_is_min";

        if (brand.equalsIgnoreCase("HUAWEI")) {
            return "navigationbar_is_min";
        } else if (brand.equalsIgnoreCase("XIAOMI")) {
            return "force_fsg_nav_bar";
        } else if (brand.equalsIgnoreCase("VIVO")) {
            return "navigation_gesture_on";
        } else if (brand.equalsIgnoreCase("OPPO")) {
            return "navigation_gesture_on";
        } else {
            return "navigationbar_is_min";
        }
    }

    /**
     * 非全面屏下 虚拟键实际高度(隐藏后高度为0)
     *
     * @return
     */
    public static int getCurrentNavigationBarHeight(Activity activity) {
        if (isNavigationBarShown(activity)) {
            return getNavigationBarHeight(activity);
        } else {
            return 0;
        }
    }


    /**
     * 非全面屏下 虚拟按键是否打开
     *
     * @param activity
     * @return
     */
    public static boolean isNavigationBarShown(Activity activity) {
        //虚拟键的view,为空或者不可见时是隐藏状态
        View view = activity.findViewById(android.R.id.navigationBarBackground);
        if (view == null) {
            return false;
        }
        int visible = view.getVisibility();
        return visible != View.GONE && visible != View.INVISIBLE;
    }

    /**
     * 非全面屏下 虚拟键高度(无论是否隐藏)
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
