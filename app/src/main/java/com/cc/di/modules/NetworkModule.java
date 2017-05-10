package com.cc.di.modules;

import android.content.Context;

import com.cc.app.BuildConfig;
import com.cc.data.MusicConstantsApp;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author: NT
 * Since: 10/27/2016.
 */

@Module
public class NetworkModule {

    private static final String API_HTTP_URL = BuildConfig.HOST;

    @Provides
    @Singleton
    @Named("BASE_URL")
    String provideBaseUrl() {
        return API_HTTP_URL;
    }

    public NetworkModule() {
    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(Context application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }


    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        builder.connectionPool(new ConnectionPool(MusicConstantsApp.CONNECTION_POOL_COUNT, MusicConstantsApp.CONNECTION_KEEP_ALIVE_DURATION, TimeUnit.MILLISECONDS));
        return builder.build();
    }



    @Provides
    @Singleton
    @Named("BASE_REST_ADAPTER")
    Retrofit provideRestAdapter(OkHttpClient okHttpClient, @Named("BASE_URL") String endpoint) {
        GsonBuilder builder = new GsonBuilder();
        Retrofit.Builder restAdapter = new Retrofit.Builder();
        restAdapter.client(okHttpClient)
                .baseUrl(endpoint)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(builder.create()));

        return restAdapter.build();
    }

    // not yet
    @Provides
    @Singleton
    @Named("paramDefault")
    HashMap<String, String> providesParamsDefault(Context application) {
        HashMap<String, String> params = new HashMap<>();
        params.put("platform", "1");
        params.put("appversion", BuildConfig.VERSION_NAME);
   /*     params.put("devid", AndroidUtilities.uniqueDeviceID(application));
        params.put("device_name", AndroidUtilities.getDeviceName());*/
        return params;
    }

}