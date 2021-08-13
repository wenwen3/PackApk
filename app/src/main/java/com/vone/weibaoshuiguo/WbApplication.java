package com.vone.weibaoshuiguo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.vone.weibaoshuiguo.bean.Notepad;


public class WbApplication extends Application {

    public static WbApplication instance;

    public static WbApplication getInstance(){
        return instance;
    }

    private DbUtils dbUtils;

    public DbUtils getDbUtils() {
        return dbUtils;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashHandler.getInstance().init(this);
        //初始化X5内核
        dbUtils = DbUtils.create(this, "pack", 1, new DbUtils.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtils dbUtils, int i, int i1) {
                try {
                    dbUtils.delete(Notepad.class);
                    dbUtils.createTableIfNotExist(Notepad.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public static String getVersion(Context context)//获取版本号
    {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "获取不到version";
        }
    }
}
