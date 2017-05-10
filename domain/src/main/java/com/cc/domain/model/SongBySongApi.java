package com.cc.domain.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Author: NT
 * Since: 11/3/2016.
 * model Song search by song name
 */
public class SongBySongApi {
    @SerializedName("album")
    public List<Album> albumList;

    @SerializedName("video")
    public List<Video> videoList;

    @SerializedName("song")
    public List<Song> songList;


    @SerializedName("artist")
    public List<Artist> artistList;

    public class Album extends BaseResponseSongList {
        @SerializedName("thumb")
        public String thumb;

        @SerializedName("artist")
        public String artist;
    }

    public class Video extends BaseResponseSongList {
        @SerializedName("thumb")
        public String thumb;

        @SerializedName("artist")
        public String artist;
    }

    public class Song extends BaseResponseSongList {
        @SerializedName("artist")
        public String artist;

        @Override
        public String toString() {
            return "Song{" +
                    "artist='" + artist + '\'' +
                    '}';
        }
    }

    public class Artist extends BaseResponseSongList {

    }

    private class BaseResponseSongList {
        @SerializedName("id")
        public String idSong;

        @SerializedName("name")
        public String nameSong;

    }
}