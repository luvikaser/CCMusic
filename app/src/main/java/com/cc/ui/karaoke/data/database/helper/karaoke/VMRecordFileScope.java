package com.cc.ui.karaoke.data.database.helper.karaoke;

import android.database.Cursor;

import java.util.List;

import com.cc.ui.karaoke.data.model.record.VMRecordFile;

/**
 * Author: NT
 * Since: 9/8/2016.
 */
public interface VMRecordFileScope {
    VMRecordFile getRecordFile(int id);

    List<VMRecordFile> getListRecordFile();

    Cursor getCursorRecordFileTable();

    int insertRecordFile(VMRecordFile recordFile);

    void insertRecordFile(List<VMRecordFile> recordFiles);

    void deleteRecordFile(int id);
}
