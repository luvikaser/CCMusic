package com.cc.ui.base;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cc.domain.utils.PermissionUtil;

import java.util.Arrays;


/**
 * Author: NT
 * Since: 10/9/2016.
 * we need to recognize the dangerous permissions that require us to request runtime permissions.
 */
public abstract class RuntimePermissionFragment extends BaseFragment {

    // define permission request
    public static final int PERMISSION_READ_STATE_PHONE_REQUEST_CODE = 40;
    public static final int PERMISSION_PUSH_STREAMING_REQUEST_CODE = 41;
    public static final int PERMISSION_READ_CONTACTS_CODE = 42;
    public static final int PERMISSION_RECORD_AUDIO_CODE = 43;
    public static final int PERMISSION_LOCATION_CODE = 44;
    public static final int PERMISSION_READ_STORAGE_CODE = 45;

    public static final int DENY_PERMISSION = -1;

    protected abstract void permissionGranted(int permissionRequestCode);

    /**
     * check type permission but you need
     *
     * @param permissions           used to get list type of permission in manifest
     * @param requestPermissionCode used to get request code of permission
     * @return result check permission
     */
    protected boolean isPermissionGranted(String[] permissions, int requestPermissionCode) {
        if (!PermissionUtil.checkAndroidMVersion())
            return true;

        int[] selfPermission = PermissionUtil.getSelfPermission(getContext(), permissions);

        if (PermissionUtil.verifyPermission(selfPermission))
            return true;//permission have been granted
        else
            requestPermissions(permissions, requestPermissionCode);

        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult requestCode: " + requestCode + " " +
                "permissions: " + Arrays.toString(permissions) + " grantResults: " + Arrays.toString(grantResults));
        // return type permission request if it granted success
        if (PermissionUtil.verifyPermission(grantResults)) {
            permissionGranted(requestCode);
        } else {
            permissionGranted(DENY_PERMISSION);
        }
    }
}