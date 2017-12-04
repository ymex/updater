package cn.ymex.updater;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadLog;

import java.net.Proxy;


public class DownLoadManage {

    public static DownLoadManage downLoadManage;

    public static DownLoadManage getInstance() {
        if (downLoadManage == null) {
            downLoadManage = new DownLoadManage();
        }
        return downLoadManage;
    }



    public static void init(Application context) {

        FileDownloadLog.NEED_LOG = isRunInDebug(context);

        FileDownloader.setupOnApplicationOnCreate(context)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                        .proxy(Proxy.NO_PROXY) // set proxy
                ))
                .commit();
    }

    /**
     * 下载升级文件
     *
     * @param info
     * @param loadListener
     * @return
     */
    public void downloadUpdateApk(DownLoadInfo info, FileDownloadSampleListener loadListener) {

        int taskid =  FileDownloader
                .getImpl()
                .create(info.getUrl())
                .setPath(info.getSavePath())
                .setListener(loadListener)
                .start();
        info.setTaskId(taskid);
    }


    public static boolean isRunInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 安装app
     * @param context
     * @param info
     * @throws Exception
     */
    public void installApk(Context context, DownLoadInfo info) throws Exception {

        if (TextUtils.isEmpty(info.getSavePath())) {
            throw new Exception("apk savePath is empty!");
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装好了，点打开，打开新版本应用的。
        intent.setDataAndType(Uri.parse("file://" + info.getSavePath()), "application/vnd.android.package-archive");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 单独文件下载
     */
    public void singleFile(DownLoadInfo info,  FileDownloadSampleListener fileDownloaderCallback) {

        FileDownloader.getImpl().create(info.getUrl())
                .setPath(info.getSavePath())
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(fileDownloaderCallback).start();
    }
}
