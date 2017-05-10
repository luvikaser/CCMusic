package com.cc.ui.base;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.cc.app.R;
import com.cc.domain.utils.LogHelper;
import com.cc.service.MusicService;
import com.cc.ui.playback.PlaybackControlsFragment;
import com.cc.ui.yourmusic.YourMusicFragment;
import com.cc.utils.HidingScrollListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

import static android.R.attr.id;

/**
 * Author: NT
 * Since: 10/31/2016.
 */
public abstract class BaseMediaFragment extends RuntimePermissionFragment {
    private static final String SAVED_MEDIA_ID = "com.example.android.uamp.MEDIA_ID";
    private static final String FRAGMENT_TAG = "uamp_list_container";
    public static final String ARG_MEDIA_ID = "media_id";
    public static final String ARG_MEDIA_LIST_SONG = "ARG_MEDIA_LIST_SONG";
    public static final String EXTRA_START_FULLSCREEN = "com.example.android.uamp.EXTRA_START_FULLSCREEN";
    protected boolean isDataLoaded = false;

    protected View viewPlaybottom;
    /**
     * Optionally used with {@link #EXTRA_START_FULLSCREEN} to carry a MediaDescription to
     * the {@link com.cc.ui.playback.PlaybackControlFullScreenFragment}, speeding up the screen rendering
     * while the {@link android.support.v4.media.session.MediaControllerCompat} is connecting.
     */
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION = "com.example.android.uamp.CURRENT_MEDIA_DESCRIPTION";

    protected MediaBrowserCompat mediaBrowserCompat;
    protected PlaybackControlsFragment mControlsFragment;
    String mMediaId;

    protected abstract void onMediaBrowserChildrenLoaded(@NonNull String parentId,
                                                         List<MediaBrowserCompat.MediaItem> children);

    protected void updateStateItem() {

    }

    protected void hideViewsPlaybackBottom() {
        if (getActivity() != null)
            viewPlaybottom = ((BaseMediaActivity) getActivity()).getViewPlaybackBottom();
        if (viewPlaybottom != null)
            viewPlaybottom.animate().translationY(viewPlaybottom.getHeight()).setInterpolator(new AccelerateInterpolator(2));

    }

