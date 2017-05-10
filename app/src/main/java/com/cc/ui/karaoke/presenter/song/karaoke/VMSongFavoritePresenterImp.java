package com.cc.ui.karaoke.presenter.song.karaoke;

import android.database.Cursor;

import com.cc.ui.karaoke.data.database.helper.karaoke.VMSongFavoriteScope;
import com.cc.ui.karaoke.data.database.helperImp.karaoke.VMSongFavoriteScopeImp;
import com.cc.ui.karaoke.presenter.song.base.VMBaseSongPresenterImp;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.VMSongFarvoriteView;

/**
 * Author: NT
 * Since: 6/22/2016.
 */
public class VMSongFavoritePresenterImp extends VMBaseSongPresenterImp implements
        VMSongFavoritePresenter<VMSongFarvoriteView> {
    private VMSongFarvoriteView mView;
    private VMSongFavoriteScope mVmSongFavoriteScope;

    @Override
    public void getAllSong() {
        Cursor cursor = mVmSongFavoriteScope.getAllSong();
        mView.setData(cursor);
    }

    @Override
    public void setView(VMSongFarvoriteView view) {
        mView = view;
        mVmSongFavoriteScope = new VMSongFavoriteScopeImp();
    }

    @Override
    public void destroyView() {
        mView = null;
    }
}
