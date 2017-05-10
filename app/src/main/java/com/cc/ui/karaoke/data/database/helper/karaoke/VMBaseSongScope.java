package com.cc.ui.karaoke.data.database.helper.karaoke;

import android.database.Cursor;

import java.util.ArrayList;

import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.data.model.karaoke.VMVolsKaraoke;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/18/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public interface VMBaseSongScope {
    Cursor getCursor();

    Cursor searchQuery(String key, String volId);

    Cursor getCursorWithVolsName(String volsName, String language);

    ArrayList<VMVolsKaraoke> getListVols(int valueManufacture);

    void addFavorite(VMSongKaraoke songKaraoke);

    void removeSongFavorite(long idSong);
}
