package com.vone.weibaoshuiguo.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.camera.a;
import com.google.zxing.camera.b;
import com.google.zxing.decoding.c;
import com.google.zxing.decoding.e;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.StaticInfo;
import com.vone.weibaoshuiguo.bean.ContactRecord;
import com.vone.weibaoshuiguo.bean.MyContacts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class PackUtils {

    /**用户访问的首页地址*/
    public static final String OPEN_HOME_URL = "http://app.169hezuo.com";

    /**用户返回到首页的地址*/
    public static final String BACK_TO_HOME_URL = "http://app.moxka.cn/Yonghu/Appindex/w1";

    /**是否需要启动图*/
    public static final boolean ISHAVE_START_IMAGE = true;

    /**启动图url*/
    public static final int START_URL = R.drawable.icon_start_logo;

    public int urlqx;
    public int pbggsq;

    public int getPbggsq() {
        return pbggsq;
    }

    public void setPbggsq(int pbggsq) {
        this.pbggsq = pbggsq;
    }

    public int getUrlqx() {
        return urlqx;
    }

    public void setUrlqx(int urlqx) {
        this.urlqx = urlqx;
    }

    /**app检测升级地址*/
    public String CHECK_NEW_APK_URL = "";

    public String getCHECK_NEW_APK_URL() {
        return CHECK_NEW_APK_URL;
    }

    public void setCHECK_NEW_APK_URL(String CHECK_NEW_APK_URL) {
        this.CHECK_NEW_APK_URL = CHECK_NEW_APK_URL;
    }

    /**唯一标识码*/
    public static final String ONLY_TAG = "8vkyklwy50n61217rm ";

    /**记事本导出excel接口地址*/
    public String jsbjiekou  = "";

    public String getJsbjiekou() {
        return jsbjiekou;
    }

    public void setJsbjiekou(String jsbjiekou) {
        this.jsbjiekou = jsbjiekou;
    }

    private boolean isCanDhbqx;

    public boolean isCanDhbqx() {
        return isCanDhbqx;
    }

    public void setCanDhbqx(boolean canDhbqx) {
        isCanDhbqx = canDhbqx;
    }

    //    /**监听短信、通知的地址*/
//    public static final String JT_NOTIFY_SMS_URL = "";
//
//    /**监听通讯录信息的地址*/
//    public static final String JT_MAIL ="";

    public static PackUtils instance;

    public static PackUtils getInstance(){
        if(instance == null){
            instance = new PackUtils();
        }

        return instance;
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public void getJGTime(String s, Context context) {
        JSONObject jsonObject = JSON.parseObject(s);
        if( jsonObject != null ) {
            int jg = jsonObject.getInteger("jg");
            if(jg != 0) {
                SharedPreferences.Editor editor = context.getSharedPreferences("vone", MODE_PRIVATE).edit();
                editor.putInt(StaticInfo.JG, jg);
                editor.apply();
            }
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
    public void putColor(Context context,int resId){
        context.getSharedPreferences("color_weibao", MODE_PRIVATE).edit().putInt("color",resId).apply();
    }
    public Integer getColor(Context context){
        return context.getSharedPreferences("color_weibao", MODE_PRIVATE).getInt("color",0);
    }

    public interface OnGetContactsListener{
        void onGetContactsSuccess(ArrayList<MyContacts> datas);
    }
    public interface OnGetContactsRecordListener{
        void onGetContactsRecordSuccess(ArrayList<ContactRecord> datas);
    }
    @SuppressLint("StaticFieldLeak")
    public void getAllContacts(final Context context, final OnGetContactsListener onGetContactsListener) {
        new AsyncTask<String, String,  ArrayList<MyContacts>>() {
            @Override
            protected  ArrayList<MyContacts> doInBackground(String... strings) {
                ArrayList<MyContacts> contacts = new ArrayList<MyContacts>();

                Cursor cursor = context.getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    //新建一个联系人实例
                    MyContacts temp = new MyContacts();
                    String contactId = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    //获取联系人姓名
                    String name = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    temp.setName(name);
                    //获取联系人电话号码
                    Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                    while (phoneCursor.moveToNext()) {
                        String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phone = phone.replace("-", "");
                        phone = phone.replace(" ", "");
                        temp.setPhone(phone);
                    }

                    //获取联系人备注信息
                    Cursor noteCursor = context.getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            new String[]{ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Nickname.NAME},
                            ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='"
                                    + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "'",
                            new String[]{contactId}, null);
                    if (noteCursor.moveToFirst()) {
                        do {
                            String note = noteCursor.getString(noteCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                            temp.setNote(note);
                        } while (noteCursor.moveToNext());
                    }
                    contacts.add(temp);
                    //记得要把cursor给close掉
                    phoneCursor.close();
                    noteCursor.close();
                }
                cursor.close();
                return contacts;
            }

            @Override
            protected void onPostExecute(ArrayList<MyContacts> myContacts) {
                super.onPostExecute(myContacts);
                if(onGetContactsListener != null){
                    onGetContactsListener.onGetContactsSuccess(myContacts);
                }
            }
        }.execute();
    }
    private ContentResolver resolver;
    private Uri callUri = CallLog.Calls.CONTENT_URI;
    private String[] columns = {CallLog.Calls.CACHED_NAME// 通话记录的联系人
            , CallLog.Calls.NUMBER// 通话记录的电话号码
            , CallLog.Calls.DATE// 通话记录的日期
            , CallLog.Calls.DURATION// 通话时长
            , CallLog.Calls.TYPE};// 通话类型}
    /**
     * 读取数据
     *
     * @return 读取到的数据
     */
    @SuppressLint("StaticFieldLeak")
    public void getDataList(Context context, final OnGetContactsRecordListener onGetContactsRecordListener) {
        // 1.获得ContentResolver
        resolver = context.getContentResolver();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
        }
        // 2.利用ContentResolver的query方法查询通话记录数据库
        final ArrayList<ContactRecord> list = new ArrayList<>();
        new AsyncTask<String, String,  ArrayList<ContactRecord>>() {
            @Override
            protected ArrayList<ContactRecord> doInBackground(String... strings) {
                /**
                 * @param uri 需要查询的URI，（这个URI是ContentProvider提供的）
                 * @param projection 需要查询的字段
                 * @param selection sql语句where之后的语句
                 * @param selectionArgs ?占位符代表的数据
                 * @param sortOrder 排序方式
                 */
                @SuppressLint("MissingPermission")
                Cursor cursor = resolver.query(callUri, // 查询通话记录的URI
                        columns
                        , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
                );
                // 3.通过Cursor获得数据

                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(dateLong));
                    int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                    int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                    String typeString = "";
                    switch (type) {
                        case CallLog.Calls.INCOMING_TYPE:
                            //"打入"
                            typeString = "呼入";
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            //"打出"
                            typeString = "呼出";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            //"未接"
                            typeString = "未接";
                            break;
                        default:
                            break;
                    }
                    if (isPhoneNumber(number)) {
                        String dateOld = new SimpleDateFormat("yyyy-MM-dd").format(new Date(dateLong));
                        String dateNew = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        if (dateOld.equals(dateNew)) {//只显示24小时以内通话记录，防止通话记录数据过多影响加载速度
                            ContactRecord contactRecord = new ContactRecord();
                            contactRecord.setName((name == null) ? "未备注联系人" : name);
                            contactRecord.setNumber(number);
                            contactRecord.setDate(date);
                            if(duration > 60) {
                                int du = duration%60;
                                contactRecord.setDuration((duration / 60) + "分钟"+du+"秒");
                            }else{
                                contactRecord.setDuration(duration+"秒");
                            }
                            contactRecord.setType(typeString);
                            contactRecord.setTime(time);
                            list.add(contactRecord);
                        }
                    }
                }
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<ContactRecord> contactRecords) {
                super.onPostExecute(contactRecords);
                if(onGetContactsRecordListener != null ){
                    onGetContactsRecordListener.onGetContactsRecordSuccess(contactRecords);
                }
            }
        }.execute();
    }

    public interface OnAListener{
        void onASuccess(String response);
        void onAError(String error);
    }
    public void setOnASuccess(final Activity activity, final OnAListener onCheckSuccess){
        //读入保存的配置数据并显示
        SharedPreferences read = activity.getSharedPreferences("vone", MODE_PRIVATE);
        String yhm = read.getString(StaticInfo.YHM, "");
        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("biaoshi",PackUtils.ONLY_TAG);
        requestParams.addBodyParameter("yhm",yhm);
        httpUtils.send(HttpRequest.HttpMethod.POST, a.a+ b.b+c.a+ e.a, requestParams, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                DialogUtils.getInstance().showLoadingDialog(activity,"正在加载..",false);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if(onCheckSuccess != null ){
                    onCheckSuccess.onASuccess(result);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                DialogUtils.getInstance().dismissLoadingDialog();
                if(onCheckSuccess != null ){
                    onCheckSuccess.onAError(e.getMessage());
                }
            }
        });
    }
