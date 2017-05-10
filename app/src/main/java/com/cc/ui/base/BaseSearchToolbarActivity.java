package com.cc.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.utils.CCSearchView;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Author: NT
 * Since: 11/26/2016.
 */
public abstract class BaseSearchToolbarActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.et_search)
    EditText etSearchView;

    @BindView(R.id.img_clear_text)
    ImageView imgClearText;

    @OnClick(R.id.img_clear_text)
    public void onClickClearText(View v) {
        etSearchView.setText("");
    }


    CCSearchView.OnQueryTextListener onQueryTextListener = new CCSearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (checkBaseMediaFragment()) {
                return getBaseMediaFragment().onQueryTextChange(query);
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (checkBaseMediaFragment()) {
                return getBaseMediaFragment().onQueryTextChange(newText);
            }
            return false;
        }
    };



    protected int getResLayoutId() {
        return R.layout.activity_common_actionbar_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        int typeMedia = getIntent().getExtras().getInt(BaseMediaActivity.BUNDLE_MEDIA_TYPE);
        if (typeMedia == MusicConstantsApp.MUSIC_TYPE_ALBUM) {
            etSearchView.setHint(getString(R.string.search_album));
        } else if (typeMedia == MusicConstantsApp.MUSIC_TYPE_ARTIST) {
            etSearchView.setHint(getString(R.string.search_artist));
        } else {
            etSearchView.setHint(getString(R.string.search_text_hint));
        }

        etSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (checkBaseMediaFragment()) {
                    imgClearText.setVisibility(View.VISIBLE);
                    getBaseMediaFragment().onQueryTextChange(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() <=0)
                    imgClearText.setVisibility(View.GONE);
            }
        });
    }

    private boolean checkBaseMediaFragment() {
        Fragment baseFragment = getActiveFragment();
        if (baseFragment instanceof BaseMediaFragment)
            return true;

        return false;
    }

    private BaseMediaFragment getBaseMediaFragment() {
        return ((BaseMediaFragment) getActiveFragment());
    }


    public Toolbar getToolbar() {
        return mToolbar;
    }

}
