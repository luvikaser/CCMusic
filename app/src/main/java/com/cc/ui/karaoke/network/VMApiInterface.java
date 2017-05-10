package com.cc.ui.karaoke.network;

import com.cc.ui.karaoke.data.model.services.response.artist.VMSearchArtistResponse;
import com.cc.ui.karaoke.data.model.services.response.lyric.VMLyricByIdResponse;
import com.cc.ui.karaoke.data.model.services.response.song.VMSearchSongResponse;
import com.cc.ui.karaoke.data.model.services.response.stream.VMMp3StreamResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/20/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public interface VMApiInterface {

    @GET("lyric_by_id")
    Call<VMLyricByIdResponse> getLyricById(@Query("id") String apiId);

    @GET("search")
    Call<VMSearchSongResponse> searchSong(@Query("song") String song);

    @GET("list_song")
    Call<VMSearchArtistResponse> searchArtis(@Query("artist") String song);

    @GET("mp3_by_id")
    Call<VMMp3StreamResponse> getMp3StreamById(@Query("id") String song);

    @GET("mp3_karaoke")
    Call<VMMp3StreamResponse> getMp3LrcStringById(@Query("id") String song);
}
