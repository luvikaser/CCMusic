package com.cc.ui.karaoke.ui.fragment.song.karaoke;

import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;

import com.cc.ui.karaoke.data.model.karaoke.VMVolsKaraoke;
import com.cc.ui.karaoke.presenter.song.karaoke.VMSongCaliforniaPresenterImp;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.base.VMBaseSongaKaraokeFragment;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongCaliforniaFragment extends VMBaseSongaKaraokeFragment implements VMSongCaliforniaView {
    public static VMSongCaliforniaFragment newInstance(Bundle b) {
        VMSongCaliforniaFragment f = new VMSongCaliforniaFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    protected void onCreateInitPresenter() {
        mPresenter = new VMSongCaliforniaPresenterImp();
        mPresenter.setView(this);
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
    public void registerReceiverChangeNetwork() {

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
