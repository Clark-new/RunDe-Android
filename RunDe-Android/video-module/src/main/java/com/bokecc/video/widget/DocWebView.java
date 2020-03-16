package com.bokecc.video.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bokecc.sdk.mobile.live.pojo.Marquee;
import com.bokecc.sdk.mobile.live.widget.DocView;
import com.bokecc.video.msg.MarqueeAction;

import java.util.ArrayList;
import java.util.List;

public class DocWebView extends DocView {
    private MarqueeView marqueeView;
    public DocWebView(Context context) {
        super(context);
    }

    public DocWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DocWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setMarquee(Activity activity, Marquee marquee){
        if (marqueeView!=null){
            removeView(marqueeView);
        }
        if (marquee != null && marquee.getAction() != null) {
            marqueeView = new MarqueeView(activity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            addView(marqueeView, params);
            List<MarqueeAction> marqueeActions = new ArrayList<>();
            for (int x = 0; x < marquee.getAction().size(); x++) {
                com.bokecc.sdk.mobile.live.pojo.MarqueeAction marqueeAction1 = marquee.getAction().get(x);
                MarqueeAction marqueeAction = new MarqueeAction();
                marqueeAction.setIndex(x);
                marqueeAction.setDuration(marqueeAction1.getDuration()*1000);
                marqueeAction.setStartXpos((float) marqueeAction1.getStart().getXpos());
                marqueeAction.setStartYpos((float) marqueeAction1.getStart().getYpos());
                marqueeAction.setStartAlpha((float) marqueeAction1.getStart().getAlpha());
                marqueeAction.setEndXpos((float) marqueeAction1.getEnd().getXpos());
                marqueeAction.setEndYpos((float) marqueeAction1.getEnd().getYpos());
                marqueeAction.setEndAlpha((float) marqueeAction1.getEnd().getAlpha());
                marqueeActions.add(marqueeAction);
            }
            marqueeView.setLoop(marquee.getLoop());
            marqueeView.setMarqueeActions(marqueeActions);
            if (marquee.getType().equals("text")) {
                marqueeView.setTextContent(marquee.getText().getContent());
                marqueeView.setTextColor(marquee.getText().getColor());
                marqueeView.setTextFontSize((int) (marquee.getText().getFont_size() * activity.getResources().getDisplayMetrics().density + 0.5f));
                marqueeView.setType(1);
            } else {
                marqueeView.setMarqueeImage(activity, marquee.getImage().getImage_url(), marquee.getImage().getWidth(), marquee.getImage().getHeight());
                marqueeView.setType(2);
            }

            marqueeView.start();
        }
    }
    public void removeMarquee(){
        if (marqueeView!=null){
            removeView(marqueeView);
        }
    }
}
