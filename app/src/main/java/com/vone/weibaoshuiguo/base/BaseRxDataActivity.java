package com.vone.weibaoshuiguo.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vone.qrcode.R;
import com.vone.weibaoshuiguo.util.PackUtils;
import com.vone.weibaoshuiguo.widget.MyLinearLayout;

public abstract class BaseRxDataActivity extends AppCompatActivity {

    private FrameLayout contentView;
    private LinearLayout rightLeftLayout;
    private LinearLayout left2Layout;
    private LinearLayout left3Layout;
    private LinearLayout shareLayout;
    private LinearLayout actionBar;
    private TextView titleTextView;
    private View backLayout;
    private View rightLayout;
    private ImageView rightImage;
    private ImageView rightLeftImage;
    private  View statusBar;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.base_content_layout);
        contentView = (FrameLayout) findViewById(R.id.contentView);
        MyLinearLayout allLayout = (MyLinearLayout) findViewById(R.id.allLayout);
        if(hasEdit()) {
            allLayout.setFitsSystemWindows(true);
        }
        rightLeftLayout = (LinearLayout) findViewById(R.id.rightLeftLayout);
        left2Layout = (LinearLayout) findViewById(R.id.left2Layout);
        left3Layout = (LinearLayout) findViewById(R.id.left3Layout);
        shareLayout = (LinearLayout) findViewById(R.id.shareLayout);
        rightLeftImage = (ImageView) findViewById(R.id.rightLeftImage);
        backLayout =  findViewById(R.id.backLayout);
        statusBar = findViewById(R.id.statusBar);
        ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = PackUtils.getInstance().getStatusBarHeight(this);
        statusBar.setLayoutParams(layoutParams);
        if(PackUtils.getInstance().getColor(this) != 0){
            statusBar.setBackgroundResource(PackUtils.getInstance().getColor(this));
        }
        rightLayout =  findViewById(R.id.rightLayout);
        rightImage = (ImageView) findViewById(R.id.rightImage);
        actionBar = (LinearLayout) findViewById(R.id.actionBar);
        titleTextView = (TextView) findViewById(R.id.title);

        if(hasToolbar()){
            if(PackUtils.getInstance().isHaveBar()) {
                if (getBarTitle() != null && !TextUtils.isEmpty(getBarTitle())) {
                    titleTextView.setText(getBarTitle());
                }
                actionBar.setVisibility(View.VISIBLE);
                backLayout.setVisibility(View.VISIBLE);
                backLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (canGoActivityBack()) {
                            onBackPressed();
                        } else {
                            goBack();
                        }
                    }
                });
            }else{
                actionBar.setVisibility(View.GONE);
            }
        }else{
            actionBar.setVisibility(View.GONE);
        }

        if(hasRightLogo()){
            rightLayout.setVisibility(View.VISIBLE);
            rightLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickRightLogo(rightLayout);
                }
            });
        }else{
            rightLayout.setVisibility(View.INVISIBLE);
        }
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShareLayout();
            }
        });
        if(hasShareLayout()){
            shareLayout.setVisibility(View.VISIBLE);

        }

        if(hasLeft2Logo()){
            left2Layout.setVisibility(View.VISIBLE);
            left2Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickLeft2Logo(left2Layout);
                }
            });
        }else{
            left2Layout.setVisibility(View.INVISIBLE);
        }

        if(hasLeft3Logo()){
            left3Layout.setVisibility(View.VISIBLE);
            left3Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickLeft3Logo(left3Layout);
                }
            });
        }else{
            left3Layout.setVisibility(View.INVISIBLE);
        }

        if(hasRightLeftLogo()){
            rightLeftLayout.setVisibility(View.VISIBLE);
            rightLeftLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickRightLeftLogo(rightLeftLayout);
                }
            });
        }else{
            rightLeftLayout.setVisibility(View.GONE);
        }

        if(onCreateRightLogo() != 0){
            rightImage.setImageResource(onCreateRightLogo());
        }

        if( onCreateContentView() != null ){
            contentView.addView(onCreateContentView());
        }

        if(PackUtils.getInstance().getColor(this) != 0){
            actionBar.setBackgroundResource(PackUtils.getInstance().getColor(this));
        }
        onActivityPrepared();
    }

    protected void onClickShareLayout() {

    }

    protected boolean hasShareLayout(){
        return false;
    }

    protected boolean hasEdit(){
        return false;
    }

    protected void showBackLogo(){
        if(hasToolbar() && backLayout != null ) {
            backLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void hideActionBar(){
        if(actionBar != null ){
            actionBar.setVisibility(View.GONE);
        }
    }

    protected void showActionBar(){
        if(actionBar != null ){
            actionBar.setVisibility(View.VISIBLE);
        }
    }
  protected void hideShareLayout(){
        if(shareLayout != null ){
            shareLayout.setVisibility(View.GONE);
        }
    }

    protected void showShareLayout(){
        if(shareLayout != null ){
            shareLayout.setVisibility(View.VISIBLE);
        }
    }
  protected void hideRightLayout(){
        if(rightLayout != null ){
            rightLayout.setVisibility(View.GONE);
        }
    }

    protected void showRightLayout(){
        if(rightLayout != null ){
            rightLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void hideLeft3Layout(){
        if(left3Layout != null ){
            left3Layout.setVisibility(View.GONE);
        }
    }

    protected void showLeft3Layout(){
        if(left3Layout != null ){
            left3Layout.setVisibility(View.VISIBLE);
        }
    }

    protected void hideBackLogo(){
        if(hasToolbar() && backLayout != null ) {
            backLayout.setVisibility(View.INVISIBLE);
        }
    }

    public View getAppBar(){
        return actionBar;
    }

    public void setActionBarBackgroundRes(int resId){
        actionBar.setBackgroundResource(resId);
        statusBar.setBackgroundResource(resId);
    }

    protected int onCreateRightLogo(){
        return 0;
    }

    protected abstract void onActivityPrepared();

    protected abstract View onCreateContentView();

    protected void onClickRightLogo(View view){

    }

    protected void onClickRightLeftLogo(View view){

    }

    protected void onClickLeft2Logo(View view){

    }

    protected void onClickLeft3Logo(View view){

    }

    protected abstract String getBarTitle();

    protected abstract boolean hasToolbar();

    protected boolean hasRightLogo(){
        return true;
    }
    protected boolean hasRightLeftLogo(){
        return false;
    }
    protected boolean hasLeft2Logo(){
        return false;
    }
    protected boolean hasLeft3Logo(){
        return false;
    }
    protected void goBack(){}

    protected boolean canGoActivityBack(){
        return true;
    }
    protected void setTitle(String title){
        if(hasToolbar()){
            titleTextView.setText(title);
        }
    }

}
