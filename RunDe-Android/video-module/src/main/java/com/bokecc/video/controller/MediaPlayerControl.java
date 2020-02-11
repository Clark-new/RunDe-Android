package com.bokecc.video.controller;

public interface MediaPlayerControl {

    void mobileStart();

    void start();

    void pause();

    long getDuration();

    long getCurrentPosition();

    void seekTo(long pos);

    boolean isPlaying();

    int getBufferedPercentage();

    void setSpeed(float speed);

    void replay(boolean resetPosition);
}