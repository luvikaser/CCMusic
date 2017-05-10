package com.cc.ui.karaoke.network;

import java.util.concurrent.TimeUnit;

import mmobile.com.karaoke.BuildConfig;
import com.cc.ui.karaoke.app.BaseConstants;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author: NT
 * Since: 6/20/2016.
 */
public class VMApiClient {
    public static final String BASE_URL = "http://cuongthuy.com:8080/";
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient;

    public static Retrofit getClient() {
        if (retrofit == null) {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(interceptor);
            }

            builder.connectionPool(new ConnectionPool(BaseConstants
                    .CONNECTION_POOL_COUNT, BaseConstants.KEEP_ALIVE_DURATION_MS, TimeUnit
                    .MILLISECONDS));
            okHttpClient = builder.build();

            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
