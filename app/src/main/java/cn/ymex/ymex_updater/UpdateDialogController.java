package cn.ymex.ymex_updater;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.ymex.popup.controller.DialogControllable;
import cn.ymex.popup.dialog.PopupDialog;

/**
 * Created by ymexc on 2018/9/4.
 */
public class UpdateDialogController implements DialogControllable {

    private ProgressBar progressBar;
    private TextView tvProgressTip, tvTitle, tvContent,tvStatus;
    private View vProgress, btnLine;

    private String positiveName;
    private String negativeName;

    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;

    private String positiveNameAfter;
    private View.OnClickListener positiveListenerAfter;

    private Button btnNegative, btnPositive;
    private boolean dismiss = true;
    private boolean forceUpdate = false;


    private UpdateDialogController() {
    }

    public static UpdateDialogController build() {
        return new UpdateDialogController();
    }

    @Override
    public View createView(Context context, ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.dialog_version_layout, parent, false);

    }

    @Override
    public PopupDialog.OnBindViewListener bindView() {

        return new PopupDialog.OnBindViewListener() {
            @Override
            public void onCreated(final PopupDialog dialog, View layout) {
                if (forceUpdate) {
                    dialog.outsideTouchHide(false);
                    dialog.backPressedHide(false);
                }


                progressBar = layout.findViewById(R.id.pbProgress);
                tvProgressTip = layout.findViewById(R.id.tvProgressTip);
                tvTitle = layout.findViewById(R.id.tvTitle);
                tvContent = layout.findViewById(R.id.tvContent);
                vProgress = layout.findViewById(R.id.vProgress);
                btnLine = layout.findViewById(R.id.btnLine);

                btnNegative = layout.findViewById(R.id.btnNegative);
                btnPositive = layout.findViewById(R.id.btnPositive);

                tvStatus = layout.findViewById(R.id.tvStatus);

                tvTitle.setText(title);
                tvContent.setText(content);

                if (!TextUtils.isEmpty(negativeName )&& !forceUpdate) {
                    btnLine.setVisibility(View.VISIBLE);
                    btnNegative.setVisibility(View.VISIBLE);
                    btnNegative.setText(negativeName);
                    btnNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (dismiss ) {
                                dialog.dismiss();
                            }
                            if (negativeListener != null) {
                                negativeListener.onClick(btnNegative);
                            }
                        }
                    });

                } else {
                    btnNegative.setText("");
                    btnLine.setVisibility(View.GONE);
                    btnNegative.setVisibility(View.GONE);
                }
                btnPositive.setText(positiveName);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (positiveListener != null) {
                            if (isLoading) {
                                return;
                            }
                            positiveListener.onClick(btnPositive);
                        }

                    }
                });
            }
        };
    }

    private String title;

    public UpdateDialogController setTitle(String title) {
        this.title = title;
        return this;
    }


    private String content;

    public UpdateDialogController setContent(String content) {
        this.content = content;
        return this;
    }

    public UpdateDialogController setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
        return this;
    }

    //positive
    public UpdateDialogController negativeButton(String text, View.OnClickListener listener) {
        this.negativeName = text;
        this.negativeListener = listener;
        return this;
    }

    public UpdateDialogController positiveButton(String text, View.OnClickListener listener) {
        this.positiveName = text;
        this.positiveListener = listener;
        return this;
    }

    public UpdateDialogController positiveButtonAfterLoading(String text, View.OnClickListener listener) {
        this.positiveNameAfter = text;
        this.positiveListenerAfter = listener;
        return this;
    }

    public UpdateDialogController clickDismiss(boolean dismiss) {
        this.dismiss = dismiss;
        return this;
    }

    private boolean isLoading = false;
    public void startLoading() {
        isLoading = true;
    }

    public void reset() {
        this.vProgress.setVisibility(View.GONE);
        this.tvStatus.setText("正在下载");
        btnPositive.setText(negativeName);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (positiveListener != null) {
                    positiveListener.onClick(btnPositive);
                }
            }
        });
        isLoading = false;
    }

    public void setProgress(int progress) {
        if (this.vProgress == null) {
            return;
        }

        this.vProgress.setVisibility(View.VISIBLE);
        tvProgressTip.setText(String.valueOf(progress) + "%");
        progressBar.setProgress(progress);
        if (progress >= 100) {
            this.tvStatus.setText("下载完成");
            btnPositive.setText(positiveNameAfter);
            btnPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (positiveListenerAfter != null) {
                        positiveListenerAfter.onClick(btnPositive);
                    }
                }
            });
            isLoading = false;
        }
    }


}
