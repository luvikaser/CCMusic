package com.cc.ui.karaoke.data.model.services.response.artist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.cc.ui.karaoke.data.model.services.response.base.VMBaseResponse;

/**
 * Author: NT
 * Since: 6/20/2016.
 */
public class VMSearchArtistResponse extends VMBaseResponse{
    @SerializedName("msg")
    private List<VMVMSearchArtistMessageResponse> listMsg;

    public List<VMVMSearchArtistMessageResponse> getListMsg() {
        return listMsg;
    }

    public void setListMsg(List<VMVMSearchArtistMessageResponse> listMsg) {
        this.listMsg = listMsg;
    }
}
