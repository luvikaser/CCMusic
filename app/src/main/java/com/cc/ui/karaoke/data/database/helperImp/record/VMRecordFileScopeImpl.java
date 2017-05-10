package com.cc.ui.karaoke.data.database.helperImp.record;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import com.cc.MusicApplication;
import com.cc.ui.karaoke.data.database.helper.karaoke.VMRecordFileScope;
import com.cc.ui.karaoke.data.database.table.record.VMRecordTable;
import com.cc.ui.karaoke.data.model.record.VMRecordFile;

/**
 * Author: NT
 * Since: 9/8/2016.
 */
public class VMRecordFileScopeImpl implements VMRecordFileScope {
    private ContentResolver contentResolver;
    private Uri mUri;
    public VMRecordFileScopeImpl() {
        contentResolver = MusicApplication.getInstance().getApplicationContext().getContentResolver();
        mUri = VMRecordTable.getInstance().getContentUri();
    }

    @Override
    public VMRecordFile getRecordFile(int id){
        String selection = VMRecordTable.COL_ID + " = ?";
        String[] selectionArg = new String[]{String.valueOf(id)};
        Cursor cursor = contentResolver
                .query(VMRecordTable.getInstance().getContentUri(), null, selection,
                        selectionArg, null);
        if (cursor != null && cursor.moveToFirst()) {
            return new VMRecordFile(cursor);
        }

        return null;
    }

    @Override
    public List<VMRecordFile> getListRecordFile() {
        List<VMRecordFile> list = new ArrayList<>();
        Cursor cursor = contentResolver
                .query(VMRecordTable.getInstance().getContentUri(), null, null, null, null);
        if(cursor == null || !cursor.moveToFirst())
            return null;

        while (!cursor.isAfterLast()) {
            VMRecordFile recordFile = new VMRecordFile(cursor);
            list.add(recordFile);
            cursor.moveToNext();
        }

        return list;
    }

    @Override
    public Cursor getCursorRecordFileTable() {
        String sortOrder = VMRecordTable.COL_ID + " DESC";
        Cursor cursor = contentResolver
                .query(VMRecordTable.getInstance().getContentUri(), null, null, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }

    @Override
    public int insertRecordFile(VMRecordFile recordFile) {
        Uri uri= contentResolver.insert(mUri, VMRecordFile.getContentValues(recordFile));
        if (uri == null) {
            return -1;
        }
        return 0;
    }

    @Override
    public void insertRecordFile(List<VMRecordFile> recordFiles) {
        ContentValues[] values = new ContentValues[recordFiles.size()];

        for (int i =0; i < recordFiles.size(); i++) {
            ContentValues contentValues;
            contentValues = VMRecordFile.getContentValues(recordFiles.get(i));
            values[i] = contentValues;
        }

        contentResolver.bulkInsert(mUri, values);
    }

    @Override
    public void deleteRecordFile(int id) {
        String selection = VMRecordTable.COL_ID + "=?";
        String[] selectionArg = new String[]{String.valueOf(id)};
        contentResolver.delete(mUri, selection, selectionArg);
    }
}