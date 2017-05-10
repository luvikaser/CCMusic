package com.cc.ui.karaoke.data.database.table.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/18/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public abstract class VMBaseTable {
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * create table
     * @param db used to get database object for crate table
     */
    public abstract void onCreate(SQLiteDatabase db);

    /**
     *
     * @param database  used to get object write into table
     * @param oldVersion used get old version of database
     * @param newVersion used to get new version of tabase
     */
    public abstract void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);

    /**
     * @param context used to get Android system
     * @param helper used to write to table
     * @param uri used to get provider current in app
     * @param selection used to get where clause
     * @param selectionArgs used to set values into where clase
     * @param sortOrder used to sort for result request
     * @return cursor query from table
     */
    public abstract Cursor query(Context context, SQLiteOpenHelper helper, Uri uri,
                                 String[] projection, String selection, String[]
                                         selectionArgs, String sortOrder);

    /**
     *
     * @param context used to get Android system
     * @param helper used to write to table
     * @param uri used to get provider current in app
     * @param values used to get value update to this row
     * @param selection used to get where clause
     * @param selectionArgs used to set values into where clase
     * @return result update into table
     */
    public abstract int update(Context context, SQLiteOpenHelper helper,
                               Uri uri, ContentValues values, String selection,
                               String[] selectionArgs);

    /**
     *
     * @param context used to get Android system
     * @param helper used to write to table
     * @param uri used to get provider current in app
     * @param selection used to get where clause
     * @param selectionArgs used to set values into where clase
     * @return result delete row in to table
     */
    public abstract int delete(Context context, SQLiteOpenHelper helper,
                               Uri uri, String selection,
                               String[] selectionArgs);


    /**
     * insert into a row table
     * @param context used to get Android system
     * @param helper used to write to table
     * @param uri used to get provider current in app
     * @param values used to get values need to insert
     * @return result insert into table
     */
    public abstract Uri insert(Context context, SQLiteOpenHelper helper, Uri uri,
                               ContentValues values);

    /**
     *
     * @param db used to get object write into table
     * @param values used to get values for row
     * @return result insert into table
     */
    public abstract Uri insert(SQLiteDatabase db,
                               ContentValues values);

    /**
     * insert array a row into table
     * @param context used to get Android system
     * @param helper used to write to table
     * @param uri used to get provider current in app
     * @param values used to get values need to insert
     * @return result insert into table
     */
    public abstract int bunkInsert(Context context, SQLiteOpenHelper helper, Uri uri,
                                   ContentValues[] values);

    /**
     * get content uri for provider db in app
     * @return
     */
    public abstract Uri getContentUri();

}
