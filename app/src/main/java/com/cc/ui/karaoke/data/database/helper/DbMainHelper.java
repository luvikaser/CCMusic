package com.cc.ui.karaoke.data.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cc.ui.karaoke.app.BaseConstants;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongArirangTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongCaliforniaTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongFavoriteTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongMusicCoreTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongVietKtvTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongVirtualArirangTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongVirtualCalifornialTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongVirtualCoreMusicTable;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongVirtualVietKtvTable;
import com.cc.ui.karaoke.data.database.table.record.VMRecordTable;


/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class DbMainHelper extends SQLiteOpenHelper {
    public DbMainHelper(Context context) {
        super(context, BaseConstants.DATABASE_NAME_MAIN, null, BaseConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        VMSongVirtualArirangTable.onCreate(db);
        VMSongVirtualCalifornialTable.onCreate(db);
        VMSongVirtualCoreMusicTable.onCreate(db);
        VMSongVirtualVietKtvTable.onCreate(db);
        VMSongArirangTable.getInstance().onCreate(db);
        VMSongCaliforniaTable.getInstance().onCreate(db);
        VMSongMusicCoreTable.getInstance().onCreate(db);
        VMSongVietKtvTable.getInstance().onCreate(db);
        VMSongFavoriteTable.getInstance().onCreate(db);
        VMRecordTable.getInstance().onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        VMSongArirangTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMSongCaliforniaTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMSongMusicCoreTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMSongVietKtvTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMSongVirtualArirangTable.onUpgrade(db, oldVersion, newVersion);
        VMSongFavoriteTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMRecordTable.getInstance().onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        VMSongArirangTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMSongCaliforniaTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMSongMusicCoreTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMSongVietKtvTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMSongVirtualArirangTable.onUpgrade(db, oldVersion, newVersion);
        VMSongFavoriteTable.getInstance().onUpgrade(db, oldVersion, newVersion);
        VMRecordTable.getInstance().onUpgrade(db, oldVersion, newVersion);
    }
}
