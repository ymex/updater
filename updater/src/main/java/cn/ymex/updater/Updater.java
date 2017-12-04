package cn.ymex.updater;

import android.content.Context;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;

import java.io.File;
import java.io.IOException;

import cn.ymex.popup.dialog.PopupDialog;
import cn.ymex.rxretrofit.OkHttpBuilder;
import cn.ymex.rxretrofit.http.LogInterceptor;
import cn.ymex.rxretrofit.http.ResultObserver;
import cn.ymex.rxretrofit.http.T;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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
    int appId;
    String channel;
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

    public Updater setAppId(int appId) {
        this.appId = appId;
        return this;
    }

    public Updater setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public Updater setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public void checkVersion() {
        getRetrofit(url).create(ApiService.class)
                .checkVersion(appId,channel)
                .compose(T.create().<ResponseBody>transformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver<ResponseBody>() {
                    @Override
                    public void onResult(ResponseBody resultVersion) {
                        super.onResult(resultVersion);
                        try {
                            System.out.println("-----::::res:::"+resultVersion.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        if (resultVersion != null && "200".equals(resultVersion.getCode())) {
//                            ResultVersion.Version version = resultVersion.getData();
//
//                            if (version != null && Integer.valueOf(version.getVersion_code()) > versionCode) {
//                                showLoadDialog(version);
//                            }
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        System.out.println("----------:::: finish"+e.getLocalizedMessage());
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        System.out.println("----------:::: finish");
                    }
                });
    }

    private void showLoadDialog(ResultVersion.Version version) {
        controller = VersionDialogController.build()
                .setTouchDismiss(Integer.valueOf(version.getForce()) == 1)
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
        OkHttpClient client = OkHttpBuilder.getInstance().builder().addInterceptor(new LogInterceptor()).build();

        return new Retrofit.Builder()
                .client(client)
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
