package com.vone.weibaoshuiguo.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.util.PackUtils;

import java.io.IOException;

public class PlayingMusicServices extends Service {
    //用于播放音乐等媒体资源
    private MediaPlayer mediaPlayer;
    /**
     * onBind，返回一个IBinder，可以与Activity交互
     * 这是Bind Service的生命周期方法
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //在此方法中服务被创建
    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer==null){
            mediaPlayer=new MediaPlayer();
        }
    }


    /**
     * 在此方法中，可以执行相关逻辑，如耗时操作
     * @param intent :由Activity传递给service的信息，存在intent中
     * @param flags ：规定的额外信息
     * @param startId ：开启服务时，如果有规定id，则传入startid
     * @return 返回值规定此startservice是哪种类型，粘性的还是非粘性的
     *          START_STICKY:粘性的，遇到异常停止后重新启动，并且intent=null
     *          START_NOT_STICKY:非粘性，遇到异常停止不会重启
     *          START_REDELIVER_INTENT:粘性的，重新启动，并且将Context传递的信息intent传递
     * 此方法是唯一的可以执行很多次的方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getIntExtra("type",-1)){
            case PackUtils.TYPE_PLAY_AUDIO:
                String url = intent.getStringExtra("url");
                    //重置mediaplayer
                    mediaPlayer.reset();
                    //将需要播放的资源与之绑定
                    if(url == null || TextUtils.isEmpty(url)) {
                        mediaPlayer = MediaPlayer.create(this, R.raw.bg_voice);
                    }else{
                        mediaPlayer =new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(url);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    //开始播放
                    mediaPlayer.start();
                    //是否循环播放
                    mediaPlayer.setLooping(true);
                break;
            case PackUtils.TYPE_STOP_AUDIO:
                if (mediaPlayer!=null){
                    //停止之后要开始播放音乐
                    mediaPlayer.stop();
                }
                break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null ) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
