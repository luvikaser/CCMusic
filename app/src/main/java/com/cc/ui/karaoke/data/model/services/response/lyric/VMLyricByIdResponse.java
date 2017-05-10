package com.cc.ui.karaoke.data.model.services.response.lyric;

import com.google.gson.annotations.SerializedName;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/20/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class VMLyricByIdResponse {
    @SerializedName("error")
    private byte errorMsg;

    @SerializedName("msg")
    private String lyric;



    public String getLyric() {
        return lyric;
    }

    public byte getErrorMsg() {
        return errorMsg;
    }
}
