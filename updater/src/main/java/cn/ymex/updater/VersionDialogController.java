package cn.ymex.updater;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.ymex.popup.controller.DialogControllable;
import cn.ymex.popup.dialog.PopupDialog;

/**
 * Created by ymexc on 2017/11/27.
 * About:升级弹出框
 */

public class VersionDialogController implements DialogControllable {

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
            public void onCreated(PopupDialog dialog, View layout) {
                dialog.outsideTouchHide(touchDismiss);
                dialog.backPressedHide(touchDismiss);
                btnUpdate = (Button) layout.findViewById(R.id.btn_update);
                vProgress = layout.findViewById(R.id.v_progress);
                tvProgress = (TextView) layout.findViewById(R.id.tv_progress);
                TextView textView = (TextView) layout.findViewById(R.id.tvContent);
                textView.setText(Html.fromHtml(content));
                TextView tvTitle = (TextView) layout.findViewById(R.id.tv_title);
                if (TextUtils.isEmpty(title)) {
                    tvTitle.setVisibility(View.GONE);
                } else {
                    tvTitle.setVisibility(View.VISIBLE);
                    tvTitle.setText(title);
                }
            }
        };
    }
}
