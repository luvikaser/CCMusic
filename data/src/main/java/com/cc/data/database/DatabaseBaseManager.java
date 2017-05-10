package com.cc.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cc.data.MusicConstantsApp;
import com.cc.data.database.transformer.SongLocalFavoriteTransformer;
import com.cc.data.database.transformer.SongLocalPlayedTransformer;
import com.cc.data.utils.sqlite.SQLiteHelper;

/**
 * Author: NT
 * Since: 11/8/2016.
 */
public class DatabaseBaseManager {
    private static final String TAG = DatabaseBaseManager.class.getSimpleName();
    private static final String SONG_LOCAL_FAVORITE_TABLE =
            "CREATE TABLE " + SongLocalFavoriteTransformer.TABLE_NAME + "(" +
                    SongLocalFavoriteTransformer.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SongLocalFavoriteTransformer.MEDIA_ID + " TEXT NOT NULL," +
                    SongLocalFavoriteTransformer.TITLE + " TEXT NOT NULL," +
                    SongLocalFavoriteTransformer.SUB_TITLE + " TEXT NOT NULL," +
                    SongLocalFavoriteTransformer.DESCRIPTION + " TEXT NOT NULL," +
                    SongLocalFavoriteTransformer.GENRE + " TEXT NOT NULL," +
                    SongLocalFavoriteTransformer.DURATION + " INTEGER DEFAULT 0," +
                    SongLocalFavoriteTransformer.TRACK_NO + " INTEGER DEFAULT 0," +
                    SongLocalFavoriteTransformer.BITMAP + " TEXT NOT NULL," +
                    SongLocalFavoriteTransformer.DATA_PATH + " TEXT NOT NULL)";

    private static final String SONG_LOCAL_PLAYED_RECENT_TABLE =
            "CREATE TABLE " + SongLocalPlayedTransformer.TABLE_NAME + "(" +
                    SongLocalPlayedTransformer.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SongLocalPlayedTransformer.MEDIA_ID + " TEXT NOT NULL," +
                    SongLocalPlayedTransformer.TITLE + " TEXT NOT NULL," +
                    SongLocalPlayedTransformer.SUB_TITLE + " TEXT NOT NULL," +
                    SongLocalPlayedTransformer.DESCRIPTION + " TEXT NOT NULL," +
                    SongLocalPlayedTransformer.GENRE + " TEXT NOT NULL," +
                    SongLocalPlayedTransformer.DURATION + " INTEGER DEFAULT 0," +
                    SongLocalPlayedTransformer.TRACK_NO + " INTEGER DEFAULT 0," +
                    SongLocalPlayedTransformer.TIME_CREATED + " INTEGER DEFAULT 0," +
                    SongLocalPlayedTransformer.BITMAP + " TEXT NOT NULL," +
                    SongLocalPlayedTransformer.ID_MP3 + " TEXT NOT NULL," +
                    SongLocalPlayedTransformer.PATH_LYRIC + " TEXT NOT NULL," +
                    SongLocalPlayedTransformer.DATA_PATH + " TEXT NOT NULL)";

    final static private String[] TABLES = {SONG_LOCAL_FAVORITE_TABLE, SONG_LOCAL_PLAYED_RECENT_TABLE};
    final static private String[] TABLE_NAMES = {SongLocalFavoriteTransformer.TABLE_NAME,
            SongLocalPlayedTransformer.TABLE_NAME};

    private Context mContext;
    private SQLiteDatabase mSqLiteDatabase;

    public DatabaseBaseManager(Context context) {
        this.mContext = context;
    }

    public SQLiteDatabase getDatabase() {
        if (mSqLiteDatabase == null) {
            SQLiteHelper sqLiteHelper = SQLiteHelper.builder()
                    .beginConfig()
                    .onCreateCallback(onCreateCallback)
                    .foreignKey(true)
                    .endConfig()
                    .tables(TABLES)
                    .tableNames(TABLE_NAMES)
                    .version(MusicConstantsApp.DATABASE_VERSION)
                    .name(MusicConstantsApp.DATABASE_NAME)
                    .build(mContext);

            mSqLiteDatabase = sqLiteHelper.getWritableDatabase();
        }

        return mSqLiteDatabase;
    }

    private static SQLiteHelper.OnCreateCallback onCreateCallback = new SQLiteHelper.OnCreateCallback() {
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "onCreateCallback");
            db.execSQL(SONG_LOCAL_FAVORITE_TABLE);
            db.execSQL(SONG_LOCAL_PLAYED_RECENT_TABLE);
        }
    };
}