package com.cc.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.cc.app.R;


/**
 * Author  : duyng
 * since   : 10/12/2016
 */

public class DialogUtil {

    public static void showDialogConfirm(Context context, String message,
                                         final OnClickDialogListener listener) {
        showDialogConfirm(context, "", message, listener);
    }

    public static void showDialogConfirm(Context context, String title, String message,
                                         final OnClickDialogListener listener) {

        DialogUtil.showDialogExit(context, title, message, new OnClickDialogListener() {
            @Override
            public void onClickPositive() {
                if (listener != null) {
                    listener.onClickPositive();
                }
            }

            @Override
            public void onClickNegative() {
                if (listener != null) {
                    listener.onClickNegative();
                }
            }
        });
    }

    public static void showDialogExit(Context context, String title, String message,
                                      final OnClickDialogListener listener) {
        DialogUtil.showDialogExit(context, title, message, new OnClickDialogListener() {
            @Override
            public void onClickPositive() {
                if (listener != null) {
                    listener.onClickPositive();
                }
            }

            @Override
            public void onClickNegative() {

            }
        });

    }

    public static void showDialogOnlyPositvie(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
    }

    public interface OnClickDialogListener {
        void onClickPositive();

        void onClickNegative();
    }
}
