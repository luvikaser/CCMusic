package com.cc.ui.yourmusic;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.data.MusicEnumApp;
import com.cc.domain.utils.LogHelper;
import com.cc.helper.Prefs;
import com.cc.service.MusicService;
import com.cc.ui.album.AlbumActivity;
import com.cc.ui.artist.ArtistActivity;
import com.cc.ui.base.BaseMediaFragment;
import com.cc.ui.song.SongLocalActivity;
import com.cc.ui.song.SongLocalAdapter;
import com.cc.utils.HidingScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: NT
 * Since: 10/30/2016.
 */
public class YourMusicFragment extends BaseMediaFragment implements SearchView.OnQueryTextListener, SongLocalAdapter.MediaFragmentListener {
    @BindView(R.id.tv_count_songs_recently)
    TextView tvCountSong;

    @BindView(R.id.tv_edit)
    TextView tvEdit;

    @BindView(R.id.rc_list_recently_played)
    RecyclerView mRecyclerView;


    @BindView(R.id.tvShowSongNotFound)
    TextView tvEmptySongPlayed;

    @BindView(R.id.img_shuffle)
    ImageView imgShuffle;

    @BindView(R.id.img_repeat)
    ImageView imgRepeat;


    @OnClick(R.id.img_shuffle)
    void OnClickShuffle(View v) {
        if (isShuffle) {
            isShuffle = false;
            imgShuffle.setImageDrawable(drawableShuffleNormal);

            Prefs.putBoolean(MusicConstantsApp.PREF_PLAY_SHUFFLE, false);
        } else {
            isShuffle = true;
            imgShuffle.setImageDrawable(drawableShuffleSelected);

            Prefs.putBoolean(MusicConstantsApp.PREF_PLAY_SHUFFLE, true);
        }
    }

    @OnClick(R.id.img_repeat)
    void OnClickRepeat(View v) {
        if (isRepeat) {
            isRepeat = false;
            imgRepeat.setImageDrawable(drawableRepeatNormal);
            Prefs.putBoolean(MusicConstantsApp.PREF_PLAY_REPEAT, false);

        } else {
            isRepeat = true;
            imgRepeat.setImageDrawable(drawableRepeatSelected);
            Prefs.putBoolean(MusicConstantsApp.PREF_PLAY_REPEAT, true);
        }
    }

    @OnClick(R.id.cv_favorite_music)
    void onClickFavorite(View view) {
        MusicService.setMusicTypeOld(MusicService.getMusicTypeCurrent());
        MusicService.setMusicTypeCurrent(MusicEnumApp.MusicType.FAVORITE);
        Intent intent = new Intent(getActivity(), SongLocalActivity.class);
        intent.putExtra(MusicConstantsApp.BUNDLE_TITLE_SONG_PAGE, getString(R.string.favorite_songs));
        startActivity(intent);
    }

