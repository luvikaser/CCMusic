package com.cc.ui.karaoke.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import com.cc.ui.karaoke.utils.DebugLog;

/**
 * Author: NT
 * Since: 9/21/2016.
 */
public class HttpFileUpload {
    public interface onUploadListener {
        void onUploadSuccess(String data);

        void onProgressUpload(long percent);

        void onUploadFailed(String error);
    }

    public static void uploadSound(File fileAudio, final onUploadListener listener) {

        RequestParams params = new RequestParams();
        try {
            params.put("audioFile", fileAudio);
        } catch (FileNotFoundException e) {
            listener.onUploadFailed("file not found");
            DebugLog.e("HttpFileUpload", e.toString());
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(5000);

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            com.loopj.android.http.MySSLSocketFactory sf = new com.loopj.android.http
                    .MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(com.loopj.android.http.MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);
        } catch (Exception e) {
        }


        client.post("http://upload.clyp.it/upload", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String data = new String(responseBody);
                DebugLog.e("HttpFileUpload", data);
                listener.onUploadSuccess(data);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                 listener.onUploadFailed("upload file error");
                DebugLog.e("HttpFileUpload", error.toString());
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                long progressPercentage = (long) 100 * bytesWritten / totalSize;
                DebugLog.e("onProgress", " " + progressPercentage);
                listener.onProgressUpload(progressPercentage);
            }

        });
    }

    public static SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};

        SSLContext sslContext = null;

        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }

}