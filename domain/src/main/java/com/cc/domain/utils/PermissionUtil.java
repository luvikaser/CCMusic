package com.cc.domain.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Author: NT
 * Since: 10/9/2016.
 * provider basic method for check permission in android M
 */
public class PermissionUtil {

    /**
     * check that all given permission have been granted, then verify each entry
     * permission
     *
     * @param grantResult used to get array grant result permission
     * @return result verify permission
     */
    public static boolean verifyPermission(int[] grantResult) {

        if (grantResult.length < 1)
            return false;

        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    /**
     * you will check version of machine, if you use android M
     *
     * @return result check version android M
     */
    public static boolean checkAndroidMVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static int[] getSelfPermission(Context context, String[] permission) {
        int[] selfPermission = new int[permission.length];
        for (int i = 0; i < permission.length; i++) {
            selfPermission[i] = ContextCompat.checkSelfPermission(context, permission[i]);
        }

        return selfPermission;
    }

}