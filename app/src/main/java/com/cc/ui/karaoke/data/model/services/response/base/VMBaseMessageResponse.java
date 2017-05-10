package com.cc.ui.karaoke.data.model.services.response.base;

import com.google.gson.annotations.SerializedName;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/20/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public abstract class VMBaseMessageResponse {
    @SerializedName("id")
    private String idSong;

    @SerializedName("name")
    private String nameSong;

    public String getIdSong() {
        return idSong;
    }

    public void setIdSong(String idSong) {
        this.idSong = idSong;
    }

    public String getNameSong() {
        return nameSong;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }
}
