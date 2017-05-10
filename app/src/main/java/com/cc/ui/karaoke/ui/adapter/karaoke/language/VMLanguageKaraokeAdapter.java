package com.cc.ui.karaoke.ui.adapter.karaoke.language;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mmobile.com.karaoke.R;
import com.cc.ui.karaoke.data.model.karaoke.VMLanguageKaraoke;
import com.cc.ui.karaoke.utils.FontUtils;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/19/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class VMLanguageKaraokeAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<VMLanguageKaraoke> list = new ArrayList<>();
    private static LayoutInflater inflater = null;

    public VMLanguageKaraokeAdapter(Context mContext, ArrayList<VMLanguageKaraoke> listLangauge) {
        this.mContext = mContext;
        this.list = listLangauge;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.row_menu_filter, parent, false);
        TextView title = (TextView) vi.findViewById(R.id.tv_name_vols);
        TextView tvChecked = (TextView) vi.findViewById(R.id.tv_checked);// title
        tvChecked.setTypeface(FontUtils.getTypefaceFontIconAwesome(mContext));
        title.setText(list.get(position).getName());

        return vi;
    }
}
