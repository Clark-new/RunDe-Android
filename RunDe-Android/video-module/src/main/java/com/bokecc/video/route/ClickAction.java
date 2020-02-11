package com.bokecc.video.route;

public class ClickAction {
    public static final int ON_CLICK_TOOLBAR_COURSE = 1;
    public static final int ON_CLICK_TOOLBAR_GIFT = 2;
    public static final int ON_CLICK_REWARD = 3;
    public static final int ON_CLICK_EVALUATE = 4;
    public static final int ON_CLICK_COUNSULT = 5;
    public int action;
    public ClickAction(int action) {
        this.action = action;
    }
}
