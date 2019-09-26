package com.vone.weibaoshuiguo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.base.BaseRxDataActivity;
import com.vone.weibaoshuiguo.util.DownloadUtils;
import com.vone.weibaoshuiguo.util.PackUtils;
import com.vone.weibaoshuiguo.util.RequestUtils;
import com.vone.weibaoshuiguo.util.SelectImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * webView activity
 **/
public class WebViewActivity extends BaseRxDataActivity {

    public static final String INTENT_WEB_URL = "intent_web_url";

    private View rootView;
    private WebView mWebView;
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onClickRightLogo(View v) {

    }

    @Override
    protected String getBarTitle() {

        return null;
    }

    @Override
    protected void onActivityPrepared() {
        mWebView = rootView.findViewById(R.id.webView);
        Intent intent = getIntent();
        url = intent.getStringExtra(INTENT_WEB_URL);
        initWebViewSetting();
        loadUrl(url);
    }
    private ValueCallback<Uri[]> mFilePathCallback;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if( requestCode == SelectImageUtils.IMAGE_REQUESTION_CODE){
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
    }

    public void loadUrl(String url){
        if(PackUtils.getInstance().getUrlqx() == 1){
            if(url.contains("?")) {
                mWebView.loadUrl(url+"&appwjh168abc=jkfhi");
            }else{
                mWebView.loadUrl(url+"?appwjh168abc=jkfhi");
            }
        }else{
            mWebView.loadUrl(url);
        }
    }

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if(title == null || TextUtils.isEmpty(title)){
                setTitle(url);
            }else {
                setTitle(title);
            }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);
        return true;
    }
    @Override
    protected boolean hasEdit() {
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.homeMenu) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isDestroyed()){
            return;
        }
        if(mWebView != null ) {
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            finish();
        }
    }

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

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if(PackUtils.getInstance().getPbggsq() == 1) {
                    url = url.toLowerCase();
                    if (PackUtils.getInstance().hasAd(WebViewActivity.this,url)) {
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
                                Toast.makeText(WebViewActivity.this, "安全防护中，非法广告已拦截", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return new WebResourceResponse(null, null, null);
                    }
                }else{
                    return super.shouldInterceptRequest(view, url);
                }
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                if(contentDisposition != null && !TextUtils.isEmpty(contentDisposition)) {
                    String fileName = contentDisposition.replace("attachment;filename=", "").replace("\"", "");
                    final File file = new File(Environment.getExternalStorageDirectory() +"/"+ WebViewActivity.this.getResources().getString(R.string.app_name)+"/download/", fileName);
                    DownloadUtils.getInstance().downloadFile(WebViewActivity.this, url, file.getAbsolutePath(), new DownloadUtils.OnDownloadStatusListener() {
                        @Override
                        public void onDownloadSuccess() {
                            WebViewActivity.this.finish();
                        }

                        @Override
                        public void onDownloadFail(String errorMessage) {
                            WebViewActivity.this.finish();
                        }
                    });
                }else{
                    String fileEndName = url.substring(url.lastIndexOf("."));
                    if(fileEndName.contains("?")){
                        fileEndName = fileEndName.substring(0,fileEndName.lastIndexOf("?"));
                    }
                    final File file = new File(Environment.getExternalStorageDirectory() +"/"+ WebViewActivity.this.getResources().getString(R.string.app_name)+"/download/", UUID.randomUUID().toString()+fileEndName);
                    DownloadUtils.getInstance().downloadFile(WebViewActivity.this, url, file.getAbsolutePath(), new DownloadUtils.OnDownloadStatusListener() {
                        @Override
                        public void onDownloadSuccess() {
                            WebViewActivity.this.finish();
                        }

                        @Override
                        public void onDownloadFail(String errorMessage) {
                            WebViewActivity.this.finish();
                        }
                    });
                }
            }
        });

    }


    @Override
    protected boolean hasRightLogo() {
        return false;
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

    public static void showActivity(Context context,String url){
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(INTENT_WEB_URL,url);
        context.startActivity(intent);
    }
}
