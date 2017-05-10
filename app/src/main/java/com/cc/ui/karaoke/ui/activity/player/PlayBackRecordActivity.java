package com.cc.ui.karaoke.ui.activity.player;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.MusicApplication;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmobile.com.karaoke.R;
import com.cc.ui.karaoke.app.BaseConstants;
import com.cc.ui.karaoke.app.Storage;
import com.cc.ui.karaoke.data.model.record.VMRecordFile;
import com.cc.ui.karaoke.network.HttpFileUpload;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.detail.VMSongKaraokeDetailFragment;
import com.cc.ui.karaoke.utils.DebugLog;

/**
 * Author  : duyng
 * since   : 9/1/2016
 */
public class PlayBackRecordActivity extends YouTubeBaseActivity implements YouTubePlayer
        .OnInitializedListener, YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener {
    private static final String TAG = "PlayBackRecordActivity";
    public static String BUNDLE_FILE_RECORD = "BUNDLE_FILE_RECORD";
    static final int TYPE_COLLAPSED = 0;
    static final int TYPE_EXPANDED = 1;
    static final int TYPE_DELETED = 2;

    private Storage storage;
    private Handler handler;
    private MediaPlayer player;
    private Runnable updatePlayer;
    private File fileRecordCurrent;

    private int durations;
    public String ID_YOUTUBE_LINK = "";

    private VMRecordFile mRecordFile;
    private YouTubePlayer mYouTubePlayer;

    @BindView(R.id.youtube_player_view)
    YouTubePlayerView mYouTubePlayerView;

    @BindView(R.id.tv_title_song)
    TextView tvTitleSong;
    private View playerBase;
    private View play;

    @OnClick(R.id.recording_player_share)
    public void onClickShareFile(View v) {
        shareAudio();
    }

    public void closeMediaPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }

        if (updatePlayer != null) {
            handler.removeCallbacks(updatePlayer);
            updatePlayer = null;
        }
    }

    private void initWidgetAndData() {
        MediaPlayer mp = MediaPlayer.create(this, Uri.fromFile(fileRecordCurrent));

        durations = mp.getDuration();
        TextView title = (TextView) findViewById(R.id.recording_title);
        title.setText(fileRecordCurrent.getName());

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TextView time = (TextView) findViewById(R.id.recording_time);
        time.setText(s.format(new Date(fileRecordCurrent.lastModified())));

        TextView dur = (TextView) findViewById(R.id.recording_duration);
        dur.setText(MusicApplication.formatDuration(this, mp.getDuration()));

        TextView size = (TextView) findViewById(R.id.recording_size);
        size.setText(MusicApplication.formatSize(this, fileRecordCurrent.length()));

        playerBase = findViewById(R.id.recording_player);
        playerBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        play = findViewById(R.id.recording_player_play);
        play.setEnabled(false);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player == null) {
                    playerPlay(playerBase, fileRecordCurrent);
                } else if (player.isPlaying()) {
                    playerPause(playerBase, fileRecordCurrent);
                } else {
                    playerPlay(playerBase, fileRecordCurrent);
                }
            }
        });
    }


    // Method to share any image.
    private void shareAudio() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Đang xử lý...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setProgress(0);
        progress.setMax(100);
        progress.show();
        HttpFileUpload.uploadSound(fileRecordCurrent, new HttpFileUpload.onUploadListener() {
            @Override
            public void onUploadSuccess(String data) {
                progress.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    shareLinkFileAudio(jsonObject.getString("Url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgressUpload(long percent) {
                progress.setProgress((int) percent);
            }

            @Override
            public void onUploadFailed(String error) {
                progress.dismiss();
                Toast.makeText(PlayBackRecordActivity.this, "Lỗi xảy ra, vui lòng thử lại", Toast
                        .LENGTH_LONG).show();
            }
        });
    }

    private void shareLinkFileAudio(String url) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(sharingIntent, "Share File record via"));
    }

    void playerPlay(View v, File f) {
        if (player == null)
            player = MediaPlayer.create(this, Uri.fromFile(f));
        if (player == null) {
            Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        player.start();
        mYouTubePlayer.play();
        updatePlayerRun(v, f);
    }

    void playerPause(View v, File f) {
        if (player != null) {
            player.pause();
        }
        if (updatePlayer != null) {
            handler.removeCallbacks(updatePlayer);
            updatePlayer = null;
        }
        mYouTubePlayer.pause();
        updatePlayerText(v, f);
    }

    void updatePlayerRun(final View v, final File f) {
        boolean playing = updatePlayerText(v, f);

        if (updatePlayer != null) {
            handler.removeCallbacks(updatePlayer);
            updatePlayer = null;
        }

        if (!playing) {
            return;
        }

        updatePlayer = new Runnable() {
            @Override
            public void run() {
                updatePlayerRun(v, f);
            }
        };
        handler.postDelayed(updatePlayer, 200);
    }

    boolean updatePlayerText(final View v, final File f) {
        ImageView i = (ImageView) v.findViewById(R.id.recording_player_play);

        final boolean playing = player != null && player.isPlaying();

        i.setImageResource(playing ? R.drawable.pause : R.drawable.play);

        TextView start = (TextView) v.findViewById(R.id.recording_player_start);
        SeekBar bar = (SeekBar) v.findViewById(R.id.recording_player_seek);
        final TextView end = (TextView) v.findViewById(R.id.recording_player_end);

        int c = 0;
        int d = durations;

        if (player != null) {
            c = player.getCurrentPosition();
            d = player.getDuration();
        }

        bar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser)
                    return;

                if (player == null)
                    playerPlay(v, f);

                if (player != null) {
                    player.seekTo(progress);
                    if (!player.isPlaying())
                        playerPlay(v, f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        start.setText(MusicApplication.formatDuration(this, c));
        bar.setMax(d);
        bar.setKeyProgressIncrement(1);
        bar.setProgress(c);
        end.setText("-" + MusicApplication.formatDuration(this, d - c));
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mYouTubePlayer.pause();
                mYouTubePlayer.seekToMillis(0);
                end.setText("-00:00");
            }
        });

        return playing;
    }

    void playerStop() {
        if (updatePlayer != null) {
            handler.removeCallbacks(updatePlayer);
            updatePlayer = null;
        }
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_playback_record);
        ButterKnife.bind(this);
        handler = new Handler();
        //set data bundle
        mRecordFile = (VMRecordFile) getIntent().getSerializableExtra(VMSongKaraokeDetailFragment
                .KEY_BUNDLE_VIDEO_YOUTUB);
        if (!TextUtils.isEmpty(mRecordFile.getYoutubeId()))
            ID_YOUTUBE_LINK = mRecordFile.getYoutubeId();
        fileRecordCurrent = new File(mRecordFile.getFilePath());

        initWidgetAndData();
        tvTitleSong.setText(mRecordFile.getName());
        mYouTubePlayerView.initialize(BaseConstants.YOUTUBE_API_KEY, this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        closeMediaPlayer();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        mYouTubePlayer = youTubePlayer;
        mYouTubePlayer.setPlaybackEventListener(this);
        mYouTubePlayer.setPlayerStateChangeListener(this);
        if (!b) {
            youTubePlayer.cueVideo(ID_YOUTUBE_LINK);
            youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, 1).show();
        } else {
            Toast.makeText(this, errorReason.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPlaying() {
        if (player != null && player.isPlaying())
            playerPlay(playerBase, fileRecordCurrent);
        else playerPlay(playerBase, fileRecordCurrent);
    }

    @Override
    public void onPaused() {
        if (player != null && player.isPlaying())
            playerPause(playerBase, fileRecordCurrent);
    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {

    }

    @Override
    public void onLoading() {
        DebugLog.d(TAG, "onLoading");
        play.setEnabled(false);
    }

    @Override
    public void onLoaded(String s) {
        DebugLog.d(TAG, "onLoaded");
        mYouTubePlayer.play();
        play.setEnabled(true);
    }

    private boolean isAdStarted = false;

    @Override
    public void onAdStarted() {
        DebugLog.d(TAG, "onAdStarted");
        isAdStarted = true;
    }

    @Override
    public void onVideoStarted() {
        DebugLog.d(TAG, "onVideoStarted");
    }

    @Override
    public void onVideoEnded() {
        DebugLog.d(TAG, "onVideoEnded");
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        DebugLog.d(TAG, "onError");
    }

    @Override
    public void onBackPressed() {
        cancelDialog(new Runnable() {
            @Override
            public void run() {
                closeMediaPlayer();
                finish();
            }
        });
    }

    void cancelDialog(final Runnable run) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_cancel);
        builder.setMessage(R.string.are_you_sure_cancel);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                run.run();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

}
