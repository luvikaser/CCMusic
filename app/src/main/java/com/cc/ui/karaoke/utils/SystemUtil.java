package com.cc.ui.karaoke.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import mmobile.com.karaoke.R;


/**
 * Project:  ZingMobile
 * Author:   Khuong Vo
 * Since:    8/10/2015
 * Time:     11:10 PM
 */
public class SystemUtil {

    public static boolean canDrawOverlays(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }else{
            return Settings.canDrawOverlays(context);
        }
    }
    /**
     * get device unique id
     *
     * @param context application context
     * @return device id value
     */
    private static String deviceId = "";

    public static String getDeviceId(Context context) {
        if (deviceId != null && deviceId.length() > 0)
            return deviceId;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            deviceId = tm.getDeviceId();
        } catch (SecurityException ex) {

        }

        if (TextUtils.isEmpty(deviceId) || deviceId.equals("000000000000000")) {
            //first two case(deviceId null or length), but if it does we fallback to android id
            //last case of 000000000000000 happens when you run the app via a simulator
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return deviceId;
    }

    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        //int sdkVersion = Build.VERSION.SDK_INT;
        //return "Android SDK: " + sdkVersion + " (" + release +")";
        return release;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    private static String appVersion = "";

    public static String getAppVersion(Context context) {
        if (appVersion != null && appVersion.length() > 0)
            return appVersion;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionName;
            return appVersion;
        } catch (PackageManager.NameNotFoundException ex) {

        }
        return "";
    }

    /**
     * call a phone number
     *
     * @param context
     * @param phoneNumber
     */
    public static void call(@NonNull Context context, @NonNull String phoneNumber) {
        String uri = "tel:" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param recipients
     * @param subject
     * @param text
     * @param cc
     */
    public static void openEmailComposer(@NonNull Context context, String[] recipients, String subject, String text, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("text/html");
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.LBL_SEND_EMAIL)));
    }


    public static void startActivity(Context context, Intent intent, boolean isFinishPreviousActivity) {
        startActivity(context, intent, isFinishPreviousActivity, R.anim.anim_activity_next, R.anim.anim_activity_next_release);
    }


    public static void startActivity(Context context, Intent intent, boolean isFinishPreviousActivity, int enterAnim, int exitAnim) {
        context.startActivity(intent);
         if (isFinishPreviousActivity)
            ((Activity) context).finish();
    }

    public static void backToActivity(Context context, Intent intent) {
        context.startActivity(intent);
         ((Activity) context).finish();
    }

    public static boolean isMainThead() {
        boolean ret = false;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ret = true;
        }
        DebugLog.d("SystemUtil", ret ? " Current Thread is Main Thread " : " Current Thread is Background Thread ");
        return ret;
    }

    public static boolean isMainThead(String TAG) {
        boolean ret = false;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ret = true;
        }
        DebugLog.d(TAG, ret ? " Current Thread is Main Thread " : " Current Thread is Background Thread ");
        return ret;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
