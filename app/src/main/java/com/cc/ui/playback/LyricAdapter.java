package com.cc.ui.playback;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cc.app.R;

import java.util.List;

import cn.zhaiyifan.lyric.model.Lyric;

/**
 * Created by Luvi Kaser on 11/16/2016.
 */

public class LyricAdapter extends ArrayAdapter<Lyric.Sentence> {
    private int positionHighlight = 0;
    public LyricAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public LyricAdapter(Context context, int resource, List<Lyric.Sentence> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_lyric, null);
        }

        Lyric.Sentence p = getItem(position);

        if (p != null) {
            TextView lyric = (TextView) v.findViewById(R.id.row_lyric);

            if (lyric != null) {
                lyric.setText(p.content);
            }

            if (position == positionHighlight){
                lyric.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                lyric.setTypeface(null, Typeface.BOLD);
            } else{
                lyric.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                lyric.setTypeface(null, Typeface.NORMAL);
            }
        }

        return v;
    }

    public void setPositionHighlight(int position){
        positionHighlight = position;
        notifyDataSetChanged();
    }

}