    @OnClick(R.id.cv_song_music)
    void onClickSong(View view) {
        MusicService.setMusicTypeOld(MusicService.getMusicTypeCurrent());
        MusicService.setMusicTypeCurrent(MusicEnumApp.MusicType.ALL_SONG);
        Intent intent = new Intent(getActivity(), SongLocalActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.cv_album_music)
    void onClickAlbums(View view) {
        MusicService.setMusicTypeOld(MusicService.getMusicTypeCurrent());
        MusicService.setMusicTypeCurrent(MusicEnumApp.MusicType.ALBUMS);
        Intent intent = new Intent(getActivity(), AlbumActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.cv_artist_music)
    void onClickArtist(View view) {
        MusicService.setMusicTypeOld(MusicService.getMusicTypeCurrent());
        MusicService.setMusicTypeCurrent(MusicEnumApp.MusicType.ARTISTS);
        Intent intent = new Intent(getActivity(), ArtistActivity.class);
        startActivity(intent);
    }


    Drawable drawableShuffleNormal;
    Drawable drawableShuffleSelected;

    Drawable drawableRepeatNormal;
    Drawable drawableRepeatSelected;

    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private MediaBrowserCompat.MediaItem mMediaItem;
    SongLocalAdapter mSongLocalAdapter;
    List<MediaBrowserCompat.MediaItem> mMediaItemList;


    private void initRecyclerGridView() {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mSongLocalAdapter);


    }


    private void checkStateButton() {
        if (Prefs.getBoolean(MusicConstantsApp.PREF_PLAY_REPEAT, false)) {
            imgRepeat.setImageDrawable(drawableRepeatSelected);
        } else {
            imgRepeat.setImageDrawable(drawableRepeatNormal);
        }

        if (Prefs.getBoolean(MusicConstantsApp.PREF_PLAY_SHUFFLE, false)) {
            imgShuffle.setImageDrawable(drawableShuffleSelected);
        } else {
            imgShuffle.setImageDrawable(drawableShuffleNormal);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawableShuffleNormal = ContextCompat.getDrawable(getActivity(), R.drawable.ic_shuffle);
        drawableShuffleSelected = ContextCompat.getDrawable(getActivity(), R.drawable.ic_shuffle_selected);

        drawableRepeatNormal = ContextCompat.getDrawable(getActivity(), R.drawable.ic_repeat);
        drawableRepeatSelected = ContextCompat.getDrawable(getActivity(), R.drawable.ic_repeat_selected);

        mMediaItemList = new ArrayList<>();
        mSongLocalAdapter = new SongLocalAdapter(getActivity(), ALPHABETICAL_COMPARATOR, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkStateButton();
        initRecyclerGridView();

        if (mRecyclerView != null)
            mRecyclerView.addOnScrollListener(new HidingScrollListener() {
                @Override
                public void onHide() {
                    hideViewsPlaybackBottom();
                }

                @Override
                public void onShow() {
                    showViewsPlaybackBottom();
                }
            });
    }

    @Override
    protected void setupFragmentComponent() {

    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_your_music;
    }

    @Override
    protected void permissionGranted(int permissionRequestCode) {

    }

    @Override
    protected void onMediaBrowserChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
        try {
            Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                    "  count=" + children.size());
            if (children.size() == 0)
                tvEmptySongPlayed.setVisibility(View.VISIBLE);
            else tvEmptySongPlayed.setVisibility(View.GONE);

            tvCountSong.setText(children.size() + " " + getString(R.string.songs));
            mMediaItemList.clear();
            mMediaItemList = children;
            mSongLocalAdapter.edit().removeAll().commit();
            mSongLocalAdapter.edit().replaceAll(mMediaItemList);
            onQueryTextChange("");

        } catch (Throwable t) {
            LogHelper.e(TAG, "Error on childrenloaded", t);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<MediaBrowserCompat.MediaItem> filteredModelList = filter(mMediaItemList, query);
        mSongLocalAdapter.edit()
                .replaceAll(filteredModelList)
                .commit();
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item, int adapterPosition) {
        if (item.isPlayable()) {
            mMediaItem = item;
            getActivity().getSupportMediaController().getTransportControls().playFromMediaId(item.getMediaId(), null);
        }
    }


    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return null;
    }

    @Override
    public void onStart() {
        MusicService.setMusicTypeOld(MusicService.getMusicTypeCurrent());
        MusicService.setMusicTypeCurrent(MusicEnumApp.MusicType.PLAYED);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imgShuffle != null) {
            checkStateButton();
        }
    }

    @Override
    protected void updateStateItem() {
        super.updateStateItem();
        if (mSongLocalAdapter != null)
            mSongLocalAdapter.notifyDataSetChanged();
    }

    @Override
    public void scrollToItemPlay(int position) {
        if (mRecyclerView != null)
            mRecyclerView.scrollToPosition(position);
    }


    @Override
    public void onStop() {
        super.onStop();
    }


}