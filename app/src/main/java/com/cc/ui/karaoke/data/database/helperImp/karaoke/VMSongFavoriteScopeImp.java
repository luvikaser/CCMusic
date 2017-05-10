package com.cc.ui.karaoke.data.database.helperImp.karaoke;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.cc.MusicApplication;
import com.cc.ui.karaoke.data.database.helper.karaoke.VMSongFavoriteScope;
import com.cc.ui.karaoke.data.database.helperImp.karaoke.base.VMBaseSongKaraokeScopeImp;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongArirangTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongFavoriteTable;
import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;

/**
 * Author: NT
 * Since: 6/22/2016.
 */
public class VMSongFavoriteScopeImp extends VMBaseSongKaraokeScopeImp implements VMSongFavoriteScope {
    private ContentResolver contentResolver;

    public VMSongFavoriteScopeImp() {
        contentResolver = MusicApplication.getInstance().getApplicationContext().getContentResolver();
    }

    @Override
    public Cursor getAllSong() {
        String sortOrder = VMSongArirangTable.SNAME_COLUMN + " ASC";
        Cursor cursor = contentResolver
                .query(VMSongFavoriteTable.getInstance().getContentUri(),
                        null, null, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }

    @Override
    public Cursor searchQuery(String key, String volId) {
        return null;
    }

    @Override
    public void insert(VMSongKaraoke songKaraoke) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VMSongArirangTable.PK_COLUMN, songKaraoke.getPk());
        contentValues.put(VMSongArirangTable.SVOL_COLUMN, songKaraoke.getSvol());
        contentValues.put(VMSongArirangTable.ENT_COLUMN, songKaraoke.getEnt());
        contentValues.put(VMSongArirangTable.OPT_COLUMN, songKaraoke.getOpt());
        contentValues.put(VMSongArirangTable.ROWID_COLUMN, songKaraoke.getRowid());
        contentValues.put(VMSongArirangTable.SABBR_COLUMN, songKaraoke.getSabbr());
        contentValues.put(VMSongArirangTable.SLANGUAGE_COLUMN, songKaraoke.getsLanguage());
        contentValues.put(VMSongArirangTable.SLYRIC_COLUMN, songKaraoke.getsLyric());
        contentValues.put(VMSongArirangTable.SLYRICCLEAN_COLUMN, songKaraoke.getsLyricClean());
        contentValues.put(VMSongArirangTable.SMETA_COLUMN, songKaraoke.getsMeta());
        contentValues.put(VMSongArirangTable.SMETACLEAN_COLUMN, songKaraoke.getsMetaClean());
        contentValues.put(VMSongArirangTable.SNAME_COLUMN, songKaraoke.getsName());
        contentValues.put(VMSongArirangTable.SNAMECLEAN_COLUMN, songKaraoke.getsNameClean());
        contentValues.put(VMSongArirangTable.ZYOUTUBE_COLUMN, songKaraoke.getsYoutube());
        contentValues.put(VMSongArirangTable.SMANUFACTURE_COLUMN, songKaraoke.getsManufacture());
//        contentValues.put(VMSongArirangTable.SFAVORITE_COLUMN, songKaraoke.getsFavorite());
        contentResolver.insert(VMSongFavoriteTable.getInstance().getContentUri(), contentValues);
    }

    @Override
    public void delete(long idSong) {
        String selection = VMSongArirangTable.PK_COLUMN + " = ?";
        String selectionArg[] = new String[]{String.valueOf(idSong)};
        contentResolver.delete(VMSongFavoriteTable.getInstance().getContentUri(),
                selection, selectionArg);
    }

    @Override
    public boolean isAddedFavorite(long idSong) {
        String selection = VMSongArirangTable.PK_COLUMN + " = ?";
        String selectionArg[] = new String[]{String.valueOf(idSong)};
        Cursor cursor = contentResolver.query(VMSongFavoriteTable.getInstance().getContentUri(),
                null, selection, selectionArg, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }


        return false;
    }
}
