package com.vone.weibaoshuiguo.receiver;

import android.app.Activity;
import android.app.Service;

import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;

import android.telephony.TelephonyManager;

import com.vone.weibaoshuiguo.StaticInfo;
import com.vone.weibaoshuiguo.WbApplication;
import com.vone.weibaoshuiguo.util.PackUtils;
import com.vone.weibaoshuiguo.util.RequestUtils;

import java.util.HashMap;

public class PhoneReceiver extends BroadcastReceiver {

    @Override

    public void onReceive(Context context, Intent intent) {


//如果是去电

        if(intent != null && intent.getAction() != null) {
            System.out.println("action"+intent.getAction());

            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {

                String phoneNumber = intent

                        .getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            } else {

//查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.

//如果我们想要监听电话的拨打状况，需要这么几步 :

/** 第一：获取电话服务管理器TelephonyManager manager = this.getSystemService(TELEPHONY_SERVICE);
 * 第二：通过TelephonyManager注册我们要监听的电话状态改变事件。manager.listen(new MyPhoneStateListener(),
 * PhoneStateListener.LISTEN_CALL_STATE);这里的PhoneStateListener.LISTEN_CALL_STATE就是我们想要
 * 监听的状态改变事件，初次之外，还有很多其他事件哦。
 * 第三步：通过extends PhoneStateListener来定制自己的规则。将其对象传递给第二步作为参数。
 * 第四步：这一步很重要，那就是给应用添加权限。android.permission.READ_PHONE_STATE
 */
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

                tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

//设置一个监听器

            }
        }

    }

    PhoneStateListener listener=new PhoneStateListener(){

        @Override

        public void onCallStateChanged(int state, String incomingNumber) {

//注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。

            super.onCallStateChanged(state, incomingNumber);

            switch(state){

                case TelephonyManager.CALL_STATE_IDLE:

                    System.out.println("挂断");

                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:

                    System.out.println("接听");

                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    if(!PackUtils.getInstance().isCanDhbqx()){
                        return;
                    }
                    SharedPreferences read = WbApplication.getInstance().getSharedPreferences("vone", Activity.MODE_PRIVATE);
                    String  wz = read.getString(StaticInfo.WZ, "");
                    String yhm = read.getString(StaticInfo.YHM, "");
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put("data",incomingNumber);
                    stringStringHashMap.put("leixing","laidian");
                    stringStringHashMap.put("yhm",yhm);
                    stringStringHashMap.put("biaoshi",PackUtils.ONLY_TAG);
                    RequestUtils.getInstance().request(stringStringHashMap,wz,null);
                    break;

            }

        }

    };

}