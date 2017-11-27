package cn.ymex.updater;

import android.content.Context;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;

import java.io.File;

import cn.ymex.popup.dialog.PopupDialog;
import cn.ymex.rxretrofit.OkHttpBuilder;
import cn.ymex.rxretrofit.http.ResultObserver;
import cn.ymex.rxretrofit.http.T;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ymex on 2017/11/26.
 * About:
 */

public class Updater extends FileDownloadSampleListener {
    String url;
    Context context;
    int versionCode;
    static Updater updater;
    VersionDialogController controller;
    DownLoadInfo loadInfo;

    public static Updater getInstance(Context context) {
        if (null == updater) {
            updater = new Updater();
        }
        return updater;
    }

    /**
     * baseUrl
     *
     * @param url baseUrl
     * @return this
     */
    public Updater setUrl(String url) {
        this.url = url;
        return this;
    }

    public Updater setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public void checkVersion() {
        getRetrofit(url).create(ApiService.class)
                .checkVersion()
                .compose(T.create().<ResultVersion>transformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver<ResultVersion>() {
                    @Override
                    public void onResult(ResultVersion resultVersion) {
                        super.onResult(resultVersion);
                        if (resultVersion != null && "200".equals(resultVersion.getCode())) {
                            ResultVersion.Version version = resultVersion.getData();
                            if (version != null && version.getVersion_code() > versionCode) {
                                showLoadDialog(version);
                            }
                        }
                    }
                });
    }

    private void showLoadDialog(ResultVersion.Version version) {
        controller = VersionDialogController.build()
                .setTouchDismiss(version.getForce() == 1)
                .setContent(version.getUpdate_content());
        PopupDialog.create(context).controller(controller).show();

        loadInfo = new DownLoadInfo(version.getUpdate_url());
        loadInfo.setSaveName(getAppDownloadName(version.getApp_name(), version.getVersion_name()));
        try {
            File file = new File(loadInfo.getSavePath());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {

        }
        controller.getBtnUpdate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownLoadManage.getInstance().downloadUpdateApk(loadInfo, Updater.this);
                controller.getBtnUpdate().setVisibility(View.INVISIBLE);
                controller.getProgressView().setVisibility(View.VISIBLE);
            }
        });
    }


    public static String getAppDownloadName(String appName, String versionName) {
        return appName + "_v" + versionName + ".apk";
    }

    public Retrofit getRetrofit(String url) {
        return new Retrofit.Builder()
                .client(OkHttpBuilder.getInstance().defaultClient())
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    //--------------------------------DownLoadListener----------------------------------------
    @Override
    public void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        controller.getProgressTextView().setText(String.valueOf(pb(soFarBytes, totalBytes)) + "%");
    }

    @Override
    public void completed(BaseDownloadTask task) {
        controller.getProgressTextView().setText("100%");
        controller.getBtnUpdate().setText("安装新版本");
        controller.getBtnUpdate().setVisibility(View.INVISIBLE);
        controller.getProgressView().setVisibility(View.VISIBLE);
        controller.getBtnUpdate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DownLoadManage.getInstance().installApk(context, loadInfo);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });
    }

    @Override
    public void started(BaseDownloadTask task) {
        controller.getProgressTextView().setText("0%");
    }

    @Override
    public void error(BaseDownloadTask task, Throwable e) {

    }

    private int pb(int soFarBytes, int totalBytes) {
        float p = (soFarBytes * 100f) / totalBytes;
        return p >= 100 ? 100 : (int) p;
    }
}
