package com.cc.ui;

import android.Manifest;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.cc.app.R;
import com.cc.ui.base.RuntimePermissionFragment;
import com.cc.ui.yourmusic.YourMusicActivity;

import com.cc.ui.karaoke.data.database.helper.AssetDatabaseOpenHelper;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Author: NT
 * Since: 11/10/2016.
 */
public class SplashScreenFragment extends RuntimePermissionFragment {

    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 500;
    public static final int ITEM_DELAY = 300;

    private boolean animationStarted = false;

    private boolean checkGrantedPermissionReadExternal() {
        return isPermissionGranted(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                RuntimePermissionFragment.PERMISSION_READ_STORAGE_CODE);
    }

    private void animate(View view) {
        ImageView logoImageView = (ImageView) view.findViewById(R.id.img_logo);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.container);

        ViewCompat.animate(logoImageView)
                .translationY(-250)
                .setStartDelay(STARTUP_DELAY)
                .setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new DecelerateInterpolator(1.2f)).start();
        ViewPropertyAnimatorCompat viewAnimator = null;
        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);


            if (!(v instanceof Button)) {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(50).alpha(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(1000);
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(500);
            }


            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
        }

        viewAnimator.setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {

            }

            @Override
            public void onAnimationEnd(View view) {
                if (checkGrantedPermissionReadExternal()) {
                }
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        });

    }

    private void prepareOpenMainActivity() {
         final AssetDatabaseOpenHelper assetDatabaseOpenHelper = new
                 AssetDatabaseOpenHelper(getActivity());
        assetDatabaseOpenHelper.openDatabase(new AssetDatabaseOpenHelper.OnOpenAssetDatabaseListener() {
            @Override
            public void onCopySuccess(SQLiteDatabase db) {
                Log.e(TAG, "onCopySuccess");
                AssetDatabaseOpenHelper.saveVersionDatabase();
                startYourMusic();
            }

            @Override
            public void onCopyAlready() {
                Log.e(TAG, "onCopyAlready");
                startYourMusic();
            }

            @Override
            public void onCopyError(String error) {
                Log.e(TAG, "onCopyError");
            }
        });

    }


    private void startYourMusic() {
        final Intent intent = new Intent(getActivity(), YourMusicActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        animate(view);
        if (checkGrantedPermissionReadExternal()) {
            prepareOpenMainActivity();
        }
    }

    @Override
    protected void permissionGranted(int permissionRequestCode) {
        switch (permissionRequestCode) {
            case RuntimePermissionFragment.PERMISSION_READ_STORAGE_CODE:
                prepareOpenMainActivity();
                break;
        }
    }

    @Override
    protected void setupFragmentComponent() {

    }

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_splash_screen_onboarding_center;
    }
}