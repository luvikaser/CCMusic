package com.cc.ui.karaoke.presenter.song.device;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import com.cc.ui.karaoke.data.database.provider.SongDevicesProvider;
import com.cc.ui.karaoke.data.model.local.SongDevicesData;
import com.cc.ui.karaoke.presenter.song.base.VMBaseSongPresenterImp;
import com.cc.ui.karaoke.ui.fragment.song.device.VMSongDevicesView;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongDevicesPresenterImp extends VMBaseSongPresenterImp implements
        VMSongDevicesPresenter<VMSongDevicesView>, SongDevicesProvider.OnUpdateProgressLoadData{
    private static final String TAG = VMSongDevicesPresenterImp.class.getSimpleName();

    private VMSongDevicesView mView;
    private SongDevicesProvider songDevicesProvider;

    @Override
    public void getAllSongDevices() {
        Log.d(TAG, "getAllSongDevices");
        Cursor cursor = songDevicesProvider.getCursorMediaSong();
        if (cursor != null) {
            mView.setData(cursor);
            mView.setData(songDevicesProvider.getListMediaStore());
        } else {
            mView.showError("Empty error");
        }
     }

    @Override
    public void setView(VMSongDevicesView view) {
        this.mView = view;
        songDevicesProvider = new SongDevicesProvider(this);
    }

    @Override
    public void destroyView() {
        this.mView = null;
    }

    @Override
    public void onUpdateProgress(int progress) {
        mView.updateProgressView(progress);
    }

    @Override
    public void onLoadDataFinish(ArrayList<SongDevicesData> datas) {

        mView.hideProgressView();
    }
}
