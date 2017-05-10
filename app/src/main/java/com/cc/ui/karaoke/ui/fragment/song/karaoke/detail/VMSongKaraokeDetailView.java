package com.cc.ui.karaoke.ui.fragment.song.karaoke.detail;

import com.cc.ui.karaoke.data.model.services.response.lyric.VMLyricByIdResponse;
import com.cc.ui.karaoke.data.model.services.response.song.VMSearchSongResponse;
import com.cc.ui.karaoke.data.model.services.response.stream.VMMp3StreamResponse;

/**
 * Author: NT
 * Since: 6/21/2016.
 */
public interface VMSongKaraokeDetailView {

    void setDataLyricSong(VMLyricByIdResponse lyricSong, VMMp3StreamResponse streamResponse, VMMp3StreamResponse mp3StreamResponse);

    void setDataListSong(VMSearchSongResponse songSearchResponse);

    void showError(String message);

    void emptyData();

    void showMessageGetLyric(String message);
}
