package com.vone.weibaoshuiguo.bean;

import java.io.Serializable;

public class MyContacts implements Serializable {
    private String name;
    private String note;
    private String phone;

    @Override
    public String toString() {
        return "MyContacts{" +
                "name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
