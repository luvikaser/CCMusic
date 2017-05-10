package com.cc.ui.playback;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.event.BeginLoadEvent;
import com.cc.event.ChangeSongEvent;
import com.cc.event.LyricReceiveEvent;
import com.cc.event.PlayPauseEvent;
import com.cc.event.SeekEvent;
import com.cc.event.VolumeChangeEvent;
import com.cc.service.MusicService;
import com.cc.ui.base.BaseFragment;
import com.cc.utils.CCMusicUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zhaiyifan.lyric.LyricUtils;
import cn.zhaiyifan.lyric.model.Lyric;
import cn.zhaiyifan.lyric.widget.LyricView;
import me.tankery.lib.circularseekbar.CircularSeekBar;

import static android.view.View.GONE;

/**
 * Created by Luvi Kaser on 11/8/2016.
 */

public class AdjustVolumeChildFragment extends BaseFragment {
    private LyricAdapter mLyricAdapter;
    private Lyric lyric = null;
    private boolean isMaxVolume = false;
    private boolean isOffVolume = false;
    private int currenPosition = -1;
    private String mTitleSongCurrent = null;
    private ObjectAnimator anim = null;
    private long currentTime = -1;
    private AudioManager audioManager;
    private SettingsContentObserver mContentObserver;
    @BindView(R.id.seekBarVolume)
    CircularSeekBar mSeekBarVolume;

    @BindView(R.id.view_lyric)
    ListView mViewLyric;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.text_lyric_not_found)
    TextView mLyricNotFound;

    @BindView(R.id.img_circle_bg_play)
    ImageView imgBgCirclePlay;

    public static AdjustVolumeChildFragment newInstance() {

        Bundle args = new Bundle();

        AdjustVolumeChildFragment fragment = new AdjustVolumeChildFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void setupFragmentComponent() {
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.child_fragment_play_control_full_screen_adjust_volume;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControlVolume();
    }

    private void initControlVolume() {

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        try {
            mSeekBarVolume.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            mSeekBarVolume.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));

            mSeekBarVolume.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
                @Override
                public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            (int)progress, 0);
                }

                @Override
                public void onStopTrackingTouch(CircularSeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(CircularSeekBar seekBar) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mContentObserver = new SettingsContentObserver(getContext(), new Handler());
        getActivity().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mContentObserver);
    }

    private class SettingsContentObserver extends ContentObserver {

        public SettingsContentObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            mSeekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MusicService.getSongPlayCurrent() == null || MusicService.getSongPlayCurrent
                ().getPathLyric() == null)
            return;

        String path = MusicService.getSongPlayCurrent().getPathLyric();
        if (!MusicService.getSongPlayCurrent().getTitle().equals(mTitleSongCurrent)){
            mTitleSongCurrent = MusicService.getSongPlayCurrent().getTitle();
            updateImage();
        }
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

    private void updateImage() {
        String[] columns =new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"};

        String where =  "title LIKE ?";
        String whereVal[] = {mTitleSongCurrent};
        Cursor cursor = null;
        Bitmap outBitmap = null;
        try{
            cursor = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, where, whereVal, null);

            if (cursor.moveToFirst()){
                long id = cursor.getLong(7);
                outBitmap = ImageLoader.getInstance().loadImageSync(CCMusicUtils.getAlbumArtUri(id).toString());
                if (outBitmap == null) {
                    outBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.bg_img_circle_play_back);
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        imgBgCirclePlay.setImageBitmap(outBitmap);

        Log.e(TAG, "updatimage");
        if (anim != null)
            anim.end();
        anim = ObjectAnimator.ofFloat(imgBgCirclePlay, "rotation", 0, 360);
        anim.setDuration(7000);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.start();
    }

    @Subscribe
    public void onEvent(VolumeChangeEvent e){
        Log.e(TAG, "change");
        mSeekBarVolume.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));

    }
    @Subscribe
    public void onEvent(LyricReceiveEvent e){
        if (e.title != MusicService.getSongPlayCurrent().getTitle())
            return;
        mProgressBar.setVisibility(GONE);
        if (!MusicService.getSongPlayCurrent().getTitle().equals(mTitleSongCurrent)){
            mTitleSongCurrent = MusicService.getSongPlayCurrent().getTitle();
            updateImage();
        }
        if (e.flag){
            mLyricNotFound.setVisibility(GONE);
            String path = MusicService.getSongPlayCurrent().getPathLyric();
            lyric = LyricUtils.parseLyric(new File(path), "UTF-8");
            mLyricAdapter = new LyricAdapter(getContext(), R.layout.row_lyric, lyric.sentenceList);
            mViewLyric.setAdapter(mLyricAdapter);
            mViewLyric.setVisibility(View.VISIBLE);
        } else{
            mViewLyric.setVisibility(GONE);
            mLyricNotFound.setVisibility(View.VISIBLE);
        }
    }

    private static void smoothScrollToPositionFromTop(final AbsListView view, final int position) {
        View child = getChildAtPosition(view, position);
        // There's no need to scroll if child is already at top or view is already scrolled to its end
        if ((child != null) && ((child.getTop() == 0) || ((child.getTop() > 0) && !view.canScrollVertically(1)))) {
            return;
        }

        view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, final int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    view.setOnScrollListener(null);

                    // Fix for scrolling bug
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            view.setSelection(position);
                        }
                    });
                }
            }

            @Override
            public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                                 final int totalItemCount) { }
        });

        // Perform scrolling to position
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                view.smoothScrollToPositionFromTop(position, 0);
            }
        });
    }

    private static View getChildAtPosition(final AdapterView view, final int position) {
        final int index = position - view.getFirstVisiblePosition();
        if ((index >= 0) && (index < view.getChildCount())) {
            return view.getChildAt(index);
        } else {
            return null;
        }
    }

    @Subscribe
    public void onEvent(PlayPauseEvent e){
        if (anim != null){
            Log.e(TAG, e.check+""+anim);
            if (e.check){
                anim.resume();
            } else{
                anim.pause();
            }
        }
    }
    @Subscribe
    public void onEvent(SeekEvent e){
        if (lyric != null){
            int position = getPosition(e.time);
            if (position != -1 && position != currenPosition){
                currenPosition = position;
                smoothScrollToPositionFromTop(mViewLyric, position);
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
        if (!MusicService.getSongPlayCurrent().getTitle().equals(mTitleSongCurrent)){
            mTitleSongCurrent = MusicService.getSongPlayCurrent().getTitle();
            updateImage();
        }
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
        getActivity().getContentResolver().unregisterContentObserver(mContentObserver);
    }


}
