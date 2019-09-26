package com.vone.weibaoshuiguo.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.StaticInfo;
import com.vone.weibaoshuiguo.WbApplication;

import java.io.File;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class UpdateUtils {

    private static UpdateUtils instance;
    public static UpdateUtils getInstance(){
        if(instance == null ){
            instance = new UpdateUtils();
        }
        return instance;
    }

}
