package com.cc.domain.repository;

import android.support.v4.media.MediaMetadataCompat;

import com.cc.domain.model.MediaLocalItem;

import java.util.Collection;

/**
 * Author: NT
 * Since: 11/10/2016.
 */
public interface SongLocalPlayedRepository {
    boolean createSong(MediaLocalItem item);

    boolean isExistSong(MediaLocalItem item);

    boolean deleteSong(MediaLocalItem item);

    MediaLocalItem readSong(MediaLocalItem item, String whereClause);

    MediaLocalItem readSong(MediaLocalItem item);


    Collection<MediaLocalItem> readAllSong();

    Collection<MediaLocalItem> readAllSong(String whereClause);

    boolean updateIDMp3Song(MediaLocalItem item, String idMp3);

    boolean updatePathLyricSong(MediaLocalItem item, String path);


}