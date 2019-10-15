package com.vone.weibaoshuiguo.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.vone.qrcode.R;
import com.white.progressview.HorizontalProgressView;

import java.io.File;

public class DownloadUtils {

    private static DownloadUtils instance;

    public static DownloadUtils getInstance(){
        if(instance == null ){
            instance = new DownloadUtils();
        }
        return instance;
    }

    public interface OnDownloadStatusListener{
        void onDownloadSuccess();
        void onDownloadFail(String errorMessage);
    }
    private HttpHandler handler;
    private AlertDialog alertDialog;
    public void downloadFile(final Context context, String url, final String targetPath, final OnDownloadStatusListener onDownloadStatusListener){
        if(alertDialog != null ){
            alertDialog.dismiss();
            alertDialog = null;
        }
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.download_file_layout, null);
        alertDialog.setView(dialogView);
        final HorizontalProgressView progressView = dialogView.findViewById(R.id.progress);
        final View openFile = dialogView.findViewById(R.id.openFile);
        final TextView saveTitle = dialogView.findViewById(R.id.saveTitle);
        saveTitle.setText("保存地址为:"+targetPath);
        openFile.setVisibility(View.GONE);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (onDownloadStatusListener != null) {
                    onDownloadStatusListener.onDownloadSuccess();
                }
                if(FileUtils.getInstance().getMIMEType(new File(targetPath)).equals("application/vnd.android.package-archive")) {
                    installApk(context,targetPath);
                }
                FileUtils.getInstance().openFile(context,targetPath);
            }
        });
        alertDialog.show();
        progressView.setProgress(0);
        final HttpUtils httpUtils = new HttpUtils();
        handler = httpUtils.download(url, targetPath, new RequestCallBack<File>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {

                if(new File(targetPath).exists()){
                    progressView.setProgress(100);
                    openFile.setVisibility(View.VISIBLE);
                }else{
                    alertDialog.dismiss();
                    if (onDownloadStatusListener != null) {
                        onDownloadStatusListener.onDownloadFail("下载失败");
                    }
                    Toast.makeText(context, "下载失败!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (onDownloadStatusListener != null) {
                    onDownloadStatusListener.onDownloadFail(e.getMessage());
                }
                Toast.makeText(context, "下载失败!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                Log.d("xgw","total:"+total+"---"+"current:"+current);
                if (current != 0) {
                    long t = current/total;
                    Log.d("xgw","tttt:"+t);
                    int currentLoading = ((int)(current*100/total));
                    progressView.setProgress(currentLoading);
                } else {
                    progressView.setProgress(0);
                }
            }
        });

        dialogView.findViewById(R.id.cancelDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.cancel();
                if (onDownloadStatusListener != null) {
                    onDownloadStatusListener.onDownloadFail("下载失败");
                }
                alertDialog.dismiss();
            }
        });
    }


    public void downloadNewApk(final Context context, String url, final String targetPath,String message, final OnDownloadStatusListener onDownloadStatusListener){
        if(alertDialog != null ){
            alertDialog.dismiss();
            alertDialog = null;
        }
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.download_file_dialog_layout, null);
        alertDialog.setView(dialogView);
        final HorizontalProgressView progressView = dialogView.findViewById(R.id.progress);
        final TextView title = dialogView.findViewById(R.id.title);
        final TextView messageTextView = dialogView.findViewById(R.id.message);
        final TextView saveTitle = dialogView.findViewById(R.id.saveTitle);
        messageTextView.setText(message);
        title.setText("正在更新");
        saveTitle.setText("保存地址为:"+targetPath);
        alertDialog.show();
        progressView.setProgress(0);
        final HttpUtils httpUtils = new HttpUtils();
        handler = httpUtils.download(url, targetPath, new RequestCallBack<File>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                if (onDownloadStatusListener != null && responseInfo.result != null && responseInfo.result.exists()) {
                    onDownloadStatusListener.onDownloadSuccess();
                }
                if(new File(targetPath).exists()){
                    alertDialog.dismiss();
                    installApk(context,targetPath);
                }else{
                    alertDialog.dismiss();
                    Toast.makeText(context, "更新失败,下载失败!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (onDownloadStatusListener != null) {
                    onDownloadStatusListener.onDownloadFail(e.getMessage());
                }
                Toast.makeText(context, "更新失败,请检查网络!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                Log.d("xgw","total:"+total+"---"+"current:"+current);
                if (current != 0) {
                    long t = current/total;
                    Log.d("xgw","tttt:"+t);
                    int currentLoading = ((int)(current*100/total));
                    progressView.setProgress(currentLoading);
                } else {
                    progressView.setProgress(0);
                }
            }
        });
    }
    public  void installApk(Context context,String downloadApk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(context,"com.vone.packApk.benchi",new File(downloadApk));
            intent.setDataAndType(uri,"application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.parse("file://" + downloadApk),"application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
