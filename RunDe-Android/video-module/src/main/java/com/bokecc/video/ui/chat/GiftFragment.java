package com.bokecc.video.ui.chat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.video.R;
import com.bokecc.video.route.GiftMsg;
import com.bokecc.video.utils.CommonUtils;
import com.bokecc.video.widget.MagicTextView;
import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 显示打赏礼物
 */
public abstract class GiftFragment extends KeyBoardFragment {

    public static final String GIFT_TAG = "x";
    public static final String REWARD_TAG = "¥";
    private static final int MSG_TAKE_GIFT = 1;
    private static final int MSG_REMOVE_GIFT = 2;

    private LinearLayout giftGroup;
    private NumberAnim giftNumberAnim;
    private Animation outAnim;
    private Animation inAnim;
    private int giftViewTopMargin;

    private BlockingDeque<GiftMsg> mGiftMsgQueue = new LinkedBlockingDeque<>();
    private Thread takeThread;
    private boolean isExit = false;
    private long lastMsgTime = 0;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TAKE_GIFT:
                    showGift((GiftMsg) msg.obj);
                    break;
                case MSG_REMOVE_GIFT:
                    int childCount = giftGroup.getChildCount();
                    if (childCount > 0) {
                        long nowTime = System.currentTimeMillis();
                        View child = giftGroup.getChildAt(0);
                        long lastUpdateTime = (long) child.getTag();
                        if (nowTime - lastUpdateTime > 2800) {
                            removeGiftView(0);
                        }
                    } else {
                        cancelTimer();
                    }
                    break;
            }
        }
    };

    private Timer mClearGiftTimer;
    private TimerTask mClearTimerTask;

    @Override
    protected void initData() {
        super.initData();
        giftViewTopMargin = CommonUtils.dip2px(getContext(), 10);
    }

    @Override
    protected void initView() {
        super.initView();
        giftGroup = findViewById(R.id.id_gift_group);
        initAnim();
        startTakeMsg();
    }


    /**
     * 初始化动画
     */
    private void initAnim() {
        giftNumberAnim = new NumberAnim(); // 初始化数字动画
        inAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.gift_in); // 礼物进入时动画
        outAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.gift_out); // 礼物退出时动画
    }


    /**
     * 消费礼物打赏消息
     */
    private void startTakeMsg() {
        takeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isExit) {
                    try {
                        GiftMsg msg = mGiftMsgQueue.takeFirst();
                        while (System.currentTimeMillis() - lastMsgTime < 1000) {
                            Thread.sleep(100);
                        }
                        lastMsgTime = System.currentTimeMillis();
                        Message.obtain(mHandler, MSG_TAKE_GIFT, msg).sendToTarget();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        takeThread.start();
    }

    /**
     * 添加礼物打赏消息
     */
    public void addGiftMsg(GiftMsg msg) {
        mGiftMsgQueue.addLast(msg);
    }


    private void cancelTimer() {
        if (mClearGiftTimer != null) {
            mClearGiftTimer.cancel();
            mClearGiftTimer = null;
            mClearTimerTask.cancel();
            mClearTimerTask = null;
        }
    }

    /**
     * 定时清理礼物列表信息
     */
    private void updateTimer() {
        cancelTimer();
        mClearGiftTimer = new Timer();
        mClearTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message.obtain(mHandler, MSG_REMOVE_GIFT).sendToTarget();
            }
        };
        mClearGiftTimer.schedule(mClearTimerTask, 2000, 1000);
    }

    /**
     * 刷礼物,更新定时器
     */
    private void showGift(final GiftMsg msg) {

        if (isExit) return;
        updateTimer();

        // 判断礼物列表是否已经有2个了，如果有那么删除掉一个没更新过的, 然后再添加新进来的礼物，始终保持只有3个
        if (giftGroup.getChildCount() >= 2) {
            // 获取前2个元素的最后更新时间
            removeGiftView(0);
        }


        // 获取礼物
        View newGiftView = getNewGiftView(msg);
        giftGroup.addView(newGiftView);


        // 播放动画
        newGiftView.startAnimation(inAnim);
        final MagicTextView mtv_giftNum = newGiftView.findViewById(R.id.mtv_giftNum);
        inAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int num = -1;
                if (msg.exta != null) {
                    if (GIFT_TAG.equals(msg.exta.substring(0, 1))) {
                        num = Integer.parseInt(msg.exta.substring(1));
                    }
                }
                giftNumberAnim.showAnimator(mtv_giftNum, num);
            }
        });
    }

    /**
     * 获取礼物
     */
    private View getNewGiftView(GiftMsg tag) {

        // 添加标识, 该view若在layout中存在，就不在生成（用于findViewWithTag判断是否存在）
        View giftView = LayoutInflater.from(getContext()).inflate(R.layout.item_gift, null);

        // 添加标识, 记录生成时间，回收时用于判断是否是最新的，回收最老的
        giftView.setTag(System.currentTimeMillis());

        TextView userNameTv = giftView.findViewById(R.id.id_user_name_tv);
        userNameTv.setText(tag.userName);

        TextView contentTv = giftView.findViewById(R.id.id_content_tv);
        contentTv.setText(tag.content);

        // 添加标识，记录礼物个数
        MagicTextView giftNumTv = giftView.findViewById(R.id.mtv_giftNum);
        giftNumTv.setTag(tag.imageUrl);
        if (tag.exta != null && !GIFT_TAG.equals(tag.exta.substring(0, 1))) {
            giftNumTv.setText(tag.exta);
        }

        ImageView giftImageView = giftView.findViewById(R.id.id_gift_img);
        Glide.with(getActivity()).load(tag.imageUrl).into(giftImageView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = giftViewTopMargin;
        giftView.setLayoutParams(lp);

        return giftView;
    }

    /**
     * 移除礼物列表里的giftView
     */
    private void removeGiftView(int index) {
        // 移除列表，外加退出动画
        final View removeGiftView = giftGroup.getChildAt(index);
        outAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                giftGroup.removeView(removeGiftView);
            }
        });
        removeGiftView.startAnimation(outAnim);
    }

    /**
     * 送的礼物后面的数字动画
     */
    public class NumberAnim {
        private Animator lastAnimator;

        void showAnimator(final TextView v, final int value) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.cancel();
                lastAnimator.end();
            }
            ObjectAnimator animScaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.3f, 1.0f);
            ObjectAnimator animScaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animScaleX, animScaleY);
            animSet.setDuration(1000);
            lastAnimator = animSet;
            animSet.setInterpolator(new LinearInterpolator() {
                @Override
                public float getInterpolation(float input) {
                    if (value > 0) {
                        int ret = (int) (value * input);
                        v.setText("X " + ret);
                    }
                    return super.getInterpolation(input);
                }
            });
            animSet.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isExit = true;
        takeThread.interrupt();
        cancelTimer();
    }

}
