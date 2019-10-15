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

    public JsonProtocol(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
