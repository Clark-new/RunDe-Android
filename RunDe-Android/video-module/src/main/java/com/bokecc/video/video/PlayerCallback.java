package com.bokecc.video.video;

public interface PlayerCallback {

    void onPrepared();

    void onStartRender();

    void onBufferEnd();

    void onBufferStart();

    void onVideoSizeChanged(int videoWidth, int videoHeight);

    void onError();
}
