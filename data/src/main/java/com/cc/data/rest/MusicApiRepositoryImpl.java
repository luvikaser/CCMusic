package com.cc.data.rest;

import com.cc.data.entities.response.LyricByIdApiResponse;
import com.cc.data.entities.response.LyricLrcByIdResponse;
import com.cc.data.entities.response.Mp3StreamByIdResponse;
import com.cc.data.entities.response.SongByArtistApiResponse;
import com.cc.data.entities.response.SongBySongApiResponse;
import com.cc.domain.model.SongByArtistApi;
import com.cc.domain.model.SongBySongApi;
import com.cc.domain.repository.MusicApiRepository;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Author: NT
 * Since: 11/3/2016.
 */
public class MusicApiRepositoryImpl implements MusicApiRepository {
    private MusicApiRest musicApiRest;
    private HashMap<String, String> paramsDefault;

    public MusicApiRepositoryImpl(MusicApiRest musicApiRest, HashMap<String, String> params) {
        this.musicApiRest = musicApiRest;
        this.paramsDefault = params;
    }

    @Override
    public Observable<List<SongBySongApi>> getListSongBySong(String songName) {
        return musicApiRest.searchSongBySong(songName).map(new Func1<SongBySongApiResponse, List<SongBySongApi>>() {
            @Override
            public List<SongBySongApi> call(SongBySongApiResponse songBySongApiResponse) {
                if (songBySongApiResponse.errorCode != 0)
                    return null;
                return songBySongApiResponse.data;
            }
        });
    }


    @Override
    public Observable<List<SongByArtistApi.Song>> getListSongByArtist(String artistName) {
        return musicApiRest.searchSongByArtis(artistName).map(new Func1<SongByArtistApiResponse, List<SongByArtistApi.Song>>() {
            @Override
            public List<SongByArtistApi.Song> call(SongByArtistApiResponse songByArtistApiResponse) {
                if (songByArtistApiResponse.errorCode != 0)
                    return null;
                return songByArtistApiResponse.data.songList;
            }
        });
    }

    @Override
    public Observable<String> getLyric(String songId) {
        return musicApiRest.getLyricById(songId).map(new Func1<LyricByIdApiResponse, String>() {
            @Override
            public String call(LyricByIdApiResponse lyricByIdApiResponse) {
                if (lyricByIdApiResponse.errorCode != 0)
                    return null;
                return lyricByIdApiResponse.data;
            }
        });
    }

    @Override
    public Observable<String> getMp3Stream(String songId) {
        return musicApiRest.getMp3StreamById(songId).map(new Func1<Mp3StreamByIdResponse, String>() {
            @Override
            public String call(Mp3StreamByIdResponse mp3StreamByIdResponse) {
                if (mp3StreamByIdResponse.errorCode != 0)
                    return null;
                return mp3StreamByIdResponse.data;
            }
        });
    }

    @Override
    public Observable<String> getLyricLrc(String songId) {
        return musicApiRest.getLyricLrcById(songId).map(new Func1<LyricLrcByIdResponse, String>() {
            @Override
            public String call(LyricLrcByIdResponse lyricLrcByIdResponse) {
                if (lyricLrcByIdResponse.errorCode != 0)
                    return null;
                return lyricLrcByIdResponse.data;
            }
        });
    }
}