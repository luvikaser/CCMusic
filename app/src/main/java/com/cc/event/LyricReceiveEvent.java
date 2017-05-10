package com.cc.event;

import java.io.File;

/**
 * Created by Luvi Kaser on 11/9/2016.
 */

public class LyricReceiveEvent {
    public boolean flag;
    public String title;
    public String fullLyric;
    public LyricReceiveEvent(boolean flag, String title, String fullLyric) {
        this.flag = flag;
        this.title = title;
        this.fullLyric = fullLyric;
    }
}
