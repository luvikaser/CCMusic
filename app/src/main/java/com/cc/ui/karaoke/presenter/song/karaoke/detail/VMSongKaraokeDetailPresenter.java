package com.cc.ui.karaoke.presenter.song.karaoke.detail;

import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.data.model.services.response.lyric.VMLyricByIdResponse;
import com.cc.ui.karaoke.presenter.base.IBasePresenter;

/**
 * Author: NT
 * Since: 6/21/2016.
 */
public interface VMSongKaraokeDetailPresenter<ViewType> extends IBasePresenter<ViewType> {
    void getListSongSearch(String songName);

    void getLyricSong(String songId);

    void addSongToFavorite(VMSongKaraoke songKaraoke);

    void removeSongToFavorite(long idSong);

    void getMp3Stream(String songId, VMLyricByIdResponse lyricResponse);
}
