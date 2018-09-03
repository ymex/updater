package cn.ymex.ymex_updater;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by ymexc on 2018/7/30.
 * About:TODO
 */
public class Updater {

    private String providerPath = "apps/ymex/files";
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
        if (context instanceof Activity) {
            this.context = ((Activity) context).getApplication();
        } else if (context instanceof Service) {
            this.context = ((Service) context).getApplication();
        } else {
            this.context = context;
        }

        return this;
    }

    public String getProviderPath() {
        return providerPath;
    }

    public String getAuthorities() {
        if (context == null) {
            throw new IllegalArgumentException("content is not allow null");
        }
        return context.getPackageName() + ".file_provider";
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
}
