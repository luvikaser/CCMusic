package com.cc.ui.karaoke.utils.encoders;

/**
 * Author: axet
 * Since: 11/03/16.
 */
public class EncoderInfo {
    public int channels;
    public int sampleRate;
    public int bps;

    public EncoderInfo(int channels, int sampleRate, int bps) {
        this.channels = channels;
        this.sampleRate = sampleRate;
        this.bps = bps;
    }
}
