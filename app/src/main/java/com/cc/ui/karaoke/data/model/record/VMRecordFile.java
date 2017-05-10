package com.cc.ui.karaoke.data.model.record;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

import com.cc.ui.karaoke.data.database.table.record.VMRecordTable;

/**
 * Author: NT
 * Since: 9/7/2016.
 */
public class VMRecordFile implements Serializable{
    private int id;
    private String name;
    private String filePath;
    private String youtubeId;
    private String date;

    public VMRecordFile() {

    }

    public VMRecordFile(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(VMRecordTable.COL_ID));
        name = cursor.getString(cursor.getColumnIndex(VMRecordTable.COL_NAME));
        filePath = cursor.getString(cursor.getColumnIndex(VMRecordTable.COL_FILE_PATH));
        youtubeId = cursor.getString(cursor.getColumnIndex(VMRecordTable.COL_YOUTUBE_ID));
        date = cursor.getString(cursor.getColumnIndex(VMRecordTable.COL_DATE_CREATE));
    }

    public static ContentValues getContentValues(VMRecordFile recordFile) {
        ContentValues values = new ContentValues();
        values.put(VMRecordTable.COL_NAME, recordFile.getName());
        values.put(VMRecordTable.COL_FILE_PATH, recordFile.getFilePath());
        values.put(VMRecordTable.COL_YOUTUBE_ID, recordFile.getYoutubeId());
        values.put(VMRecordTable.COL_DATE_CREATE, recordFile.getDate());
        return values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "VMRecordFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", youtubeId='" + youtubeId + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}