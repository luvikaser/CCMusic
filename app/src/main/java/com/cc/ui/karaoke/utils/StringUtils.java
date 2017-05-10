package com.cc.ui.karaoke.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Author: NT
 * Since: 8/26/2016.
 */
public class StringUtils {
    public static String removeAccent(String data) {
        String temp = Normalizer.normalize(data, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
    }
}