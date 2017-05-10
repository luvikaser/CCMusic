package com.cc.ui.karaoke.data.database.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cc.MusicApplication;
import com.cc.ui.karaoke.app.BaseConstants;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class AssetDatabaseOpenHelper {

    private static final String DB_NAME = BaseConstants.DATABASE_NAME_MAIN;
    private static final String TAG = "AssetDatabaseOpenHelper";
    private String packageName = "";
    private Context context;

    public AssetDatabaseOpenHelper(Context context) {
        this.context = context;
        this.packageName = context.getPackageName();
    }

    public void openDatabase(OnOpenAssetDatabaseListener listener) {
        if (readVersionDatabase() >= BaseConstants.DATABASE_VERSION) {
            listener.onCopyAlready();
            return;
        }
        String pathFile = "data/data/" + packageName + "/databases/";
        File dbFile = new File(pathFile, DB_NAME);
       boolean isResult = dbFile.getParentFile().mkdirs();
        try {
            copyDatabase(dbFile);
        } catch (IOException e) {
            listener.onCopyError("Error creating source database");
            return;
        }
        Log.e(TAG, "dbFile.getPath() is " + dbFile.getPath());

        listener.onCopySuccess(SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                SQLiteDatabase.OPEN_READONLY));
    }

    private void copyDatabase(File dbFile) throws IOException {
        InputStream is = context.getAssets().open(DB_NAME);
        OutputStream os = new FileOutputStream(dbFile);

        byte[] buffer = new byte[1024];
        while (is.read(buffer) > 0) {
            os.write(buffer);
        }

        os.flush();
        os.close();
        is.close();
    }


    // read database version
    public static int readVersionDatabase() {
        return MusicApplication.getInstance().getSharedPreferences().getInt(BaseConstants.PRE_DATABASE_VERSION, 0);//
        // default = 0;
    }

    // save database version
    public static void saveVersionDatabase() {
        SharedPreferences.Editor editor = MusicApplication.getInstance().getSharedPreferences().edit();
        editor.putInt(BaseConstants.PRE_DATABASE_VERSION, BaseConstants.DATABASE_VERSION);
        editor.apply();
    }

    public interface OnOpenAssetDatabaseListener {
        void onCopySuccess(SQLiteDatabase db);

        void onCopyAlready();

        void onCopyError(String error);
    }

}
