package com.cc.ui.karaoke.data.model.services.response.song;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.cc.ui.karaoke.data.model.services.response.base.VMBaseResponse;

/**
 * Author: NT
 * Since: 6/20/2016.
 */
public class VMSearchSongResponse extends VMBaseResponse {
    @SerializedName("msg")
    private List<VMSearchSongMessageResponse> listMsg;


    public List<VMSearchSongMessageResponse> getListMsg() {
        return listMsg;
    }

    public void setListMsg(List<VMSearchSongMessageResponse> listMsg) {
        this.listMsg = listMsg;
    }
}
