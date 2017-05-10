package com.cc.ui.karaoke.ui.activity.player;

import android.Manifest;
 import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.MusicApplication;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmobile.com.karaoke.R;
import com.cc.ui.karaoke.app.BaseConstants;
import com.cc.ui.karaoke.app.RawSamples;
import com.cc.ui.karaoke.app.Sound;
import com.cc.ui.karaoke.app.Storage;
import com.cc.ui.karaoke.data.database.helper.karaoke.VMRecordFileScope;
import com.cc.ui.karaoke.data.database.helperImp.record.VMRecordFileScopeImpl;
import com.cc.ui.karaoke.data.model.VideoItem;
import com.cc.ui.karaoke.data.model.record.VMRecordFile;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.detail.VMSongKaraokeDetailFragment;
import com.cc.ui.karaoke.utils.DebugLog;
import com.cc.ui.karaoke.utils.SystemUtil;
import com.cc.ui.karaoke.utils.encoders.Encoder;
import com.cc.ui.karaoke.utils.encoders.EncoderInfo;
import com.cc.ui.karaoke.utils.encoders.FileEncoder;
import com.cc.ui.karaoke.utils.encoders.Format3GP;
import com.cc.ui.karaoke.utils.encoders.FormatM4A;
import com.cc.ui.karaoke.utils.encoders.FormatWAV;

/**
 * Author  : duyng
 * since   : 8/9/2016
 */
