package com.vone.weibaoshuiguo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NeNotificationService2  extends NotificationListenerService {
    private String TAG = "NeNotificationService2";
    private String host = "";
    private String key = "";
    private String yhm = "";
    private Thread newThread = null;
    private PowerManager.WakeLock mWakeLock = null;


    //申请设备电源锁
    @SuppressLint("InvalidWakeLockTag")
    public void acquireWakeLock(Context context) {
        if (null == mWakeLock)
        {
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "WakeLock");
            if (null != mWakeLock)
            {
                mWakeLock.acquire();
            }
        }
    }
    //释放设备电源锁
    public void releaseWakeLock() {
        if (null != mWakeLock)
        {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }

    private int nextNum = 30;
    //心跳进程
    public void initAppHeart(){
        Log.d(TAG, "开始启动心跳线程");
        if (newThread!=null){
            return;
        }
        acquireWakeLock(this);

    }

    //当收到一条消息的时候回调，sbn是收到的消息
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "接受到通知消息");
        SharedPreferences read = getSharedPreferences("vone", MODE_PRIVATE);
        host = read.getString("host", "");
        key = read.getString("key", "");


        Notification notification = sbn.getNotification();
        String pkg = sbn.getPackageName();
        if (notification != null) {
            Bundle extras = notification.extras;
            if (extras != null) {
                String title = extras.getString(NotificationCompat.EXTRA_TITLE, "");
                String content = extras.getString(NotificationCompat.EXTRA_TEXT, "");
                Log.d(TAG, "**********************");
                Log.d(TAG, "包名:" + pkg);
                Log.d(TAG, "标题:" + title);
                Log.d(TAG, "内容:" + content);
                Log.d(TAG, "**********************");


                if (pkg.equals("com.eg.android.AlipayGphone")){
                    if (content!=null && !content.equals("")) {
                        if (content.indexOf("通过扫码向你付款")!=-1 || content.indexOf("成功收款")!=-1){
                            String money = getMoney(content);
                            if (money!=null){
                                Log.d(TAG, "onAccessibilityEvent: 匹配成功： 支付宝 到账 " + money);
                                appPush(2, Double.valueOf(money),title,content);
                            }
                        }else{
                            appPushNotify(title,content,pkg);
                        }
                    }

                }else if(pkg.equals("com.tencent.mm")){

                    if (content!=null && !content.equals("")){
                        if (title.equals("微信支付") || title.equals("微信收款助手") || title.equals("微信收款商业版")){
                            String money = getMoney(content);
                            if (money!=null){
                                Log.d(TAG, "onAccessibilityEvent: 匹配成功： 微信到账 "+ money);
                                appPush(1,Double.valueOf(money), title, content);
                            }
                        }else{
                            appPushNotify(title,content,pkg);
                        }
                    }

                }else if(pkg.equals("com.vone.qrcode")){

                    if (content.equals("这是一条测试推送信息，如果程序正常，则会提示监听权限正常")){
                        Handler handlerThree=new Handler(Looper.getMainLooper());
                        handlerThree.post(new Runnable(){
                            public void run(){
                                Toast.makeText(getApplicationContext() ,"监听正常！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    appPushNotify(title,content,pkg);
                }



            }
        }

    }
    //当移除一条消息的时候回调，sbn是被移除的消息
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
    //当连接成功时调用，一般在开启监听后会回调一次该方法
    @Override
    public void onListenerConnected() {
        //开启心跳线程
        initAppHeart();

    }





    public void appPush(int type, double price, String title, String content){
        SharedPreferences read = getSharedPreferences("vone", MODE_PRIVATE);
        host = read.getString(StaticInfo.HOST, "");
        key = read.getString(StaticInfo.KEY, "");
        yhm = read.getString(StaticInfo.YHM, "");

        if(TextUtils.isEmpty(yhm)){
            return;
        }

        Log.d(TAG, "onResponse  push: 开始:"+type+"  "+price);
        String t = String.valueOf(new Date().getTime());
        String sign = md5(t + key);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("t",t);
        requestParams.addBodyParameter("laiyuan",title);
        requestParams.addBodyParameter("neirong",content);
        requestParams.addBodyParameter("type",type+"");
        requestParams.addBodyParameter("price",price+"");
        requestParams.addBodyParameter("sign",sign);
        requestParams.addBodyParameter("yhm",yhm);
        String url = host;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.d(TAG, "onResponse  addPush: "+responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    public void appPushNotify(String title,String message,String pkg){
        if(title == null || TextUtils.isEmpty(title)|| message == null || TextUtils.isEmpty(message)){
            return;
        }
        SharedPreferences read = getSharedPreferences("vone", MODE_PRIVATE);
        host = read.getString(StaticInfo.HOST, "");
        key = read.getString(StaticInfo.KEY, "");
        yhm = read.getString(StaticInfo.YHM, "");
        int jttzqx = read.getInt("jttzqx", 0);

        if(TextUtils.isEmpty(yhm)){
            return;
        }

        if(jttzqx == 0){
            return;
        }

        String t = String.valueOf(new Date().getTime());
        String sign = md5(t + key);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("t",t);
        requestParams.addBodyParameter("type", 6 + "");
        requestParams.addBodyParameter("price","0");
        requestParams.addBodyParameter("laiyuan",title);
        requestParams.addBodyParameter("neirong",message);
        requestParams.addBodyParameter("sign",sign);
        requestParams.addBodyParameter("yhm",yhm);
        requestParams.addBodyParameter("zitype",pkg);
        String url = host;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.d(TAG, "onResponse  addPush: "+responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    public static String getMoney(String content){

        int index = content.indexOf("]");
        if (index!=-1){
            content = content.substring(index+1);
        }


        Pattern p = Pattern.compile("([0-9]\\d*\\.?\\d*)|(0\\.\\d*[0-9])");
        Matcher m = p.matcher(content);
        boolean result = m.find();
        String find_result = null;
        if (result) {
            find_result = m.group(1);
        }
        return find_result;
    }
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


}
