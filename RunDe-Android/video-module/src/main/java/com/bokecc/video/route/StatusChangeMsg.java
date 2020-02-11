package com.bokecc.video.route;

import java.util.ArrayList;

public class StatusChangeMsg {
    public ArrayList<String> chatIds;
    public String status;

    public StatusChangeMsg(ArrayList<String> chatIds, String status) {
        this.chatIds = chatIds;
        this.status = status;
    }
}
