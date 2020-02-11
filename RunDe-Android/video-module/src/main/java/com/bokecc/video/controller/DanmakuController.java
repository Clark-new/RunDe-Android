package com.bokecc.video.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.video.R;
import com.bokecc.video.utils.CommonUtils;
import com.bokecc.video.utils.SpannableCache;
import com.bokecc.video.widget.SwitchButton;

import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * 弹幕
 */
public abstract class DanmakuController extends BaseVideoController {

    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser;
    private SwitchButton mDanmuBtn;
    private TextView mDanmuTxt;

    public DanmakuController(@NonNull Context context) {
        super(context);
    }

    public DanmakuController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DanmakuController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDanmaku();
    }

    private void initDanmaku() {
        mDanmuBtn = findViewById(R.id.id_danmu_btn);
        mDanmuTxt = findViewById(R.id.id_danmu_txt);

        BaseCacheStuffer.Proxy cacheStufferAdapter = new BaseCacheStuffer.Proxy() {

            @Override
            public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
                if (danmaku.text != null) {
                    final SpannableString emotionContent = SpannableCache.get().getEmotionContent(-1, getContext(), (int) (danmaku.textSize * 1.3), danmaku.text);
                    danmaku.text = SpannableCache.get().createUrlSpannable(getContext(), emotionContent, danmaku.text, (int) danmaku.textSize, new SpannableCache.ImageDownLoadListener() {
                        @Override
                        public void onImageDownLoadSuccess(Bitmap bmp, int start, int end) {
                            ImageSpan span = new ImageSpan(getContext(), bmp);
                            emotionContent.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            danmaku.text = emotionContent;
                            if (danmakuView != null) {
                                danmakuView.invalidateDanmaku(danmaku, false);
                            }
                        }
                    });
                    if (danmakuView != null) {
                        danmakuView.invalidateDanmaku(danmaku, true);
                    }
                }
            }

            @Override
            public void releaseResource(BaseDanmaku danmaku) {
//                ((SpannableString)danmaku.text).removeSpan();
            }
        };


        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行

        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        danmakuContext = DanmakuContext.create();
        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE, 1)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
                .setDuplicateMergingEnabled(false)
                .preventOverlapping(overlappingEnablePair)
                .setMaximumLines(maxLinesPair)
                .setCacheStuffer(new SpannedCacheStuffer(), cacheStufferAdapter);

        if (danmakuView != null) {
            danmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {
                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                }

                @Override
                public void prepared() {
                    danmakuView.start();
                }
            });


            parser = getDefaultDanmakuParser();
            danmakuView.prepare(parser, danmakuContext);
            danmakuView.enableDanmakuDrawingCache(true);
        }

        mDanmuBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    danmakuView.show();
                } else {
                    danmakuView.hide();
                }
            }
        });

    }

    public BaseDanmakuParser getDefaultDanmakuParser() {
        return new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        };
    }


    public void addDanmaku(String text, boolean islive) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || danmakuView == null) {
            return;
        }
        danmaku.padding = 5;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = islive;
        danmaku.text = text;
        danmaku.setTime(danmakuView.getCurrentTime() + 1200);
        danmaku.textSize = CommonUtils.sp2px(getContext(), 14);
        danmaku.textColor = Color.WHITE;
        danmakuView.addDanmaku(danmaku);
    }

    public void setLive(boolean isLive) {
        if (isLive) {
            mDanmuBtn.setVisibility(VISIBLE);
            mDanmuTxt.setVisibility(VISIBLE);
        } else {
            mDanmuBtn.setVisibility(GONE);
            mDanmuTxt.setVisibility(GONE);
        }
    }

    public void resume() {
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.resume();
        }
    }

    public void pause() {
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    public void release() {
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

}
