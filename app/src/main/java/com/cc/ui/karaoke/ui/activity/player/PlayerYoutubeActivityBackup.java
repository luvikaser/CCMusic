//package com.cc.ui.karaoke.ui.activity.player;
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.annotation.NonNull;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Toast;
//
//import com.google.android.youtube.player.YouTubeBaseActivity;
//import com.google.android.youtube.player.YouTubeInitializationResult;
//import com.google.android.youtube.player.YouTubePlayer;
//import com.google.android.youtube.player.YouTubePlayerView;
//import com.newventuresoftware.waveform.WaveformView;
//
//import java.io.File;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import mmobile.com.karaoke.R;
//import com.cc.ui.karaoke.app.BaseConstants;
//import com.cc.ui.karaoke.data.model.VideoItem;
//import com.cc.ui.karaoke.ui.fragment.song.karaoke.detail.VMSongKaraokeDetailFragment;
//import com.cc.ui.karaoke.utils.DebugLog;
//
///**
// * Author  : duyng
// * since   : 8/9/2016
// */
//public class PlayerYoutubeActivityBackup extends YouTubeBaseActivity implements YouTubePlayer
//        .OnInitializedListener, YouTubePlayer.PlaybackEventListener {
//    private final String TAG = this.getClass().getSimpleName();
//    public String ID_YOUTUBE_LINK = "";
//    private static final int RECOVERY_REQUEST = 1;
//    private static final int REQUEST_RECORD_AUDIO = 13;
//
//    private VideoItem mVideoItem;
//    private YouTubePlayer mYouTubePlayer;
//    private String outPutFile;
//
//    private boolean isFirstOpen = true;
//    @Bind(R.id.youtube_player_view)
//    YouTubePlayerView mYouTubePlayerView;
//
//    private RecordingThread mRecordingThread;
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_youtube_player);
//        ButterKnife.bind(this);
//
//        File folerKaraoke = new File(Environment.getExternalStorageDirectory(), "karaoke");
//        if (!folerKaraoke.exists()) folerKaraoke.mkdirs();
//
//        //set data bundle
//        Bundle bundle = getIntent().getExtras();
//        mVideoItem = (VideoItem) bundle.getSerializable(VMSongKaraokeDetailFragment.KEY_BUNDLE_VIDEO_YOUTUB);
//        if (!TextUtils.isEmpty(mVideoItem.getId()))
//            ID_YOUTUBE_LINK = mVideoItem.getId();
//
//        mYouTubePlayerView.initialize(BaseConstants.YOUTUBE_API_KEY,
//                PlayerYoutubeActivityBackup.this);
//
//        //init data record
//        initRecord();
//        mRecordingThread = new RecordingThread(outPutFile, new AudioDataReceivedListener
//                () {
//            @Override
//            public void onAudioDataReceived(byte[] data) {
//                mRealtimeWaveformView.setSamples(convertByteToShort(data));
//            }
//        });
//
//        //event button start record
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!mRecordingThread.recording()) {
//                    mYouTubePlayer.play();
//                    startAudioRecordingSafe();
//                } else {
//                    mYouTubePlayer.pause();
//                    mRecordingThread.stopRecording();
//                }
//            }
//        });
//
//    }
//
//    public short[] convertByteToShort(byte[] data) {
//        int size = data.length;
//        short[] shortsData = new short[size];
//        for (int i = 0; i < size; i++) {
//            shortsData[i] = data[i];
//        }
//        return shortsData;
//    }
//
//    private void startAudioRecordingSafe() {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
//                == PackageManager.PERMISSION_GRANTED) {
//            mRecordingThread.startRecording();
//        } else {
//            requestMicrophonePermission();
//        }
//    }
//
//    private void requestMicrophonePermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
//            // Show dialog explaining why we need record audio
//            Snackbar.make(mRealtimeWaveformView, "Microphone access is required in order to record audio",
//                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ActivityCompat.requestPermissions(PlayerYoutubeActivityBackup.this, new String[]{
//                            android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
//                }
//            }).show();
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{
//                    android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_RECORD_AUDIO && grantResults.length > 0 &&
//                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            mRecordingThread.stopRecording();
//        }
//    }
//
//
//    private void initRecord() {
//        DebugLog.e(TAG, "onInitializationSuccess with id = " + ID_YOUTUBE_LINK);
//        outPutFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/karaoke/"
//                + ID_YOUTUBE_LINK + ".raw";
//
//    }
//
//
//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//        mYouTubePlayer = youTubePlayer;
//        mYouTubePlayer.setPlaybackEventListener(PlayerYoutubeActivityBackup.this);
//        if (!b) {
//            youTubePlayer.cueVideo(ID_YOUTUBE_LINK);
//            youTubePlayer.play();
//        }
//    }
//
//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
//        if (errorReason.isUserRecoverableError()) {
//            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
//        } else {
////            String error = String.format(getString(R.string.player_error), errorReason.toString());
//            Toast.makeText(this, errorReason.toString(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RECOVERY_REQUEST) {
//            mYouTubePlayerView.initialize(BaseConstants.YOUTUBE_API_KEY, this);
//        }
//    }
//
//    @Override
//    public void onPlaying() {
//        DebugLog.d(TAG, "onPlaying");
//    }
//
//    @Override
//    public void onPaused() {
//        DebugLog.d(TAG, "onPaused");
//    }
//
//    @Override
//    public void onStopped() {
//        DebugLog.d(TAG, "onStopped");
//        mRecordingThread.stopRecording();
//    }
//
//    @Override
//    public void onBuffering(boolean b) {
//        DebugLog.d(TAG, "onBuffering");
//    }
//
//    @Override
//    public void onSeekTo(int i) {
//        DebugLog.d(TAG, "onSeekTo");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mRecordingThread.stopRecording();
//        mRecordingThread.closeFile();
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//}
