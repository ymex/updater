package cn.ymex.updater;

/**
 * Created by ymexc on 2018/7/27.
 * About:TODO
 */
public interface DownloadListener {
    void onDownProgress(int progress);
    void onDownFinish(String path);
    void onDownError(Throwable throwable);
}
