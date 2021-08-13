package com.vone.weibaoshuiguo.bean;

import java.io.Serializable;

public class ContactRecord implements Serializable {
    /**姓名*/
    private String name;
    /**手机号*/
    private String number;
    /**通话日期*/
    private String date;
    /**时长*/
    private String duration;
    /**类型 呼入，呼出，未接*/
    private String type;
    /**通话时间*/
    private String time;

    @Override
    public String toString() {
        return "ContactRecord{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", type='" + type + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
