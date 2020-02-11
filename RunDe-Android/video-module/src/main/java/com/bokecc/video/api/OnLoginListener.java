package com.bokecc.video.api;

import com.bokecc.sdk.mobile.live.pojo.TemplateInfo;

public interface OnLoginListener {
    void onLogin(TemplateInfo info);

    void onException(Exception e);
}
