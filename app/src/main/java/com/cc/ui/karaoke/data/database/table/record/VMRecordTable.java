package com.cc.ui.karaoke.data.database.table.record;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.cc.ui.karaoke.data.database.provider.DbProvider;
import com.cc.ui.karaoke.data.database.table.base.VMBaseTable;

/**
 * Author  : duyng
 * since   : 9/7/2016
 */
public class VMRecordTable extends VMBaseTable {
    private final String TAG = this.getClass().getSimpleName();

    public static final String TABLE_NAME = "RECORD_TABLE";
    public static Uri CONTENT_URI = Uri.withAppendedPath(DbProvider.CONTENT_URI, TABLE_NAME);

    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_FILE_PATH = "file_path";
    public static final String COL_YOUTUBE_ID = "youtube_id";
    public static final String COL_URL_RECORD_UPLOAD = "url_record_upload";
    public static final String COL_DATE_CREATE = "date_create";

    public static final String QUERY_CREATE_TABLE
            = "CREATE TABLE " + TABLE_NAME +
            "( " + COL_ID + " INTEGER AUTO_INCREMENT PRIMARY KEY, "
                + COL_NAME + " VARCHAR, "
                + COL_FILE_PATH + " VARCHAR, "
                + COL_YOUTUBE_ID + " VARCHAR, "
                + COL_DATE_CREATE + " VARCHAR)";

    private static VMRecordTable instance = null;

    public static VMRecordTable getInstance() {
        if (instance == null) {
            instance = new VMRecordTable();
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(QUERY_CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

    @Override
    public Cursor query(Context context, SQLiteOpenHelper helper, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs,
                null, null, sortOrder);
//        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Context context, SQLiteOpenHelper helper, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.update(TABLE_NAME, values, selection, selectionArgs);

        if (0 != rows) {
            // send event to parent hold context
            context.getContentResolver().notifyChange(uri, null);
            return rows;
        } else {
            throw new SQLiteException("update error: " + uri);
        }
    }

    @Override
    public int delete(Context context, SQLiteOpenHelper helper, Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, selection, selectionArgs);

        if (0 != rows) {
            // send event to parent hold context
            context.getContentResolver().notifyChange(uri, null);
            return rows;
        } else {
            throw new SQLiteException("delete error: " + uri);
        }
    }

    @Override
    public Uri insert(Context context, SQLiteOpenHelper helper, Uri uri, ContentValues values) {
        long id = -1;
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            id = db.insertOrThrow(TABLE_NAME, null, values);
            Log.e(TAG, "inser success");
            if (-1 != id) {
                // send event to parent hold context
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return Uri.withAppendedPath(uri, Long.toString(id));
            } else {
                throw new SQLiteException("Insert error:" + uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Uri insert(SQLiteDatabase db, ContentValues values) {
        long id = -1;
        try {
            id = db.insertOrThrow(TABLE_NAME, null, values);
//            Log.e(TAG, "inser success");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int bunkInsert(Context context, SQLiteOpenHelper helper, Uri uri, ContentValues[] values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = 0;
        for (int i = 0; i < values.length; i++) {
            ContentValues value = values[i];
            if (value == null) {
                continue;
            }

            try {
                db.insertOrThrow(TABLE_NAME, null, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        context.getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public Uri getContentUri() {
        return Uri.withAppendedPath(DbProvider.CONTENT_URI, TABLE_NAME);
    }
}
