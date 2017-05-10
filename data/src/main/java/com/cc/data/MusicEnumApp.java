package com.cc.data;

import java.security.PublicKey;

/**
 * Author: NT
 * Since: 11/4/2016.
 */
public class MusicEnumApp {

    public enum MusicType {
        FAVORITE(1),
        ALL_SONG(2),
        PLAYED(3),
        ALBUMS(4),
        ARTISTS(5),
        SONGS(6),
        SONG_ONLINE(7),
        ARTIST_ONLINE(8),
        ALBUM_ONLINE(9),
        UNKNOWN(10);
        public int type;

        MusicType(int type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type + "";
        }
    }

    public enum MusicGenreStr{
        FAVORITE("FAVORITE"),
        SONG_ONLINE("SONG_ONLINE"),
        PLAYED("PLAYED"),
        ALL_SONGS("ALL_SONGS");

        public String type;

        MusicGenreStr(String type) {
            this.type = type;
        }
    }
}