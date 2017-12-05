package cn.ymex.updater;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;

import cn.ymex.popup.controller.DialogControllable;
import cn.ymex.popup.dialog.PopupDialog;

/**
 * Created by ymexc on 2017/11/27.
 * About:升级弹出框
 */

public class VersionDialogController extends FileDownloadSampleListener implements DialogControllable {

    private boolean touchDismiss = true;
    private String content;
    private Button btnUpdate;
    private View vProgress;
    private TextView tvProgress;
    private String title;


    public static VersionDialogController build() {
        return new VersionDialogController();
    }

    public VersionDialogController setTouchDismiss(boolean touchDismiss) {
        this.touchDismiss = touchDismiss;
        return this;
    }

    public VersionDialogController setContent(String content) {
        this.content = content;
        return this;
    }

    public Button getBtnUpdate() {
        return btnUpdate;
    }

    public TextView getProgressTextView() {
        return tvProgress;
    }

    public View getProgressView() {
        return vProgress;
    }

    public VersionDialogController setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public View createView(Context context, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.dialog_version_layout, parent, false);
    }

    @Override
    public PopupDialog.OnBindViewListener bindView() {
        return new PopupDialog.OnBindViewListener() {
            @Override
            public void onCreated(final PopupDialog dialog, View layout) {
                dialog.outsideTouchHide(false);
                dialog.backPressedHide(false);
                btnUpdate = (Button) layout.findViewById(R.id.btn_update);
                vProgress = layout.findViewById(R.id.v_progress);
                tvProgress = (TextView) layout.findViewById(R.id.tv_progress);
                TextView textView = (TextView) layout.findViewById(R.id.tvContent);
                textView.setText(content);
                TextView tvTitle = (TextView) layout.findViewById(R.id.tv_title);
                if (touchDismiss) {
                    ImageView ivClose = layout.findViewById(R.id.iv_close);
                    ivClose.setVisibility(View.VISIBLE);
                    ivClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }

                if (TextUtils.isEmpty(title)) {
                    tvTitle.setVisibility(View.GONE);
                } else {
                    tvTitle.setVisibility(View.VISIBLE);
                    tvTitle.setText(title);
                }
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getBtnUpdate().setVisibility(View.INVISIBLE);
                        getProgressView().setVisibility(View.VISIBLE);
                        if (onDownStartClickListener != null) {
                            onDownStartClickListener.startDownload();
                        }
                    }
                });
            }
        };
    }


    //----DownLoadListener----
    @Override
    public void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        getProgressTextView().setText(String.valueOf(pb(soFarBytes, totalBytes)) + "%");
    }

    @Override
    public void completed(BaseDownloadTask task) {
        getProgressTextView().setText("100%");
        getBtnUpdate().setText("安装新版本");
        getBtnUpdate().setVisibility(View.VISIBLE);
        getProgressView().setVisibility(View.INVISIBLE);
        getBtnUpdate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completedClickListener != null) {
                    completedClickListener.installApp();
                }
            }
        });
    }

    @Override
    public void started(BaseDownloadTask task) {
        getProgressTextView().setText("0%");
    }

    @Override
    public void error(BaseDownloadTask task, Throwable e) {

    }

    private int pb(int soFarBytes, int totalBytes) {
        float p = (soFarBytes * 100f) / totalBytes;
        return p >= 100 ? 100 : (int) p;
    }

    public VersionDialogController setCompletedClickListener(onCompletedClickListener completedClickListener) {
        this.completedClickListener = completedClickListener;
        return this;
    }

    private onCompletedClickListener completedClickListener;

    public interface onCompletedClickListener {
        void installApp();
    }

    public VersionDialogController setOnDownStartClickListener(VersionDialogController.onDownStartClickListener onDownStartClickListener) {
        this.onDownStartClickListener = onDownStartClickListener;
        return this;
    }

    private onDownStartClickListener onDownStartClickListener;

    public interface onDownStartClickListener {
        void startDownload();
    }

}
