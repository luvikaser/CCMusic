package com.cc.ui.karaoke.ui.activity.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import mmobile.com.karaoke.R;


/**
 * Project: Minion
 * Author: NT
 * Since: 6/15/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public abstract class VMBaseFragmentHostActivity extends VMBaseActivity implements VMIBaseActivity {

    @BindView(R.id.toolbar)
    protected  Toolbar mToolbar;

    protected int getResLayoutId() {
        Log.d(TAG, "VMBaseFragmentHostActivity - getResLayoutId");
        return R.layout.activity_common_toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void updateActionBarUI(boolean isShowed) {
        mToolbar.setVisibility(isShowed ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDisplayBackButton(boolean isBack) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isBack);
    }

    @Override
    public void setTitle(CharSequence title) {
        Log.d(TAG, "title = " + title);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        getSupportActionBar().setTitle(titleId);
    }

    @Override
    public void setToolbarTitle(CharSequence title) {
        setTitle(title);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
