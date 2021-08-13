package com.vone.weibaoshuiguo.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.StaticInfo;
import com.vone.weibaoshuiguo.util.PackUtils;
import com.vone.weibaoshuiguo.util.RequestUtils;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class HeartService extends Service {

    private Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startHeart();
    }

    public long jg = 10;
    private int num = 0;
    private void startHeart() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(PackUtils.getInstance().getDsxtkg() == 0){
                    return;
                }
                if(PackUtils.getInstance().getXintiaojiekou() == null || TextUtils.isEmpty(PackUtils.getInstance().getXintiaojiekou())){
                    return;
                }
                if(num < jg){
                    num++;
                    return;
                }else{
                    num = 0;
                }
                SharedPreferences read = getSharedPreferences("vone", MODE_PRIVATE);
                String yhm = read.getString(StaticInfo.YHM, "");
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("mac", PackUtils.getInstance().getMacAddress(getApplicationContext()));
                hashMap.put("biaoshi",PackUtils.ONLY_TAG);
                hashMap.put("yhm",yhm);
                RequestUtils.getInstance().request(hashMap, PackUtils.getInstance().getXintiaojiekou(), new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if(responseInfo != null &&responseInfo.result != null ){
                            JSONObject jsonObject = JSON.parseObject(responseInfo.result);
                            if(jsonObject.containsKey("naozhong") && jsonObject.get("naozhong") != null){
                                int naozhong = jsonObject.getInteger("naozhong");
                                if(naozhong == 1){
                                    if(jsonObject.containsKey("mp3") && jsonObject.get("mp3") != null){
                                        String mp3 = jsonObject.getString("mp3");
                                        PackUtils.getInstance().playAudio(getApplicationContext(),mp3, R.raw.lingsheng);
                                    }
                                    if(jsonObject.containsKey("jg") && jsonObject.get("jg") != null){
                                        jg = jsonObject.getInteger("jg");
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }
        },1000,1000);
    }
}
