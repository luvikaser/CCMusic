package com.cc.ui.karaoke.data.model.services.response;

import com.google.gson.annotations.SerializedName;

import com.cc.ui.karaoke.data.model.services.response.base.VMBaseMessageResponse;

/**
 * Author: NT
 * Since: 6/20/2016.
 */
public class VMArtist extends VMBaseMessageResponse {
    @SerializedName("thumb")
    private String thumb;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

}
