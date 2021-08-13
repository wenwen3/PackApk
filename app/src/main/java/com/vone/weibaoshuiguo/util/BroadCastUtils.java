package com.vone.weibaoshuiguo.util;

import android.os.Handler;

import com.vone.weibaoshuiguo.bean.JsonProtocol;

import java.util.ArrayList;
import java.util.List;

public class BroadCastUtils {

    private static BroadCastUtils instance;

    public static BroadCastUtils getInstance(){
        if( instance == null ){
            instance = new BroadCastUtils();
        }

        return instance;
    }

    private Handler handler = new Handler();
    private List<OnBroadCastListener> mListener = new ArrayList<>();

    public void sendBroadCast(final JsonProtocol protocol){
        if(protocol != null ){
            if(mListener.size() != 0){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < mListener.size(); i++) {
                            OnBroadCastListener onBroadCastListener = mListener.get(i);
                            if( onBroadCastListener != null ){
                                onBroadCastListener.onReceiver(protocol);
                            }
                        }
                    }
                });
            }
        }
    }

    public void addBroadCastListener(final OnBroadCastListener onBroadCastListener){
        if(onBroadCastListener != null ){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.add(onBroadCastListener);
                }
            });
        }
    }

    public void removeBroastCastListener(final OnBroadCastListener onBroadCastListener){
        if(onBroadCastListener != null && mListener.size() != 0){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.remove(onBroadCastListener);
                }
            });
        }
    }

    public interface OnBroadCastListener{
        void onReceiver(JsonProtocol protocol);
    }
}
