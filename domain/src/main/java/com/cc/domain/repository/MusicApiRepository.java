package com.cc.domain.repository;

import com.cc.domain.model.SongByArtistApi;
import com.cc.domain.model.SongBySongApi;

import java.util.List;

import rx.Observable;

/**
 * Author: NT
 * Since: 11/3/2016.
 */

public interface MusicApiRepository {
    Observable<List<SongBySongApi>> getListSongBySong(String songName);

    Observable<List<SongByArtistApi.Song>> getListSongByArtist(String artistName);

    Observable<String> getLyric(String songId);

    Observable<String> getMp3Stream(String songId);

    Observable<String> getLyricLrc(String songId);

}
