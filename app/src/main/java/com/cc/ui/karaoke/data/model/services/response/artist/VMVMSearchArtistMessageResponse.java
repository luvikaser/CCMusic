package com.cc.ui.karaoke.data.model.services.response.artist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.cc.ui.karaoke.data.model.services.response.VMSong;

/**
 * Author: NT
 * Since: 6/20/2016.
 */
public class VMVMSearchArtistMessageResponse {
    @SerializedName("total_page")
    private String totalPage;

    @SerializedName("songs")
    private List<VMSong> vmSongList;

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public List<VMSong> getVmSongList() {
        return vmSongList;
    }

    public void setVmSongList(List<VMSong> vmSongList) {
        this.vmSongList = vmSongList;
    }
}
