package com.cc.data.rest;

import com.cc.data.entities.response.LyricByIdApiResponse;
import com.cc.data.entities.response.LyricLrcByIdResponse;
import com.cc.data.entities.response.SongByArtistApiResponse;
import com.cc.data.entities.response.Mp3StreamByIdResponse;
import com.cc.data.entities.response.SongBySongApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Author: NT
 * Since: 11/3/2016.
 */

public interface MusicApiRest {
    @GET("lyric_by_id")
    Observable<LyricByIdApiResponse> getLyricById(@Query("id") String songId);

    @GET("search")
    Observable<SongBySongApiResponse> searchSongBySong(@Query("song") String song);

    @GET("list_song")
    Observable<SongByArtistApiResponse> searchSongByArtis(@Query("artist") String song);

    @GET("mp3_by_id")
    Observable<Mp3StreamByIdResponse> getMp3StreamById(@Query("id") String songId);

    @GET("mp3_karaoke")
    Observable<LyricLrcByIdResponse> getLyricLrcById(@Query("id") String songId);
}
