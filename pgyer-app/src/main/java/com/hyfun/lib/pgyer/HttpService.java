package com.hyfun.lib.pgyer;

import com.google.gson.Gson;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class HttpService {
    public static API api;

    public static synchronized final API getService() {
        if (api == null) {
            synchronized (HttpService.class) {
                if (api == null) {
                    // okhttp
                    OkHttpClient.Builder okbuilder = new OkHttpClient.Builder();
                    okbuilder.addNetworkInterceptor(getLoggingInterceptor(BuildConfig.DEBUG));
                    //错误重连
                    okbuilder.retryOnConnectionFailure(true);

                    // retrofit2
                    Retrofit.Builder builder = new Retrofit.Builder();
                    builder.client(okbuilder.build());
                    builder.baseUrl(API.UPDATE_APP_URL);//设置远程地址
                    builder.addConverterFactory(GsonConverterFactory.create(new Gson()));
                    builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());

                    api = builder.build().create(API.class);

                }
            }
        }
        return api;
    }


    /**
     * 日志的Interceptor
     *
     * @return
     */
    private static HttpLoggingInterceptor getLoggingInterceptor(boolean debug) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (debug) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 测试
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE); // 打包
        }
        return interceptor;
    }

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }};
}
