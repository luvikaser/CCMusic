package com.cc.ui.karaoke.data.database.helper.karaoke;

import android.database.Cursor;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/18/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public interface VMVolScope extends VMBaseSongScope {
    Cursor getVolsWithManufactureId(int value);
}
