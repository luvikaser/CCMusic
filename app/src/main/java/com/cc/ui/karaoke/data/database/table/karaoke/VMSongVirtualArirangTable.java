package com.cc.ui.karaoke.data.database.table.karaoke;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.cc.ui.karaoke.data.database.provider.DbProvider;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongVirtualArirangTable {
    private static final String TAG = VMSongVirtualArirangTable.class.getSimpleName();

    public static final String TABLE_NAME = "ZSONGVIRTUAL_ARIRANG";
    public static Uri CONTENT_URI = Uri.withAppendedPath(DbProvider.CONTENT_URI, TABLE_NAME);

    public static final String KEY_COL_SEARCH = "key_search";
    public static final String KEY_COL_RESULT = "pk_id";
    public static final String KEY_MANUFACTURE = "manufature_id";

    public static String QUERY_CREATE_TABLE =
            "CREATE VIRTUAL TABLE `ZSONGVIRTUAL_ARIRANG` USING FTS3 ( "
                    + KEY_COL_SEARCH + " TEXT NOT NULL, "
                    + KEY_COL_RESULT + " INTEGER PRIMARY KEY)";

    public static final String SELECTION_SEARCH =
            VMSongArirangTable.PK_COLUMN
            + " IN (SELECT "
            + KEY_COL_RESULT
            + " FROM " + TABLE_NAME
            + " AS a WHERE a."
            + KEY_COL_SEARCH
            + " MATCH  %s)";

    public static void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(QUERY_CREATE_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * will Upgrade db when change version
     *
     * @param database
     * @param oldVersion
     * @param newVersion
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

    /**
     * CHECK EXIST COLUMN DOR AVOID CRASH APP AFTER CHANGE DATABASE
     *
     * @param db
     * @param columnName
     * @return
     */
    private static boolean checkColumnExist(SQLiteDatabase db, String columnName) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 0", null);
        int value = cursor.getColumnIndex(columnName);
        if (value == -1) {
            return false;// no exist
        }

        return true;
    }


    public static Cursor query(Context context, SQLiteOpenHelper helper, Uri uri,
                               String[] projection, String selection, String[]
                                       selectionArgs, String sortOrder) {
        Log.d(TAG, "query");
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs,
                null, null, sortOrder);
//        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    public static Cursor queryRaw(Context context, SQLiteOpenHelper helper, Uri uri,
                                  String[] projection, String selection, String[]
                                          selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(selection, null);
//        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    /**
     * insert data into db
     *
     * @param context from activity
     * @param helper  used to Write db
     * @param uri     used to return
     * @param values  used to set data
     * @return uri
     */
    public static Uri insert(Context context, SQLiteOpenHelper helper, Uri uri,
                             ContentValues values) {
        long id = -1;
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            id = db.insertOrThrow(TABLE_NAME, null, values);
            Log.e(TAG, "insert success");
            if (-1 != id) {
                // send event to parent hold context
                if (context != null) {
//                    context.getContentResolver().notifyChange(uri, null);
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

    public static Uri insertQuickly(SQLiteOpenHelper helper, ContentValues values, Uri uri) {
        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(helper.getWritableDatabase(), TABLE_NAME);
        int colSearch = ih.getColumnIndex(KEY_COL_SEARCH);
        int colResult = ih.getColumnIndex(KEY_COL_RESULT);
        try {
            ih.prepareForInsert();
            ih.bind(colSearch, values.getAsString(KEY_COL_SEARCH));
            ih.bind(colResult, values.getAsString(KEY_COL_RESULT));
            long id = ih.execute();
            Log.d(TAG, "insertQuickly");
            ih.close();
            return Uri.withAppendedPath(uri, Long.toString(id));
         } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ih.close();
        }

        return null;
    }

    public static Uri insert(SQLiteDatabase db,
                             ContentValues values) {
        long id = -1;
        try {
            id = db.insertOrThrow(TABLE_NAME, null, values);
//            Log.e(TAG, "inser success");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * update data into db
     *
     * @param context from activity
     * @param helper  used to Write db
     * @param uri     used to return
     * @param values  used to set data
     * @return uri
     */
    public static int update(Context context, SQLiteOpenHelper helper,
                             Uri uri, ContentValues values, String selection,
                             String[] selectionArgs) {
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

    /**
     * delete row in db
     *
     * @param context from activity
     * @param helper  used to Write db
     * @param uri     used to return
     * @return uri
     */
    public static int delete(Context context, SQLiteOpenHelper helper,
                             Uri uri, String selection,
                             String[] selectionArgs) {
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

    public static int bunkInsert(Context context, SQLiteOpenHelper helper, Uri uri,
                                 ContentValues[] values) {
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
//        context.getContentResolver().notifyChange(uri, null);
        return rows;
    }

}
