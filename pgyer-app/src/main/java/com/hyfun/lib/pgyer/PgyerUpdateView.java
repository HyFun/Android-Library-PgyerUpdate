package com.hyfun.lib.pgyer;

import java.io.File;

/**
 * Created by HyFun on 2018/10/30.
 * Email:775183940@qq.com
 */

interface PgyerUpdateView {
    void showCheckDialog(String message);

    void showCheckDialogResult(String message);

    void hideCheckDialog();

    void isNeedUpdate(boolean need);

    void showUpdateDialog(PgyerUpdatePresenter presenter, String versionName, String downLoadUrl, String updateDetail);

    void showDownLoadDilog(String message);

    void onDownLoadProgress(int progress);

    void onDownLoadSuccess(File file);

    void onDownLoadFailed(String message);
}
