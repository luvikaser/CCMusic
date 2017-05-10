/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.cc.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.cc.MusicApplication;
import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.data.MusicEnumApp;
import com.cc.domain.model.MediaLocalItem;
import com.cc.domain.model.SongBySongApi;
import com.cc.domain.utils.LogHelper;
import com.cc.event.BeginLoadEvent;
import com.cc.event.ChangeSongEvent;
import com.cc.event.LyricReceiveEvent;
import com.cc.helper.Prefs;
import com.cc.helper.music.LocalPlayback;
import com.cc.helper.music.PlaybackManager;
import com.cc.helper.music.QueueManager;
import com.cc.notify.MediaNotificationManager;
import com.cc.notify.PackageValidator;
import com.cc.provider.music.MusicProvider;
import com.cc.ui.yourmusic.YourMusicActivity;
import com.cc.utils.LyricUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.cc.helper.music.MediaIDHelper.MEDIA_ID_ROOT;

/**
 * This class provides a MediaBrowser through a service. It exposes the media library to a browsing
 * client, through the onGetRoot and onLoadChildren methods. It also creates a MediaSession and
 * exposes it through its MediaSession.Token, which allows the client to create a MediaController
 * that connects to and send control commands to the MediaSession remotely. This is useful for
 * user interfaces that need to interact with your media session, like Android Auto. You can
 * (should) also use the same service from your app's UI, which gives a seamless playback
 * experience to the user.
 * <p>
 * To implement a MediaBrowserService, you need to:
 * <p>
 * <ul>
 * <p>
 * <li> Extend {@link android.service.media.MediaBrowserService}, implementing the media browsing
 * related methods {@link android.service.media.MediaBrowserService#onGetRoot} and
 * {@link android.service.media.MediaBrowserService#onLoadChildren};
 * <li> In onCreate, start a new {@link android.media.session.MediaSession} and notify its parent
 * with the session's token {@link android.service.media.MediaBrowserService#setSessionToken};
 * <p>
 * <li> Set a callback on the
 * {@link android.media.session.MediaSession#setCallback(android.media.session.MediaSession.Callback)}.
 * The callback will receive all the user's actions, like play, pause, etc;
 * <p>
 * <li> Handle all the actual music playing using any method your app prefers (for example,
 * {@link android.media.MediaPlayer})
 * <p>
 * <li> Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 * {@link android.media.session.MediaSession#setPlaybackState(android.media.session.PlaybackState)}
 * {@link android.media.session.MediaSession#setMetadata(android.media.MediaMetadata)} and
 * {@link android.media.session.MediaSession#setQueue(java.util.List)})
 * <p>
 * <li> Declare and export the service in AndroidManifest with an intent receiver for the action
 * android.media.browse.MediaBrowserService
 * <p>
 * </ul>
 * <p>
 * To make your app compatible with Android Auto, you also need to:
 * <p>
 * <ul>
 * <p>
 * <li> Declare a meta-data tag in AndroidManifest.xml linking to a xml resource
 * with a &lt;automotiveApp&gt; root element. For a media app, this must include
 * an &lt;uses name="media"/&gt; element as a child.
 * For example, in AndroidManifest.xml:
 * &lt;meta-data android:name="com.google.android.gms.car.application"
 * android:resource="@xml/automotive_app_desc"/&gt;
 * And in res/values/automotive_app_desc.xml:
 * &lt;automotiveApp&gt;
 * &lt;uses name="media"/&gt;
 * &lt;/automotiveApp&gt;
 * <p>
 * </ul>
 *
 * @see <a href="README.md">README.md</a> for more details.
 */
