package com.cc.ui.karaoke.data.database.helperImp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import com.cc.MusicApplication;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongArirangTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongVirtualArirangTable;
import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class ZSongScopeImp   {
    private static final String TAG = ZSongScopeImp.class.getSimpleName();
    private OnUpdateProgressLoadData listener;
    private ArrayList<VMSongKaraoke> listSong = new ArrayList<>();

    public ZSongScopeImp(OnUpdateProgressLoadData listener) {
        this.listener = listener;
    }

    public ZSongScopeImp() {
    }


    public void copyToVirtualTable() {
        Log.d(TAG, "copyToVirtualTable");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = 0;
                String sortOrder = VMSongArirangTable.PK_COLUMN + " ASC";
                ContentResolver cr = MusicApplication.getInstance().getApplicationContext().getContentResolver();
                Cursor cursor = cr.query(VMSongArirangTable.CONTENT_URI, null, null, null, sortOrder);

                if (cursor != null && cursor.moveToFirst()) {
                    ContentResolver contentResolver = MusicApplication.getInstance()
                            .getApplicationContext().getContentResolver();
                    while (!cursor.isAfterLast()) {
                        VMSongKaraoke zs = new VMSongKaraoke();
                        zs.setPk(cursor.getLong(cursor.getColumnIndex(VMSongArirangTable.PK_COLUMN)));
                        zs.setSvol(cursor.getLong(cursor.getColumnIndex(VMSongArirangTable.SVOL_COLUMN)));
                        zs.setEnt((byte) cursor.getInt(cursor.getColumnIndex(VMSongArirangTable.ENT_COLUMN)));
                        zs.setOpt((byte) cursor.getInt(cursor.getColumnIndex(VMSongArirangTable
                                .OPT_COLUMN)));
                        zs.setRowid(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .ROWID_COLUMN)));
                        zs.setSabbr(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SABBR_COLUMN)));
                        zs.setsLanguage(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SLANGUAGE_COLUMN)));
                        zs.setsLyric(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SLYRIC_COLUMN)));
                        zs.setsLyricClean(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SLYRICCLEAN_COLUMN)));
                        zs.setsManufacture(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SMANUFACTURE_COLUMN)));
                        zs.setsMeta(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SMETA_COLUMN)));
                        zs.setsMetaClean(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SMETACLEAN_COLUMN)));
                        zs.setsName(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SNAME_COLUMN)));
                        zs.setsNameClean(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SNAMECLEAN_COLUMN)));
                        zs.setsFavorite(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                                .SFAVORITE_COLUMN)));
                        String search = zs.getSabbr() + " " + zs.getsLyric() + " "
                                + zs.getsLyricClean() + " " + zs.getsMeta() + " "
                                + zs.getsMetaClean() + " " + zs.getsName()
                                + " " + zs.getsNameClean();
//                        Log.d(TAG, "search " + search);
                        ContentValues serValues = new ContentValues();
                        serValues.put(VMSongVirtualArirangTable.KEY_COL_SEARCH, search);
                        serValues.put(VMSongVirtualArirangTable.KEY_COL_RESULT, zs.getPk());
                        contentResolver.insert(VMSongVirtualArirangTable.CONTENT_URI, serValues);
                        cursor.moveToNext();
                    }

                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public Cursor getCursorListZSong() {
        String sortOrder = VMSongArirangTable.SNAMECLEAN_COLUMN + " ASC";
        ContentResolver cr = MusicApplication.getInstance().getApplicationContext().getContentResolver();
        Cursor cursor = cr.query(VMSongArirangTable.CONTENT_URI, null, null, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }


    public Cursor searchQuery(String key) {
        String selection = String.format(VMSongVirtualArirangTable.SELECTION_SEARCH,
                DatabaseUtils.sqlEscapeString(key + "*"));
        Log.d(TAG, "searchQuery = " + selection);
        ContentResolver contentResolver = MusicApplication.getInstance()
                .getApplicationContext().getContentResolver();

        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = contentResolver.query(VMSongArirangTable.CONTENT_URI, null, selection,
                selectionArgs, sortOrder);
        Log.d(TAG, "searchQuery with cursor = " + cursor.getCount());
        return cursor;
    }

    public interface OnUpdateProgressLoadData {
        void onUpdateProgress(int progress);

        void onLoadDataFinish(ArrayList<VMSongKaraoke> datas);
    }

    public class AsyncTaskLoadDb extends AsyncTask<String, Double, ArrayList<VMSongKaraoke>> {


        @Override
        protected ArrayList<VMSongKaraoke> doInBackground(String... params) {
            Double percentage;
            String sortOrder = VMSongArirangTable.SNAME_COLUMN + " ASC ORDER BY " + VMSongArirangTable
                    .SNAMECLEAN_COLUMN;
            ContentResolver cr = MusicApplication.getInstance().getApplicationContext().getContentResolver();
            Cursor cursor = cr.query(VMSongArirangTable.CONTENT_URI, null, null, null, sortOrder);
            int total;
            int unitLoaded = 0;
            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getCount();
                while (!cursor.isAfterLast()) {
                    unitLoaded++;
                    VMSongKaraoke zs = new VMSongKaraoke();
                    zs.setPk(cursor.getLong(cursor.getColumnIndex(VMSongArirangTable.PK_COLUMN)));
                    zs.setSvol(cursor.getLong(cursor.getColumnIndex(VMSongArirangTable.SVOL_COLUMN)));
                    zs.setEnt((byte) cursor.getInt(cursor.getColumnIndex(VMSongArirangTable.ENT_COLUMN)));
                    zs.setOpt((byte) cursor.getInt(cursor.getColumnIndex(VMSongArirangTable
                            .OPT_COLUMN)));
                    zs.setRowid(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .ROWID_COLUMN)));
                    zs.setSabbr(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SABBR_COLUMN)));
                    zs.setsLanguage(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SLANGUAGE_COLUMN)));
                    zs.setsLyric(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SLYRIC_COLUMN)));
                    zs.setsLyricClean(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SLYRICCLEAN_COLUMN)));
                    zs.setsManufacture(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SMANUFACTURE_COLUMN)));
                    zs.setsMeta(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SMETA_COLUMN)));
                    zs.setsMetaClean(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SMETACLEAN_COLUMN)));
                    zs.setsName(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SNAME_COLUMN)));
                    zs.setsNameClean(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SNAMECLEAN_COLUMN)));
                    zs.setsFavorite(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                            .SFAVORITE_COLUMN)));
                    listSong.add(zs);

                    Log.d(TAG, "unitLoaded =  " + unitLoaded);
                    Log.d(TAG, "total =  " + total);
                    percentage = (((double) unitLoaded) / total) * 100;
                    publishProgress(percentage);

                    cursor.moveToNext();
//                Log.d(TAG, "PK_COLUMN = " + zs.getPk());
                }

            }
            return listSong;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            listener.onUpdateProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(ArrayList<VMSongKaraoke> VMSongKaraokes) {
            listener.onLoadDataFinish(VMSongKaraokes);
        }
    }

}
