package com.cc.data.rest;

import com.cc.data.database.DatabaseBaseManager;
import com.cc.data.database.dao.SongLocalFavoriteDAO;
import com.cc.data.database.transformer.SongLocalFavoriteTransformer;
import com.cc.domain.model.MediaLocalItem;
import com.cc.domain.repository.SongLocalFavoriteRepository;

import java.util.Collection;

/**
 * Author: NT
 * Since: 11/10/2016.
 */
public class SongLocalFavoriteRepositoryImpl implements SongLocalFavoriteRepository {
    private SongLocalFavoriteDAO mSongLocalFavoriteDAO;

    public SongLocalFavoriteRepositoryImpl(DatabaseBaseManager databaseBaseManager) {
        this.mSongLocalFavoriteDAO = new SongLocalFavoriteDAO(databaseBaseManager.getDatabase(), new SongLocalFavoriteTransformer());
    }

    @Override
    public boolean createSong(MediaLocalItem item) {
        try {
            if (mSongLocalFavoriteDAO.create(item) != null)
                return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isExistSong(MediaLocalItem item) {
        try {
            MediaLocalItem mediaLocalItem = mSongLocalFavoriteDAO.read(item);
            if (mediaLocalItem != null)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteSong(MediaLocalItem item) {
        try {
            return mSongLocalFavoriteDAO.delete(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public MediaLocalItem readSong(MediaLocalItem item, String whereClause) {
        try {
            return mSongLocalFavoriteDAO.read(item, whereClause);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<MediaLocalItem> readAllSong() {
        try {
            return mSongLocalFavoriteDAO.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<MediaLocalItem> readAllSong(String whereClause) {
        try {
            return mSongLocalFavoriteDAO.readAll(whereClause);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}