package com.bokecc.video.route;

public class Message {
    private int type;
    private String msg;
    private int code;


    public Message() {
    }

    public Message(int code) {
        this.code = code;
    }

    public Message(int type, int code) {
        this.type = type;
        this.code = code;
    }

    public Message(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Message(int type, String msg, int code) {
        this.type = type;
        this.msg = msg;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
