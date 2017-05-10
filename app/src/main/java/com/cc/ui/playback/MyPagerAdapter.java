package com.cc.ui.playback;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

/**
 * Created by Luvi Kaser on 11/8/2016.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;
    private String mMediaParentID;
    List<MediaBrowserCompat.MediaItem> mListSong;

    public MyPagerAdapter(FragmentManager fm, String s) {
        super(fm);
        mMediaParentID = s;
    }

    public void setListSong(List<MediaBrowserCompat.MediaItem> listSong) {
        mListSong = listSong;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AdjustVolumeChildFragment.newInstance();
            case 1:
                return LyricSongChildFragment.newInstance();
            case 2:
                return ListSongChildFragment.newInstance(mMediaParentID, mListSong);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
