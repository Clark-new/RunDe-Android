package com.bokecc.video.widget.heart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bokecc.video.R;
import com.bokecc.video.utils.CommonUtils;

import java.lang.ref.WeakReference;
import java.util.Random;

public class HeartView extends RelativeLayout {

    private int width, height;
    private int iconWidth, iconHeight;
    private Random random = new Random();
    private Interpolator[] interpolators;
    private FrameLayout loveIconContainer;
    private ImageView heartView;

    private HeartBeatAnimation heartBeatAnimation;

    public HeartView(Context context) {
        this(context, null);
    }

    public HeartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initInterpolator();
        initChild();
    }

    private void initChild() {
        //漂浮爱心容器
        loveIconContainer = new FrameLayout(getContext());
        LayoutParams containerLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loveIconContainer.setLayoutParams(containerLp);
        //圆背景
        ImageView bg = new ImageView(getContext());
        bg.setBackgroundResource(R.drawable.live_praise_bg);
        Drawable heart = getResources().getDrawable(R.drawable.live_praise);
        float d = heart.getIntrinsicWidth() * Const.BG_RATIO;

        LayoutParams bgLp = new LayoutParams((int) d, (int) d);

        bgLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bgLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        bgLp.addRule(Gravity.CENTER);

        bgLp.bottomMargin = CommonUtils.dip2px(getContext(), Const.BOTTOM_MARGIN_DP);
        bgLp.rightMargin = CommonUtils.dip2px(getContext(), Const.RIGHT_MARGIN_DP);

        bg.setLayoutParams(bgLp);
        bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                heartBeatAnimation.beat();
                addLoveIcon();
                if (onButtonClickListener != null) {
                    onButtonClickListener.onClick(v);
                }
            }
        });

        //心跳
        heartView = new ImageView(getContext());
        //心跳动效
        heartBeatAnimation = new HeartBeatAnimation(heartView);
        heartView.setImageDrawable(heart);
        LayoutParams heartLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        heartLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        heartLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        heartLp.addRule(Gravity.CENTER);
        heartLp.bottomMargin = (int) (bgLp.bottomMargin + d / 2 - heart.getIntrinsicHeight() / 2) - Const.OFFSET_OF_HEART;
        heartLp.rightMargin = (int) (bgLp.rightMargin + d / 2 - heart.getIntrinsicWidth() / 2);
        heartView.setLayoutParams(heartLp);
        //圆背景
        addView(bg);
        //漂浮爱心容器
        addView(loveIconContainer);
        //心跳爱心
        addView(heartView);
    }

    private void initInterpolator() {
        interpolators = new Interpolator[]{
                new LinearInterpolator(),
                new AccelerateDecelerateInterpolator(),
                new AccelerateInterpolator(),
                new DecelerateInterpolator(),
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    private OnClickListener onButtonClickListener;

    public void setOnButtonClickListener(OnClickListener l) {
        onButtonClickListener = l;
    }

    @Override
    protected void onDetachedFromWindow() {
        heartBeatAnimation.destroy();
        removeAllViews();
        super.onDetachedFromWindow();
    }

    private void startAnimator(ImageView view) {
        if (height < 2 || width < 2) {
            return;
        }
        //曲线的两个顶点
        PointF pointF1 = new PointF(
                random.nextInt(width),
                random.nextInt(height / 2) + height / 2.5f);
        PointF pointF2 = new PointF(
                random.nextInt(width),
                random.nextInt(height / 2));
        PointF pointStart = new PointF(heartView.getLeft() + (float) heartView.getDrawable()
                .getIntrinsicWidth() / 2 - (float) iconWidth / 2,
                height - heartView.getDrawable().getIntrinsicHeight() - iconHeight);
        PointF pointEnd = new PointF(random.nextInt(width), random.nextInt(height / 2));

        //贝塞尔估值器
        BezierEvaluator evaluator = new BezierEvaluator(pointF1, pointF2);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, pointStart, pointEnd);
        animator.setTarget(view);
        animator.setDuration(Const.DURATION_FLY_LOVE_ICON);
        animator.addUpdateListener(new UpdateListener(view));
        animator.addListener(new AnimatorListener(view, (ViewGroup) view.getParent()));
        animator.setInterpolator(interpolators[random.nextInt(4)]);
        animator.start();
    }

    private int[] srcs = new int[]{R.drawable.heart0, R.drawable.heart1, R.drawable.heart2, R.drawable.heart3, R.drawable.heart4};
    private Random randomColor = new Random();

    public void addLoveIcon() {
        for (int i = 0; i < 5; i++) {
            ImageButton view = new ImageButton(getContext());
            view.setImageResource(srcs[randomColor.nextInt(srcs.length)]);
            view.setBackgroundColor(Color.TRANSPARENT);
            iconWidth = view.getDrawable().getIntrinsicWidth();
            iconHeight = view.getDrawable().getIntrinsicHeight();

            view.setLayoutParams(new FrameLayout.LayoutParams(iconWidth, iconHeight,
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL));
            loveIconContainer.addView(view);
            startAnimator(view);
        }
    }




    private static class UpdateListener implements ValueAnimator.AnimatorUpdateListener {

        private WeakReference<View> iv;

        UpdateListener(View iv) {
            this.iv = new WeakReference<>(iv);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointF = (PointF) animation.getAnimatedValue();
            View view = iv.get();
            if (null != view) {
                view.setX(pointF.x);
                view.setY(pointF.y);
                view.setAlpha(1 - animation.getAnimatedFraction() + 0.1f);
            }
        }
    }

    private static class AnimatorListener extends AnimatorListenerAdapter {

        private WeakReference<View> iv;
        private WeakReference<ViewGroup> parent;

        AnimatorListener(View iv, ViewGroup parent) {
            this.iv = new WeakReference<>(iv);
            this.parent = new WeakReference<>(parent);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            View view = iv.get();
            ViewGroup parent = this.parent.get();
            if (null != view
                    && null != parent) {
                parent.removeView(view);
            }
        }
    }

    static class Const {
        //心跳在父布局的内边距
        static final int BOTTOM_MARGIN_DP = 44 / 3;
        static final int RIGHT_MARGIN_DP = 44 / 3;

        //让心跳的圆心向下偏移一点距离（原本圆心重合），让爱心和背景的相对位置更协调。
        static final int OFFSET_OF_HEART = 5;

        //背景圆形和心跳的直径比
        static final float BG_RATIO = 1.0f;

        //心跳缩放比例
        static final float BEAT_ZOOM_RATIO = 1.3f;

        //漂浮爱心的持续时间
        static final int DURATION_FLY_LOVE_ICON = 3000;
    }

    //心跳动画
    private static class HeartBeatAnimation {
        private static final int FIRST_DURATION = 50;
        private static final int SECOND_DURATION = 300;


        private WeakReference<View> target;

        private Animation first;

        HeartBeatAnimation(View view) {
            target = new WeakReference<>(view);

            //第二阶段的动画。
            final Animation second = new ScaleAnimation(Const.BEAT_ZOOM_RATIO, 1f, Const.BEAT_ZOOM_RATIO, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5F);
            second.setDuration(SECOND_DURATION);
            //第一阶段的动画。
            first = new ScaleAnimation(1f, Const.BEAT_ZOOM_RATIO, 1f, Const.BEAT_ZOOM_RATIO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            first.setDuration(FIRST_DURATION);
            first.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {/**/}

                @Override
                public void onAnimationEnd(Animation animation) {
                    target.get().startAnimation(second);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {/**/}
            });
        }

        void beat() {
            if (target.get() != null) {
                target.get().startAnimation(first);
            }
        }

        //移除引用
        void destroy() {
            if (target.get() != null) {
                target.get().clearAnimation();
                target.clear();
            }
        }
    }

}
