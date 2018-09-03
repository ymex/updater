package cn.ymex.ymex_updater;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class AppLaunchActivity extends AppCompatActivity implements DownloadListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launch);
        try {
            getxxPackageInfo(getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://ojlyqybn1.qnssl.com/lejinsuo_latest.apk";
                Updater.get().downloadApp(AppLaunchActivity.this, url);
            }
        });

    }


    private void getxxPackageInfo(String packageName) throws PackageManager.NameNotFoundException {

        ComponentName cn = new ComponentName(this, CompatFileProvider.class);
        ProviderInfo providerInfo = getPackageManager().getProviderInfo(cn, PackageManager.GET_META_DATA);
        System.out.println("-------------------:::" + providerInfo.authority);


        for (String key : providerInfo.metaData.keySet()) {
            System.out.println("-------------------:::" + key + "   " + providerInfo.metaData.get(key));
        }

        XmlResourceParser xrp = getResources().getXml(providerInfo.metaData.getInt("android.support.FILE_PROVIDER_PATHS"));

        try {

            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                // 判断事件类型是否为文档结束
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    // 判断事件类型是否为开始标志
                    String name = xrp.getName();
                    System.out.println("--------------HI:"+name);
                    xrp.nextTag();
                    if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                        // 判断事件类型是否为开始标志
                        String name1 = xrp.getName();
                        System.out.println("--------------HI:"+name1);
                        System.out.println("--------------HI:"+xrp.getAttributeValue(0));
                        System.out.println("--------------HI:"+xrp.getAttributeValue(1));
                    }

                }
                xrp.next();
                // 下一行
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDownProgress(int progress) {
        System.out.println("-------------------:::onDownProgress " + progress);
    }

    @Override
    public void onDownFinish(String path) {
        System.out.println("-------------------:::onDownFinish " + path);
        try {
            Updater.get().installApp(path);
        } catch (Exception e) {
            System.out.println("---------------------" + e.getLocalizedMessage());
        }
    }

    @Override
    public void onDownError(Throwable throwable) {
        System.out.println("-------------------:::onDownError " + throwable.getLocalizedMessage());
    }
}
