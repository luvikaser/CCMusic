package com.cc.ui.playback;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.domain.model.MediaLocalItem;
import com.cc.domain.model.SongBySongApi;
import com.cc.domain.utils.LogHelper;
import com.cc.event.ListSongChildEvent;
import com.cc.event.PlayPauseEvent;
import com.cc.event.SeekEvent;
import com.cc.helper.Prefs;
import com.cc.service.MusicService;
import com.cc.ui.base.BaseFragment;
import com.cc.ui.base.BaseMediaActivity;
import com.cc.ui.base.BaseMediaFragment;
import com.cc.ui.equalizer.EqualizerActivity;
import com.cc.utils.CCMusicUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import me.relex.circleindicator.CircleIndicator;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


/**
 * Author: NT
 * Since: 11/2/2016.
 */
public class PlaybackControlFullScreenFragment extends BaseFragment implements
        IViewPlaybackFullScreen {
    private static final String TAG = LogHelper.makeLogTag(PlaybackControlFullScreenFragment.class);
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private String mCurrentArtUrl;
    private final Handler mHandler = new Handler();
    private MediaBrowserCompat mMediaBrowser;
    private MediaLocalItem mSongPlayCurrent;
    private String mTitleSongCurrent = null;

    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private Drawable mFavoriteNormal;
    private Drawable mFavoriteSelect;


    Drawable drawableShuffleNormal;
    Drawable drawableShuffleSelected;

    Drawable drawableRepeatNormal;
    Drawable drawableRepeatSelected;

    private ImageView mBackgroundImage;
    private String mMediaId;

    private boolean isShuffle = false;
    private boolean isRepeat = false;
    protected boolean isDataSend = false;
    List<MediaBrowserCompat.MediaItem> mListSong;

    @Inject
    PlaybackFullScreenPresenter mPresenter;

    @BindView(R.id.rootView)
    ImageView mRootView;

    @BindView(R.id.view_pager_play_music)
    ViewPager mViewPager;

    @BindView(R.id.indicator)
    CircleIndicator mIndicator;

    @BindView(R.id.seekbar_duration)
    SeekBar mSeekbar;

    @BindView(R.id.tv_time_increasing)
    TextView mStart;

    @BindView(R.id.tv_time_decreasing)
    TextView mEnd;

    @BindView(R.id.tv_song_title)
    TextView tvSongTitle;

    @BindView(R.id.tv_artist_song)
    TextView tvArtistSong;

    @BindView(R.id.img_preview_song)
    ImageView mSkipPrev;

    @BindView(R.id.img_next_song)
    ImageView mSkipNext;

    @BindView(R.id.img_play_pause)
    ImageView mPlayPause;

    @BindView(R.id.img_favorite)
    ImageView mFavoriteButton;

    @BindView(R.id.img_shuffle)
    ImageView imgShuffle;

    @BindView(R.id.img_repeat)
    ImageView imgRepeat;

    @BindView(R.id.img_setting)
    ImageView imgSetting;

    private MyPagerAdapter myPagerAdapter;

    @OnClick(R.id.img_setting)
    void OnClickSetting(View v){
        Intent intent = new Intent(getActivity(), EqualizerActivity.class);
        startActivity(intent);
    }

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


    @OnClick(R.id.img_next_song)
    void OnClickSkipNext(View v) {
        MediaControllerCompat.TransportControls controls =
                getActivity().getSupportMediaController().getTransportControls();

        controls.skipToNext();
    }

    @OnClick(R.id.img_preview_song)
    void OnClickPrevNext(View v) {
        MediaControllerCompat.TransportControls controls =
                getActivity().getSupportMediaController().getTransportControls();
        controls.skipToPrevious();
    }

    @OnClick(R.id.img_favorite)
    void onClickSongFavorite(View v) {
        if (mPresenter.checkExistSongFavorite(mSongPlayCurrent)) {
            mFavoriteButton.setImageDrawable(mFavoriteNormal);
            mPresenter.deleteSongFavorite(mSongPlayCurrent);
        } else {
            mFavoriteButton.setImageDrawable(mFavoriteSelect);
            mPresenter.createSongFavorite(mSongPlayCurrent);
        }
    }


    @OnClick(R.id.img_play_pause)
    void OnClickPlayPause(View v) {
        PlaybackStateCompat state = getActivity().getSupportMediaController()
                .getPlaybackState();

        if (state != null) {
            MediaControllerCompat.TransportControls controls =
                    getActivity().getSupportMediaController().getTransportControls();
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING: // fall through
                case PlaybackStateCompat.STATE_BUFFERING:
                    controls.pause();
                    //  mLyricView.pause();
                    stopSeekbarUpdate();
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                case PlaybackStateCompat.STATE_STOPPED:
                    controls.play();
                    //  mLyricView.play(mSeekbar.getProgress());
                    scheduleSeekbarUpdate();
                    break;
                default:
                    LogHelper.d(TAG, "onClick with state ", state.getState());
            }
        }
    }

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;
    private PlaybackStateCompat mLastPlaybackState;

    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            Log.e(TAG, "onPlaybackstate changed " + state);
            if (getActivity() != null) {
                updatePlaybackState(state);
                updateStateItem();
            }

        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.e(TAG, "onMetadataChanged changed " + metadata.toString());
            if (metadata != null && getActivity() != null) {
                updateMediaSongCurrent(metadata);
                updateMediaDescription(metadata.getDescription());
                updateDuration(metadata);
                updateStateItem();
            }
        }
    };

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    LogHelper.d(TAG, "onConnected");
                    if (getActivity() == null)
                        return;
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        LogHelper.e(TAG, e, "could not connect media controller");
                    }
                }
            };

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            Log.e(TAG, "onChildrenLoaded");
            mListSong = children;
            if(!isDataSend) {
                isDataSend = true;
                myPagerAdapter.setListSong(children);
            }

        }

        @Override
        public void onError(@NonNull String parentId, @NonNull Bundle options) {
            super.onError(parentId, options);
            LogHelper.e(TAG, "browse fragment subscription onError, id=" + parentId);
            showToast(R.string.error_loading_media);
        }
    };

    protected void updateStateItem() {
        EventBus.getDefault().post(new ListSongChildEvent(true));
    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(
                getActivity(), token);
        if (mediaController.getMetadata() == null) {
            getActivity().finish();
            return;
        }
        getActivity().setSupportMediaController(mediaController);
        mediaController.registerCallback(mCallback);
        onConnectedMediaId();

        PlaybackStateCompat state = mediaController.getPlaybackState();
        updatePlaybackState(state);
        MediaMetadataCompat metadata = mediaController.getMetadata();

        if (metadata != null) {
            updateMediaSongCurrent(metadata);
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
        updateProgress();
        if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }
    }

    private void onConnectedMediaId() {
        if (isDetached()) {
            return;
        }
        if (getArguments() != null) {
            mMediaId = getArguments().getString(BaseMediaFragment.ARG_MEDIA_ID);
            Log.e(TAG, "------------- mMediaId = " + mMediaId);
        }

        if (mMediaId == null) {
            mMediaId = mMediaBrowser.getRoot();
        }

        mMediaBrowser.unsubscribe(mMediaId);

        mMediaBrowser.subscribe(mMediaId, mSubscriptionCallback);
    }


    private void updateFromParams() {
        if (getArguments() != null) {
            MediaMetadataCompat metadataCompat = getArguments().getParcelable(
                    BaseMediaActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
            if (metadataCompat != null) {
                updateMediaSongCurrent(metadataCompat);
                updateMediaDescription(metadataCompat.getDescription());
            }
        }
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }


    private void updateMediaDescription(MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }
        Log.e(TAG, "updateMediaDescription called " + description.getTitle());
        tvSongTitle.setText(description.getTitle());
        tvArtistSong.setText(description.getSubtitle());

        if (!description.getTitle().toString().equals(mTitleSongCurrent)) {
            mTitleSongCurrent = description.getTitle().toString();
            String[] columns = new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"};

            String where = "title LIKE ?";
            String whereVal[] = {mTitleSongCurrent};
            Cursor cursor = null;
            Bitmap outBitmap = null;
            try {
                cursor = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, where, whereVal, null);

                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(7);
                    outBitmap = ImageLoader.getInstance().loadImageSync(CCMusicUtils.getAlbumArtUri(id).toString());
                    if (outBitmap == null) {
                        outBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.bg_blurred_play);
                    } else{
                        outBitmap = CCMusicUtils.blur(getContext(), outBitmap);
                    }
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            mRootView.setImageBitmap(outBitmap);
        }


    }

    private void updateMediaSongCurrent(MediaMetadataCompat mediaMetadataCompat) {
        if (mediaMetadataCompat == null)
            return;

        // build Object
        mSongPlayCurrent = new MediaLocalItem(mediaMetadataCompat);

        mMediaId = mediaMetadataCompat.getDescription().getMediaId();
        if (mPresenter.checkExistSongFavorite(mSongPlayCurrent)) {
            mFavoriteButton.setImageDrawable(mFavoriteSelect);
        } else {
            mFavoriteButton.setImageDrawable(mFavoriteNormal);
        }
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        LogHelper.d(TAG, "updateDuration called ");
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mSeekbar.setMax(duration);
        mEnd.setText(DateUtils.formatElapsedTime(duration / 1000));
        EventBus.getDefault().post(new SeekEvent(duration));
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }

        mLastPlaybackState = state;
        Log.e(TAG, "state.getState() = " + state.getState());
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mPlayPause.setVisibility(VISIBLE);
                mPlayPause.setImageDrawable(mPauseDrawable);
                scheduleSeekbarUpdate();
                EventBus.getDefault().post(new PlayPauseEvent(true));
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mPlayPause.setVisibility(VISIBLE);
                mPlayPause.setImageDrawable(mPlayDrawable);
                stopSeekbarUpdate();
                EventBus.getDefault().post(new PlayPauseEvent(false));

                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                mPlayPause.setVisibility(VISIBLE);
                mPlayPause.setImageDrawable(mPlayDrawable);
                EventBus.getDefault().post(new PlayPauseEvent(false));
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
//                mPlayPause.setVisibility(INVISIBLE);
                stopSeekbarUpdate();
                break;
            default:
                LogHelper.d(TAG, "Unhandled state ", state.getState());
        }

        if (getActivity() == null || mSkipNext == null)
            return;
        mSkipNext.setVisibility((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) == 0
                ? INVISIBLE : VISIBLE);
        mSkipPrev.setVisibility((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) == 0
                ? INVISIBLE : VISIBLE);
    }

    private void updateProgress() {
        if (mLastPlaybackState == null || getActivity() == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() != PlaybackStateCompat.STATE_PAUSED) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        mSeekbar.setProgress((int) currentPosition);
        EventBus.getDefault().post(new SeekEvent(currentPosition));
    }


    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_play_control_full_screen;
    }

    private String getMediaParentID() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getString(BaseMediaFragment.ARG_MEDIA_ID);
        }
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), getMediaParentID());

        mMediaBrowser = new MediaBrowserCompat(getActivity(), new ComponentName
                (getActivity(), MusicService.class), mConnectionCallback, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.setView(this);
        mViewPager.setAdapter(myPagerAdapter);

        mIndicator.setViewPager(mViewPager);
        myPagerAdapter.registerDataSetObserver(mIndicator.getDataSetObserver());

        mPauseDrawable = ContextCompat.getDrawable(getActivity(), R.drawable
                .ic_pause_played);
        mPlayDrawable = ContextCompat.getDrawable(getActivity(), R.drawable
                .ic_play_played);

        mFavoriteNormal = ContextCompat.getDrawable(getActivity(), R.drawable
                .ic_favorite_play_song);
        mFavoriteSelect = ContextCompat.getDrawable(getActivity(), R.drawable
                .ic_favorite_play_song_selected);

        drawableShuffleNormal = ContextCompat.getDrawable(getActivity(), R.drawable.ic_shuffle_normal_play);
        drawableShuffleSelected = ContextCompat.getDrawable(getActivity(), R.drawable.ic_shuffle_selected_play);

        drawableRepeatNormal = ContextCompat.getDrawable(getActivity(), R.drawable.ic_repeat_play);
        drawableRepeatSelected = ContextCompat.getDrawable(getActivity(), R.drawable.ic_repeat_selected_play);

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


        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mStart.setText(DateUtils.formatElapsedTime(progress / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getActivity().getSupportMediaController().getTransportControls().seekTo(seekBar.getProgress());
                scheduleSeekbarUpdate();
            }
        });


        // Only update from the intent if we are not recreating from a config change:
        if (savedInstanceState == null) {
            updateFromParams();
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart   ");
        if (mMediaBrowser != null) {
            mMediaBrowser.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop   ");
        if (mMediaBrowser != null) {
            mMediaBrowser.disconnect();
        }
        if (getActivity().getSupportMediaController() != null) {
            getActivity().getSupportMediaController().unregisterCallback(mCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mPresenter.destroyView();
        mExecutorService.shutdown();
    }

}