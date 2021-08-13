package com.vone.weibaoshuiguo.util;

import android.content.Context;
import android.content.DialogInterface;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogUtils {

    public static DialogUtils dialogUtils;

    public static DialogUtils getInstance(){
        if(dialogUtils == null ){
            dialogUtils = new DialogUtils();
        }

        return dialogUtils;
    }
    private SweetAlertDialog sad;
    public void showLoadingDialog(Context context,String message,boolean cancelable){
        if (sad == null) {
            sad = new SweetAlertDialog(context);
            sad.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
            sad.setTitleText(message);
            sad.setCancelable(cancelable);

            if (cancelable) {
                sad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                });
            }

            if (!sad.isShowing()) {
                sad.show();
            }
        }
    }
    public interface OnShouldUseListener{
        void onShouldUse();
    }

    public void showShouldUseDialog(Context context, final OnShouldUseListener onShouldUseListener,String message){
        new SweetAlertDialog(context).setTitleText("提示").setContentText(message)
               .setConfirmButton("确认", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if(onShouldUseListener != null ){
                    onShouldUseListener.onShouldUse();
                }
                sweetAlertDialog.dismiss();
            }
        }).show();
    }
    public void showMessageDialog(Context context, final OnShouldUseListener onShouldUseListener,String message){
        new SweetAlertDialog(context).setTitleText("提示").setContentText(message)
               .setConfirmButton("确认", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if(onShouldUseListener != null ){
                    onShouldUseListener.onShouldUse();
                }
                sweetAlertDialog.dismiss();
            }
        }).setCancelButton("取消", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        }).show();
    }

    public void dismissLoadingDialog(){
        if (sad != null) {
            sad.dismiss();
            sad = null;
        }
    }
}
