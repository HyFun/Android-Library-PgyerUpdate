package com.hyfun.lib.pgyer;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface API {
    String UPDATE_APP_URL = "https://www.pgyer.com/apiv2/";

    @FormUrlEncoded
    @POST("app/check")
    Observable<ResponseBody> checkUpdate(@Field("_api_key") String api_key, @Field("appKey") String app_key, @Field("buildVersion") String versionName);

}
