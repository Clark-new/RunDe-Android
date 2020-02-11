/**
 *
 */
package com.bokecc.video.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.LruCache;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bokecc.video.R;
import com.bokecc.video.route.GiftMsg;
import com.bokecc.video.widget.RoundBackgroundColorSpan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description :文本中的emojb字符处理为表情图片
 */
public class SpannableCache {

    //初始化占位
    private Drawable mDrawable;

    private final LruCache<String, Bitmap> bitmapCache;

    private static SpannableCache mInstance = null;

    private SpannableCache() {
        mDrawable = new ColorDrawable(0x00ffffff);
        mDrawable.setCallback(null);
        mDrawable.setBounds(0, 0, 0, 0);

        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        bitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (!oldValue.isRecycled()) {
                    oldValue.recycle();
                }
            }
        };
    }

    public static SpannableCache get() {
        if (mInstance == null) {
            synchronized (SpannableCache.class) {
                if (mInstance == null) {
                    mInstance = new SpannableCache();
                }
            }
        }
        return mInstance;
    }


    /**
     * 提取礼物消息
     */
    public static GiftMsg extractGift(String source) {
        GiftMsg msg = null;
        String regexUrl = "\\[cem_\\S*]";
        Pattern pattern = Pattern.compile(regexUrl);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            String key = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            String url = key.substring(5, key.length() - 1);
            msg = new GiftMsg();
            msg.imageUrl = url;
            msg.content = source.substring(0, start);
            msg.exta = source.substring(end);
        }
        return msg;
    }


    public SpannableString getEmotionContent(int emotion_map_type, final Context context, final TextView tv, CharSequence source) {
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();
        String regexEmotion = "\\[em2_(([0-3][0-9]{1,2})|(q([12])))\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利用表情名字获取到对应的图片
            Integer imgRes = EmotionUtils.getImgByName(EmotionUtils.EMOTION_ALL_TYPE, key);
            if (imgRes != null) {
                // 压缩表情图片
                int size = (int) tv.getTextSize() * 13 / 10;
                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                if (imgRes == R.drawable.q_one || imgRes == R.drawable.q_two) {
                    size = CommonUtils.dip2px(context, 25);
                }
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    public  SpannableString setTeacherTag(CharSequence source, Context context) {
        SpannableString spannableString = new SpannableString(source);


        int resId = R.drawable.ic_teacher;
        Bitmap scaleBitmap = bitmapCache.get("[key_teacher]" + resId);
        int size = CommonUtils.dip2px(context, 13);
        if (scaleBitmap == null) {
            // 压缩表情图片
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);

            scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
            bitmapCache.put("[key_teacher]" + resId,scaleBitmap);
        }
        RoundBackgroundColorSpan backgroundColorSpan = new RoundBackgroundColorSpan(scaleBitmap,size,0xffFF454B,Color.WHITE);
        spannableString.setSpan(backgroundColorSpan, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public SpannableString getEmotionContent(int emotion_map_type, final Context context, int size, CharSequence source) {
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();
        String regexEmotion = "\\[em2_(([0-3][0-9]{1,2})|(q([12])))\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);
        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利用表情名字获取到对应的图片
            Integer imgRes = EmotionUtils.getImgByName(EmotionUtils.EMOTION_ALL_TYPE, key);
            if (imgRes != null) {
                Bitmap scaleBitmap = bitmapCache.get("[key12]" + ((int) imgRes));
                if (scaleBitmap == null) {
                    // 压缩表情图片
                    Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                    if (imgRes == R.drawable.q_one || imgRes == R.drawable.q_two) {
                        size = CommonUtils.dip2px(context, 25);
                    }
                    scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                    bitmapCache.put("[key12]" + ((int) imgRes),scaleBitmap);
                }
                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }


    public SpannableString setUrlImageSpannable(final Context context,
                                                SpannableString spannable,
                                                final TextView tv,
                                                CharSequence source) {
        if (spannable == null) {
            spannable = new SpannableString(source);
        }

        String regexUrlEmotion = "\\[cem_\\S*]";
        Pattern patternEmotion = Pattern.compile(regexUrlEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(source);
        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            String url = key.substring(5, key.length() - 1);
            tv.setTag(url);
            int size = (int) tv.getTextSize() * 13 / 10;
            Bitmap bitmap = bitmapCache.get(url);
            if (bitmap == null) {
                dowLoadImage(context, url, spannable, size, start, start + key.length(), tv);
                ImageSpan span = new ImageSpan(mDrawable);
                spannable.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                ImageSpan span = new ImageSpan(context, bitmap);
                spannable.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannable;
    }

    @SuppressLint("CheckResult")
    private void dowLoadImage(final Context context, final String url,
                              final SpannableString spannable,
                              final int size, final int start, final int end, final TextView textView) {
        RequestManager mRequestManager = Glide.with(context);
        RequestBuilder<File> mRequestBuilder = mRequestManager.downloadOnly();
        mRequestBuilder.load(url);
        mRequestBuilder.listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                try {
                    FileInputStream fis = new FileInputStream(resource);
                    Bitmap bmp = BitmapFactory.decodeStream(fis);
                    Bitmap scaleBitmap = Bitmap.createScaledBitmap(bmp, size, size, true);
                    if (bitmapCache.get(url) == null) {
                        bitmapCache.put(url, scaleBitmap);
                    }
                    bmp.recycle();
                    ImageSpan span = new ImageSpan(context, scaleBitmap);
                    spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (url.equals(textView.getTag())) {
                        textView.setText(spannable);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        mRequestBuilder.preload();
    }


    public SpannableString createUrlSpannable(Context context, SpannableString spannable,
                                              CharSequence source, final int size, ImageDownLoadListener listener) {
        String regexUrlEmotion = "\\[cem_\\S*]";
        Pattern patternEmotion = Pattern.compile(regexUrlEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(source);

        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            String url = key.substring(5, key.length() - 1);
            Bitmap bitmap = bitmapCache.get(url);
            if (bitmap == null) {
                startDownLoadImage(context, spannable,
                        url, size,
                        start, start + key.length(),
                        listener);
                ImageSpan span = new ImageSpan(mDrawable);
                spannable.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                ImageSpan span = new ImageSpan(context, bitmap);
                spannable.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannable;
    }


    @SuppressLint("CheckResult")
    private void startDownLoadImage(final Context context, final SpannableString spannable,
                                    final String url, final int size,
                                    final int start, final int end,
                                    final ImageDownLoadListener listener) {
        RequestManager mRequestManager = Glide.with(context);
        RequestBuilder<File> mRequestBuilder = mRequestManager.downloadOnly();
        mRequestBuilder.load(url);
        mRequestBuilder.listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                try {
                    FileInputStream fis = new FileInputStream(resource);
                    Bitmap bmp = BitmapFactory.decodeStream(fis);
                    Bitmap scaleBitmap = Bitmap.createScaledBitmap(bmp, size, size, true);
                    bmp.recycle();
                    if (bitmapCache.get(url) == null) {
                        bitmapCache.put(url, scaleBitmap);
                    }
                    ImageSpan span = new ImageSpan(context, scaleBitmap);
                    spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (listener != null) {
                        listener.onImageDownLoadSuccess(scaleBitmap, start, end);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        mRequestBuilder.preload();
    }

    public void release() {
//        bitmapCache.
    }

    public interface ImageDownLoadListener {
        void onImageDownLoadSuccess(Bitmap bmp, int start, int end);
    }
}
