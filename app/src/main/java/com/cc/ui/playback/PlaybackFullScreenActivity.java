package com.cc.ui.playback;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cc.app.R;
import com.cc.event.VolumeChangeEvent;
import com.cc.ui.base.BaseActivity;
import com.cc.ui.base.BaseFragment;
import com.facebook.CallbackManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Author: NT
 * Since: 11/2/2016.
 */
public class PlaybackFullScreenActivity extends BaseActivity {
    public static final String ARG_PARENT_MEDIA_ID = "media_id";
    public static CallbackManager callbackManager;
    @Override
    public BaseFragment getFragmentToHost() {
        return PlaybackControlFullScreenFragment.newInstance(new
                PlaybackControlFullScreenFragment(), getIntent().getExtras());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

    }

    @Override
    public void onPause() {
        super.onPause();

        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

//    private void runStart() {
//        Animation a = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
//        a.reset();
//        FrameLayout ll = (FrameLayout) findViewById(R.id.fragment_container);
//        ll.clearAnimation();
//        ll.startAnimation(a);
//    }
//
//    private void runFinish() {
//        Animation a = AnimationUtils.loadAnimation(this, R.anim.slide_in_down);
//        a.reset();
//        FrameLayout ll = (FrameLayout) findViewById(R.id.fragment_container);
//        ll.clearAnimation();
//        ll.startAnimation(a);
//    }
}