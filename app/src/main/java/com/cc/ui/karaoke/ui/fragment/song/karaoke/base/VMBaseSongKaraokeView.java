package com.cc.ui.karaoke.ui.fragment.song.karaoke.base;

import android.database.Cursor;

import java.util.ArrayList;

import com.cc.ui.karaoke.data.model.karaoke.VMVolsKaraoke;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/18/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public interface VMBaseSongKaraokeView {
    void setCursor(Cursor cursor);

    void setSearchQuery(Cursor cursor);

    void setDataVols(ArrayList<VMVolsKaraoke> lisDataVols);

    void hideLoading();

    void showLoading();

}