public class PlayerYoutubeActivity extends YouTubeBaseActivity implements YouTubePlayer
        .OnInitializedListener, YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener {
    private final String TAG = this.getClass().getSimpleName();
    public String ID_YOUTUBE_LINK = "";
    private static final int RECOVERY_REQUEST = 1;
    private static final int REQUEST_RECORD_AUDIO = 13;

    private VideoItem mVideoItem;
    private YouTubePlayer mYouTubePlayer;
    private String outPutFile;

    String nativeSampleRate;
    String nativeSampleBufSize;

    public static String START_PAUSE = PlayerYoutubeActivity.class.getCanonicalName() + ".START_PAUSE";
    public static String PAUSE_BUTTON = PlayerYoutubeActivity.class.getCanonicalName() + ".PAUSE_BUTTON";

    PhoneStateChangeListener pscl = new PhoneStateChangeListener();
    Handler handle = new Handler();
    FileEncoder encoder;

    // do we need to start recording immidiatly?
    boolean start = true;

    Thread thread;
    // lock for bufferSize
    final Object bufferSizeLock = new Object();
    // dynamic buffer size. big for backgound recording. small for realtime view updates.
    int bufferSize;
    // variable from settings. how may samples per second.
    int sampleRate;
    // pitch size in samples. how many samples count need to update view. 4410 for 100ms update.
    int samplesUpdate;
    // output target file 2016-01-01 01.01.01.wav
    File targetFile;
    // how many samples passed for current recording
    long samplesTime;
    // current cut position in samples from begining of file
    long editSample = -1;

    // current sample index in edit mode while playing;
    long playIndex;
    // send ui update every 'playUpdate' samples.
    int playUpdate;
    // current play sound track
    AudioTrack play;

    TextView title;
    TextView time;
    TextView state;
    ImageButton pause;
    Storage storage;
    Sound sound;
    RecordingReceiver receiver;

    private VMRecordFileScope mRecordFileScope;
    private VMRecordFile recordFileCurrent;

    private boolean isFirstOpen = true;
    @BindView(R.id.youtube_player_view)
    YouTubePlayerView mYouTubePlayerView;

    @BindView(R.id.sw_echo)
    Switch swEcho;

    private View btnDone;
    boolean supportRecording;

    public static void startActivity(Context context, boolean pause) {
        Intent i = new Intent(context, PlayerYoutubeActivity.class);
        if (pause) {
            i.setAction(PlayerYoutubeActivity.START_PAUSE);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(i);
    }

    @Override
    public void onLoading() {
        DebugLog.e(TAG, "onLoading");
        pause.setEnabled(false);
    }

    @Override
    public void onLoaded(String s) {
        DebugLog.e(TAG, "onLoaded");
        mYouTubePlayer.play();

    }

    @Override
    public void onAdStarted() {
        DebugLog.e(TAG, "onAdStarted");
        stopRecording();
    }

    @Override
    public void onVideoStarted() {
        DebugLog.e(TAG, "onVideoStarted");
    }

    @Override
    public void onVideoEnded() {
        DebugLog.e(TAG, "onVideoEnded");
        stopRecording(getString(R.string.encoding));
        encoding(new Runnable() {
            @Override
            public void run() {
                int result = mRecordFileScope.insertRecordFile(recordFileCurrent);
                if (result == 0)
                    showDialogDone();
                else
                    new AlertDialog.Builder(PlayerYoutubeActivity.this)
                            .setTitle("Save File Error")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
            }
        });
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    class RecordingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PAUSE_BUTTON)) {
                pauseButton();
            }
        }
    }

    void loadSamples() {

    }

    boolean isEmulator() {
        return "goldfish".equals(Build.HARDWARE);
    }

    void pauseButton() {
        if (thread != null) {
            mYouTubePlayer.pause();
            stopRecording(getString(R.string.pause));
        } else {
            editCut();
            mYouTubePlayer.play();
            startRecording();
            isFirstOpen = false;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_youtube_player);
        ButterKnife.bind(this);

        // effect audio
        // create

        //init sample rate echo
        AudioManager myAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        nativeSampleRate = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        nativeSampleBufSize = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);

        // init handle for record table, query to database.
        mRecordFileScope = new VMRecordFileScopeImpl();
        // init record file object for write to database
        recordFileCurrent = new VMRecordFile();

        File folerKaraoke = new File(Environment.getExternalStorageDirectory(), "karaoke");
        if (!folerKaraoke.exists()) folerKaraoke.mkdirs();

        //set data bundle
        Bundle bundle = getIntent().getExtras();
        mVideoItem = (VideoItem) bundle.getSerializable(VMSongKaraokeDetailFragment.KEY_BUNDLE_VIDEO_YOUTUB);
        assert mVideoItem != null;

        recordFileCurrent.setYoutubeId(mVideoItem.getId());

        if (!TextUtils.isEmpty(mVideoItem.getId()))
            ID_YOUTUBE_LINK = mVideoItem.getId();

        mYouTubePlayerView.initialize(BaseConstants.YOUTUBE_API_KEY,
                PlayerYoutubeActivity.this);

        time = (TextView) findViewById(R.id.recording_time);
        state = (TextView) findViewById(R.id.recording_state);
        title = (TextView) findViewById(R.id.recording_title);

        storage = new Storage(this);
        sound = new Sound(this);

        try {
//            targetFile = storage.getNewFile();
            targetFile = storage.getNewFile(mVideoItem.getTitleSong());
            recordFileCurrent.setFilePath(targetFile.getPath());
            recordFileCurrent.setDate(storage.getDateTimeFormat());
        } catch (RuntimeException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            finish();
            return;
        }
        recordFileCurrent.setName(targetFile.getName());
        title.setText(targetFile.getName());

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);

        if (shared.getBoolean(MusicApplication.PREFERENCE_CALL, false)) {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(pscl, PhoneStateListener.LISTEN_CALL_STATE);
        }

        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/

        sampleRate = Integer.parseInt(shared.getString(MusicApplication.PREFERENCE_RATE, ""));

        if (Build.VERSION.SDK_INT < 23 && isEmulator()) {
            // old emulators are not going to record on high sample rate.
            Toast.makeText(this, "Emulator Detected. Reducing Sample Rate to 8000 Hz", Toast.LENGTH_SHORT).show();
            sampleRate = 8000;
        }

        samplesUpdate = (int) (sampleRate / 1000.0);

        updateBufferSize(false);

        loadSamples();

        View cancel = findViewById(R.id.recording_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog(new Runnable() {
                    @Override
                    public void run() {
                        stopRecording();
                        storage.delete(storage.getTempRecording());
                        mYouTubePlayer.pause();
                        finish();
                    }
                });
            }
        });

        pause = (ImageButton) findViewById(R.id.recording_pause);
        pause.setEnabled(false);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pauseButton();
            }
        });

        btnDone = findViewById(R.id.recording_done);
        btnDone.setEnabled(false);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYouTubePlayer.pause();
                stopRecording(getString(R.string.encoding));
                encoding(new Runnable() {
                    @Override
                    public void run() {
                        int result = mRecordFileScope.insertRecordFile(recordFileCurrent);
                        if (result == 0)
                            showDialogDone();
                        else
                            new AlertDialog.Builder(PlayerYoutubeActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Save File Error")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                    }
                });
            }
        });

        String a = getIntent().getAction();
        if (a != null && a.equals(START_PAUSE)) {
            // pretend we already start it
            start = false;
            stopRecording(getString(R.string.pause));
        }

        stopRecording(getString(R.string.pause));

        receiver = new RecordingReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PAUSE_BUTTON);
        registerReceiver(receiver, filter);

        swEcho.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }


    private void showDialogDone() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Thông báo");
        // set dialog message
        alertDialogBuilder
                .setMessage("Bạn muốn nghe lại file ghi âm")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(PlayerYoutubeActivity.this, PlayBackRecordActivity.class);
                        intent.putExtra(VMSongKaraokeDetailFragment.KEY_BUNDLE_VIDEO_YOUTUB, recordFileCurrent);
                        SystemUtil.startActivity(PlayerYoutubeActivity.this, intent, false);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        mYouTubePlayer = youTubePlayer;

        mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
        mYouTubePlayer.setPlaybackEventListener(PlayerYoutubeActivity.this);
        mYouTubePlayer.setPlayerStateChangeListener(this);
        if (!b) {
            youTubePlayer.cueVideo(ID_YOUTUBE_LINK);
            youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
//            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, errorReason.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_REQUEST) {
            mYouTubePlayerView.initialize(BaseConstants.YOUTUBE_API_KEY, this);
        }
    }

    @Override
    public void onPlaying() {
        DebugLog.d(TAG, "onPlaying");
        pause.setAlpha(1.0f);
        pause.setEnabled(true);
        startRecording();
    }

    @Override
    public void onPaused() {
        DebugLog.d(TAG, "onPaused");
        stopRecording(getString(R.string.encoding));
    }

    @Override
    public void onStopped() {
        DebugLog.d(TAG, "onStopped");

    }

    @Override
    public void onBuffering(boolean b) {
        DebugLog.d(TAG, "onBuffering");
    }

    @Override
    public void onSeekTo(int i) {
        DebugLog.d(TAG, "onSeekTo");
    }


    @Override
    protected void onResume() {
        super.onResume();

        updateBufferSize(false);

        // start once
        if (start) {
            start = false;
            if (permitted()) {
//                startRecording();
            }
        }

        boolean recording = thread != null;
        RecordingService.startService(this, targetFile.getName(), recording);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        updateBufferSize(true);
    }


    void stopRecording(String status) {
        setState(status);
        pause.setImageResource(R.drawable.ic_mic_24dp);

        stopRecording();

        RecordingService.startService(this, targetFile.getName(), thread != null);
    }

    void stopRecording() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

