package cn.ymex.updater;

import android.app.Application;
import android.content.Context;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;

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
 * About:
 */

public class Updater extends FileDownloadSampleListener {
    private static boolean isDownloadInit = false;
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


    public static void init(Application context) {
        DownLoadManage.init(context);
    }

    public Updater setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public void checkVersion(String mm) {
        if (!isDownloadInit) {
            throw new IllegalArgumentException("set Update.init(this) int Application onCreate func..");
        }
        getRetrofit().create(updateService.class)
                .checkVersion(mm)
                .compose(T.create().<ResultVersion>transformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver<ResultVersion>() {
                    @Override
                    public void onResult(ResultVersion resultVersion) {
                        super.onResult(resultVersion);
                        if (resultVersion != null && resultVersion.getCode() == 20000) {
                            ResultVersion.Version version = resultVersion.getData();
                            if (version != null && Integer.valueOf(version.getVersion_code()) > versionCode) {
                                showLoadDialog(version);
                            }
                        }
                    }
                });
    }

    private void showLoadDialog(ResultVersion.Version version) {
        controller = VersionDialogController.build()
                .setTitle("发现新版本")
                .setTouchDismiss(Integer.valueOf(version.getForce()) == 1)
                .setContent(version.getUpdate_content());
        PopupDialog.create(context).controller(controller).show();

        loadInfo = new DownLoadManage.Info(version.getUpdate_url());
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

    public Retrofit getRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl("http://127.0.0.1/")
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
        controller.getBtnUpdate().setVisibility(View.VISIBLE);
        controller.getProgressView().setVisibility(View.INVISIBLE);
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

    /**
     * Created by ymexc on 2017/11/27.
     * About: api
     */

    public static interface updateService {
        @GET
        Observable<ResultVersion> checkVersion(@Url String url);
    }
}
