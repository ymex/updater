package cn.ymex.ymex_updater;

import android.app.Application;

import cn.ymex.updater.DownLoadManage;

/**
 * Created by ymexc on 2017/12/4.
 * About:TODO
 */

public class AppContent extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DownLoadManage.init(this);
    }
}
