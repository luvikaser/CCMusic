package com.cc.ui.karaoke.data.database.table.karaoke;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.cc.ui.karaoke.data.database.provider.DbProvider;
import com.cc.ui.karaoke.data.database.table.base.VMBaseTable;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongCaliforniaTable extends VMBaseTable {

    public static final String TABLE_NAME =  "ZSONG_CALIFORNIA";
    public static Uri CONTENT_URI = Uri.withAppendedPath(DbProvider.CONTENT_URI, TABLE_NAME);

    public static final String PK_COLUMN = "Z_PK";
    public static final String ENT_COLUMN = "Z_ENT";
    public static final String OPT_COLUMN = "Z_OPT";
    public static final String ROWID_COLUMN = "ZROWID";
    public static final String SVOL_COLUMN = "ZSVOL";
    public static final String SABBR_COLUMN = "ZSABBR";
    public static final String SLANGUAGE_COLUMN = "ZSLANGUAGE";
    public static final String SLYRIC_COLUMN = "ZSLYRIC";
    public static final String SLYRICCLEAN_COLUMN = "ZSLYRICCLEAN";
    public static final String SMANUFACTURE_COLUMN = "ZSMANUFACTURE";
    public static final String SMETA_COLUMN = "ZSMETA";
    public static final String SMETACLEAN_COLUMN = "ZSMETACLEAN";
    public static final String SNAME_COLUMN = "ZSNAME";
    public static final String SNAMECLEAN_COLUMN = "ZSNAMECLEAN";
    public static final String SFAVORITE_COLUMN = "ZISFAVORITE";
    public static final String ZYOUTUBE_COLUMN = "ZYOUTUBE";

    public static String QUERY_CREATE_TABLE = "CREATE TABLE`ZSONG_ARIRANG` (\n" +
            "\t`Z_PK`\tINTEGER,\n"+
            "\t`Z_ENT`\tINTEGER,\n"+
            "\t`Z_OPT`\tINTEGER,\n"+
            "\t`ZROWID`\tINTEGER,\n"+
            "\t`ZSVOL`\tINTEGER,\n"+
            "\t`ZSABBR`\tVARCHAR,\n"+
            "\t`ZSLANGUAGE`\tVARCHAR,\n"+
            "\t`ZSLYRIC`\tVARCHAR,\n"+
            "\t`ZSLYRICCLEAN`\tVARCHAR,\n"+
            "\t`ZSMANUFACTURE`\tVARCHAR,\n"+
            "\t`ZSMETA`\tVARCHAR,\n"+
            "\t`ZSMETACLEAN`\tVARCHAR,\n"+
            "\t`ZSNAME`\tVARCHAR,\n"+
            "\t`ZSNAMECLEAN`\tVARCHAR,\n"+
            "\t`ZYOUTUBE`\tVARCHAR,\n"+
            "\tPRIMARY KEY(Z_PK)\n"+
            ");";

    private static VMSongCaliforniaTable instance = null;

    public static VMSongCaliforniaTable getInstance() {
        if (instance == null) {
            instance = new VMSongCaliforniaTable();
        }

        return instance;
    }

    /**
     * create table
     *
     * @param db used to get database object for crate table
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(QUERY_CREATE_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param database   used to get object write into table
     * @param oldVersion used get old version of database
     * @param newVersion used to get new version of tabase
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

    /**
     * @param context       used to get Android system
     * @param helper        used to write to table
     * @param uri           used to get provider current in app
     * @param projection
     * @param selection     used to get where clause
     * @param selectionArgs used to set values into where clase
     * @param sortOrder     used to sort for result request    @return cursor query from table
     */
    @Override
    public Cursor query(Context context, SQLiteOpenHelper helper, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs,
                null, null, sortOrder);
//        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    /**
     * @param context       used to get Android system
     * @param helper        used to write to table
     * @param uri           used to get provider current in app
     * @param values        used to get value update to this row
     * @param selection     used to get where clause
     * @param selectionArgs used to set values into where clase
     * @return result update into table
     */
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

    /**
     * @param context       used to get Android system
     * @param helper        used to write to table
     * @param uri           used to get provider current in app
     * @param selection     used to get where clause
     * @param selectionArgs used to set values into where clase
     * @return result delete row in to table
     */
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
        }    }

    /**
     * insert into a row table
     *
     * @param context used to get Android system
     * @param helper  used to write to table
     * @param uri     used to get provider current in app
     * @param values  used to get values need to insert
     * @return result insert into table
     */
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

    /**
     * @param db     used to get object write into table
     * @param values used to get values for row
     * @return result insert into table
     */
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

    /**
     * insert array a row into table
     *
     * @param context used to get Android system
     * @param helper  used to write to table
     * @param uri     used to get provider current in app
     * @param values  used to get values need to insert
     * @return result insert into table
     */
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

    /**
     * get content uri for provider db in app
     *
     * @return
     */
    @Override
    public Uri getContentUri() {
        return Uri.withAppendedPath(DbProvider.CONTENT_URI, TABLE_NAME);
    }

}
