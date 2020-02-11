package com.bokecc.video.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

public class RoundBackgroundColorSpan extends ReplacementSpan {
    private int bgColor;
    private int textColor;
    private Bitmap bitmap;
    private int btSize;
    public RoundBackgroundColorSpan(Bitmap bitmap, int size,int bgColor, int textColor) {
        super();
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.bitmap = bitmap;
        this.btSize = size;
    }
    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return ((int)paint.measureText(text, start, end)+60);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color1 = paint.getColor();
        paint.setColor(this.bgColor);
        canvas.drawRoundRect(new RectF(x, top+1, x + ((int) paint.measureText(text, start, end)+30+btSize), bottom-1), 15, 15, paint);
        paint.setColor(this.textColor);
        canvas.drawText(text, start, end, x+20+btSize, y, paint);
        canvas.drawBitmap(bitmap,x+10,((bottom-top)-btSize)/2.0f,null);
        paint.setColor(color1);
    }
}
