package cn.ymex.ymex_updater;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.ymex.updater.DownLoadManage;
import cn.ymex.updater.Updater;

public class AppLaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launch);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String url = "http://47.93.245.14:8080/api/version/";
        Updater.getInstance(this)
                .setVersionCode(0)
                .setUrl(url)
                .setAppId(16)
                .setChannel("default")
                .checkVersion();
    }
}
