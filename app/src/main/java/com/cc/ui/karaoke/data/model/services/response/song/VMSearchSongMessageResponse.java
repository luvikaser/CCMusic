package com.cc.ui.karaoke.data.model.services.response.song;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.cc.ui.karaoke.data.model.services.response.VMAlbum;
import com.cc.ui.karaoke.data.model.services.response.VMSong;
import com.cc.ui.karaoke.data.model.services.response.VMVideo;

/**
 * Author: NT
 * Since: 6/20/2016.
 */
public class VMSearchSongMessageResponse {

    @SerializedName("album")
    private List<VMAlbum> vmAlbumList;

    @SerializedName("video")
    private List<VMVideo> vmVideoList;

    @SerializedName("song")
    private List<VMSong> vmSongList;

    public List<VMAlbum> getVmAlbumList() {
        return vmAlbumList;
    }

    public void setVmAlbumList(List<VMAlbum> vmAlbumList) {
        this.vmAlbumList = vmAlbumList;
    }

    public List<VMVideo> getVmVideoList() {
        return vmVideoList;
    }

    public void setVmVideoList(List<VMVideo> vmVideoList) {
        this.vmVideoList = vmVideoList;
    }

    public List<VMSong> getVmSongList() {
        return vmSongList;
    }

    public void setVmSongList(List<VMSong> vmSongList) {
        this.vmSongList = vmSongList;
    }
}