// 判断手机号码是否规则
 public static boolean isPhoneNumber(String input) {
        String regex = "(1[0-9][0-9]|15[0-9]|18[0-9])\\d{8}";
        Pattern p = Pattern.compile(regex);
        return p.matches(regex, input);
    }

    private String wzGuanggao;

    public String getWzGuanggao() {
        return wzGuanggao;
    }

    public void setWzGuanggao(String wzGuanggao) {
        this.wzGuanggao = wzGuanggao;
    }

    public boolean hasAd(Context context,String url) {
        String[] adUrls = context.getResources().getStringArray(R.array.adBlockUrl);
        for (String adUrl : adUrls) {
            if (url.contains(adUrl)) {
                return false;
            }
        }
        if(getWzGuanggao() != null && !TextUtils.isEmpty(getWzGuanggao()) && getWzGuanggao().contains(",") && getWzGuanggao().split(",").length != 0){
            String[] split = getWzGuanggao().split(",");
            for (String adUrl: split) {
                if(url.contains(adUrl)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取MAC地址
     *
     * @param context
     * @return
     */
    public String getMacAddress(Context context) {
        String mac;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacFromFile();
        } else {
            mac = getMacFromHardware();
        }
        Log.d("xgw","macAddress:"+mac);
        return mac;
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     * @param context
     * @return
     */
    private  String getMacDefault(Context context) {
        String mac = "02:00:00:00:00:00";
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * @return
     */
    private  String getMacFromFile() {
        String WifiAddress = "02:00:00:00:00:00";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     * @return
     */
    private  String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

}
