package com.vone.weibaoshuiguo.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.gsm.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.vone.weibaoshuiguo.StaticInfo;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.vone.weibaoshuiguo.NeNotificationService2.md5;

public class SMSReceiver extends BroadcastReceiver {
    public static final String TAG = "ImiChatSMSReceiver";
    //android.provider.Telephony.Sms.Intents
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private String host = "";
    private String key = "";
    private String yhm = "";
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onReceive(Context context, Intent intent) {
        if( intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
                SmsMessage[] messages = getMessagesFromIntent(intent);
                for (SmsMessage message : messages) {

                    if(message == null || message.getOriginatingAddress() == null ||
                                message.getDisplayMessageBody() == null ||
                                TextUtils.isEmpty(message.getOriginatingAddress())||
                                TextUtils.isEmpty(message.getDisplayMessageBody())){
                        continue;
                    }
                    SharedPreferences read = context.getSharedPreferences("vone", MODE_PRIVATE);
                    host = read.getString(StaticInfo.HOST, "");
                    key = read.getString(StaticInfo.KEY, "");
                    yhm = read.getString(StaticInfo.YHM, "");
                    int jtdxqx = read.getInt("jtdxqx", 0);

                    if(host == null || TextUtils.isEmpty(host)){
                        return;
                    }

                    if(jtdxqx == 0){
                        return;
                    }

                    String t = String.valueOf(new Date().getTime());
                    String sign = md5(t + key);
                    HttpUtils httpUtils = new HttpUtils();
                    RequestParams requestParams = new RequestParams();
                    requestParams.addBodyParameter("t",t);
                    requestParams.addBodyParameter("type", 5 + "");
                    requestParams.addBodyParameter("price","0");
                    requestParams.addBodyParameter("laiyuan",message.getOriginatingAddress()+"");
                    requestParams.addBodyParameter("neirong",message.getDisplayMessageBody()+"");
                    requestParams.addBodyParameter("sign",sign);
                    requestParams.addBodyParameter("yhm",yhm);
                    requestParams.addBodyParameter("zitype","");
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
            }
        }
    }

    public final SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }
}
