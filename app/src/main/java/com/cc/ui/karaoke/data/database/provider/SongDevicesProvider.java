package com.cc.ui.karaoke.data.database.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.cc.MusicApplication;
import com.cc.di.components.ApplicationComponent;
import com.cc.ui.karaoke.data.model.local.SongDevicesData;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class SongDevicesProvider {
    private static final String TAG = SongDevicesProvider.class.getSimpleName();
    private static Uri mediaUriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static Uri mediaUriInternal = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
    private static Uri thumbnailUriInternal = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
    private ArrayList<SongDevicesData> songList = new ArrayList<>();
    private OnUpdateProgressLoadData listener;

    public SongDevicesProvider(OnUpdateProgressLoadData listener) {
        this.listener = listener;
    }

    public Cursor getCursorMediaSong() {
        final String musicsOnly = MediaStore.Audio.Media.IS_MUSIC + "=1";
        String sortOrder = android.provider.MediaStore.Audio.Media.TITLE + " ASC";
        ContentResolver musicResolver = MusicApplication.getInstance().getApplicationContext().getContentResolver();
        Cursor musicCursor = musicResolver.query(mediaUriExternal, null, musicsOnly, null, sortOrder);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            return musicCursor;
        }

        return null;
    }

    /**
     * get list music from media store of devices
     *
     * @return list object music custom
     */
    public void getListMediaStores() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ContentResolver musicResolver = MusicApplication.getInstance().getApplicationContext().getContentResolver();
                Cursor musicCursor = musicResolver.query(mediaUriExternal, null, null, null, null);
                int total;
                int unitLoaded = 0;
                if (musicCursor != null && musicCursor.moveToFirst()) {
                    total = musicCursor.getCount();
                    //get columns
                    int titleColumn = musicCursor.getColumnIndex
                            (android.provider.MediaStore.Audio.Media.TITLE);
                    int idColumn = musicCursor.getColumnIndex
                            (android.provider.MediaStore.Audio.Media._ID);
                    int artistColumn = musicCursor.getColumnIndex
                            (android.provider.MediaStore.Audio.Media.ARTIST);
                    int albumColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.ALBUM);
                    int albumIdColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.ALBUM_ID);
                    int dataPathColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.DATA);
                    int yearColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.YEAR);
                    int durationColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.DURATION);
                    int trackNoColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.TRACK);
                    int dateAddedColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.DATE_ADDED);

                    Double percentage;
                    //add songs to list
                    do {
                        unitLoaded++;
                        String dataPath = musicCursor.getString(dataPathColumn);
                        percentage = (((double) unitLoaded) / total) * 100;
                        updateProgress.sendEmptyMessage(percentage.intValue());
                        if (dataPath.contains("vuigame") || dataPath.contains("100in1games")
                                || dataPath.contains("com.google.android.talk") || dataPath.contains("mePlay"))
                            continue;

                        long id = musicCursor.getLong(idColumn);
                        long albumId = musicCursor.getLong(albumIdColumn);
                        long duration = musicCursor.getLong(durationColumn);

                        String title = musicCursor.getString(titleColumn);
                        String artist = musicCursor.getString(artistColumn);
                        String album = musicCursor.getString(albumColumn);
                        String year = musicCursor.getString(yearColumn);
                        String trackNo = musicCursor.getString(trackNoColumn);

                        SongDevicesData ms = new SongDevicesData();
                        ms.setId(id);
                        ms.setAlbumId(albumId);
                        ms.setDuration(duration);
                        ms.setTitle(title);
                        ms.setArtist(artist);
                        ms.setAlbum(album);
                        ms.setDataPath(dataPath);
                        ms.setYear(year);
                        ms.setTrack(trackNo);
                   /*     ms.setThumbnail(getThumbnail(musicCursor.getColumnIndex(MediaStore
                                .Images.Media._ID)));*/
                        songList.add(ms);
                    }
                    while (musicCursor.moveToNext());
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * get list music from media store of devices
     *
     * @return list object music custom
     */
    public ArrayList<SongDevicesData> getListMediaStore() {
        ArrayList<SongDevicesData> songList = new ArrayList<>();
        ContentResolver musicResolver = MusicApplication.getInstance().getApplicationContext().getContentResolver();
        Cursor musicCursor = musicResolver.query(mediaUriExternal, null, null, null, null);
        int total;
        int unitLoaded = 0;
        if (musicCursor != null && musicCursor.moveToFirst()) {
            total = musicCursor.getCount();
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int dataPathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int yearColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.YEAR);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int trackNoColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TRACK);
            int dateAddedColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATE_ADDED);

            Double percentage;
            //add songs to list
            do {
                unitLoaded++;
                String dataPath = musicCursor.getString(dataPathColumn);
                Log.d(TAG, "unitLoaded =  " + unitLoaded);
                Log.d(TAG, "total =  " + total);
                percentage = ((double) (unitLoaded / total)) *100;
                listener.onUpdateProgress(percentage.intValue());
                if (dataPath.contains("vuigame") || dataPath.contains("100in1games")
                        || dataPath.contains("com.google.android.talk") || dataPath.contains("mePlay"))
                    continue;

                long id = musicCursor.getLong(idColumn);
                long albumId = musicCursor.getLong(albumIdColumn);
                long duration = musicCursor.getLong(durationColumn);

                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                String year = musicCursor.getString(yearColumn);
                String trackNo = musicCursor.getString(trackNoColumn);

                SongDevicesData ms = new SongDevicesData();
                ms.setId(id);
                ms.setAlbumId(albumId);
                ms.setDuration(duration);
                ms.setTitle(title);
                ms.setArtist(artist);
                ms.setAlbum(album);
                ms.setDataPath(dataPath);
                ms.setYear(year);
                ms.setTrack(trackNo);
                ms.setThumbnail(getThumbnail(musicCursor.getColumnIndex(MediaStore
                        .Images.Media._ID)));
                songList.add(ms);
            }
            while (musicCursor.moveToNext());
        }

        if (songList.size() > 0) {
            Collections.sort(songList, new Comparator<SongDevicesData>() {
                @Override
                public int compare(SongDevicesData lhs, SongDevicesData rhs) {
                    return lhs.getTitle().compareTo(rhs.getTitle());
                }
            });
        }

        listener.onLoadDataFinish(songList);
        return songList;

    }



    /**
     * get bitmap for song
     *
     * @param id
     * @return
     */
    private static Bitmap getThumbnail(long id) {
        String thumb_DATA = MediaStore.Images.Thumbnails.DATA;
        String thumb_IMAGE_ID = MediaStore.Images.Thumbnails._ID;
        String[] thumbColumns = {thumb_DATA, thumb_IMAGE_ID};

        CursorLoader thumbCursorLoader = new CursorLoader(
                MusicApplication.getInstance().getApplicationContext(),
                thumbnailUriInternal,
                thumbColumns,
                thumb_IMAGE_ID + "=" + id,
                null,
                null);

        Cursor thumbCursor = thumbCursorLoader.loadInBackground();

        Bitmap thumbBitmap = null;
        if (thumbCursor.moveToFirst()) {
            int thCulumnIndex = thumbCursor.getColumnIndex(thumb_DATA);

            String thumbPath = thumbCursor.getString(thCulumnIndex);
            Log.d(TAG, "thumbPath = " + thumbPath);

            thumbBitmap = BitmapFactory.decodeFile(thumbPath);


        } else {
            Log.d(TAG, "No thumbnail");
        }

        return thumbBitmap;
    }

    Handler updateProgress = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int percent = msg.what;
            listener.onUpdateProgress(percent);
            if (percent == 100) {
                if (songList.size() > 0) {
                    Collections.sort(songList, new Comparator<SongDevicesData>() {
                        @Override
                        public int compare(SongDevicesData lhs, SongDevicesData rhs) {
                            return lhs.getTitle().compareTo(rhs.getTitle());
                        }
                    });
                }
                listener.onLoadDataFinish(songList);
            }
        }
    };

    public interface OnUpdateProgressLoadData {
        void onUpdateProgress(int progress);

        void onLoadDataFinish(ArrayList<SongDevicesData> datas);
    }
}
