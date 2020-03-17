package com.hyfun.lib.pgyer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.ResourceObserver;
import okhttp3.ResponseBody;

/**
 * Created by HyFun on 2018/10/30.
 * Email:775183940@qq.com
 */

class PgyerUpdatePresenter {

    private PgyerUpdateView view;
    private Gson gson;

    public PgyerUpdatePresenter(PgyerUpdateView pgyerUpdateView) {
        this.view = pgyerUpdateView;
        gson = new Gson();
    }


    /**
     * 检测更新
     *
     * @param api_key     蒲公英用户的apikey
     * @param app_key     蒲公英app的app key
     * @param versionName 当前版本的版本号
     */
    public void check(String api_key, String app_key, String versionName) {
        // 开始检测更新
        view.showCheckDialog("正在检测更新");

        HttpService.getService().checkUpdate(api_key, app_key, versionName)
                .flatMap(new Function<ResponseBody, ObservableSource<PgyerUpdateInfo>>() {
                    @Override
                    public ObservableSource<PgyerUpdateInfo> apply(ResponseBody responseBody) throws Exception {
                        String json = responseBody.string();
                        JSONObject jsonObject = new JSONObject(json);
                        int code = jsonObject.getInt("code");
                        if (code == 0) {
                            String data = jsonObject.getString("data");
                            PgyerUpdateInfo updateInfo = gson.fromJson(data, new TypeToken<PgyerUpdateInfo>() {
                            }.getType());
                            return Util.createData(updateInfo);
                        } else {
                            String error = jsonObject.getString("message");
                            return Observable.error(new Exception(error));
                        }
                    }
                })
                .compose(Util.<PgyerUpdateInfo>io_main())
                .subscribeWith(new ResourceObserver<PgyerUpdateInfo>() {
                    @Override
                    public void onNext(PgyerUpdateInfo updateInfo) {
                        String downLoadUrl = updateInfo.getDownloadURL();
                        view.isNeedUpdate(updateInfo.isBuildHaveNewVersion());
                        if (!updateInfo.isBuildHaveNewVersion()) {
                            // 说明是最新版本的app
                            view.showCheckDialogResult("当前应用已是最新版本");
                        } else {
                            // 说明需要更新app
                            view.hideCheckDialog();
                            view.showUpdateDialog(PgyerUpdatePresenter.this, updateInfo.getBuildVersion(), downLoadUrl, updateInfo.getBuildUpdateDescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showCheckDialogResult(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 下载文件 带进度
     *
     * @param url
     * @param fileName
     * @param savePath
     */
    public void downLoadApk(String url, String fileName, String savePath) {
        new DownloadTask.Builder(url, savePath, fileName)
                .setMinIntervalMillisCallbackProcess(30)
                .setPassIfAlreadyCompleted(false)
                .build()
                .enqueue(new DownloadListener4WithSpeed() {

                    private long totalLength;

                    @Override
                    public void taskStart(@NonNull DownloadTask task) {
                        view.showDownLoadDilog("正在准备更新APP");
                    }

                    @Override
                    public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

                    }

                    @Override
                    public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

                    }

                    @Override
                    public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
                        totalLength = info.getTotalLength();
                    }

                    @Override
                    public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {

                    }

                    @Override
                    public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
                        int progress = (int) ((currentOffset * 1.0 / totalLength) * 100);
                        view.onDownLoadProgress(progress);
                    }

                    @Override
                    public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {

                    }

                    @Override
                    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                        if (cause == EndCause.COMPLETED) {
                            view.onDownLoadSuccess(task.getFile());
                        } else {
                            view.onDownLoadFailed(realCause.getMessage());
                        }
                    }
                });
    }
}
