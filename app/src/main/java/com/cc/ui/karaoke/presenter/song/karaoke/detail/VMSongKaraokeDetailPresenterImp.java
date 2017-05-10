package com.cc.ui.karaoke.presenter.song.karaoke.detail;

import android.text.TextUtils;
import android.util.Log;

import com.cc.MusicApplication;
import com.cc.ui.karaoke.data.database.helper.karaoke.VMSongKaraokeScope;
import com.cc.ui.karaoke.data.database.helperImp.karaoke.VMSongArirangScopeImp;
import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.data.model.services.response.lyric.VMLyricByIdResponse;
import com.cc.ui.karaoke.data.model.services.response.song.VMSearchSongResponse;
import com.cc.ui.karaoke.data.model.services.response.stream.VMMp3StreamResponse;
import com.cc.ui.karaoke.presenter.song.base.VMBaseSongPresenterImp;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.detail.VMSongKaraokeDetailView;
import com.cc.ui.karaoke.utils.DebugLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Author: NT
 * Since: 6/21/2016.
 */
public class VMSongKaraokeDetailPresenterImp extends VMBaseSongPresenterImp implements
        VMSongKaraokeDetailPresenter<VMSongKaraokeDetailView> {

    private VMSongKaraokeDetailView mView;
    private VMSongKaraokeScope songCommonScope;

    @Override
    public void getListSongSearch(String songName) {
        DebugLog.e(TAG, "songName = " + songName);
        if (TextUtils.isEmpty(songName))
            return;

        if (songName.contains(","))
            songName = songName.substring(0, songName.indexOf(","));

        Call<VMSearchSongResponse> call =
                MusicApplication.getInstance()
                        .getApiService()
                        .searchSong(songName);

        call.enqueue(new Callback<VMSearchSongResponse>() {
            @Override
            public void onResponse(Call<VMSearchSongResponse> call, Response<VMSearchSongResponse> response) {
                VMSearchSongResponse searchSongResponse = response.body();
                if (searchSongResponse.getError() == 0) {
                    if (mView == null) return;
                    mView.setDataListSong(searchSongResponse);
                    if (searchSongResponse.getListMsg().get(3).getVmSongList().size() == 0)
                        mView.emptyData();
                } else {
                    mView.emptyData();
                }
            }

            @Override
            public void onFailure(Call<VMSearchSongResponse> call, Throwable t) {
//                DebugLog.e(TAG, t.getMessage());
//                DebugLog.e(TAG, t.toString());
//                DebugLog.e(TAG, call.toString());
                if (mView != null)
                    mView.showError("No Internet Connection!");
            }
        });
    }

    @Override
    public void getLyricSong(final String songId) {
        Call<VMLyricByIdResponse> call =
                MusicApplication
                        .getInstance()
                        .getApiService()
                        .getLyricById(songId);

        call.enqueue(new Callback<VMLyricByIdResponse>() {
            @Override
            public void onResponse(Call<VMLyricByIdResponse> call, Response<VMLyricByIdResponse> response) {
                VMLyricByIdResponse lyricResponse = response.body();
                Log.d(TAG, "getErrorMsg = " + lyricResponse.getErrorMsg());
                if (lyricResponse.getErrorMsg() == 0) {
                    getMp3Stream(songId, lyricResponse);
                } else {
                    mView.showMessageGetLyric(lyricResponse.getLyric());
                }

            }

            @Override
            public void onFailure(Call<VMLyricByIdResponse> call, Throwable t) {
                mView.showMessageGetLyric("No Internet Connection!");
            }
        });
    }

    @Override
    public void addSongToFavorite(VMSongKaraoke songKaraoke) {
        songCommonScope.addFavorite(songKaraoke);
    }

    @Override
    public void removeSongToFavorite(long idSong) {
        songCommonScope.removeSongFavorite(idSong);
    }

    @Override
    public void getMp3Stream(final String songId, final VMLyricByIdResponse lyricResponse) {
        Call<VMMp3StreamResponse> call =
                MusicApplication
                        .getInstance()
                        .getApiService()
                        .getMp3StreamById(songId);

        final Call<VMMp3StreamResponse> callGetLrcString =
                MusicApplication
                        .getInstance()
                        .getApiService()
                        .getMp3LrcStringById(songId);

        call.enqueue(new Callback<VMMp3StreamResponse>() {
            @Override
            public void onResponse(Call<VMMp3StreamResponse> call, Response<VMMp3StreamResponse> response) {
                final VMMp3StreamResponse mp3StreamResponse = response.body();

                callGetLrcString.enqueue(new Callback<VMMp3StreamResponse>() {
                    @Override
                    public void onResponse(Call<VMMp3StreamResponse> call, Response<VMMp3StreamResponse> response) {
                        VMMp3StreamResponse lrcData = response.body();
                        mView.setDataLyricSong(lyricResponse, mp3StreamResponse, lrcData);
                    }

                    @Override
                    public void onFailure(Call<VMMp3StreamResponse> call, Throwable t) {
                        mView.setDataLyricSong(lyricResponse, mp3StreamResponse, null);
                    }
                });
            }

            @Override
            public void onFailure(Call<VMMp3StreamResponse> call, Throwable t) {
                mView.showMessageGetLyric("No Internet Connection!");
            }
        });

    }

    @Override
    public void setView(VMSongKaraokeDetailView view) {
        this.mView = view;
        this.songCommonScope = new VMSongArirangScopeImp();
    }

    @Override
    public void destroyView() {
        mView = null;
    }
}
