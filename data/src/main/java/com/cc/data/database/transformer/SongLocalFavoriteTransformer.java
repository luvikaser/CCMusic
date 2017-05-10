package com.cc.data.database.transformer;

import android.content.ContentValues;
import android.database.Cursor;

import com.cc.data.utils.sqlite.SQLiteTransformer;
import com.cc.domain.model.MediaLocalItem;

/**
 * Author: NT
 * Since: 11/8/2016.
 */
public class SongLocalFavoriteTransformer implements SQLiteTransformer<MediaLocalItem> {
    public static final String ID = "_id";
    public static final String MEDIA_ID = "media_id";
    public static final String TITLE = "title";
    public static final String GENRE = "genre";
    public static final String DURATION = "duration";
    public static final String TRACK_NO = "track_no";
    public static final String SUB_TITLE = "sub_title";
    public static final String BITMAP = "bitmap";
    public static final String DESCRIPTION = "description";
    public static final String DATA_PATH = "data_path";

    public static final String TABLE_NAME = "SongLocalFavorite";
    public static final String[] FIELDS = {ID, MEDIA_ID, TITLE, SUB_TITLE, BITMAP, DESCRIPTION,
            DATA_PATH, DURATION, GENRE, TRACK_NO};

    @Override
    public MediaLocalItem transform(Cursor cursor) throws Exception {
        String id = cursor.getString(cursor.getColumnIndex(ID));
        String mediaId = cursor.getString(cursor.getColumnIndex(MEDIA_ID));
        String title = cursor.getString(cursor.getColumnIndex(TITLE));
        String subTitle = cursor.getString(cursor.getColumnIndex(SUB_TITLE));
        String bitmap = cursor.getString(cursor.getColumnIndex(BITMAP));
        String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        String dataPath = cursor.getString(cursor.getColumnIndex(DATA_PATH));
        String genre = cursor.getString(cursor.getColumnIndex(GENRE));
        long duration = cursor.getLong(cursor.getColumnIndex(DURATION));
        long trackNo = cursor.getLong(cursor.getColumnIndex(TRACK_NO));

        MediaLocalItem mediaLocalItem = new MediaLocalItem();
        mediaLocalItem.setId(id);
        mediaLocalItem.setMediaId(mediaId);
        mediaLocalItem.setTitle(title);
        mediaLocalItem.setSubTitle(subTitle);
        mediaLocalItem.setDescription(description);
        mediaLocalItem.setDataPath(dataPath);
        mediaLocalItem.setBitmap(bitmap);
        mediaLocalItem.setDuration(duration);
        mediaLocalItem.setGenre(genre);
        mediaLocalItem.setTrackNo(trackNo);

        return mediaLocalItem;
    }

    @Override
    public ContentValues transform(MediaLocalItem dto) throws Exception {
        ContentValues values = new ContentValues();
        values.put(MEDIA_ID, dto.getMediaId());
        values.put(TITLE, dto.getTitle());
        values.put(SUB_TITLE, dto.getSubTitle());
        values.put(DESCRIPTION, dto.getDescription());
        values.put(DATA_PATH, dto.getDataPath());
        values.put(BITMAP, dto.getBitmap());
        values.put(DURATION, dto.getDuration());
        values.put(GENRE, dto.getGenre());
        values.put(TRACK_NO, dto.getTrackNo());
        return values;
    }

    @Override
    public String getWhereClause(MediaLocalItem dto) throws Exception {
        return MEDIA_ID + "=" + dto.getMediaId();
    }

    @Override
    public MediaLocalItem setId(MediaLocalItem dto, int id) throws Exception {
        dto.setId(String.valueOf(id));
        return null;
    }

    @Override
    public String[] getFields() throws Exception {
        return FIELDS;
    }

    @Override
    public String getTableName() throws Exception {
        return TABLE_NAME;
    }


}