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

public class LyricCache {
    public final static String ERROR_STATEMENT = "[10:00.00]Không tìm thấy lời của bài hát này";

    public static File getFileCache(){
        File sdcard = Environment.getExternalStorageDirectory();
        File dir = new File(sdcard.getAbsolutePath() + "/lyricCCMusic/");
        dir.mkdir();
        return new File(dir, "cache.lrc");
    }

    public static void createFileCache(String s){
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(getFileCache());
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

    public static void createFileCacheError(){
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(getFileCache());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            os.write(ERROR_STATEMENT.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFileCache(){
        getFileCache().delete();
    }
}
