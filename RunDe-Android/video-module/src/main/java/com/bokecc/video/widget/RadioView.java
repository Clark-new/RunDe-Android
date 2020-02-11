package com.bokecc.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bokecc.video.R;


public class RadioView extends LinearLayout {

    private ImageView imageView;
    private boolean select = false;
    public RadioView(Context context) {
        this(context,null);
    }

    public RadioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context ctx){
        LayoutInflater.from(ctx).inflate(R.layout.item_radio_view,this,true);
        imageView = findViewById(R.id.id_radio_image);
    }

    public void select(boolean select){
        this.select = select;
        if(select){
            imageView.setImageResource(R.drawable.live_radio_on);
        }else{
            imageView.setImageResource(R.drawable.live_radio_normal);
        }
    }

    public boolean isSelect(){
        return select;
    }
}
