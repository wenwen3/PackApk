package com.vone.weibaoshuiguo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.util.PackUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if(!PackUtils.ISHAVE_START_IMAGE){
            //页面的跳转
            Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
        }else{
            setContentView(R.layout.splash_activity_layout);
            ImageView splashImage = findViewById(R.id.splashImage);
            Glide.with(this).load(PackUtils.START_URL).into(splashImage);
            LinearLayout allLayout = findViewById(R.id.allLayout);
            AlphaAnimation alphaAnimation=new AlphaAnimation(0.1f,1.0f);
            alphaAnimation.setDuration(2000);//设置动画播放时长1000毫秒（1秒）
            allLayout.startAnimation(alphaAnimation);
            //设置动画监听
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                //动画结束
                @Override
                public void onAnimationEnd(Animation animation) {
                    //页面的跳转
                    Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

}
