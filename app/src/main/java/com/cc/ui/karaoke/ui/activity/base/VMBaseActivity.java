package com.cc.ui.karaoke.ui.activity.base;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import mmobile.com.karaoke.R;
import com.cc.ui.karaoke.ui.fragment.base.VMBaseFragment;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public abstract class VMBaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected int enterAnim = R.anim.anim_activity_previous;
    protected int exitAnim = R.anim.anim_activity_previous_release;
    private Unbinder unbinder;
    public abstract VMBaseFragment getFragmentToHost();

    protected void hostFragment(VMBaseFragment fragment, int id) {
        Log.d(TAG, "hostFragment");
        if (fragment != null
                && getFragmentManager().findFragmentByTag(fragment.getTag()) == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(id, fragment, fragment.TAG);
            ft.commit();
        }
    }

    protected int getResLayoutId() {
        Log.d(TAG, "getResLayoutId");
        return R.layout.activity_common;
    }


    protected void hostFragment(VMBaseFragment fragment) {
        hostFragment(fragment, R.id.fragment_container);
    }


    protected void styleBackPopup() {
        enterAnim = R.anim.hold;
        exitAnim = R.anim.slide_down;
    }

    protected VMBaseFragment getActiveFragment() {
        return (VMBaseFragment) getSupportFragmentManager().findFragmentById(R.id
                .fragment_container);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayoutId());
        unbinder = ButterKnife.bind(this);
        if (savedInstanceState == null) {
            hostFragment(getFragmentToHost());
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        VMBaseFragment activeFragment = getActiveFragment();
        if (activeFragment == null || !activeFragment.onBackPressed(true)) {
            super.onBackPressed();
//            overridePendingTransition(enterAnim, exitAnim);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
