package com.vone.weibaoshuiguo;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.bean.JsonProtocol;
import com.vone.weibaoshuiguo.util.BroadCastUtils;
import com.vone.weibaoshuiguo.util.PackUtils;

public class MyJsInterface {
    private Context context;
    public MyJsInterface (Context context){
        this.context = context;
    }
    @JavascriptInterface
    public void playAudio(String url){
        PackUtils.getInstance().playAudio(context,url, R.raw.lingsheng);
    }
    @JavascriptInterface
    public void stopAudio(){
        PackUtils.getInstance().stopAudio(context);
    }

    @JavascriptInterface
    public void startScanEWM(String url){

        JsonProtocol<String> jsonProtocol = new JsonProtocol<>(JsonProtocol.COMMAND_WEB_START_SCAN);
        jsonProtocol.setData(url);
        BroadCastUtils.getInstance().sendBroadCast(jsonProtocol);
    }

    @JavascriptInterface
    public void startConfigure(String configure){

        JsonProtocol<String> jsonProtocol = new JsonProtocol<>(JsonProtocol.COMMAND_WEB_JS_DATA_PEIZHI);
        jsonProtocol.setData(configure);
        BroadCastUtils.getInstance().sendBroadCast(jsonProtocol);
    }
    @JavascriptInterface
    public void sendCallRecord(int num){

        JsonProtocol<Integer> jsonProtocol = new JsonProtocol<>(JsonProtocol.COMMAND_WEB_SEND_PHONE);
        jsonProtocol.setData(num);
        BroadCastUtils.getInstance().sendBroadCast(jsonProtocol);
    }

    @JavascriptInterface
    public void savePhoto(String url){

        JsonProtocol<String> jsonProtocol = new JsonProtocol<>(JsonProtocol.COMMAND_SAVE_PHOTO);
        jsonProtocol.setData(url);
        BroadCastUtils.getInstance().sendBroadCast(jsonProtocol);
    }

    @JavascriptInterface
    public void canshuData(String message){
        JSONObject jsonObject = JSON.parseObject(message);
        if(jsonObject != null ){
            if(jsonObject.containsKey("key") && jsonObject.get("key") != null){
                String key = jsonObject.getString("key");
                JsonProtocol<String> jsonProtocol = new JsonProtocol<>(JsonProtocol.COMMAND_BACK_DATA);
                jsonProtocol.setData(key);
                BroadCastUtils.getInstance().sendBroadCast(jsonProtocol);
            }
        }
    }

    @JavascriptInterface
    public void shareData(String message){

        JSONObject jsonObject = JSON.parseObject(message);
        if(jsonObject.containsKey("neirong1") && jsonObject.get("neirong1") != null
                    && jsonObject.containsKey("neirong2") && jsonObject.get("neirong2") != null
                    && jsonObject.containsKey("neirong3") && jsonObject.get("neirong3") != null ){
            String neirong1 = jsonObject.getString("neirong1");
            String neirong2 = jsonObject.getString("neirong2");
            String neirong3 = jsonObject.getString("neirong3");
            String shareMessage =  neirong1 +"\n"+neirong2+"\n"+neirong3;
            Log.d("xgw","share:::"+shareMessage);
            JsonProtocol<String> jsonProtocol = new JsonProtocol<>(JsonProtocol.COMMAND_SHARE_DATA);
            jsonProtocol.setData(shareMessage);
            BroadCastUtils.getInstance().sendBroadCast(jsonProtocol);
        }
    }
}
