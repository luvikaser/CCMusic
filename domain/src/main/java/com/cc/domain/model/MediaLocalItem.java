package com.cc.domain.model;

import android.support.v4.media.MediaMetadataCompat;

import java.util.List;

/**
 * Author: NT
 * Since: 11/8/2016.
 * <p>
 * .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(id))
 * .putString(CUSTOM_METADATA_TRACK_SOURCE, dataPath)
 * .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
 * .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
 * .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
 * .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
 * .putString(MediaMetadataCompat.METADATA_KEY_GENRE, GENRE)
 * .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "")
 * .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
 * .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNo)
 */
public class MediaLocalItem {
    private String id;
    private String mediaId;
    private String dataPath;
    private String genre;
    private String title;
    private String subTitle;
    private String description;
    private String bitmap;
    private long trackNo;
    private long duration;
    private long timeCreate;
    private String idMp3;
    private String pathLyric;
    public String getId() {
        return id;
    }



    public void setId(String id) {
        this.id = id;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public long getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(long trackNo) {
        this.trackNo = trackNo;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(long timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getIdMp3() {
        return idMp3;
    }

    public void setIdMp3(String idMp3) {
        this.idMp3 = idMp3;
    }

    public String getPathLyric() {
        return pathLyric;
    }

    public void setPathLyric(String pathLyric) {
        this.pathLyric = pathLyric;
    }

    public MediaLocalItem() {

    }

    public MediaLocalItem(MediaMetadataCompat mediaDescriptionCompat) {
        mediaId = mediaDescriptionCompat.getDescription().getMediaId();
        title = (String) mediaDescriptionCompat.getDescription().getTitle();
        subTitle = (String) mediaDescriptionCompat.getDescription().getSubtitle();
        description =  mediaDescriptionCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION);
        dataPath = (String) mediaDescriptionCompat.getString("__SOURCE__");
        duration = (int) mediaDescriptionCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        genre = mediaDescriptionCompat.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
        trackNo = mediaDescriptionCompat.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER);
        bitmap = "no";
        idMp3 = "";
        pathLyric = "";
    }

    @Override
    public String toString() {
        return "MediaLocalItem{" +
                "id='" + id + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", dataPath='" + dataPath + '\'' +
                ", genre='" + genre + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", description='" + description + '\'' +
                ", bitmap='" + bitmap + '\'' +
                ", trackNo=" + trackNo +
                ", duration=" + duration +
                ", timeCreate=" + timeCreate +
                ", idMp3='" + idMp3 + '\'' +
                ", pathLyric='" + pathLyric + '\'' +
                '}';
    }
}