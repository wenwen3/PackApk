package com.vone.weibaoshuiguo;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.vone.weibaoshuiguo.util.PackUtils;

public class MyJsInterface {
    private Context context;
    public MyJsInterface (Context context){
        this.context = context;
    }
    @JavascriptInterface
    public void playAudio(String url){
        Toast.makeText(context, "接收到了playAudio  url::"+url, Toast.LENGTH_SHORT).show();
        PackUtils.getInstance().playAudio(context,url);
    }
    @JavascriptInterface
    public void stopAudio(){
        Toast.makeText(context, "接收到了stopAudio", Toast.LENGTH_SHORT).show();
        PackUtils.getInstance().stopAudio(context);
    }
}
