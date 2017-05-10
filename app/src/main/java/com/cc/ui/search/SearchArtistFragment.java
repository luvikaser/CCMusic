package com.cc.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.cc.app.R;
import com.cc.data.MusicEnumApp;
import com.cc.domain.utils.LogHelper;
import com.cc.service.MusicService;
import com.cc.ui.artist.ArtistActivity;
import com.cc.ui.artist.ArtistAdapter;
import com.cc.ui.song.SongLocalAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Author: NT
 * Since: 11/26/2016.
 */
public class SearchArtistFragment extends BaseFragmentSearch implements SongLocalAdapter
        .MediaFragmentListener {


    private ArtistAdapter mArtistAdapter;

    List<MediaBrowserCompat.MediaItem> mMediaItemList;

    private void initRecyclerGridView() {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mArtistAdapter);
    }

    @OnClick(R.id.view_search_more)
    void onClickSearchMore(View v) {
        mPresenter.searchArtist(searchText);
    }



    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            Log.e(TAG, "isEmpty");
            hideSearchMore();
            mArtistAdapter.edit().replaceAll(new ArrayList<MediaBrowserCompat.MediaItem>()).commit();
            return false;
        }

        final List<MediaBrowserCompat.MediaItem> filteredModelList = filter(mMediaItemList, newText);
        if (filteredModelList.size() <= 0)
            showSearchMore(newText);
        else hideSearchMore();

        mArtistAdapter.edit().replaceAll(filteredModelList).commit();
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
            mArtistAdapter.edit().removeAll().commit();
            mArtistAdapter.edit().replaceAll(mMediaItemList);
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
        mArtistAdapter = new ArtistAdapter(getActivity(), ALPHABETICAL_COMPARATOR);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.setView(this);
        initRecyclerGridView();

    }


    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item, int adapterPosition) {

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
        MusicService.setMusicTypeOld(MusicEnumApp.MusicType.SONG_ONLINE);
        MusicService.setMusicTypeCurrent(MusicEnumApp.MusicType.ARTIST_ONLINE);
        Intent intent = new Intent(getActivity(), ArtistActivity.class);
        startActivity(intent);
    }

}