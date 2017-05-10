package com.cc.data.rest;

import com.cc.data.database.DatabaseBaseManager;
import com.cc.data.database.dao.SongLocalPlayedDAO;
import com.cc.data.database.transformer.SongLocalPlayedTransformer;
import com.cc.domain.model.MediaLocalItem;
import com.cc.domain.repository.SongLocalPlayedRepository;

import java.util.Collection;

/**
 * Author: NT
 * Since: 11/10/2016.
 */
public class SongLocalPlayedRepositoryImpl implements SongLocalPlayedRepository {
    private SongLocalPlayedDAO mSongLocalPlayedDAO;

    public SongLocalPlayedRepositoryImpl(DatabaseBaseManager databaseBaseManager) {
        this.mSongLocalPlayedDAO = new SongLocalPlayedDAO(databaseBaseManager.getDatabase(), new
                SongLocalPlayedTransformer());
    }

    @Override
    public boolean createSong(MediaLocalItem item) {
        try {
            if (mSongLocalPlayedDAO.create(item) != null)
                return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isExistSong(MediaLocalItem item) {
        try {
            MediaLocalItem mediaLocalItem = mSongLocalPlayedDAO.read(item);
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
            return mSongLocalPlayedDAO.delete(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public MediaLocalItem readSong(MediaLocalItem mediaLocalItem, String whereClause) {
        try {
            return mSongLocalPlayedDAO.read(mediaLocalItem, whereClause);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MediaLocalItem readSong(MediaLocalItem mediaLocalItem) {
        try {
            return mSongLocalPlayedDAO.read(mediaLocalItem);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Collection<MediaLocalItem> readAllSong() {
        try {
            if (mSongLocalPlayedDAO != null)
                return mSongLocalPlayedDAO.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<MediaLocalItem> readAllSong(String whereClause) {
        try {
            return mSongLocalPlayedDAO.readAll(whereClause);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateIDMp3Song(MediaLocalItem item, String idMp3) {
        try {
            MediaLocalItem mediaLocalItem = mSongLocalPlayedDAO.read(item);
            if (mediaLocalItem != null){
                mediaLocalItem.setIdMp3(idMp3);
                mSongLocalPlayedDAO.update(mediaLocalItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updatePathLyricSong(MediaLocalItem item, String path) {
        try {
            MediaLocalItem mediaLocalItem = mSongLocalPlayedDAO.read(item);
            if (mediaLocalItem != null){
                mediaLocalItem.setPathLyric(path);
                mSongLocalPlayedDAO.update(mediaLocalItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}