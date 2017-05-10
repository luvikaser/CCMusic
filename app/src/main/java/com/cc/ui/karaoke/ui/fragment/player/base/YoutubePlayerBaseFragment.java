package com.cc.ui.karaoke.ui.fragment.player.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.ButterKnife;
import com.cc.ui.karaoke.utils.DebugLog;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

/**
 * Author  : duyng
 * since   : 8/9/2016
 */
public abstract class YoutubePlayerBaseFragment extends YouTubePlayerSupportFragment {
    public final String TAG = this.getClass().getSimpleName();
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugLog.d(TAG, "onCreate" + savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugLog.d(TAG, "onCreateView" + savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public synchronized void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DebugLog.d(TAG, "onViewCreated");
        ButterKnife.bind(this, view);
    }

    @Override
    public void onStop() {
        super.onStop();
     }
}
