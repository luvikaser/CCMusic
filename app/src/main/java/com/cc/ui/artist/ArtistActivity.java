package com.cc.ui.artist;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.ui.album.AlbumFragment;
import com.cc.ui.base.BaseActivity;
import com.cc.ui.base.BaseFragment;
import com.cc.ui.base.BaseMediaActivity;
import com.cc.ui.base.BaseMediaActivityListener;

import butterknife.BindView;

/**
 * Author: NT
 * Since: 10/31/2016.
 */
public class ArtistActivity extends BaseMediaActivity implements BaseMediaActivityListener {

    @Override
    public BaseFragment getFragmentToHost() {
        return ArtistFragment.newInstance(new ArtistFragment(), getIntent().getExtras());
    }
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_title_name)
    TextView mTitle;

    @BindView(R.id.tv_count)
    TextView mCount;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setCount(int count) {
        mCount.setVisibility(View.VISIBLE);
        mCount.setText(count + " artists");
    }
}