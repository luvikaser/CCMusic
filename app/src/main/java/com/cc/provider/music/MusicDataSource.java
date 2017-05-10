/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cc.provider.music;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.cc.MusicApplication;
import com.cc.data.MusicEnumApp;
import com.cc.domain.model.MediaLocalItem;
import com.cc.domain.model.SongBySongApi;
import com.cc.domain.utils.LogHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class to get a list of MusicTrack's based on a server-side JSON
 * configuration.
 */
public class MusicDataSource implements MusicProviderSource {

    private static final String TAG = LogHelper.makeLogTag(MusicDataSource.class);

    @Override
    public Iterator<MediaMetadataCompat> iterator(MusicEnumApp.MusicType musicType) {
        try {
            Log.e(TAG, "iterator(MusicEnumApp.MusicType musicType)");
            if (musicType.type == MusicEnumApp.MusicType.FAVORITE.type || musicType
                    .type == MusicEnumApp.MusicType.PLAYED.type) {
                return getListMediaStoreByDB(musicType).iterator();
            } else if (musicType.type == MusicEnumApp.MusicType.ARTIST_ONLINE.type || musicType
                    .type == MusicEnumApp.MusicType.SONG_ONLINE.type || musicType.type == MusicEnumApp.MusicType.ALBUM_ONLINE.type) {
                return getListMediaSearchOnline(musicType).iterator();
            } else {
                return getListMediaStoreByProvider(musicType).iterator();
            }

        } catch (Exception e) {
            LogHelper.e(TAG, e, "Could not retrieve music list");
            throw new RuntimeException("Could not retrieve music list", e);
        }
    }

