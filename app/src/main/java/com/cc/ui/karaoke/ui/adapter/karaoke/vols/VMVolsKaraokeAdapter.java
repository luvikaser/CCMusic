

package com.cc.ui.karaoke.ui.adapter.karaoke.vols;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mmobile.com.karaoke.R;
import com.cc.ui.karaoke.data.model.karaoke.VMVolsKaraoke;
import com.cc.ui.karaoke.utils.DebugLog;
import com.cc.ui.karaoke.utils.FontUtils;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/19/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class VMVolsKaraokeAdapter extends ArrayAdapter<VMVolsKaraoke> {
    private  LayoutInflater inflater = null;

    public VMVolsKaraokeAdapter(Context context, List<VMVolsKaraoke> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.row_menu_filter, parent, false);
        TextView title = (TextView) vi.findViewById(R.id.tv_name_vols);
        TextView tvChecked = (TextView) vi.findViewById(R.id.tv_checked);// title
        tvChecked.setTypeface(FontUtils.getTypefaceFontIconAwesome(parent.getContext()));
        title.setText(getItem(position).getName());
        DebugLog.e("getView", SystemClock.currentThreadTimeMillis() + "getView");
        return vi;
    }
}

