package cn.ymex.updater;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载应用
 *
 * @author Administrator
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

    // 下载存储的文件名
    private DownloadListener downloadListener;
    private Context context;

    public DownloadTask(DownloadListener downloadListener, Context context) {
        this.downloadListener = downloadListener;
        this.context = context;
    }

    public static final String PROVIDER_PATH = "quzhuazhua/files";
    public static final String AUTHORITIES = "com.shiguang.quzhuazhua.fileprovider";


    private PowerManager.WakeLock mWakeLock;


    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        File file = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                String error = "Server returned HTTP "
                        + connection.getResponseCode() + " "
                        + connection.getResponseMessage();
                if (downloadListener != null) {
                    downloadListener.onDownError(new Exception(error));
                }
                return error;
            }

            int fileLength = connection.getContentLength();
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Uri uri = Uri.parse(sUrl[0]);
                file = new File(Environment.getExternalStorageDirectory(), PROVIDER_PATH + File.separator + uri.getLastPathSegment());

                if (!file.exists()) {
                    // 判断父文件夹是否存在
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                }

            } else {
                if (downloadListener != null) {
                    downloadListener.onDownError(new Exception("sd卡未挂载"));
                }
            }
            input = connection.getInputStream();
            output = new FileOutputStream(file);
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) { // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                }
                output.write(data, 0, count);

            }
        } catch (Exception e) {
            return e.toString();

        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
        return file == null ? "" : file.getAbsolutePath();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        //systemSettingActivity.pBar.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if (downloadListener != null) {
            downloadListener.onDownProgress(progress[0]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();

        if (downloadListener != null) {
            downloadListener.onDownFinish(result);
        }

//        systemSettingActivity.update();
    }

    /**
     * 安装app
     *
     * @param context
     * @throws Exception
     */
    public static void installApk(Context context, String apkPath) throws Exception {
        if (TextUtils.isEmpty(apkPath)) {
            throw new Exception("apk url is null");
        }
        File apkFile = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装好了，点打开，打开新版本应用的。
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, AUTHORITIES, apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
