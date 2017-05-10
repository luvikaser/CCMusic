package com.cc.ui.karaoke.ui.adapter.devices;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mmobile.com.karaoke.R;
import com.cc.ui.karaoke.data.model.local.SongDevicesData;
import com.cc.ui.karaoke.ui.adapter.BaseCursorAdapter.VMCursorRecyclerAdapter;
import com.cc.ui.karaoke.utils.FontUtils;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongDevicesAdapter extends VMCursorRecyclerAdapter<VMSongDevicesAdapter.MyHolder> {

    private ArrayList<SongDevicesData> zSongArrayList = new ArrayList<>();
    private Context mContext;
    private int mLayout;
    private String[] mOriginalForm;
    private OnClickItemListener listener;

    public VMSongDevicesAdapter(Context context, int layout, Cursor cursor, String[] columns, String
            columnId) {
        super(cursor, columnId);
        this.mContext = context;
        this.mLayout = layout;
        this.mOriginalForm = columns;
    }

    public void setOnClickItemInList(OnClickItemListener listener) {
        this.listener = listener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, Cursor cursor, final int postion) {
        holder.tvTitleSong.setText(cursor.getString(cursor.getColumnIndex(mOriginalForm[0])));
        holder.tvSongArtist.setText(cursor.getString(cursor.getColumnIndex(mOriginalForm[1])));
        holder.tvAlbum.setText(cursor.getString(cursor.getColumnIndex(mOriginalForm[2])));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(postion);
            }
        });

        if(getCursor().getPosition() % 2 == 0){
            holder.cardView.setCardBackgroundColor(Color.rgb(238, 233, 233));
        }
        else {
            holder.cardView.setCardBackgroundColor(Color.rgb(255, 255, 255));
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ImageView imgThumbnail;
        public TextView tvThumbnail;
        public TextView tvTitleSong;
        public TextView tvSongArtist;
        public TextView tvAlbum;
        public CardView cardView;

        public MyHolder(View v) {
            super(v);
            imgThumbnail = (ImageView) v.findViewById(R.id.img_song_image);
            tvTitleSong = (TextView) v.findViewById(R.id.tv_song_title);
            tvThumbnail = (TextView) v.findViewById(R.id.tv_song_image);
            tvThumbnail.setTypeface(FontUtils.getTypefaceFontIconAwesome(mContext));
            tvSongArtist = (TextView) v.findViewById(R.id.tv_song_artist);
            tvAlbum = (TextView) v.findViewById(R.id.tv_song_album);
            cardView = (CardView) v.findViewById(R.id.card_view);
        }
    }

    public interface OnClickItemListener{
        void onClickItem(int position);
    }
}
