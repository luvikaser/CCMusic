package com.cc.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author  : Luvi Kaser
 * since   : 11/9/2016
 */

public class LyricUtils {

    public static File getFileCache(String name){
        File sdcard = Environment.getExternalStorageDirectory();
        File dir = new File(sdcard.getAbsolutePath() + "/lyricCCMusic/");
        dir.mkdir();
        return new File(dir, name + ".lrc");
    }

    public static void writeData(File file, String s){
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            os.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
