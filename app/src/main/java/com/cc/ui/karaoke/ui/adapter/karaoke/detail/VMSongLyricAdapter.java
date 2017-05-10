package com.cc.ui.karaoke.ui.adapter.karaoke.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import mmobile.com.karaoke.R;
import com.cc.ui.karaoke.data.model.services.response.VMSong;
import com.cc.ui.karaoke.data.model.services.response.song.VMSearchSongResponse;
import com.cc.ui.karaoke.utils.DebugLog;

/**
 * Author: NT
 * Since: 6/21/2016.
 */
public class VMSongLyricAdapter extends RecyclerView.Adapter<VMSongLyricAdapter.ViewHolder>{

    private static final String TAG = VMSongLyricAdapter.class.getSimpleName();
    private Context context;
    private List<VMSong> listDataArtist = new ArrayList<>();
    private OnClickItemView listener;
    public VMSongLyricAdapter(Context context, VMSearchSongResponse listData,
                              OnClickItemView onClickItemView) {
        this.context = context;
        this.listDataArtist = listData.getListMsg().get(3).getVmSongList();
        DebugLog.d(TAG, "size list = " + this.listDataArtist.size());
        this.listener = onClickItemView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_song_lyric,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DebugLog.d(TAG, "************** = " + listDataArtist.get(position).getArtist());
        holder.tvArtist.setText(listDataArtist.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return listDataArtist.size();
    }

      class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_artist)
        TextView tvArtist;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickArtist(listDataArtist.get(getPosition()));
                }
            });
        }
    }

    public interface OnClickItemView {
        void onClickArtist(VMSong song);
    }
}
