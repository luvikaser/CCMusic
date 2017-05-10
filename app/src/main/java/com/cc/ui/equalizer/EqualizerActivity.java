package com.cc.ui.equalizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.ui.base.BaseActivity;
import com.cc.ui.base.BaseFragment;
import com.cc.ui.base.BaseMediaActivity;
import com.cc.ui.base.BaseMediaActivityListener;
import com.cc.ui.playback.PlaybackControlFullScreenFragment;

import butterknife.BindView;

public class EqualizerActivity extends BaseMediaActivity implements BaseMediaActivityListener {
    @BindView(R.id.tv_title_name)
    TextView mTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public BaseFragment getFragmentToHost() {
        return EqualizerFragment.newInstance(new
                EqualizerFragment(), getIntent().getExtras());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setCount(int count) {

    }
}