//        sound.unsilent();
    }

    void setState(String s) {
        long free = storage.getFree(storage.getTempRecording());

        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);

        int rate = Integer.parseInt(shared.getString(MusicApplication.PREFERENCE_RATE, ""));
        int m = RawSamples.CHANNEL_CONFIG == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
        int c = RawSamples.AUDIO_FORMAT == AudioFormat.ENCODING_PCM_16BIT ? 2 : 1;

        long perSec = (c * m * rate);
        long sec = free / perSec * 1000;
        state.setText(s + "\n(" + ((MusicApplication) getApplication()).formatFree(free, sec) + ")");
    }

    void editCut() {
        if (editSample == -1)
            return;

        RawSamples rs = new RawSamples(storage.getTempRecording());
        rs.trunk(editSample + samplesUpdate);
        rs.close();
        loadSamples();
    }

    @Override
    public void onBackPressed() {
        cancelDialog(new Runnable() {
            @Override
            public void run() {
                stopRecording();
                storage.delete(storage.getTempRecording());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory");

        stopRecording();

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        RecordingService.stopService(this);

        if (pscl != null) {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(pscl, PhoneStateListener.LISTEN_NONE);
            pscl = null;
        }
    }

    void startRecording() {


        setState(getString(R.string.recording));

//        sound.silent();

        pause.setImageResource(R.drawable.ic_pause_24dp);

        mYouTubePlayer.play();
        if (thread != null) {
            thread.interrupt();
        }
        supportRecording = true;
        if (supportRecording) {
            supportRecording = false;
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                int p = android.os.Process.getThreadPriority(android.os.Process.myTid());

                if (p != android.os.Process.THREAD_PRIORITY_URGENT_AUDIO) {
                    Log.e(TAG, "Unable to set Thread Priority " + android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                }

                RawSamples rs = null;
                AudioRecord recorder = null;
                try {
                    rs = new RawSamples(storage.getTempRecording());

                    rs.open(samplesTime);

                    int min = AudioRecord.getMinBufferSize(sampleRate, RawSamples.CHANNEL_CONFIG, RawSamples.AUDIO_FORMAT);
                    if (min <= 0) {
                        throw new RuntimeException("Unable to initialize AudioRecord: Bad audio values");
                    }

                    // min = 1 sec
                    min = Math.max(sampleRate * (RawSamples.CHANNEL_CONFIG == AudioFormat.CHANNEL_IN_MONO ? 1 : 2), min);

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, RawSamples.CHANNEL_CONFIG, RawSamples.AUDIO_FORMAT, min);
                    DebugLog.e(TAG, "bufferSize = " + bufferSize);
                    DebugLog.e(TAG, "sampleRate = " + sampleRate);


                    if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                        throw new RuntimeException("Unable to initialize AudioRecord");
                    }

                    long start = System.currentTimeMillis();
                    recorder.startRecording();

                    int samplesTimeCount = 0;
                    // how many samples we need to update 'samples'. time clock. every 1000ms.
                    int samplesTimeUpdate = 1000 / 1000 * sampleRate * (RawSamples.CHANNEL_CONFIG == AudioFormat.CHANNEL_IN_MONO ? 1 : 2);

                    short[] buffer = null;

                    boolean stableRefresh = false;

                    while (!Thread.currentThread().isInterrupted()) {
                        synchronized (bufferSizeLock) {
                            if (buffer == null || buffer.length != bufferSize)
                                buffer = new short[bufferSize];
                        }

                        final int readSize = recorder.read(buffer, 0, buffer.length);
                        if (readSize <= 0) {
                            break;
                        }
                        long end = System.currentTimeMillis();

                        long diff = (end - start) * sampleRate / 1000;

                        start = end;

                        int s = RawSamples.CHANNEL_CONFIG == AudioFormat.CHANNEL_IN_MONO ? readSize : readSize / 2;

                        if (stableRefresh || diff >= s) {
                            stableRefresh = true;

                            rs.write(buffer);

                            for (int i = 0; i < readSize; i += samplesUpdate) {
                                final double dB = RawSamples.getDB(buffer, i, samplesUpdate);
                                handle.post(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }

                            samplesTime += s;
                            samplesTimeCount += s;
                            if (samplesTimeCount > samplesTimeUpdate) {
                                final long m = samplesTime;
                                handle.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateSamples(m);
                                    }
                                });
                                samplesTimeCount -= samplesTimeUpdate;
                            }
                        }
                    }
                } catch (final RuntimeException e) {
                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, Log.getStackTraceString(e));
                            Toast.makeText(PlayerYoutubeActivity.this, "AudioRecord error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } finally {
                    // redraw view, we may add one last pich which is not been drawen because draw tread already interrupted.
                    // to prevent resume recording jump - draw last added pitch here.
                    handle.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    if (rs != null)
                        rs.close();

                    if (recorder != null)
                        recorder.release();
                }
            }
        }, "RecordingThread");
        thread.start();

        RecordingService.startService(this, targetFile.getName(), thread != null);
    }

    // calcuale buffer length dynamically, this way we can reduce thread cycles when activity in background
    // or phone screen is off.
    void updateBufferSize(boolean pause) {
        synchronized (bufferSizeLock) {
            int samplesUpdate;

            if (pause) {
                // we need make buffer multiply of pitch.getPitchTime() (100 ms).
                // to prevent missing blocks from view otherwise:

                // file may contain not multiply 'samplesUpdate' count of samples. it is about 100ms.
                // we can't show on pitchView sorter then 100ms samples. we can't add partial sample because on
                // resumeRecording we have to apply rest of samplesUpdate or reload all samples again
                // from file. better then confusing user we cut them on next resumeRecording.

                long l = 1000;
                samplesUpdate = (int) (l * sampleRate / 1000.0);
            } else {
                samplesUpdate = this.samplesUpdate;
            }

            bufferSize = RawSamples.CHANNEL_CONFIG == AudioFormat.CHANNEL_IN_MONO ? samplesUpdate : samplesUpdate * 2;
            DebugLog.e(TAG, "bufferSize = " + bufferSize);
        }
    }

    void updateSamples(long samplesTime) {
        long ms = samplesTime / sampleRate * 1000;
        if (ms >= 10000) {
            btnDone.setEnabled(true);
            btnDone.setAlpha(1);
        }

        time.setText(MusicApplication.formatDuration(this, ms));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (permitted(permissions)) {
                    startRecording();
                } else {
                    Toast.makeText(this, R.string.not_permitted, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO
    };

    boolean permitted(String[] ss) {
        for (String s : ss) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    boolean permitted() {
        for (String s : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
                return false;
            }
        }
        return true;
    }

    EncoderInfo getInfo() {
        final int channels = RawSamples.CHANNEL_CONFIG == AudioFormat.CHANNEL_IN_STEREO ? 2 : 1;
        final int bps = RawSamples.AUDIO_FORMAT == AudioFormat.ENCODING_PCM_16BIT ? 16 : 8;

        return new EncoderInfo(channels, sampleRate, bps);
    }

    void encoding(final Runnable run) {
        final File in = storage.getTempRecording();
        final File out = targetFile;

        EncoderInfo info = getInfo();

        Encoder e = null;

        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        String ext = shared.getString(MusicApplication.PREFERENCE_ENCODING, "");

        if (ext.equals("wav")) {
            e = new FormatWAV(info, out);
        }
        if (ext.equals("m4a")) {
            e = new FormatM4A(info, out);
        }
        if (ext.equals("3gp")) {
            e = new Format3GP(info, out);
        }

        encoder = new FileEncoder(this, in, e);

        final ProgressDialog d = new ProgressDialog(this);
        d.setTitle(getString(R.string.encoding_title));
        d.setMessage(".../" + targetFile.getName());
        d.setMax(100);
        d.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        d.setIndeterminate(false);

        d.show();

        encoder.run(new Runnable() {
            @Override
            public void run() {
                d.setProgress(encoder.getProgress());
            }
        }, new Runnable() {
            @Override
            public void run() {
                d.cancel();
                storage.delete(in);

                SharedPreferences.Editor edit = shared.edit();
                edit.putString(MusicApplication.PREFERENCE_LAST, out.getName());
                edit.apply();

                run.run();
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PlayerYoutubeActivity.this, encoder.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                finish();
                new AlertDialog.Builder(PlayerYoutubeActivity.this)
                        .setTitle("Lỗi save file")
                        .setMessage("No space left on device")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }


    @Override
    public void finish() {
        super.finish();

//        PlayerYoutubeActivity.startActivity(this);
    }

    class PhoneStateChangeListener extends PhoneStateListener {
        public boolean wasRinging;
        public boolean pausedByCall;

        @Override
        public void onCallStateChanged(int s, String incomingNumber) {
            switch (s) {
                case TelephonyManager.CALL_STATE_RINGING:
                    wasRinging = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    wasRinging = true;
                    if (thread != null) {
                        stopRecording(getString(R.string.hold_by_call));
                        pausedByCall = true;
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (pausedByCall) {
                        startRecording();
                    }
                    wasRinging = false;
                    pausedByCall = false;
                    break;
            }
        }
    }


}
