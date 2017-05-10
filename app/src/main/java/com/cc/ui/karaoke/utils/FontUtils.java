package com.cc.ui.karaoke.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Project : VuiGame (MePlay)
 * Author  : duyng
 * since   : 6/15/2016
 * company : vng
 */
public class FontUtils {
    public static Typeface getTypefaceFontIconAwesome(Context context) {
        Typeface fontAwesomeFont = Typeface.createFromAsset(context.getAssets(),
                "fontawesome-webfont.ttf");
        return fontAwesomeFont;
    }
}
