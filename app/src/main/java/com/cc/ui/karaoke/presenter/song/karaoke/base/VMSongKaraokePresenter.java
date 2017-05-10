package com.cc.ui.karaoke.presenter.song.karaoke.base;

import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.presenter.base.IBasePresenter;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public interface VMSongKaraokePresenter<View> extends IBasePresenter<View> {
    void getAllSong();

    void searchSong(String key, int idVols);

    void getListVols(int value);

    void getSongWithVols(String volsName, String language);

    void addSongToFavorite(VMSongKaraoke songKaraoke);
    void removeSongToFavorite(long idSong);
}