    protected void showViewsPlaybackBottom() {
        if (getActivity() != null)
            viewPlaybottom = ((BaseMediaActivity) getActivity()).getViewPlaybackBottom();
        if (viewPlaybottom != null)
            viewPlaybottom.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    protected void onMediaControllerConnected() {
        // empty implementation, can be overridden by clients.
        onConnectedMediaId();
    }

    //callback that ensure that we are showing the controls
    private final MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (shouldShowControls()) {
                showPlaybackControls();
            } else {

                hidePlaybackControls();
            }
            updateStateItem();
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            //TODO: check state media for show or hide playback control
            if (shouldShowControls()) {
                showPlaybackControls();
            } else {
                LogHelper.d(TAG, "mediaControllerCallback.onMetadataChanged: " +
                        "hiding controls because metadata is null");
                hidePlaybackControls();
            }
            updateStateItem();
        }
    };

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback = new
            MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    super.onConnected();
                    //TODO: check connection media
                    Log.e(TAG, "onConnected");
                    try {
                        connectToSession(mediaBrowserCompat.getSessionToken());
                    } catch (RemoteException e) {
                        LogHelper.e(TAG, e, "could not connect media controller");
                        hidePlaybackControls();
                    }
                }
            };

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            Log.e(TAG, "fragment onChildrenLoaded, parentId=" + parentId + "  count=" + children.size());

            if (!isDataLoaded) {
                isDataLoaded = true;
                onMediaBrowserChildrenLoaded(parentId, children);
            } else if (BaseMediaFragment.this instanceof YourMusicFragment) {
                Log.e(TAG, "BaseMediaFragment.this instanceof YourMusicFragment");
                onMediaBrowserChildrenLoaded(parentId, children);
            }

        }

        @Override
        public void onError(@NonNull String parentId, @NonNull Bundle options) {
            super.onError(parentId, options);
            LogHelper.e(TAG, "browse fragment subscription onError, id=" + id);
            showToast(R.string.error_loading_media);
        }
    };


    public String getMediaId() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getString(ARG_MEDIA_ID);
        }
        return null;
    }

    public void setMediaId(String mediaId) {
        Bundle args = new Bundle(1);
        args.putString(ARG_MEDIA_ID, mediaId);
        setArguments(args);
    }


    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(getActivity(), token);
        getActivity().setSupportMediaController(mediaController);
        mediaController.registerCallback(mMediaControllerCallback);

        if (shouldShowControls()) {
            showPlaybackControls();
        } else {
            LogHelper.d(TAG, "connectionCallback.onConnected: " +
                    "hiding controls because metadata is null");
            hidePlaybackControls();
        }

        if (mControlsFragment != null) {
            mControlsFragment.onConnected();
        }

        onMediaControllerConnected();
    }

    protected void showPlaybackControls() {
        if (mControlsFragment == null) return;
        mControlsFragment.setmMediaID(getMediaId());
        LogHelper.d(TAG, "showPlaybackControls");
        getFragmentManager().beginTransaction()
                .show(mControlsFragment)
                .commit();

    }

    protected void hidePlaybackControls() {
        if (mControlsFragment == null) return;
        LogHelper.d(TAG, "hidePlaybackControls");
        getFragmentManager().beginTransaction()
                .hide(mControlsFragment)
                .commit();
    }

    /**
     * Check if the MediaSession is active and in a "playback-able" state
     * (not NONE and not STOPPED).
     *
     * @return true if the MediaSession's state requires playback controls to be visible.
     */
    protected boolean shouldShowControls() {
        MediaControllerCompat mediaController = getActivity().getSupportMediaController();
        if (mediaController == null ||
                mediaController.getMetadata() == null ||
                mediaController.getPlaybackState() == null) {
            return false;
        }
        switch (mediaController.getPlaybackState().getState()) {
            case PlaybackStateCompat.STATE_ERROR:
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                return false;
            default:
                return true;
        }
    }

    private void onConnectedMediaId() {
        if (isDetached()) {
            return;
        }
        if (getArguments() != null) {
            mMediaId = getArguments().getString(ARG_MEDIA_ID);
            Log.e(TAG, "------------- mMediaId = " + mMediaId);
        }

        if (mMediaId == null) {
            mMediaId = mediaBrowserCompat.getRoot();
        }

        // Unsubscribing before subscribing is required if this mediaId already has a subscriber
        // on this MediaBrowser instance. Subscribing to an already subscribed mediaId will replace
        // the callback, but won't trigger the initial callback.onChildrenLoaded.
        //
        // This is temporary: A bug is being fixed that will make subscribe
        // consistently call onChildrenLoaded initially, no matter if it is replacing an existing
        // subscriber or not. Currently this only happens if the mediaID has no previous
        // subscriber or if the media content changes on the service side, so we need to
        // unSubscribe first.
        mediaBrowserCompat.unsubscribe(mMediaId);

        mediaBrowserCompat.subscribe(mMediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = getActivity()
                .getSupportMediaController();
        if (controller != null) {
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowserCompat = new MediaBrowserCompat(getActivity(), new ComponentName
                (getActivity(), MusicService.class), mConnectionCallback, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        mControlsFragment = (PlaybackControlsFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_playback_controls_player);

        if (mControlsFragment == null) {
//            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }
        mediaBrowserCompat.connect();
        updateStateItem();
    }


    @Override
    public void onStop() {
        super.onStop();
        MediaControllerCompat controller = getActivity().getSupportMediaController();

        if (controller != null) {
            controller.unregisterCallback(mMediaControllerCallback);
        }

        mediaBrowserCompat.disconnect();
        Log.e(TAG, "-----------------------------------------onStop");
    }

    protected static List<MediaBrowserCompat.MediaItem> filter(List<MediaBrowserCompat
            .MediaItem> models, String query) {
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

    protected static final Comparator<MediaBrowserCompat.MediaItem> ALPHABETICAL_COMPARATOR =
            new Comparator<MediaBrowserCompat.MediaItem>() {
                @Override
                public int compare(MediaBrowserCompat.MediaItem a, MediaBrowserCompat.MediaItem b) {
                    return a.getDescription().getTitle().toString().compareTo(b.getDescription
                            ().getTitle().toString());
                }

            };

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public boolean onSearchClose() {
        return false;
    }


}