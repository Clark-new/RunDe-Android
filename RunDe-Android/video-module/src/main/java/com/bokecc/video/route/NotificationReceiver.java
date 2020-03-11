package com.bokecc.video.route;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_LAST = "hd_notification_last";
    public static final String ACTION_PLAY_PAUSE = "hd_notification_play_pause";
    public static final String ACTION_NEXT = "hd_notification_next";
    public static final String ACTION_DESTROY = "hd_notification_destroy";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("hd_notification_play_pause")){
            CCEventBus.getDefault().post(new NotificationPlayMsg(NotificationPlayMsg.PLAY_PAUSE));
        }else if(action.equals("hd_notification_last")){
            CCEventBus.getDefault().post(new NotificationPlayMsg(NotificationPlayMsg.LAST));
        }else if(action.equals("hd_notification_next")){
            CCEventBus.getDefault().post(new NotificationPlayMsg(NotificationPlayMsg.NEXT));
        }else if(action.equals("hd_notification_destroy")){
            CCEventBus.getDefault().post(new NotificationPlayMsg(NotificationPlayMsg.DESTROY));
        }
    }
}
