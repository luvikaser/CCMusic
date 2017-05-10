package com.cc.ui.karaoke.data.database.helperImp.karaoke;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import java.util.ArrayList;

import com.cc.MusicApplication;
import com.cc.ui.karaoke.data.database.helper.karaoke.VMVolScope;
import com.cc.ui.karaoke.data.database.helperImp.karaoke.base.VMBaseSongKaraokeScopeImp;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongArirangTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongVirtualArirangTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMVolTable;
import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.data.model.karaoke.VMVolsKaraoke;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/18/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class VMVolScopeImp extends VMBaseSongKaraokeScopeImp implements VMVolScope {


    @Override
    public Cursor getCursor() {
        String sortOrder = VMVolTable.SNAME_COLUMN + " ASC";
        ContentResolver cr = MusicApplication.getInstance().getApplicationContext().getContentResolver();
        Cursor cursor = cr.query(VMVolTable.getInstance().getContentUri(), null, null, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }

    @Override
    public Cursor searchQuery(String key, String volId) {
        String selection = String.format(VMSongVirtualArirangTable.SELECTION_SEARCH,
                DatabaseUtils.sqlEscapeString(key + "*"));
        Log.d(TAG, "searchQuery = " + selection);
        ContentResolver contentResolver = MusicApplication.getInstance()
                .getApplicationContext().getContentResolver();

        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = contentResolver.query(VMVolTable.getInstance().getContentUri(), null, selection,
                selectionArgs, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }

    @Override
    public Cursor getCursorWithVolsName(String volsName, String language) {
        String sortOrder = VMSongArirangTable.SNAMECLEAN_COLUMN + " ASC";
        String selection = VMSongArirangTable.SVOL_COLUMN + " = ? AND " +
                VMSongArirangTable.SLANGUAGE_COLUMN + " = ?";
        String[] selectionArg = new String[]{volsName, language};
        ContentResolver cr = MusicApplication.getInstance().getApplicationContext().getContentResolver();
        Cursor cursor = cr.query(VMSongArirangTable.getInstance().getContentUri(), null, selection, selectionArg,
                sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }

    @Override
    public ArrayList<VMVolsKaraoke> getListVols(int valueManufacture) {
        ArrayList<VMVolsKaraoke> list = new ArrayList<>();
        if (valueManufacture > 0) {

            Log.d(TAG, "values of manufacture = " + valueManufacture);
            VMVolScope vmVolScope = new VMVolScopeImp();
            Cursor cursorVols = vmVolScope.getVolsWithManufactureId(valueManufacture);

            if (cursorVols != null && cursorVols.moveToFirst()) {
                while (!cursorVols.isAfterLast()) {
                    VMVolsKaraoke vmVolsKaraoke = new VMVolsKaraoke();
                    vmVolsKaraoke.setId(cursorVols.getLong(cursorVols.getColumnIndex
                            (VMVolTable.SVOL_COLUMN)));
                    vmVolsKaraoke.setName(cursorVols.getString(cursorVols.getColumnIndex
                            (VMVolTable.SNAME_COLUMN)));
                    list.add(vmVolsKaraoke);
                    cursorVols.moveToNext();
                }
            }

        }

        return list;
    }

    @Override
    public void addFavorite(VMSongKaraoke songKaraoke) {

    }

    @Override
    public void removeSongFavorite(long idSong) {

    }


    @Override
    public Cursor getVolsWithManufactureId(int value) {
        String selection =  VMVolTable.SMANUFACTURE_COLUMN + " = ?";
        String[] selectionArg = new String[] {String.valueOf(value)};
        String sortOrder = VMVolTable.SNAME_COLUMN + " DESC";
        ContentResolver cr = MusicApplication.getInstance().getApplicationContext().getContentResolver();
        Cursor cursor = cr.query(VMVolTable.getInstance().getContentUri(), null, selection, selectionArg, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }
}