    public ArrayList<MediaMetadataCompat> getListMediaSearchOnline(MusicEnumApp.MusicType musicType) {
        Log.e(TAG, "getListMediaSearchOnline");
        ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
        if (musicType.type == MusicEnumApp.MusicType.SONG_ONLINE.type) {

            try {
                //TODO: READ SONG SEARCH ONLINE
                List<SongBySongApi.Song> listSong = MusicApplication
                        .getInstance().getUserComponent().searchOnlineManager().getSongListBySong();
                for (SongBySongApi.Song mediaLocalItem : listSong) {
                    Log.e(TAG, "mediaLocalItem = " + mediaLocalItem.toString());
                     tracks.add(new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaLocalItem.idSong)
                            .putString(CUSTOM_METADATA_TRACK_SOURCE, "http://www.stephaniequinn.com/Music/Rondeau.mp3")
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaLocalItem.nameSong)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "")
                            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, mediaLocalItem.nameSong + "-" + mediaLocalItem.artist)
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaLocalItem.artist)
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
                            .putString(MediaMetadataCompat.METADATA_KEY_GENRE, MusicEnumApp.MusicGenreStr.SONG_ONLINE.type)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "")
                            .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1)
                            .build());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (musicType.type == MusicEnumApp.MusicType.ARTIST_ONLINE.type) {
            try {
                //TODO: READ ARTIST SEARCH ONLINE
                List<SongBySongApi.Artist> listArtist = MusicApplication
                        .getInstance().getUserComponent().searchOnlineManager().getSongListByArtist();
                for (SongBySongApi.Artist mediaLocalItem : listArtist) {
                    Log.e(TAG, "mediaLocalItem = " + mediaLocalItem.toString());
                    tracks.add(new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaLocalItem.idSong)
                            .putString(CUSTOM_METADATA_TRACK_SOURCE, "http://www.stephaniequinn.com/Music/Rondeau.mp3")
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaLocalItem.nameSong)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "")
                            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, mediaLocalItem.nameSong + "-" + mediaLocalItem.nameSong)
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaLocalItem.nameSong)
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
                            .putString(MediaMetadataCompat.METADATA_KEY_GENRE, mediaLocalItem.nameSong)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "")
                            .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1)
                            .build());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (musicType.type == MusicEnumApp.MusicType.ALBUM_ONLINE.type) {
            try {
                //TODO: READ ARTIST SEARCH ONLINE
                List<SongBySongApi.Album> listArtist = MusicApplication
                        .getInstance().getUserComponent().searchOnlineManager().getAlbumSearchOnline();
                for (SongBySongApi.Album mediaLocalItem : listArtist) {
                    Log.e(TAG, "mediaLocalItem = " + mediaLocalItem.toString());
                    tracks.add(new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaLocalItem.idSong)
                            .putString(CUSTOM_METADATA_TRACK_SOURCE, "http://www.stephaniequinn.com/Music/Rondeau.mp3")
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaLocalItem.nameSong)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "")
                            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, mediaLocalItem.nameSong + "-" + mediaLocalItem.nameSong)
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                                    mediaLocalItem.artist)
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
                            .putString(MediaMetadataCompat.METADATA_KEY_GENRE,
                                    mediaLocalItem.nameSong + " - " + mediaLocalItem.artist)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "")
                            .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1)
                            .build());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return tracks;
    }


    public ArrayList<MediaMetadataCompat> getListMediaStoreByDB(MusicEnumApp.MusicType musicType) {
        Log.e(TAG, "getListMediaStoreByDB");
        ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
        if (musicType.type == MusicEnumApp.MusicType.FAVORITE.type) {

            try {
                //TODO: READ ALL SONG FAVORITE
                Collection<MediaLocalItem> listSongFavorite = MusicApplication.getInstance().getUserComponent().songFavoriteLocalRepository().readAllSong();
                Iterator<MediaLocalItem> itemIterator = listSongFavorite.iterator();
                Log.e(TAG, "itemIterator FAVORITE = " + itemIterator.toString());
                while (itemIterator.hasNext()) {
                    MediaLocalItem mediaLocalItem = itemIterator.next();
                    tracks.add(new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(mediaLocalItem.getMediaId()))
                            .putString(CUSTOM_METADATA_TRACK_SOURCE, mediaLocalItem.getDataPath())
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaLocalItem.getTitle())
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "")
                            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, mediaLocalItem.getDescription())
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaLocalItem.getSubTitle())
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaLocalItem.getDuration())
                            .putString(MediaMetadataCompat.METADATA_KEY_GENRE, MusicEnumApp.MusicGenreStr.FAVORITE.type)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "")
                            .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, mediaLocalItem.getTrackNo())
                            .build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (musicType.type == MusicEnumApp.MusicType.PLAYED.type) {
            try {
                //TODO: READ ALL SONG PLAYED
                Collection<MediaLocalItem> listSongFavorite = MusicApplication
                        .getInstance().getUserComponent().songLocalPlayedRepository().readAllSong();
                if (listSongFavorite == null)
                    return tracks;

                Iterator<MediaLocalItem> itemIterator = listSongFavorite.iterator();
                while (itemIterator.hasNext()) {
                    MediaLocalItem mediaLocalItem = itemIterator.next();
                    tracks.add(new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(mediaLocalItem.getMediaId()))
                            .putString(CUSTOM_METADATA_TRACK_SOURCE, mediaLocalItem.getDataPath())
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaLocalItem.getTitle())
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "")
                            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, mediaLocalItem.getDescription())
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaLocalItem.getSubTitle())
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaLocalItem.getDuration())
                            .putString(MediaMetadataCompat.METADATA_KEY_GENRE, MusicEnumApp.MusicGenreStr.PLAYED.type)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "")
                            .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, mediaLocalItem.getTrackNo())
                            .build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return tracks;
    }

    /**
     * get music from local machine
     *
     * @param musicType use to get type music
     * @return
     */
    public ArrayList<MediaMetadataCompat> getListMediaStoreByProvider(MusicEnumApp.MusicType musicType) {
        Uri mediaUriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
        ContentResolver musicResolver = MusicApplication.getInstance().getApplicationContext().getContentResolver();
        String selection = MediaStore.Audio.Media.MIME_TYPE + "<> ?";
        String selectionArg[] = new String[]{"audio/ogg"};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor musicCursor = musicResolver.query(mediaUriExternal, null, selection, selectionArg, sortOrder);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int dataPathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int yearColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.YEAR);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int trackNoColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TRACK);
            int dateAddedColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATE_ADDED);
            int genreCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.MIME_TYPE);

            //add songs to list
            do {
                long id = musicCursor.getLong(idColumn);
                long albumId = musicCursor.getLong(albumIdColumn);
                long duration = musicCursor.getLong(durationColumn);

                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                String year = musicCursor.getString(yearColumn);
                String dataPath = musicCursor.getString(dataPathColumn);
                long trackNo = musicCursor.getLong(trackNoColumn);

                //GENRE NEED GET, EX: ALBUM, ARTIST, ALL SONG
                String GENRE;

                if (musicType.type == MusicEnumApp.MusicType.ALBUMS.type) {
                    GENRE = album;
                } else if (musicType.type == MusicEnumApp.MusicType.ARTISTS.type) {
                    GENRE = artist;
                } else {
                    GENRE = MusicEnumApp.MusicGenreStr.ALL_SONGS.type;
                }

//                Log.e(TAG, "dataPath = " + dataPath);
                tracks.add(new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(id))
                        .putString(CUSTOM_METADATA_TRACK_SOURCE, dataPath)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, dataPath)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, GENRE)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "")
                        .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNo)
                        .build());
            }
            while (musicCursor.moveToNext());
        }

        if (musicCursor != null) {
            musicCursor.close();
        }
        return tracks;
    }

}
