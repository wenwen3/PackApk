package com.vone.weibaoshuiguo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import com.vone.weibaoshuiguo.util.Constant;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity{


    private TextView txthost;
    private TextView txtkey;

    private boolean isOk = false;
    private static String TAG = "MainActivity";

    private static String host="";
    private static String key="";
    private static String wz="";
    private static String yhm="";

    int id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        txthost = (TextView) findViewById(R.id.txt_host);
        txtkey = (TextView) findViewById(R.id.txt_key);



        //检测通知使用权是否启用
        if (!isNotificationListenersEnabled()) {
            //跳转到通知使用权页面
            gotoNotificationAccessSetting();
        }
        //重启监听服务
        toggleNotificationListenerService(this);



        //读入保存的配置数据并显示
        SharedPreferences read = getSharedPreferences("vone", MODE_PRIVATE);
        host = read.getString(StaticInfo.HOST, "");
        yhm = read.getString(StaticInfo.YHM, "");
        wz = read.getString(StaticInfo.WZ, "");
        key = read.getString(StaticInfo.KEY, "");

        if (host!=null && yhm!=null && !TextUtils.isEmpty(host) && !TextUtils.isEmpty(yhm)){
            txthost.setText(" 通知地址："+host);
            txtkey.setText(" 用户名："+yhm);
            isOk = true;
        }
    }

    //扫码配置
    public void startQrCode(View v) {
        // 申请相机权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQ_PERM_EXTERNAL_STORAGE);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(MainActivity.this, com.vone.weibaoshuiguo.CaptureActivity.class);
        startActivityForResult(intent, 0);
    }
    //手动配置
    public void doInput(View v){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入配置数据").setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String scanResult = inputServer.getText().toString();
                getSuccess(scanResult);
            }
        });
        builder.show();

    }

    public void getMoney(View view){
        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("jtwz",host);
        requestParams.addBodyParameter("yhm",yhm);
        httpUtils.send(HttpRequest.HttpMethod.POST, "http://wbsq.169hezuo.com/Weibao/dz", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if(result != null ){
                    JSONObject jsonObject = JSON.parseObject(result);
                    int code = jsonObject.getInteger("code");
                    if(code == 1){
                        String url = jsonObject.getString("url");
                        WebViewActivity.showActivity(MainActivity.this,url);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }
    public void openUrl(View view){
        if(!isOk){
            Toast.makeText(this, "未配置成功，请先配置", Toast.LENGTH_SHORT).show();
            return;
        }
        WebViewActivity.showActivity(MainActivity.this,wz);
    }

    //检测心跳
    public void doStart(View view) {
        if (isOk==false){
            Toast.makeText(MainActivity.this, "请您先配置!", Toast.LENGTH_SHORT).show();
            return;
        }

        String t = String.valueOf(new Date().getTime());
        String sign = md5(t+key);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("t",t);
        requestParams.addBodyParameter("type","9");
        requestParams.addBodyParameter("price","0");
        requestParams.addBodyParameter("sign",sign);
        requestParams.addBodyParameter("yhm",yhm);
        String url = host+"/Jt/js";
        httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String body = responseInfo.result;
                getJGTime(body);

                Toast.makeText(MainActivity.this, "心跳返回"+body, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(MainActivity.this, "心跳状态错误，请检查配置是否正确!", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences("vone", MODE_PRIVATE).edit();
                editor.putInt(StaticInfo.JG, 100);
                editor.apply();
            }
        });

    }

    private void getJGTime(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        if( jsonObject != null ) {
            int jg = jsonObject.getInteger("jg");
            if(jg != 0) {
                SharedPreferences.Editor editor = getSharedPreferences("vone", MODE_PRIVATE).edit();
                editor.putInt(StaticInfo.JG, jg);
                editor.apply();
            }
        }
    }

    //检测监听
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void checkPush(View v){

        Notification mNotification;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1",
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);
            mNotificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(this,"1");

            mNotification = builder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("这是一条测试推送信息，如果程序正常，则会提示监听权限正常")
                    .setContentTitle("微宝监听")
                    .setContentText("这是一条测试推送信息，如果程序正常，则会提示监听权限正常")
                    .build();
        }else{
            mNotification = new Notification.Builder(MainActivity.this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("这是一条测试推送信息，如果程序正常，则会提示监听权限正常")
                    .setContentTitle("微宝监听")
                    .setContentText("这是一条测试推送信息，如果程序正常，则会提示监听权限正常")
                    .build();
        }

        //Toast.makeText(MainActivity.this, "已推送信息，如果权限，那么将会有下一条提示！", Toast.LENGTH_SHORT).show();



        mNotificationManager.notify(id++, mNotification);
    }

    //各种权限的判断
    private void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NeNotificationService2.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, NeNotificationService2.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Toast.makeText(MainActivity.this, "监听服务启动中...", Toast.LENGTH_SHORT).show();
    }
    public boolean isNotificationListenersEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean gotoNotificationAccessSetting() {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;

        } catch (ActivityNotFoundException e) {//普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                startActivity(intent);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Toast.makeText(this, "对不起，您的手机暂不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
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
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");//mdiatype 这个需要和服务端保持一致

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            getSuccess(scanResult);
        }
        if (resultCode == com.vone.weibaoshuiguo.CaptureActivity.EWM_RESULT) {
            String result = data.getStringExtra("result");
            getSuccess(result);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, Constant.REQ_PERM_SMS);
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, Constant.REQ_PERM_SMS);
        }
    }


    private void getSuccess(String scanResult) {
        final String[] tmp = scanResult.split(",");
        if (tmp.length!=4){
            Toast.makeText(MainActivity.this, "二维码错误，请您扫描网站上显示的二维码!", Toast.LENGTH_SHORT).show();
            return;
        }

        final String t = String.valueOf(new Date().getTime());
        final String sign = md5(t+tmp[3]);

        final String jtwz = tmp[1];
        final String yhmString = tmp[2];
        final String wzString = tmp[0];
        final String keyString = tmp[3];
        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("jtwz",jtwz);
        requestParams.addBodyParameter("yhm",yhmString);
        requestParams.addBodyParameter("key",sign);
        requestParams.addBodyParameter("wz",wzString);
        httpUtils.send(HttpRequest.HttpMethod.POST, "http://wbsq.169hezuo.com/Weibao/sq", requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                JSONObject jsonObject = JSON.parseObject(result);
                int code = jsonObject.getInteger("code");
                String msg = jsonObject.getString("msg");
                if(code == 1) {
                    HttpUtils httpUtils = new HttpUtils();
                    RequestParams requestParams = new RequestParams();
                    requestParams.addBodyParameter("t", t);
                    requestParams.addBodyParameter("type", "9");
                    requestParams.addBodyParameter("price", "0");
                    requestParams.addBodyParameter("sign", sign);
                    requestParams.addBodyParameter("yhm", yhmString);
                    String url = jtwz + "/Jt/js";
                    httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            getJGTime(responseInfo.result);
                            isOk = true;
                            //将扫描出的信息显示出来
                            txthost.setText(" 监听地址：" + jtwz);
                            txtkey.setText(" 用户名：" + yhmString);
                            host = jtwz;
                            key = keyString;
                            yhm = yhmString;
                            wz = wzString;
                            SharedPreferences.Editor editor = getSharedPreferences("vone", MODE_PRIVATE).edit();
                            editor.putString(StaticInfo.HOST, host);
                            editor.putString(StaticInfo.KEY, key);
                            editor.putString(StaticInfo.WZ, wz);
                            editor.putString(StaticInfo.YHM, yhm);
                            editor.commit();
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            txthost.setText("");
                            txtkey.setText("配置失败," + e.getMessage());
                            SharedPreferences.Editor editor = getSharedPreferences("vone", MODE_PRIVATE).edit();
                            editor.putInt(StaticInfo.JG, 100);
                            editor.apply();
                            Toast.makeText(MainActivity.this,"配置失败,"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    txthost.setText("");
                    txtkey.setText("配置失败,"+msg);
                    Toast.makeText(MainActivity.this,"配置失败,"+ msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                txthost.setText("");
                SharedPreferences.Editor editor = getSharedPreferences("vone", MODE_PRIVATE).edit();
                editor.putInt(StaticInfo.JG, 100);
                editor.apply();
                txtkey.setText("网络错误，请联系上级或者稍后再试...");
                Toast.makeText(MainActivity.this, "网络错误，请联系上级或者稍后再试...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void checkPermission(){
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, Constant.REQ_PERM_SMS);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode(null);
                } else {
                    // 被禁止授权
                    Toast.makeText(MainActivity.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
            case Constant.REQ_PERM_EXTERNAL_STORAGE:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode(null);
                } else {
                    // 被禁止授权
                    Toast.makeText(MainActivity.this, "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_LONG).show();
                }
                break;
            case Constant.REQ_PERM_SMS:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    checkPermission();
                } else {
                    // 被禁止授权
                    Toast.makeText(MainActivity.this, "请至权限中心打开本应用的读取短信服务", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }



}
