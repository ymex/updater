package cn.ymex.ymex_updater;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.ymex.permission.PermissionRequest;
import cn.ymex.popup.dialog.PopupDialog;
import cn.ymex.updater.Updater;

public class AppLaunchActivity extends AppCompatActivity implements Updater.DownloadListener, PermissionRequest.Dispatcher {

    PermissionRequest permissionRequest;
    UpdateDialogController updateDialogController;//更新对话框
    String apkPath ;//下载的apk路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launch);
//需要使用的权限
        permissionRequest = PermissionRequest.build(this)
                .requestCode(1)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .dispatcher(this);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final String url = "https://ojlyqybn1.qnssl.com/lejinsuo_latest.apk";



        updateDialogController = UpdateDialogController.build()
                .setTitle("新版本V1.0.3")
                .setContent("1.新增加模块\n2.fix bug")
                .negativeButton("取消", null)
                .setForceUpdate(true)
                .positiveButton("下载更新", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Updater.get().downloadApp(AppLaunchActivity.this, url);
                    }
                }).positiveButtonAfterLoading("安装", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Updater.get().installApp(apkPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!permissionRequest.checkSelfPermission()) {
                    permissionRequest.checkPermissions();
                    return;
                }
                //显示更新框
                PopupDialog.create(AppLaunchActivity.this).controller(updateDialogController).show();
            }
        });

    }


    @Override
    public void onDownProgress(int progress) {
        updateDialogController.setProgress(progress);//进度
    }

    @Override
    public void onStartDown() {
        updateDialogController.startLoading();//设置加载中（防止重复点击下载）
    }

    @Override
    public void onDownFinish(String path) {
        apkPath = path;
        updateDialogController.setProgress(100);//设置完成
        try {
            Updater.get().installApp(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownError(Throwable throwable) {
       updateDialogController.reset();
    }


    //------------------动态权限
    @Override
    public void onPermissionGranted(int requestCode) {
        //获取权限
    }

    @Override
    public void onPermissionDenied(int requestCode) {
        //拒绝权限
    }

    @Override
    public void onShowPermissionRationale(int requestCode) {
        //拒绝权限且没有点击不再提醒。
        // 此场景可给予解释权限，并重新请求权限。
        // permissionRequest.requestPermissions();//请求权限

    }

    @Override
    public void OnNeverAskPermission(int requestCode) {//设置不再询问
        //拒绝权限且勾选了不再提醒。
        //此场景再请求权限则无效，要引导用户去设置里开户权限
        //permissionRequest.startAppSettings();//打开权限设置界面
    }
}
