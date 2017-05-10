package com.cc.domain.repository;

import com.cc.domain.model.MediaLocalItem;

import java.util.Collection;

/**
 * Author: NT
 * Since: 11/10/2016.
 */
public interface SongLocalFavoriteRepository {
    boolean createSong(MediaLocalItem item);

    boolean isExistSong(MediaLocalItem item);

    boolean deleteSong(MediaLocalItem item);

    MediaLocalItem readSong(MediaLocalItem item, String whereClause);

    Collection<MediaLocalItem> readAllSong();

    Collection<MediaLocalItem> readAllSong(String whereClause);
}