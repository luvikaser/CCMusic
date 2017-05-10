package com.cc.event;

import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

/**
 * Author: NT
 * Since: 11/19/2016.
 */
public class ListSongChildEvent {
    public List<MediaBrowserCompat.MediaItem> children;
    public String parentId;

    public boolean isUpdateStateItem = false;

    public ListSongChildEvent(List<MediaBrowserCompat.MediaItem> children, String parentId) {
        this.children = children;
        this.parentId = parentId;
    }

    public ListSongChildEvent(boolean isUpdateStateItem) {
        this.isUpdateStateItem = isUpdateStateItem;
    }


}