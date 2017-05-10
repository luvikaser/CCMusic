package com.cc.ui.karaoke.data.model.services.response;

import com.google.gson.annotations.SerializedName;

import com.cc.ui.karaoke.data.model.services.response.base.VMBaseMessageResponse;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/20/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class VMAlbum extends VMBaseMessageResponse{
    @SerializedName("thumb")
    private String thumb;

    @SerializedName("artist")
    private String artist;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
