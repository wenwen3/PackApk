package com.vone.weibaoshuiguo.bean;

import java.io.Serializable;

public class JsonProtocol<T> implements Serializable {
    private String command;

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static final String COMMAND_REFRESH = "command_refresh";
    public static final String COMMAND_WEB_JS_DATA_PEIZHI = "command_web_js_data_peizhi";
    public static final String COMMAND_WEB_START_SCAN = "command_web_start_scan";
    public static final String COMMAND_WEB_SEND_PHONE = "command_web_send_phone";
    public static final String COMMAND_SAVE_PHOTO = "command_save_photo";
    public static final String COMMAND_SHARE_DATA = "command_share_data";
    public static final String COMMAND_BACK_DATA = "command_back_data";

    public JsonProtocol(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
