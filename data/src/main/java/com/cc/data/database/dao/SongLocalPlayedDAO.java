package com.cc.data.database.dao;

import android.database.sqlite.SQLiteDatabase;

import com.cc.data.utils.sqlite.SQLiteDelegate;
import com.cc.data.utils.sqlite.SQLiteTransformer;
import com.cc.domain.model.MediaLocalItem;

/**
 * Author  : duyng
 * since   : 11/9/2016
 */

public class SongLocalPlayedDAO extends SQLiteDelegate<MediaLocalItem> {
    /**
     * Constructor with two parameters, database and transformer.
     *  @param db          SQLiteDatabase android
     * @param transformer SQLiteTransformer
     */
    public SongLocalPlayedDAO(SQLiteDatabase db,
                              SQLiteTransformer<MediaLocalItem> transformer) {
        super(db, transformer);
    }
}
