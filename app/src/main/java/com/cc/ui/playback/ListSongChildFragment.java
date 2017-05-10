package com.cc.ui.playback;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.domain.utils.LogHelper;
import com.cc.event.ListSongChildEvent;
import com.cc.helper.Prefs;
import com.cc.ui.base.BaseFragment;
import com.cc.ui.base.BaseMediaFragment;
import com.cc.ui.song.CustomLayoutManager;
import com.cc.ui.song.LinearLayoutManagerWithSmoothScroller;
import com.cc.ui.song.SongLocalAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Luvi Kaser on 11/8/2016.
 */

public class ListSongChildFragment extends BaseFragment implements SongLocalChildAdapter
        .MediaFragmentListener {
    @BindView(R.id.rc_list_music)
    RecyclerView mRecyclerView;
    private MediaBrowserCompat mMediaBrowser;
    SongLocalChildAdapter mSongLocalAdapter;
    List<MediaBrowserCompat.MediaItem> mMediaItemList;

    private static List<MediaBrowserCompat.MediaItem> filter(List<MediaBrowserCompat.MediaItem> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<MediaBrowserCompat.MediaItem> filteredModelList = new ArrayList<>();
        for (MediaBrowserCompat.MediaItem model : models) {
            final String text = model.getDescription().getTitle().toString().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private static final Comparator<MediaBrowserCompat.MediaItem> ALPHABETICAL_COMPARATOR = new Comparator<MediaBrowserCompat.MediaItem>() {
        @Override
        public int compare(MediaBrowserCompat.MediaItem a, MediaBrowserCompat.MediaItem b) {
            return a.getDescription().getTitle().toString().compareTo(b.getDescription
                    ().getTitle().toString());
        }

    };

    private void initRecyclerGridView() {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new CustomLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mSongLocalAdapter);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate ---------------");

        mMediaItemList = new ArrayList<>();
        mSongLocalAdapter = new SongLocalChildAdapter(getActivity(), ALPHABETICAL_COMPARATOR, this);
        onMediaBrowserChildrenLoaded("0", getArguments().<MediaBrowserCompat.MediaItem>getParcelableArrayList(BaseMediaFragment.ARG_MEDIA_LIST_SONG));
    }


    public void onMediaBrowserChildrenLoaded(@NonNull String parentId,
                                             List<MediaBrowserCompat.MediaItem> children) {

        try {
            mMediaItemList.clear();
            mMediaItemList = children;
            mSongLocalAdapter.edit().removeAll().commit();
            mSongLocalAdapter.edit().replaceAll(mMediaItemList);
            onQueryTextChange("");
        } catch (Throwable t) {
            LogHelper.e(TAG, "Error on childrenloaded", t);
        }
    }

    protected void updateStateItem() {
        if (mSongLocalAdapter != null) {
            final int positionItemPlaying = Prefs.getInt(MusicConstantsApp.PREF_PLAY_ALL_SONG_POSITION, 0);
            Log.e(TAG, "positionItemPlaying = " + positionItemPlaying);
            mSongLocalAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void setupFragmentComponent() {
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.child_fragment_play_control_full_screen_list_song;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerGridView();
    }

    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item) {

        if (item.isPlayable()) {
            getActivity().getSupportMediaController().getTransportControls().playFromMediaId(item.getMediaId(), null);
        }
    }


    @Override
    public void setToolbarTitle(CharSequence title) {

    }

    @Override
    public void scrollToItemPlay(int position) {
        Log.e(TAG, "scrollToItemPlay = " + position);
       if (mRecyclerView != null)
           mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return null;
    }


    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean onQueryTextChange(String query) {
        final List<MediaBrowserCompat.MediaItem> filteredModelList = filter(mMediaItemList, query);
        mSongLocalAdapter.edit()
                .replaceAll(filteredModelList)
                .commit();
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    public static ListSongChildFragment newInstance(String s, List<MediaBrowserCompat.MediaItem> list) {
        Bundle args = new Bundle();
        args.putString(BaseMediaFragment.ARG_MEDIA_ID, s);
        args.putParcelableArrayList(BaseMediaFragment.ARG_MEDIA_LIST_SONG, (ArrayList<? extends Parcelable>) list);
        ListSongChildFragment fragment = new ListSongChildFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe
    public void onEvent(ListSongChildEvent event) {
        if (event.isUpdateStateItem) {
            updateStateItem();
        } else {
            onMediaBrowserChildrenLoaded(event.parentId, event.children);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
