package cn.ymex.ymex_updater;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadTask extends AsyncTask<String, Integer, String> {

    // 下载存储的文件名
    private DownloadListener downloadListener;


    public DownloadTask(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

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
                return null;
            }

            int fileLength = connection.getContentLength();
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Uri uri = Uri.parse(sUrl[0]);

                file = new File(Environment.getExternalStorageDirectory(), Updater.get().getProviderPath() + File.separator + uri.getLastPathSegment());
                if (!file.exists()) {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                }

            } else {
                if (downloadListener != null) {
                    downloadListener.onDownError(new Exception("sd卡未挂载"));
                }
                return null;
            }
            input = connection.getInputStream();
            output = new FileOutputStream(file);
            byte data[] = new byte[2048];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    if (downloadListener != null) {
                        downloadListener.onDownError(new Exception("取消任务下载"));
                    }
                    return null;
                }
                total += count;
                if (fileLength > 0) { // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                }
                output.write(data, 0, count);

            }
        } catch (IOException e) {
            file = null;
            if (downloadListener != null) {
                downloadListener.onDownError(new Exception(e.getMessage()));
            }
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
        if (result == null || "".equals(result)) {
            return;
        }
        if (downloadListener != null) {
            downloadListener.onDownFinish(result);
        }
    }
}
