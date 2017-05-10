package com.cc.ui.karaoke.ui.widget.progress;

import android.content.res.Resources;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/14/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class UtilsProgress {
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
