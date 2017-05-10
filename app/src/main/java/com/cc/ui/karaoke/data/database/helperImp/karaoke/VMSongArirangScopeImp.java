package com.cc.ui.karaoke.data.database.helperImp.karaoke;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import java.util.ArrayList;

import com.cc.MusicApplication;
import com.cc.ui.karaoke.data.database.helper.karaoke.VMSongFavoriteScope;
import com.cc.ui.karaoke.data.database.helper.karaoke.VMSongKaraokeScope;
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
public class VMSongArirangScopeImp extends VMBaseSongKaraokeScopeImp implements VMSongKaraokeScope {


    @Override
    public Cursor getCursor() {
        String sortOrder = VMSongArirangTable.SNAME_COLUMN + " ASC";
        String selection = VMSongArirangTable.SLANGUAGE_COLUMN + " = ?";
        String[] selectionArg = new String[]{"vn"};
        ContentResolver cr = MusicApplication.getInstance().getApplicationContext().getContentResolver();
        Cursor cursor = cr.query(VMSongArirangTable.getInstance().getContentUri(), null, selection, selectionArg,
                sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }

    @Override
    public Cursor searchQuery(String key, String volId) {
//        key = toAscii(key);
        String selection = String.format(VMSongVirtualArirangTable.SELECTION_SEARCH,
                DatabaseUtils.sqlEscapeString(key));

        Log.d(TAG, "searchQuery = " + selection);
        ContentResolver contentResolver = MusicApplication.getInstance()
                .getApplicationContext().getContentResolver();
        String[] selectionArgs = new String[]{key, key, key};
        String sortOrder = "length(" +VMSongArirangTable.SNAME_COLUMN + ")";
        Cursor cursor = contentResolver
                .query(VMSongArirangTable.getInstance()
                        .getContentUri(), null, selection, null, null);
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
        VMSongFavoriteScope vmSongFavoriteScope = new VMSongFavoriteScopeImp();
        vmSongFavoriteScope.insert(songKaraoke);
    }

    @Override
    public void removeSongFavorite(long idSong) {
        VMSongFavoriteScope vmSongFavoriteScope = new VMSongFavoriteScopeImp();
        vmSongFavoriteScope.delete(idSong);
    }
}
