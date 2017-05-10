package com.cc.ui.karaoke.utils.encoders;

public interface Encoder {
    public void encode(short[] buf);

    public void close();
}
