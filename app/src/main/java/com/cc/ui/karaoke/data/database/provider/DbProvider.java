package com.cc.ui.karaoke.data.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cc.ui.karaoke.data.database.helper.DbMainHelper;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongArirangTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongCaliforniaTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongFavoriteTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongMusicCoreTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongVietKtvTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMVolTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongVirtualArirangTable;
import com.cc.ui.karaoke.data.database.table.record.VMRecordTable;


/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class DbProvider extends ContentProvider {
    private static final String TAG = DbProvider.class.getSimpleName();
    private DbMainHelper mDbHelper;


    private static final int SONG_ARIRANG_INFO_QUERY = 1001;
    private static final int SONG_CALIFORNIA_INFO_QUERY = 1002;
    private static final int SONG_MUSICCORE_QUERY = 1003;
    private static final int SONG_VIET_KTV_INFO_QUERY = 1004;
    private static final int VOL_QUERY = 1005;
    private static final int SONG_VIRTUAL_INFO_QUERY = 1006;
    private static final int SONG_FAVORITE_INFO_QUERY = 1007;
    private static final int RECORD_FILE_INFO_QUERY = 1008;
    // define provider db
    private static final String AUTHORITY = "vn.cccorp.music.karaoke.provider";
    private static final String URL = "content://" + AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, VMSongArirangTable.TABLE_NAME, SONG_ARIRANG_INFO_QUERY);
        uriMatcher.addURI(AUTHORITY, VMSongCaliforniaTable.TABLE_NAME, SONG_CALIFORNIA_INFO_QUERY);
        uriMatcher.addURI(AUTHORITY, VMSongMusicCoreTable.TABLE_NAME, SONG_MUSICCORE_QUERY);
        uriMatcher.addURI(AUTHORITY, VMSongVietKtvTable.TABLE_NAME, SONG_VIET_KTV_INFO_QUERY);
        uriMatcher.addURI(AUTHORITY, VMSongVirtualArirangTable.TABLE_NAME, SONG_VIRTUAL_INFO_QUERY);
        uriMatcher.addURI(AUTHORITY, VMVolTable.TABLE_NAME, VOL_QUERY);
        uriMatcher.addURI(AUTHORITY, VMSongFavoriteTable.TABLE_NAME, SONG_FAVORITE_INFO_QUERY);
        uriMatcher.addURI(AUTHORITY, VMRecordTable.TABLE_NAME, RECORD_FILE_INFO_QUERY);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbMainHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor retCursor = null;

        switch (uriMatcher.match(uri)) {

            case SONG_ARIRANG_INFO_QUERY:
                retCursor = VMSongArirangTable.getInstance().query(getContext(), mDbHelper, uri,
                        projection, selection, selectionArgs, sortOrder);

                break;

            case SONG_CALIFORNIA_INFO_QUERY:
                retCursor = VMSongCaliforniaTable.getInstance().query(getContext(), mDbHelper, uri,
                        projection, selection, selectionArgs, sortOrder);

                break;

            case SONG_MUSICCORE_QUERY:
                retCursor = VMSongMusicCoreTable.getInstance().query(getContext(), mDbHelper, uri,
                        projection, selection, selectionArgs, sortOrder);

                break;

            case SONG_VIET_KTV_INFO_QUERY:
                retCursor = VMSongVietKtvTable.getInstance().query(getContext(), mDbHelper, uri,
                        projection, selection, selectionArgs, sortOrder);

                break;
            case SONG_VIRTUAL_INFO_QUERY:
                retCursor = VMSongVirtualArirangTable.query(getContext(), mDbHelper, uri,
                        projection, selection, selectionArgs, sortOrder);

                break;

            case SONG_FAVORITE_INFO_QUERY:
                retCursor = VMSongFavoriteTable.getInstance().query(getContext(), mDbHelper, uri,
                        projection, selection, selectionArgs, sortOrder);

                break;


            case VOL_QUERY:
                Log.d(TAG, "VOL_QUERY");
                retCursor = VMVolTable.getInstance().query(getContext(), mDbHelper, uri,
                        projection, selection, selectionArgs, sortOrder);

                break;

            case RECORD_FILE_INFO_QUERY:
                Log.d(TAG, "RECORD_FILE_INFO_QUERY");
                retCursor = VMRecordTable.getInstance().query(getContext(), mDbHelper, uri,
                        projection, selection, selectionArgs, sortOrder);

                break;

        }

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri reUri = null;
        Log.e(TAG, "uriMatcher.match(uri) = " + uriMatcher.match(uri) + "");
        switch (uriMatcher.match(uri)) {

            case SONG_ARIRANG_INFO_QUERY:
                reUri = VMSongArirangTable.getInstance().insert(getContext(), mDbHelper, uri, values);
                break;

            case SONG_CALIFORNIA_INFO_QUERY:
                reUri = VMSongCaliforniaTable.getInstance().insert(getContext(), mDbHelper, uri, values);

                break;

            case SONG_MUSICCORE_QUERY:
                reUri = VMSongMusicCoreTable.getInstance().insert(getContext(), mDbHelper, uri, values);
                break;

            case SONG_VIET_KTV_INFO_QUERY:
                reUri = VMSongVietKtvTable.getInstance().insert(getContext(), mDbHelper, uri, values);
                break;

            case SONG_FAVORITE_INFO_QUERY:
                reUri = VMSongFavoriteTable.getInstance().insert(getContext(), mDbHelper, uri, values);
                break;


            case SONG_VIRTUAL_INFO_QUERY:
                reUri = VMSongVirtualArirangTable.insertQuickly(mDbHelper , values, uri);
                break;


            case RECORD_FILE_INFO_QUERY:
                reUri = VMRecordTable.getInstance().insert(getContext(), mDbHelper, uri, values);

                break;

        }
        return reUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rows = 0;
        switch (uriMatcher.match(uri)) {
            case SONG_ARIRANG_INFO_QUERY:
                rows = VMSongArirangTable.getInstance().delete(getContext(), mDbHelper, uri,
                    selection, selectionArgs);
                break;

            case SONG_CALIFORNIA_INFO_QUERY:
                rows = VMSongCaliforniaTable.getInstance().delete(getContext(), mDbHelper, uri,
                        selection, selectionArgs);
                break;

            case SONG_MUSICCORE_QUERY:
                rows = VMSongMusicCoreTable.getInstance().delete(getContext(), mDbHelper, uri,
                        selection, selectionArgs);
                break;

            case SONG_VIET_KTV_INFO_QUERY:
                rows = VMSongVietKtvTable.getInstance().delete(getContext(), mDbHelper, uri,
                        selection, selectionArgs);
                break;

            case SONG_FAVORITE_INFO_QUERY:
                rows = VMSongFavoriteTable.getInstance().delete(getContext(), mDbHelper, uri,
                        selection, selectionArgs);
                break;

            case RECORD_FILE_INFO_QUERY:
                rows = VMRecordTable.getInstance().delete(getContext(), mDbHelper, uri, selection, selectionArgs);

                break;
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rows = 0;
        switch (uriMatcher.match(uri)) {
            case SONG_ARIRANG_INFO_QUERY:
                rows = VMSongArirangTable.getInstance().update(getContext(), mDbHelper, uri,
                    values, selection, selectionArgs);
                break;

            case SONG_CALIFORNIA_INFO_QUERY:
                rows = VMSongCaliforniaTable.getInstance().update(getContext(), mDbHelper, uri,
                        values, selection, selectionArgs);
                break;

            case SONG_MUSICCORE_QUERY:
                rows = VMSongMusicCoreTable.getInstance().update(getContext(), mDbHelper, uri,
                        values, selection, selectionArgs);
                break;

            case SONG_VIET_KTV_INFO_QUERY:
                rows = VMSongVietKtvTable.getInstance().update(getContext(), mDbHelper, uri,
                        values, selection, selectionArgs);
                break;

            case SONG_FAVORITE_INFO_QUERY:
                rows = VMSongFavoriteTable.getInstance().update(getContext(), mDbHelper, uri,
                        values, selection, selectionArgs);
                break;

            case RECORD_FILE_INFO_QUERY:
                rows = VMRecordTable.getInstance().update(getContext(), mDbHelper, uri,
                        values, selection, selectionArgs);
                break;
        }
        return rows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int rows = 0;
        switch (uriMatcher.match(uri)) {
            case SONG_VIRTUAL_INFO_QUERY:
                rows = VMSongVirtualArirangTable.bunkInsert(getContext(), mDbHelper, uri, values);
                break;
        }
        return rows;
    }
}
