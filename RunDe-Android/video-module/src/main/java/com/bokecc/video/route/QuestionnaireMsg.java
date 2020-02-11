package com.bokecc.video.route;

/**
 * 问卷消息
 */
public class QuestionnaireMsg extends Message {

    public static final int QUESTION = 0;
    public static final int ANSWER = 1;
    public static final int CLOSE = 2;
    public Object extra;

    public QuestionnaireMsg(int code, Object extra) {
        super(code);
        this.extra = extra;
    }

}
