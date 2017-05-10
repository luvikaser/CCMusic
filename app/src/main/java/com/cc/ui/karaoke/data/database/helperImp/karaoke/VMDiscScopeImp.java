/*
package com.cc.ui.karaoke.data.database.helperImp.karaoke;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import java.util.ArrayList;

import com.cc.ui.karaoke.app.VMobileApplication;
import com.cc.ui.karaoke.data.database.helper.karaoke.VMDiscScope;
import com.cc.ui.karaoke.data.database.helperImp.karaoke.base.VMBaseSongKaraokeScopeImp;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongArirangTable;
import com.cc.ui.karaoke.data.database.table.karaoke.ZSongVirtualTable;
import com.cc.ui.karaoke.data.model.VMVolsKaraoke;

*/
/**
 * Project: Minion
 * Author: NT
 * Since: 6/18/2016.
 * Email: duynguyen.developer@yahoo.com
 *//*

public class VMDiscScopeImp extends VMBaseSongKaraokeScopeImp implements VMDiscScope {


    @Override
    public Cursor getAllSong() {
        String sortOrder = VMSongArirangTable.SNAMECLEAN_COLUMN + " ASC";
        ContentResolver cr = VMobileApplication.getInstance().getApplicationContext().getContentResolver();
        Cursor cursor = cr.query(VMSongArirangTable.CONTENT_URI, null, null, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }

    @Override
    public Cursor searchQuery(String key) {
        String selection = String.format(VMSongVirtualArirangTable.SELECTION_SEARCH,
                DatabaseUtils.sqlEscapeString(key + "*"));
        Log.d(TAG, "searchQuery = " + selection);
        ContentResolver contentResolver = VMobileApplication.getInstance()
                .getApplicationContext().getContentResolver();

        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = contentResolver.query(VMSongArirangTable.CONTENT_URI, null, selection,
                selectionArgs, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }

    @Override
    public Cursor getCursorWithVolsName(String name, String volsName) {
        return null;
    }

    @Override
    public Cursor getListVols(int valueManufacture) {
        return null;
    }

}
*/
