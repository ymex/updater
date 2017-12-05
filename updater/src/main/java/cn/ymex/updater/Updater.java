package cn.ymex.updater;

import android.app.Application;
import android.content.Context;

import java.io.File;

import cn.ymex.popup.dialog.PopupDialog;
import cn.ymex.rxretrofit.http.LogInterceptor;
import cn.ymex.rxretrofit.http.ResultObserver;
import cn.ymex.rxretrofit.http.T;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by ymex on 2017/11/26.
 * response json
 * {
 *  "code": 20000,
 *  "data": {
 *          "id": "4",
 *          "pid": null,
 *          "app_name": "乐金所Def",
 *          "version_name": "v4.2",
 *          "version_code": "42",
 *          "update_url": "https://ojlyqybn1.qnssl.com/lejinsuo_latest.apk",
 *          "update_content": "这个版本，我们做了一些小的调整： \n1.影片列表，点击海报快速观看预告片 \n2.针对预售的影片，显示“想看人数” \n3.增加影院的在线选座停售时间说明，减少购前焦虑 \n4.下单页面，显示放映结束时间，提前知道几点散场",
 *          "force": "1",
 *          "channel": "default"
 * },
 * "message": "success",
 * "path": "/api/version/16"
 * }
 *
 * 1.  Updater.init(this);
 *
 * 2. Updater.getInstance(AppLaunchActivity.this).setVersionCode(0).checkVersion(url);
 */

public class Updater {
    private static boolean isDownloadInit = false;

    int successCode = 20000;
    Context context;
    int versionCode;
    static Updater updater;
    VersionDialogController controller;
    DownLoadManage.Info loadInfo;


    public Updater(Context context) {
        this.context = context;
    }

    public static Updater getInstance(Context context) {
        if (null == updater) {
            updater = new Updater(context);
        }
        return updater;
    }

    public Updater setSuccessCode(int successCode) {
        this.successCode = successCode;
        return this;
    }

    public static void init(Application context) {
        DownLoadManage.init(context);
        isDownloadInit = true;
    }

    public Updater setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public void checkVersion(String url) {
        if (!isDownloadInit) {
            throw new IllegalArgumentException("set Update.init(this) int Application onCreate func..");
        }

        getRetrofit().create(updateService.class)
                .checkVersion(url)
                .compose(T.create().<ResultVersion>transformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver<ResultVersion>() {
                    @Override
                    public void onResult(ResultVersion resultVersion) {
                        super.onResult(resultVersion);
                        if (resultVersion != null && resultVersion.getCode() == successCode) {
                            ResultVersion.Version version = resultVersion.getData();
                            if (version != null && Integer.valueOf(version.getVersion_code()) > versionCode) {
                                showLoadDialog(version);
                            }
                        }
                    }
                });
    }

    private void showLoadDialog(ResultVersion.Version version) {

        loadInfo = new DownLoadManage.Info(version.getUpdate_url());
        loadInfo.setSaveName(getAppDownloadName(version.getApp_name(), version.getVersion_name()));

        controller = VersionDialogController.build()
                .setTitle("发现新版本 " + version.getVersion_name())
                .setTouchDismiss(Integer.valueOf(version.getForce()) == 0)
                .setContent(version.getUpdate_content())
                .setCompletedClickListener(new VersionDialogController.onCompletedClickListener() {
                    @Override
                    public void installApp() {
                        try {
                            DownLoadManage.getInstance().installApk(context, loadInfo);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                })
                .setOnDownStartClickListener(new VersionDialogController.onDownStartClickListener() {
                    @Override
                    public void startDownload() {
                        try {
                            File file = new File(loadInfo.getSavePath());
                            if (file.exists()) {
                                file.delete();
                            }
                        } catch (Exception e) {
                        }
                        DownLoadManage.getInstance().downloadUpdateApk(loadInfo, controller);
                    }
                });

        PopupDialog.create(context).controller(controller).show();

    }


    public static String getAppDownloadName(String appName, String versionName) {
        return appName + "_v" + versionName + ".apk";
    }

    public Retrofit getRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();
        return new Retrofit.Builder()
                .client(client)
                .baseUrl("http://127.0.0.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    public interface updateService {
        @GET
        Observable<ResultVersion> checkVersion(@Url String url);
    }
}
