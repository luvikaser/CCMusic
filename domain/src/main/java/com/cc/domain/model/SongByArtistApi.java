package com.cc.domain.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Author: NT
 * Since: 11/3/2016.
 * model song search by artist
 */
public class SongByArtistApi {
    @SerializedName("current_page")
    public int currentPage;

    @SerializedName("total_page")
    public int totalPage;

    @SerializedName("songs")
    public List<Song> songList;

    public class Song {
        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;
    }

}