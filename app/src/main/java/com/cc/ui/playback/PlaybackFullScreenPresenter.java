
package com.cc.ui.playback;

import android.util.Log;
import android.widget.Toast;

import com.cc.MusicApplication;
import com.cc.app.R;
import com.cc.domain.model.MediaLocalItem;
import com.cc.domain.model.SongBySongApi;
import com.cc.domain.repository.MusicApiRepository;
import com.cc.domain.repository.SongLocalFavoriteRepository;
import com.cc.event.BeginLoadEvent;
import com.cc.event.ChangeSongEvent;
import com.cc.event.LyricReceiveEvent;
import com.cc.presenter.BaseUserPresenter;
import com.cc.presenter.IPresenter;
import com.cc.provider.music.MusicProvider;
import com.cc.service.MusicService;
import com.cc.utils.LyricUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Author: NT
 * Since: 11/3/2016.
 */
public class PlaybackFullScreenPresenter extends BaseUserPresenter implements
        IPresenter<IViewPlaybackFullScreen> {
    private final MusicApiRepository musicApiRepository;
    private IViewPlaybackFullScreen mView;
    private SongLocalFavoriteRepository mSongFavoriteLocalRepository;

    @Inject
    public PlaybackFullScreenPresenter(MusicApiRepository musicApiRepository,
                                       SongLocalFavoriteRepository songFavoriteLocalRepository) {
        this.musicApiRepository = musicApiRepository;
        this.mSongFavoriteLocalRepository = songFavoriteLocalRepository;
    }

    public void createSongFavorite(MediaLocalItem mediaLocalItem) {
       boolean isCreate = mSongFavoriteLocalRepository.createSong(mediaLocalItem);
        //TODO: implement result if need
    }

    public void deleteSongFavorite(MediaLocalItem mediaLocalItem) {
        //TODO: implement result if need
       boolean isDelete =  mSongFavoriteLocalRepository.deleteSong(mediaLocalItem);
    }

    public boolean checkExistSongFavorite(MediaLocalItem item) {
        Log.e(TAG, "item = " + item.toString());

        return mSongFavoriteLocalRepository.isExistSong(item);
    }


    @Override
    public void setView(IViewPlaybackFullScreen iViewPlaybackFullScreen) {
        this.mView = iViewPlaybackFullScreen;
    }

    @Override
    public void destroyView() {
        this.mView = null;
        unSubscribe();
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }


}