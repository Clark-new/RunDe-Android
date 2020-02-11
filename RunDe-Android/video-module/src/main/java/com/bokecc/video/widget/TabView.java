package com.bokecc.video.widget;

import android.content.Context;
import android.graphics.Color;

import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bokecc.video.R;


public class TabView extends LinearLayout {
    private static final String TAG = "TabView";
    private TextView mTitleView;
    private View mLineView;

    public TabView(Context context) {
        this(context, null);
    }

    public TabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.layout_tabview_item, this, true);
        mTitleView = findViewById(R.id.id_title_tv);
        mLineView = findViewById(R.id.id_line_view);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure: ");
        measureChild(mTitleView, widthMeasureSpec, heightMeasureSpec);
        float allTextWidth = Layout.getDesiredWidth(mTitleView.getText(), mTitleView.getPaint());
        int length = mTitleView.getText().toString().length();
        int lineWidth = (int) (allTextWidth * 2 / length);
        int widthSpec = MeasureSpec.makeMeasureSpec(lineWidth, MeasureSpec.EXACTLY);
        measureChild(mLineView, widthSpec, heightMeasureSpec);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void select(boolean select) {
        if(select){
            mTitleView.setTextColor(getResources().getColor(R.color.pink));
            mLineView.setBackground(getResources().getDrawable(R.drawable.tab_view_select_bg));
        }else{
            mTitleView.setTextColor(getResources().getColor(R.color.text_color));
            mLineView.setBackground(getResources().getDrawable(R.drawable.tab_view_normal_bg));
        }
    }
}
