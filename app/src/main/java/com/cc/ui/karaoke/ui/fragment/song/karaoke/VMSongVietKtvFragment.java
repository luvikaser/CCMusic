package com.cc.ui.karaoke.ui.fragment.song.karaoke;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;

import com.cc.ui.karaoke.data.model.karaoke.VMVolsKaraoke;
import com.cc.ui.karaoke.presenter.song.karaoke.VMSongVietKtvPresenterImp;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.base.VMBaseSongaKaraokeFragment;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongVietKtvFragment extends VMBaseSongaKaraokeFragment implements
        VMSongVietKtvView {
    public static VMSongVietKtvFragment newInstance(Bundle b) {
        VMSongVietKtvFragment f = new VMSongVietKtvFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    protected void onCreateInitPresenter() {
        mPresenter = new VMSongVietKtvPresenterImp();
        mPresenter.setView(this);
    }

    @Override
    public void registerReceiverChangeNetwork() {

    }

    @Override
    public synchronized void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.getAllSong();
            }
        }, 300);
    }

    @Override
    public void setCursor(Cursor cursor) {
        setAdapter(cursor);
    }

    @Override
    public void setSearchQuery(Cursor cursor) {
        setAdapter(cursor);
    }

    @Override
    public void setDataVols(ArrayList<VMVolsKaraoke> lisDataVols) {
        showListVols(lisDataVols);
    }

    @Override
    public void hideLoading() {
        hideTextLoading();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.destroyView();
    }
}
