package com.cc.ui.search;

import com.cc.domain.model.SongBySongApi;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Author: NT
 * Since: 12/3/2016.
 */
public class SearchOnlineManager {
    private List<SongBySongApi.Song> songSearchOnline = new ArrayList<>();
    private List<SongBySongApi.Artist> artistSearchOnline = new ArrayList<>();
    private List<SongBySongApi.Album> albumSearchOnline= new ArrayList<>();

    @Inject
    public SearchOnlineManager() {

    }

    public List<SongBySongApi.Song> getSongListBySong() {
        return songSearchOnline;
    }

    public void setSongSearchOnline(List<SongBySongApi.Song> songListBySong) {
        songSearchOnline.clear();
        this.songSearchOnline = songListBySong;
    }

    public List<SongBySongApi.Artist> getSongListByArtist() {
        return artistSearchOnline;
    }

    public void setArtistSearchOnline(List<SongBySongApi.Artist> songListByArtist) {
        artistSearchOnline.clear();
        this.artistSearchOnline = songListByArtist;
    }

    public List<SongBySongApi.Album> getAlbumSearchOnline() {
        return albumSearchOnline;
    }

    public void setAlbumSearchOnline(List<SongBySongApi.Album> albumSearchOnline) {
        this.albumSearchOnline = albumSearchOnline;
    }
}