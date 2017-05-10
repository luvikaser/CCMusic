package com.cc.data.database.dao;

import android.database.sqlite.SQLiteDatabase;

import com.cc.data.database.transformer.SongLocalFavoriteTransformer;
import com.cc.data.utils.sqlite.SQLiteDelegate;
import com.cc.data.utils.sqlite.SQLiteTransformer;
import com.cc.domain.model.MediaLocalItem;

/**
 * Author: NT
 * Since: 11/8/2016.
 */
public class SongLocalFavoriteDAO extends SQLiteDelegate<MediaLocalItem> {
    /**
     * Constructor with two parameters, database and transformer.
     *
     * @param db          SQLiteDatabase android
     * @param transformer SQLiteTransformer
     */

    public SongLocalFavoriteDAO(SQLiteDatabase db, SQLiteTransformer<MediaLocalItem> transformer) {
        super(db, transformer);
    }


}