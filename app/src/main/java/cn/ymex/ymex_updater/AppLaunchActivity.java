package cn.ymex.ymex_updater;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.ymex.kits.Finder;
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

        Finder.find(this,R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://47.93.245.14:8080/api/version/16?channel=default";
                Updater.getInstance(AppLaunchActivity.this).setVersionCode(100).checkVersion(url,false);
            }
        });

    }
}
