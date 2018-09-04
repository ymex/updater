package cn.ymex.ymex_updater;

import android.app.Application;

import cn.ymex.updater.Updater;

/**
 * Created by ymexc on 2017/12/4.
 * About:TODO
 */

public class AppContent extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Updater.get().config(this);

        /**
         * 自定义fileprovider .
         * 注意provider 在要项目的manifest中注册。
         */
        //Updater.get().config(this,MyFileProvider.class);
    }
}
