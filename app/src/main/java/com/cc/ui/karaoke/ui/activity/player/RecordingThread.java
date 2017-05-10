/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.cc.ui.karaoke.ui.activity.player;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecordingThread {
    private static final String LOG_TAG = RecordingThread.class.getSimpleName();
    private static final int SAMPLE_RATE = 44100;
    private FileOutputStream fileOutputStream;

     public static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    public static final int CHANNEL_IN_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_CONFIG, AUDIO_FORMAT);
    public static final String AUDIO_RECORDING_FILE_NAME = "recording.raw";

    public RecordingThread(String file, AudioDataReceivedListener listener) {
        this.fileOutput = file;
        mListener = listener;
        try {
            fileOutputStream = new FileOutputStream(fileOutput);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean mShouldContinue;
    private AudioDataReceivedListener mListener;
    private Thread mThread;
    private String fileOutput;

    public boolean recording() {
        return mThread != null;
    }

    public void startRecording() {
        if (mThread != null)
            return;

        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        mThread.start();
    }

    public void stopRecording() {
        if (mThread == null)
            return;

        mShouldContinue = false;
        mThread = null;
    }

    private void record() {
     /*   Log.v(LOG_TAG, "Start");
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        byte[] audioBuffer = new byte[bufferSize];
//        byte[] byteBuffer = new byte[bufferSize / 2];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!");
            return;
        }

        record.startRecording();

        Log.v(LOG_TAG, "Start recording");

        long shortsRead = 0;
        while (mShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;


            if (numberOfShort == AudioRecord.ERROR_INVALID_OPERATION ||
                    numberOfShort == AudioRecord.ERROR_BAD_VALUE) {
                Log.e(LOG_TAG, "Error reading audio data!");
                return;
            }

            // Notify waveform
            mListener.onAudioDataReceived(audioBuffer);
            // // writes the data to file from buffer
            // // stores the voice buffer

            try {
                fileOutputStream.write(audioBuffer, 0,audioBuffer.length);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }

        record.stop();
        record.release();*/
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        Log.v(LOG_TAG, "Starting recording…");

        byte audioData[] = new byte[BUFFER_SIZE];
        AudioRecord recorder = new AudioRecord(AUDIO_SOURCE,
                SAMPLE_RATE, CHANNEL_IN_CONFIG,
                AUDIO_FORMAT, BUFFER_SIZE);
        recorder.startRecording();

        String filePath = Environment.getExternalStorageDirectory().getPath()
                + "/" + AUDIO_RECORDING_FILE_NAME;
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(filePath));
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "File not found for recording ", e);
        }

        while (mShouldContinue) {
            int status = recorder.read(audioData, 0, audioData.length);

            if (status == AudioRecord.ERROR_INVALID_OPERATION ||
                    status == AudioRecord.ERROR_BAD_VALUE) {
                Log.e(LOG_TAG, "Error reading audio data!");
                return;
            }

            try {
                assert os != null;
                os.write(audioData, 0, audioData.length);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error saving recording ", e);
                return;
            }
        }

        recorder.stop();
        recorder.release();
        /*Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", status));*/
    }

    public void closeFile() {
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileOutputStream = null;
        }

    }
}
