package cn.ymex.updater;

import android.text.TextUtils;

import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;


public class DownLoadInfo {
    private String url;
    private String savePath;
    private String saveName;

    private int taskId;

    public DownLoadInfo(String url) {
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getSaveName() {
        return saveName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSaveName(String saveName) {
        if (TextUtils.isEmpty(saveName)) {
            return;
        }
        this.saveName = saveName;
        this.savePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + saveName;
    }
}
