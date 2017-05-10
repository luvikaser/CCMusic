package com.cc.ui.karaoke.data.database.helper.karaoke;

import android.database.Cursor;

import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;

/**
 * Author: NT
 * Since: 6/22/2016.
 */
public interface VMSongFavoriteScope {
    Cursor getAllSong();

    Cursor searchQuery(String key, String volId);

    void insert(VMSongKaraoke songKaraoke);

    void delete(long idSong);

    boolean isAddedFavorite(long idSong);
}
