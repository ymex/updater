package cn.ymex.updater;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by ymexc on 2018/7/30.
 * About:updater
 */
public class Updater {

    private String providerPath;
    private String authorities;
    private Context context;
    private static Updater mUpdater;

    public static Updater get() {
        if (mUpdater == null) {
            mUpdater = new Updater();
        }
        return mUpdater;
    }

    private Updater() {
    }

    public Updater config(Context context) {
        return config(context, null);
    }

    public Updater config(Context context, Class<? extends FileProvider> provider) {
        if (context instanceof Activity) {
            this.context = ((Activity) context).getApplication();
        } else if (context instanceof Service) {
            this.context = ((Service) context).getApplication();
        } else {
            this.context = context;
        }
        try {
            initFilePathConfig(context, provider == null ? UpdateFileProvider.class : provider);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    private void initFilePathConfig(Context context, Class<? extends FileProvider> provider) throws Exception {

        ComponentName cn = new ComponentName(context, provider);
        ProviderInfo providerInfo = context.getPackageManager().getProviderInfo(cn, PackageManager.GET_META_DATA);
        authorities = providerInfo.authority;

        XmlResourceParser xrp = context.getResources().getXml(providerInfo.metaData.getInt("android.support.FILE_PROVIDER_PATHS"));
        while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
            // 判断事件类型是否为文档结束
            if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                // 判断事件类型是否为开始标志
                xrp.nextTag();
                while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                    if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                        String name1 = xrp.getName();
                        if ("external-path".equalsIgnoreCase(name1)) {
                            for (int i = 0; i < xrp.getAttributeCount(); i++) {
                                if ("path".equalsIgnoreCase(xrp.getAttributeName(i))) {
                                    providerPath = xrp.getAttributeValue(i);
                                }
                            }
                        }
                    }
                    xrp.next();
                }

            }
            xrp.next();
        }
    }

    public String getProviderPath() {
        return providerPath;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void downloadApp(DownloadListener downloadListener, String apkUrl) {
        new DownloadTask(downloadListener).execute(apkUrl);
    }

    public void installApp(String apkPath) throws Exception {
        if (TextUtils.isEmpty(apkPath)) {
            throw new Exception("apk url is null");
        }
        File apkFile = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装好了，点打开，打开新版本应用的。
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, Updater.get().getAuthorities(), apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        if (context == null) {
            throw new IllegalArgumentException("content is not allow null");
        }
        context.startActivity(intent);
    }

    public interface DownloadListener {
        void onDownProgress(int progress);

        void onDownFinish(String path);

        void onDownError(Throwable throwable);

        void onStartDown();
    }
}
