package com.cc.ui.song;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.data.MusicEnumApp;
import com.cc.domain.utils.LogHelper;
import com.cc.event.BeginLoadEvent;
import com.cc.event.ChangeSongEvent;
import com.cc.event.LyricReceiveEvent;
import com.cc.event.PlayPauseEvent;
import com.cc.event.SeekEvent;
import com.cc.helper.Prefs;
import com.cc.service.MusicService;
import com.cc.ui.base.BaseMediaActivityListener;
import com.cc.ui.base.BaseMediaFragment;
import com.cc.ui.playback.LyricAdapter;
import com.cc.utils.HidingScrollListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zhaiyifan.lyric.LyricUtils;
import cn.zhaiyifan.lyric.model.Lyric;

import static android.view.View.GONE;

/**
 * Author: NT
 * Since: 11/1/2016.
 */
public class SongLocalFragment extends BaseMediaFragment implements SongLocalAdapter
        .MediaFragmentListener, SearchView.OnQueryTextListener, IViewSongLocal {

    @BindView(R.id.rc_list_music)
    RecyclerView mRecyclerView;

    @BindView(R.id.view_lyric)
    ListView mViewLyric;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.text_lyric_not_found)
    TextView mLyricNotFound;

    @BindView(R.id.img_shuffle)
    ImageView imgShuffle;

    @BindView(R.id.img_repeat)
    ImageView imgRepeat;

    Drawable drawableShuffleNormal;
    Drawable drawableShuffleSelected;

    Drawable drawableRepeatNormal;
    Drawable drawableRepeatSelected;

    private int currentPosition = -1;
    private LyricAdapter mLyricAdapter;
    private Lyric lyric = null;
    private boolean isNoLyric = false;
    private BaseMediaActivityListener mListener;

    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private MediaBrowserCompat.MediaItem mMediaItem;

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



    SongLocalAdapter mSongLocalAdapter;
    List<MediaBrowserCompat.MediaItem> mMediaItemList;


    private void initRecyclerGridView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mSongLocalAdapter);
        mRecyclerView.setLayoutManager(new CustomLayoutManager(getActivity()));

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
    protected void updateStateItem() {
        super.updateStateItem();

        if (mSongLocalAdapter != null) {
            final int positionItemPlaying = Prefs.getInt(MusicConstantsApp.PREF_PLAY_ALL_SONG_POSITION, 0);
            Log.e(TAG, "positionItemPlaying = " + positionItemPlaying);
            mSongLocalAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(positionItemPlaying);

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = (SongLocalActivity)getActivity();
        mMediaItemList = new ArrayList<>();
        mSongLocalAdapter = new SongLocalAdapter(getActivity(), ALPHABETICAL_COMPARATOR, this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onMediaBrowserChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
        try {
            Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                    "  count=" + children.size());
            mListener.setCount(children.size());
            mMediaItemList.clear();
            mMediaItemList = children;
            mSongLocalAdapter.edit().removeAll().commit();
            mSongLocalAdapter.edit().replaceAll(mMediaItemList).commit();
            onQueryTextChange("");

        } catch (Throwable t) {
            LogHelper.e(TAG, "Error on childrenloaded", t);
        }
    }

    @Override
    protected void setupFragmentComponent() {
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_song_local_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawableShuffleNormal = ContextCompat.getDrawable(getActivity(), R.drawable.ic_shuffle);
        drawableShuffleSelected = ContextCompat.getDrawable(getActivity(), R.drawable.ic_shuffle_selected);

        drawableRepeatNormal = ContextCompat.getDrawable(getActivity(), R.drawable.ic_repeat);
        drawableRepeatSelected = ContextCompat.getDrawable(getActivity(), R.drawable.ic_repeat_selected);

        checkStateButton();


        if (getArguments() != null)
            mListener.setTitle(getArguments().getString(MusicConstantsApp.BUNDLE_TITLE_SONG_PAGE, ""));
        else {
            mListener.setTitle(getString(R.string.song));
        }

        initRecyclerGridView();

    }

    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item, int adapterPosition) {
        mProgressBar.setVisibility(View.VISIBLE);
        Prefs.putInt(MusicConstantsApp.PREF_PLAY_ALL_SONG_POSITION, adapterPosition);
        mMediaItem = item;
        if (item.isPlayable()) {
            getActivity().getSupportMediaController().getTransportControls().playFromMediaId(item.getMediaId(), null);
        }
    }

    @Subscribe
    public void onEvent(PlayPauseEvent e){
        if (mProgressBar.getVisibility() == GONE){
            if (!e.check){
                mLyricNotFound.setVisibility(GONE);
                mViewLyric.setVisibility(GONE);
            } else{
                if (!isNoLyric){
                    mViewLyric.setVisibility(View.VISIBLE);
                } else{
                    mLyricNotFound.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    @Subscribe
    public void onEvent(LyricReceiveEvent e) {
        if (e.title != MusicService.getSongPlayCurrent().getTitle())
            return;
        mProgressBar.setVisibility(GONE);
        if (e.flag) {
            mLyricNotFound.setVisibility(GONE);
            String path = MusicService.getSongPlayCurrent().getPathLyric();
            lyric = LyricUtils.parseLyric(new File(path), "UTF-8");
            mLyricAdapter = new LyricAdapter(getContext(), R.layout.row_lyric, lyric.sentenceList);
            mViewLyric.setAdapter(mLyricAdapter);
            mViewLyric.setVisibility(View.VISIBLE);
        } else {
            isNoLyric = true;
            mViewLyric.setVisibility(GONE);
            mLyricNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onEvent(SeekEvent e) {
        if (lyric != null) {
            int position = getPosition(e.time);
            if (position != -1 && position != currentPosition) {
                currentPosition = position;
                mViewLyric.smoothScrollToPositionFromTop(position, mViewLyric.getHeight() / 2);
                ((LyricAdapter) mViewLyric.getAdapter()).setPositionHighlight(position);
            }
        }
    }

    private int getPosition(long time) {
        for (int i = 0; i < lyric.sentenceList.size() - 1; ++i) {
            if (time >= lyric.sentenceList.get(i).fromTime && time < lyric.sentenceList.get(i + 1).fromTime) {
                return i;
            }
        }
        return -1;
    }

    @Subscribe
    public void onEvent(BeginLoadEvent e) {
        isNoLyric = false;
        mProgressBar.setVisibility(View.VISIBLE);
        mViewLyric.setVisibility(GONE);
        mLyricNotFound.setVisibility(GONE);
    }

    @Subscribe
    public void onEvent(ChangeSongEvent e) {
        isNoLyric = false;
        mProgressBar.setVisibility(GONE);
        mLyricNotFound.setVisibility(View.GONE);
        String path = MusicService.getSongPlayCurrent().getPathLyric();
        lyric = LyricUtils.parseLyric(new File(path), "UTF-8");
        mLyricAdapter = new LyricAdapter(getContext(), R.layout.row_lyric, lyric.sentenceList);
        mViewLyric.setAdapter(mLyricAdapter);
        mViewLyric.setVisibility(View.VISIBLE);
    }



    @Override
    public void scrollToItemPlay(final int position) {
        Log.e(TAG, "scrollToItemPlay = " + position);
        if (mRecyclerView != null)
            mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return null;
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MusicService.getSongPlayCurrent() == null)
            return;
        String path = MusicService.getSongPlayCurrent().getPathLyric();
        if (!path.equals("")) {
            mProgressBar.setVisibility(GONE);
            mLyricNotFound.setVisibility(GONE);
            lyric = LyricUtils.parseLyric(new File(path), "UTF-8");
            mLyricAdapter = new LyricAdapter(getContext(), R.layout.row_lyric, lyric.sentenceList);
            mViewLyric.setAdapter(mLyricAdapter);
            mViewLyric.setVisibility(View.VISIBLE);
        } else {
            MusicService.getLyric();
        }
        if (imgShuffle != null) {
            checkStateButton();
        }
    }

    @Override
    protected void permissionGranted(int permissionRequestCode) {
        //not used
    }
}