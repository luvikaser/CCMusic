package com.cc.ui.karaoke.ui.fragment.song.device;

import android.database.Cursor;

import java.util.ArrayList;

import com.cc.ui.karaoke.data.model.local.SongDevicesData;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public interface VMSongDevicesView {
    void setData(ArrayList<SongDevicesData> data);

    void setData(Cursor cursor);

    void updateProgressView(int progress);

    void hideProgressView();

    void showError(String error);

    void HideError();
}
