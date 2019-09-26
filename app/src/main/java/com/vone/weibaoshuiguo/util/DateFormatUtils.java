package com.vone.weibaoshuiguo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {

    private static DateFormatUtils instance;

    public static DateFormatUtils getInstance(){
        if(instance == null ){
            instance = new DateFormatUtils();
        }
        return instance;
    }

    public String formatTime(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return  simpleDateFormat.format(date.getTime());
    }

    public String formatTime(Date date,String formatType){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatType);
        return  simpleDateFormat.format(date.getTime());
    }

    public String formatTime(Date date,boolean hasMinute){
        if(hasMinute) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return simpleDateFormat.format(date.getTime());
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return  simpleDateFormat.format(date.getTime());
        }
    }
}
