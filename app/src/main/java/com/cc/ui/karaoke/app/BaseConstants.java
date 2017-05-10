package com.cc.ui.karaoke.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import mmobile.com.karaoke.R;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class BaseConstants {
    public static final String DATABASE_NAME_SOURCE = "KaraokeVietnam.sqlite";
    public static final String DATABASE_NAME_MAIN = "KaraokeVietnam.sqlite";
    public static final int DATABASE_VERSION = 1;
    public static final int DATABASE_VERSION_SOURCE = 1;

    // preference used to this app
    public static final String PREFERENCE_NAME_APP = "KaraokeVietnam_preference";
    public static final String PRE_DATABASE_VERSION = "database_version";

    public static final int CONNECTION_POOL_COUNT = 1;
    public static final long KEEP_ALIVE_DURATION_MS = 5000l;

    //youtube define
    public static final String YOUTUBE_API_KEY = "AIzaSyAXGePTwN9sPhZ4WhSwH7vwjlcf4Jj6dqc";

    //define action
    public static final String ACTION_MAIN = "action.main";
    public static final String ACTION_INIT = "action.init";
    public static final String ACTION_PREV = "action.prev";
    public static final String ACTION_NEXT = "action.next";
    public static final String ACTION_PLAY = "action.play";
    public static final String ACTION_START_FOREGROUND_ACTION = "action.start.foreground";
    public static final String ACTION_STOP_FOREGROUND_ACTION = "action.stop.foreground";

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_art,
                options);

        return bm;
    }
}