public class MusicService extends MediaBrowserServiceCompat implements
        PlaybackManager.PlaybackServiceCallback {

    private static final String TAG = LogHelper.makeLogTag(MusicService.class);

    // Extra on MediaSession that contains the Cast device name currently connected to
    public static final String EXTRA_CONNECTED_CAST = "vn.cccorp.music.android.CAST_NAME";
    // The action of the incoming Intent indicating that it contains a command
    // to be executed (see {@link #onStartCommand})
    public static final String ACTION_CMD = "vn.cccorp.music.android.ACTION_CMD";

    //set shuffle music
    public static final String CMD_SHUFFLE = "CMD_SHUFFLE";

    // SET RESET music list normal
    public static final String CMD_UN_SHUFFLE = "CMD_UN_SHUFFLE";

    // VALUE IN THE EXTRAS
    public static final String CMD_VALUE = "CMD_VALUE";

    // The key in the extras of the incoming Intent indicating the command that
    // should be executed (see {@link #onStartCommand})
    public static final String CMD_NAME = "CMD_NAME";
    // A value of a CMD_NAME key in the extras of the incoming Intent that
    // indicates that the music playback should be paused (see {@link #onStartCommand})
    public static final String CMD_PAUSE = "CMD_PAUSE";
    // A value of a CMD_NAME key that indicates that the music playback should switch
    // to local playback from cast playback.
    public static final String CMD_STOP_CASTING = "CMD_STOP_CASTING";
    // Delay stopSelf by using a handler.
    private static final int STOP_DELAY = 30000;

    private static MusicProvider mMusicProvider;
    private PlaybackManager mPlaybackManager;

    private MediaSessionCompat mSession;
    private MediaNotificationManager mMediaNotificationManager;
    private Bundle mSessionExtras;
    private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);

    private PackageValidator mPackageValidator;

    private static MusicEnumApp.MusicType mMusicTypeCurrent = MusicEnumApp.MusicType.PLAYED;
    private static MusicEnumApp.MusicType mMusicTypeOld;
    private static MediaLocalItem mSongPlayCurrent = null;
    private static String mNameSearchLyric;
    private static LocalPlayback playback;

    public static void setMusicTypeCurrent(MusicEnumApp.MusicType mMusicTypeCurrent) {
        MusicService.mMusicTypeCurrent = mMusicTypeCurrent;
    }

    public static Equalizer getEqualizer() {
        return playback.mEqualizer;
    }

    public static BassBoost getBassBoost() {
        return playback.mBassBoost;
    }

    public static Virtualizer getVirtualizer() {
        return playback.mVirtualizer;
    }

    public static PresetReverb getReverb() {
        return playback.mPresentReverb;
    }

    public static MediaLocalItem getSongPlayCurrent() {
        return mSongPlayCurrent;
    }

    public static void setSongPlayCurrent(MediaLocalItem songPlayCurrent) {
        mSongPlayCurrent = songPlayCurrent;
    }

    public static MusicEnumApp.MusicType getMusicTypeCurrent() {
        return mMusicTypeCurrent;
    }

    public static MusicEnumApp.MusicType getMusicTypeOld() {
        return mMusicTypeOld;
    }

    public static void setMusicTypeOld(MusicEnumApp.MusicType mMusicTypeOld) {
        MusicService.mMusicTypeOld = mMusicTypeOld;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate ------------ service");
        mMusicProvider = new MusicProvider();

        // To make the app more responsive, fetch and cache catalog information now.
        // This can help improve the response time in the method
        // {@link #onLoadChildren(String, Result<List<MediaItem>>) onLoadChildren()}.
        mMusicProvider.retrieveMediaAsync(mMusicTypeCurrent, null /* Callback */);

        mPackageValidator = new PackageValidator(this);

        QueueManager queueManager = new QueueManager(mMusicProvider, getResources(),
                new QueueManager.MetadataUpdateListener() {
                    @Override
                    public void onMetadataChanged(MediaMetadataCompat metadata) {
                        Log.e(TAG, "onMetadataChanged  = " + metadata.toString());
                        mSession.setMetadata(metadata);
                        MediaLocalItem mediaLocalItem = new MediaLocalItem(metadata);
                        try {
                            if (!MusicApplication.getInstance().getUserComponent()
                                    .songLocalPlayedRepository().isExistSong(mediaLocalItem)) {
                                Log.e(TAG, "save new song played  = " + metadata.getDescription().toString());
                                MusicApplication.getInstance().getUserComponent()
                                        .songLocalPlayedRepository().createSong(mediaLocalItem);
                            }
                            mSongPlayCurrent = MusicApplication.getInstance().getUserComponent().songLocalPlayedRepository()
                                    .readSong(new MediaLocalItem(metadata));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mSongPlayCurrent == null || mSongPlayCurrent.getPathLyric()
                                == null)
                            return;

                        else if (mSongPlayCurrent.getPathLyric().equals("")) {
                            EventBus.getDefault().post(new BeginLoadEvent());
                            getLyric();
                        } else {
                            EventBus.getDefault().post(new ChangeSongEvent());
                        }
                    }

                    @Override
                    public void onMetadataRetrieveError() {
                        mPlaybackManager.updatePlaybackState(getString(R.string.error_no_metadata));
                    }

                    @Override
                    public void onCurrentQueueIndexUpdated(int queueIndex) {
                        mPlaybackManager.handlePlayRequest();
                    }

                    @Override
                    public void onQueueUpdated(String title,
                                               List<MediaSessionCompat.QueueItem> newQueue) {
                        mSession.setQueue(newQueue);
                        mSession.setQueueTitle(title);
                    }
                });

        playback = new LocalPlayback(this, mMusicProvider);
        mPlaybackManager = new PlaybackManager(this, getResources(), mMusicProvider, queueManager, playback);
        // Start a new MediaSession
        mSession = new MediaSessionCompat(this, "MusicService");
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(mPlaybackManager.getMediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Context context = getApplicationContext();
        Intent intent = new Intent(context, YourMusicActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 99 /*request code*/,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setSessionActivity(pi);

        mSessionExtras = new Bundle();

        mSession.setExtras(mSessionExtras);

        mPlaybackManager.updatePlaybackState(null);

        try {
            mMediaNotificationManager = new MediaNotificationManager(this);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not c reate a " +
                    "MediaNotificationManager", e);
        }

    }

    /**
     * (non-Javadoc)
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    mPlaybackManager.handlePauseRequest();
                } else if (CMD_STOP_CASTING.equals(command)) {
                } else if (CMD_SHUFFLE.equals(command)) {
                    //TODO: SAVE SHARE PREFERENCE
                    Prefs.putBoolean(MusicConstantsApp.PREF_PLAY_SHUFFLE, true);
//                    mPlaybackManager.setShuffleMusicList();
                } else if (CMD_UN_SHUFFLE.equals(command)) {
                    Prefs.putBoolean(MusicConstantsApp.PREF_PLAY_SHUFFLE, false);
//                    mPlaybackManager.setQueueFromMusic(startIntent.getStringExtra(CMD_VALUE));
                }
            } else {
                // Try to handle the intent as a media button event wrapped by MediaButtonReceiver
                MediaButtonReceiver.handleIntent(mSession, startIntent);
            }
        }
        // Reset the delay handler to enqueue a message to stop the service if
        // nothing is playing.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        return START_STICKY;
    }

    /**
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        LogHelper.d(TAG, "onDestroy");
        // Service is being killed, so make sure we release our resources
        mPlaybackManager.handleStopRequest(null);
        mMediaNotificationManager.stopNotification();

        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mSession.release();
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid,
                                 Bundle rootHints) {
        LogHelper.d(TAG, "OnGetRoot: clientPackageName=" + clientPackageName,
                "; clientUid=" + clientUid + " ; rootHints=", rootHints);
        // To ensure you are not allowing any arbitrary app to browse your app's contents, you
        // need to check the origin:
        if (!mPackageValidator.isCallerAllowed(this, clientPackageName, clientUid)) {
            // If the request comes from an untrusted package, return null. No further calls will
            // be made to other media browsing methods.
            LogHelper.w(TAG, "OnGetRoot: IGNORING request from untrusted package " + clientPackageName);
            return null;
        }

        return new BrowserRoot(MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId,
                               @NonNull final Result<List<MediaItem>> result) {
        Log.e(TAG, "OnLoadChildren: parentMediaId=" + parentMediaId);
        if (getMusicTypeCurrent() != getMusicTypeOld()) {
            mMusicProvider.resetStateCurrent();
        }

        if (mMusicProvider.isInitialized()) {

            Log.e(TAG, "OnLoadChildren: mMusicProvider.isInitialized()=" + parentMediaId);
            // if music library is ready, return immediately
            result.sendResult(mMusicProvider.getChildren(parentMediaId, getResources()));
        } else {
            // otherwise, only return results when the music library is retrieved
            Log.e(TAG, "OnLoadChildren: result.detach();=" + parentMediaId);
            result.detach();
            mMusicProvider.retrieveMediaAsync(mMusicTypeCurrent, new MusicProvider
                    .Callback() {
                @Override
                public void onMusicCatalogReady(boolean success) {
                    result.sendResult(mMusicProvider.getChildren(parentMediaId, getResources()));
                }
            });
        }
    }

    /**
     * Callback method called from PlaybackManager whenever the music is about to play.
     */
    @Override
    public void onPlaybackStart() {
        if (!mSession.isActive()) {
            mSession.setActive(true);
        }

        mDelayedStopHandler.removeCallbacksAndMessages(null);

        // The service needs to continue running even after the bound client (usually a
        // MediaController) disconnects, otherwise the music playback will stop.
        // Calling startService(Intent) will keep the service running until it is explicitly killed.
        Log.e(TAG, "onPlaybackStart");
        startService(new Intent(getApplicationContext(), MusicService.class));
    }


    /**
     * Callback method called from PlaybackManager whenever the music stops playing.
     */
    @Override
    public void onPlaybackStop() {
        // Reset the delayed stop handler, so after STOP_DELAY it will be executed again,
        // potentially stopping the service.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        stopForeground(true);
    }

    @Override
    public void onNotificationRequired() {
        mMediaNotificationManager.startNotification();
    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        mSession.setPlaybackState(newState);
    }

    /**
     * A simple handler that stops the service if playback is not active (playing)
     */
    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicService> mWeakReference;

        private DelayedStopHandler(MusicService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicService service = mWeakReference.get();
            if (service != null && service.mPlaybackManager.getPlayback() != null) {
                if (service.mPlaybackManager.getPlayback().isPlaying()) {
                    LogHelper.d(TAG, "Ignoring delayed stop since the media player is in use.");
                    return;
                }
                LogHelper.d(TAG, "Stopping service with delay handler.");
                service.stopSelf();
            }
        }
    }

    private static String fullLyric;
    private static String lyricLrc;
    private static String idSong;
    protected static CompositeSubscription compositeSubscription;

    public static MusicProvider getmMusicProvider() {
        return mMusicProvider;
    }

    public static void getLyric() {
        mNameSearchLyric = mSongPlayCurrent.getTitle() + " " + mSongPlayCurrent.getSubTitle();
        fullLyric = null;
        lyricLrc = null;
        idSong = null;
        compositeSubscription = new CompositeSubscription();
        while (mNameSearchLyric.contains("-")) {
            mNameSearchLyric = mNameSearchLyric.substring(0, mNameSearchLyric.indexOf("-"));
        }
        while (mNameSearchLyric.contains(",")) {
            mNameSearchLyric = mNameSearchLyric.substring(0, mNameSearchLyric.indexOf(","));
        }
        while (mNameSearchLyric.contains(".")) {
            mNameSearchLyric = mNameSearchLyric.substring(0, mNameSearchLyric.indexOf("."));
        }

        Subscription subscription = MusicApplication.getInstance().getUserComponent()
                .musicApiRepository().getListSongBySong(mNameSearchLyric)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<SongBySongApi>>() {
                    @Override
                    public void call(List<SongBySongApi> songBySongApis) {
                        //TODO: HANDLE songBySongApis = NULL

                    }
                }).flatMap(new Func1<List<SongBySongApi>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<SongBySongApi> songBySongApis) {
                        //TODO: HANDLE songBySongApis = NULL
                        if (songBySongApis == null || songBySongApis.get(3).songList.size() < 1) {
                            return Observable.<String>error(new Throwable("not found " +
                                    "data"));
                        } else {
                            idSong = songBySongApis.get(3)
                                    .songList.get(0).idSong;
                            if (idSong == null)
                                return Observable.<String>error(new Throwable("not " +
                                        "found data"));
                            MusicApplication.getInstance().getUserComponent().songLocalPlayedRepository().updateIDMp3Song(mSongPlayCurrent, idSong);
                            mSongPlayCurrent = MusicApplication.getInstance().getUserComponent().songLocalPlayedRepository()
                                    .readSong(mSongPlayCurrent);
                            return MusicApplication.getInstance().getUserComponent().musicApiRepository()
                                    .getLyric(idSong)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        }
                    }
                }).onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        Log.e(TAG, "------------------error = " + throwable.getLocalizedMessage());
                        EventBus.getDefault().post(new LyricReceiveEvent(false, mSongPlayCurrent.getTitle(), fullLyric));
                        return null;
                    }
                }).flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        //TODO: HANDLE S = NULL
                        fullLyric = s;
                        return MusicApplication.getInstance().getUserComponent().musicApiRepository().getLyricLrc(idSong)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                }).subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        if (lyricLrc != null && idSong != null) {
                            File file = LyricUtils.getFileCache(idSong);
                            LyricUtils.writeData(file, lyricLrc);
                            MusicApplication.getInstance().getUserComponent().songLocalPlayedRepository()
                                    .updatePathLyricSong(mSongPlayCurrent, file.getPath().toString());
                            mSongPlayCurrent = MusicApplication.getInstance().getUserComponent().songLocalPlayedRepository()
                                    .readSong(mSongPlayCurrent);
                            EventBus.getDefault().post(new LyricReceiveEvent(true, mSongPlayCurrent.getTitle(), fullLyric));
                        } else {
                            EventBus.getDefault().post(new LyricReceiveEvent(false, mSongPlayCurrent.getTitle(), fullLyric));
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "------------------onError-- = " + e.getLocalizedMessage());
                        e.printStackTrace();
                        EventBus.getDefault().post(new LyricReceiveEvent(false, mSongPlayCurrent.getTitle(), fullLyric));

                    }

                    @Override
                    public void onNext(String s) {
                        lyricLrc = s;
                    }
                });

        compositeSubscription.add(subscription);

    }


}