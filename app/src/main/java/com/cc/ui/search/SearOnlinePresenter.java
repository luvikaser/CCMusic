package com.cc.ui.search;

import android.util.Log;

import com.cc.domain.model.SongBySongApi;
import com.cc.domain.repository.MusicApiRepository;
import com.cc.presenter.BaseAppPresenter;
import com.cc.presenter.IPresenter;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: NT
 * Since: 12/2/2016.
 */
public class SearOnlinePresenter extends BaseAppPresenter implements IPresenter<ISearchOnlineView> {
    private MusicApiRepository musicApiRepository;

    private ISearchOnlineView mView;
    private SearchOnlineManager searchOnlineManager;

    @Inject
    public SearOnlinePresenter(MusicApiRepository musicApiRepository, SearchOnlineManager searchOnlineManager) {
        this.musicApiRepository = musicApiRepository;
        this.searchOnlineManager = searchOnlineManager;
    }

    public void searchSongs(String songName) {
        Log.e("searchSongs", " = songName");
    Subscription subscription=  musicApiRepository.getListSongBySong(songName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SongBySongApi>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.songSearchError();
                    }

                    @Override
                    public void onNext(List<SongBySongApi> songBySongApis) {
                        if (songBySongApis.get(3).songList.size() == 0)
                            mView.songSearchNotFound();
                        else {
                            searchOnlineManager.setSongSearchOnline(songBySongApis.get(3).songList);
                            mView.songSearchComplete();
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    public void searchAlbum(String albumName) {
        Log.e("searchSongs", " = songName");
        Subscription subscription=  musicApiRepository.getListSongBySong(albumName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SongBySongApi>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.songSearchError();
                    }

                    @Override
                    public void onNext(List<SongBySongApi> songBySongApis) {
                        if (songBySongApis.get(1).albumList.size() == 0)
                            mView.songSearchNotFound();
                        else {
                            searchOnlineManager.setAlbumSearchOnline(songBySongApis.get(1).albumList);
                            mView.songSearchComplete();
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }


    public void searchArtist(String artistName) {
        Log.e("searchSongs", " = songName");
        Subscription subscription=  musicApiRepository.getListSongBySong(artistName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SongBySongApi>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.songSearchError();
                    }

                    @Override
                    public void onNext(List<SongBySongApi> songBySongApis) {
                        if (songBySongApis.get(0).artistList.size() == 0)
                            mView.songSearchNotFound();
                        else {
                            searchOnlineManager.setArtistSearchOnline(songBySongApis.get(0).artistList);
                            mView.songSearchComplete();
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }


    @Override
    public void setView(ISearchOnlineView iSearchOnlineView) {
        mView = iSearchOnlineView;
    }

    @Override
    public void destroyView() {
        mView = null;
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