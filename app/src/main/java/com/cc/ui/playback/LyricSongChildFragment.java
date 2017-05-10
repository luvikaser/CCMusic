package com.cc.ui.playback;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.app.R;
import com.cc.domain.repository.SongLocalPlayedRepository;
import com.cc.event.BeginLoadEvent;
import com.cc.event.ChangeSongEvent;
import com.cc.event.LyricReceiveEvent;
import com.cc.event.SeekEvent;
import com.cc.service.MusicService;
import com.cc.ui.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import cn.zhaiyifan.lyric.LyricUtils;
import cn.zhaiyifan.lyric.model.Lyric;
import cn.zhaiyifan.lyric.widget.LyricView;

import static android.view.View.GONE;

/**
 * Created by Luvi Kaser on 11/8/2016.
 */

public class LyricSongChildFragment extends BaseFragment {
    private int currentPosition = -1;
    private LyricAdapter mLyricAdapter;
    private Lyric lyric = null;
    private boolean autoScroll = true;
    @BindView(R.id.view_lyric)
    ListView mViewLyric;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.text_lyric_not_found)
    TextView mLyricNotFound;
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    public static LyricSongChildFragment newInstance() {
        
        Bundle args = new Bundle();
        
        LyricSongChildFragment fragment = new LyricSongChildFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void setupFragmentComponent() {
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.child_fragment_play_control_full_screen_lyric_song;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MusicService.getSongPlayCurrent() == null || MusicService.getSongPlayCurrent
                ().getPathLyric() == null)
            return;

        String path = MusicService.getSongPlayCurrent().getPathLyric();
        if (!path.equals("")){
            mProgressBar.setVisibility(GONE);
            mLyricNotFound.setVisibility(GONE);
            lyric = LyricUtils.parseLyric(new File(path), "UTF-8");
            mLyricAdapter = new LyricAdapter(getContext(), R.layout.row_lyric, lyric.sentenceList);
            mViewLyric.setAdapter(mLyricAdapter);
            mViewLyric.setVisibility(View.VISIBLE);

        } else{
            MusicService.getLyric();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewLyric.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    new CountDownTimer(3000, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval
                        public void onTick(long millisUntilFinished) {
                            autoScroll = false;
                        }

                        public void onFinish() {
                            autoScroll = true;
                        }
                    }.start();

                }
                return false;
            }
        });
    }

    @Subscribe
    public void onEvent(LyricReceiveEvent e){
        if (e.title != MusicService.getSongPlayCurrent().getTitle())
            return;
        mProgressBar.setVisibility(GONE);
        if (e.flag){
            mLyricNotFound.setVisibility(GONE);
            String path = MusicService.getSongPlayCurrent().getPathLyric();
            lyric = LyricUtils.parseLyric(new File(path), "UTF-8");
            mLyricAdapter = new LyricAdapter(getContext(), R.layout.row_lyric, lyric.sentenceList);
            mViewLyric.setAdapter(mLyricAdapter);
            mViewLyric.setVisibility(View.VISIBLE);
        } else{
            if (e.fullLyric != null && !e.fullLyric.equals("not found music")) {
                mLyricNotFound.setText(Html.fromHtml(e.fullLyric));
                mLyricNotFound.setMovementMethod(new ScrollingMovementMethod());
            } else{
                mLyricNotFound.setText(getString(R.string.song_lyric_not_found));
            }
            mViewLyric.setVisibility(GONE);
            mLyricNotFound.setVisibility(View.VISIBLE);
        }
    }
    @Subscribe
    public void onEvent(SeekEvent e){
        if (lyric != null){
            int position = getPosition(e.time);
            if (position != -1 && position != currentPosition && autoScroll){
                currentPosition = position;
                mViewLyric.smoothScrollToPositionFromTop(position, mViewLyric.getHeight() / 2);
                ((LyricAdapter)mViewLyric.getAdapter()).setPositionHighlight(position);
            }
        }
    }

    private int getPosition(long time) {
        for(int i = 0; i < lyric.sentenceList.size() - 1; ++i){
            if (time >= lyric.sentenceList.get(i).fromTime && time < lyric.sentenceList.get(i + 1).fromTime){
                return i;
            }
        }
        return -1;
    }

    @Subscribe
    public void onEvent(BeginLoadEvent e) {
        mProgressBar.setVisibility(View.VISIBLE);
        mViewLyric.setVisibility(GONE);
        mLyricNotFound.setVisibility(GONE);
    }

    @Subscribe
    public void onEvent(ChangeSongEvent e) {
        mProgressBar.setVisibility(GONE);
        mLyricNotFound.setVisibility(View.GONE);
        String path = MusicService.getSongPlayCurrent().getPathLyric();
        lyric = LyricUtils.parseLyric(new File(path), "UTF-8");
        mLyricAdapter = new LyricAdapter(getContext(), R.layout.row_lyric, lyric.sentenceList);
        mViewLyric.setAdapter(mLyricAdapter);
        mViewLyric.setVisibility(View.VISIBLE);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
