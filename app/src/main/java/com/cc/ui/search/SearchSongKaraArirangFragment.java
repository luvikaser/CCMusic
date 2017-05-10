package com.cc.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.cc.app.R;
import com.cc.data.MusicEnumApp;
import com.cc.domain.utils.LogHelper;
import com.cc.service.MusicService;
import com.cc.ui.karaoke.ui.adapter.karaoke.VMSongKaraokeAdapter;
import com.cc.ui.song.CustomLayoutManager;
import com.cc.ui.song.SongLocalActivity;
import com.cc.ui.song.SongLocalAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Author: NT
 * Since: 11/26/2016.
 */
public class SearchSongKaraArirangFragment extends BaseFragmentSearch implements SongLocalAdapter
        .MediaFragmentListener {

    VMSongKaraokeAdapter vmSongKaraokeAdapter;
    List<MediaBrowserCompat.MediaItem> mMediaItemList;

    @OnClick(R.id.view_search_more)
    void onClickSearchMore(View v) {
        mPresenter.searchSongs(searchText);
    }


    private void initRecyclerListView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mSongLocalAdapter);
        mRecyclerView.setLayoutManager(new CustomLayoutManager(getActivity()));
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e(TAG, "onQueryTextChange");
        if (TextUtils.isEmpty(newText)) {
            Log.e(TAG, "isEmpty");
            hideSearchMore();
            mSongLocalAdapter.edit().replaceAll(new ArrayList<MediaBrowserCompat.MediaItem>()).commit();
            return false;
        }
        final List<MediaBrowserCompat.MediaItem> filteredModelList = filter(mMediaItemList, newText);

        if (filteredModelList.size() <= 0) {
            showSearchMore(newText);
        } else hideSearchMore();

        mSongLocalAdapter.edit().replaceAll(filteredModelList).commit();
        return super.onQueryTextChange(newText);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return super.onQueryTextSubmit(query);
    }

    @Override
    public boolean onSearchClose() {
        return super.onSearchClose();
    }

    @Override
    protected void onMediaBrowserChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
        try {
            Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                    "  count=" + children.size());
            mMediaItemList.clear();
            mMediaItemList = children;
            mSongLocalAdapter.edit().removeAll().commit();
            mSongLocalAdapter.edit().replaceAll(mMediaItemList);
        } catch (Throwable t) {
            LogHelper.e(TAG, "Error on childrenloaded", t);
        }
    }

    @Override
    protected void permissionGranted(int permissionRequestCode) {

    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaItemList = new ArrayList<>();
        mSongLocalAdapter = new SongLocalAdapter(getActivity(), ALPHABETICAL_COMPARATOR, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.setView(this);
        initRecyclerListView();
    }


    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item, int adapterPosition) {
        if (item.isPlayable()) {
            getActivity().getSupportMediaController().getTransportControls().playFromMediaId(item.getMediaId(), null);
        }
    }

    @Override
    public void scrollToItemPlay(int position) {

    }

    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return null;
    }

    @Override
    public void songSearchComplete() {
        MusicService.setMusicTypeOld(MusicEnumApp.MusicType.ARTIST_ONLINE);
        MusicService.setMusicTypeCurrent(MusicEnumApp.MusicType.SONG_ONLINE);
        Intent intent = new Intent(getActivity(), SongLocalActivity.class);
        startActivity(intent);
    }

}