package com.hyfun.lib.pgyer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

public class Pgyer {
    private static final String TAG = "Pgyer";


    public static final void checkUpdate(Context context, boolean showDialog) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (info.metaData == null) {
                Log.e(TAG, "请在application中设置meta-data pyger_api_key和pyger_app_key");
                return;
            }
            String apiKey = info.metaData.getString("pyger_api_key");
            String appKey = info.metaData.getString("pyger_app_key");
            if (TextUtils.isEmpty(apiKey)) {
                Log.e(TAG, "请在application中设置meta-data pyger_api_key");
                return;
            }

            if (TextUtils.isEmpty(appKey)) {
                Log.e(TAG, "请在application中设置meta-data pyger_app_key");
                return;
            }

            PgyerUpdatePresenter presenter = new PgyerUpdatePresenter(new UpdateImp(context, context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), showDialog));
            presenter.check(apiKey, appKey, Util.getPackageInfo(context).versionName);

        } catch (PackageManager.NameNotFoundException e) {
            return;
        }
    }


    private static class UpdateImp implements PgyerUpdateView {
        private Context context;
        private String savePath;
        private boolean showDialog;
//        private AppNeedUpdateListener needUpdateListener;

        public UpdateImp(Context context, String savePath, boolean showDialog) {
            this.context = context;
            this.savePath = savePath;
            this.showDialog = showDialog;
//            this.needUpdateListener = needUpdateListener;
        }

        private PgyerSimpleDialog simpleDialog;

        @Override
        public void showCheckDialog(String message) {
            if (showDialog) {
                if (simpleDialog == null) {
                    simpleDialog = new PgyerSimpleDialog.Builder(context)
                            .cancel(false)
                            .content(message)
                            .build();
                }
                simpleDialog.show();
            }
        }

        @Override
        public void showCheckDialogResult(String message) {
            if (showDialog) {
                if (simpleDialog != null) {
                    simpleDialog.setContent(message);
                    simpleDialog.setCancel(true);
                }
            }
        }

        @Override
        public void hideCheckDialog() {
            if (simpleDialog != null) {
                simpleDialog.dismiss();
            }
        }

        @Override
        public void isNeedUpdate(boolean need) {
        }

        @Override
        public void showUpdateDialog(final PgyerUpdatePresenter presenter, String versionName, final String downLoadUrl, String updateDetail) {
            // 显示更新提示框
            // 首先判断该activity是否已经下载了
            final String apkName = versionName + ".apk";
            final File localFile = getLocalExistFile(apkName, savePath);

            new MaterialDialog.Builder(context)
                    .title(localFile == null ? "是否更新到" + versionName + "版本？" : "已下载好" + versionName + "版本，是否安装？")
                    .content(updateDetail)
                    .negativeText("取消")
                    .positiveText(localFile == null ? "立即更新" : "立即安装")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (localFile == null) {
                                presenter.downLoadApk(downLoadUrl, apkName, savePath);
                            } else {
                                Util.installApk(context, localFile);
                            }
                            dialog.dismiss();
                        }
                    })
                    .show();
        }


        private MaterialDialog downloadDialog;

        @Override
        public void showDownLoadDilog(String message) {
            if (downloadDialog == null) {
                downloadDialog = new MaterialDialog.Builder(context)
                        .title("下载更新")
                        .progress(false, 100)
                        .build();
            }
            downloadDialog.show();
        }

        @Override
        public void onDownLoadProgress(int progress) {
            if (downloadDialog != null) {
                downloadDialog.setCancelable(false);
                downloadDialog.setProgress(progress);
            }
        }

        @Override
        public void onDownLoadSuccess(File file) {
            if (downloadDialog != null) {
                downloadDialog.dismiss();
            }
            Util.installApk(context, file);
        }

        @Override
        public void onDownLoadFailed(String message) {
            if (downloadDialog != null) {
                downloadDialog.setTitle("下载错误");
                downloadDialog.setContent(message);
                downloadDialog.setCancelable(true);
            }
        }

    }


    private static final File getLocalExistFile(String fileName, String path) {
        File file = null;
        File parentDir = new File(path);
        if (parentDir.isDirectory()) {
            File[] files = parentDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (fileName.equals(files[i].getName())) {
                    file = files[i];
                    return file;
                }
            }
        }
        return file;
    }
}
