package com.cc.ui.karaoke.ui.adapter.karaoke;

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
import com.cc.ui.karaoke.data.database.helper.karaoke.VMSongFavoriteScope;
import com.cc.ui.karaoke.data.database.helperImp.karaoke.VMSongFavoriteScopeImp;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongArirangTable;
import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.ui.activity.song.SongKaraokeDetailActivity;
import com.cc.ui.karaoke.ui.adapter.BaseCursorAdapter.VMCursorRecyclerAdapter;
import com.cc.ui.karaoke.utils.SystemUtil;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongKaraokeAdapter extends VMCursorRecyclerAdapter<VMSongKaraokeAdapter.MyHolder> {
    private ArrayList<VMSongKaraoke> VMSongKaraokeArrayList = new ArrayList<>();
    private Context mContext;
    private int mLayout;
    private int[] mFrom;
    private int[] mTo;
    private String[] mOriginalFrom;
    private OnClickItemSongListener listener = null;
    private VMSongFavoriteScope mVmSongFavoriteScope;

    public VMSongKaraokeAdapter(Context context, int layout, Cursor c, String[] from,
                                OnClickItemSongListener onClickItemSongListener) {
        super(c, VMSongArirangTable.PK_COLUMN);
        this.mContext = context;
        mLayout = layout;
        mOriginalFrom = from;
        this.listener = onClickItemSongListener;
        findColumns(c, from);
        mVmSongFavoriteScope = new VMSongFavoriteScopeImp();
    }

    public VMSongKaraokeAdapter(Context context, int layout, Cursor c, String[] from) {
        super(c, VMSongArirangTable.PK_COLUMN);
        this.mContext = context;
        mLayout = layout;
        mOriginalFrom = from;
        findColumns(c, from);
        mVmSongFavoriteScope = new VMSongFavoriteScopeImp();
    }

    public void setData(Cursor cursor) {
        this.mCursor = cursor;
    }

    /**
     * Create a map from an array of strings to an array of column-id integers in cursor c.
     * If c is null, the array will be discarded.
     *
     * @param c    the cursor to find the columns from
     * @param from the Strings naming the columns of interest
     */
    private void findColumns(Cursor c, String[] from) {
        if (c != null) {
            int i;
            int count = from.length;
            if (mFrom == null || mFrom.length != count) {
                mFrom = new int[count];
            }
            for (i = 0; i < count; i++) {
                mFrom[i] = c.getColumnIndexOrThrow(from[i]);
            }
        } else {
            mFrom = null;
        }
    }


    @Override
    public void onBindViewHolder(final MyHolder holder, final Cursor cursor, final int postion) {
        if(listener == null) {
            onBindViewHolderFavorite(holder,cursor, postion);
        } else {
            onBindViewHolderCommon(holder,cursor, postion);
        }
    }

    private void onBindViewHolderFavorite(final MyHolder holder, final Cursor cursor,
                                          final int postion) {
        VMSongKaraoke songKaraoke = parseCursor(cursor);
        holder.tvIdSong.setText(String.valueOf(cursor.getLong(cursor.getColumnIndex
                (mOriginalFrom[0]))));
        holder.tvTitleSong.setText(cursor.getString(cursor.getColumnIndex
                (mOriginalFrom[1])));
        holder.tvSongArtist.setText(cursor.getString(cursor.getColumnIndex
                (mOriginalFrom[2])));
        if (getCursor().getPosition() % 2 == 0) {
            holder.cardView.setCardBackgroundColor(Color.rgb(238, 233, 233));
        } else {
            holder.cardView.setCardBackgroundColor(Color.rgb(255, 255, 255));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(postion);
                SystemUtil.startActivity(mContext, SongKaraokeDetailActivity.getIntent
                        (mContext, parseCursor(mCursor)), false);
            }
        });

        holder.imgFavorite.setVisibility(View.GONE);
    }

    private void onBindViewHolderCommon(final MyHolder holder, final Cursor cursor,
                                          final int postion) {
        VMSongKaraoke songKaraoke = parseCursor(cursor);
        holder.tvIdSong.setText(String.valueOf(cursor.getLong(cursor.getColumnIndex
                (mOriginalFrom[0]))));
        holder.tvTitleSong.setText(cursor.getString(cursor.getColumnIndex
                (mOriginalFrom[1])));
        holder.tvSongArtist.setText(cursor.getString(cursor.getColumnIndex
                (mOriginalFrom[2])));
        if (getCursor().getPosition() % 2 == 0) {
            holder.cardView.setCardBackgroundColor(Color.rgb(238, 233, 233));
        } else {
            holder.cardView.setCardBackgroundColor(Color.rgb(255, 255, 255));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(postion);
                VMSongKaraoke clickVmSongKaraoke = parseCursor(mCursor);
                listener.onViewDetailSong(clickVmSongKaraoke);

            }
        });

        if (mVmSongFavoriteScope.isAddedFavorite(songKaraoke.getPk())) {
            holder.imgFavorite.setImageResource(R.drawable
                    .ic_favorite_red_600_24dp);
        } else {
            holder.imgFavorite.setImageResource(R.drawable
                    .ic_favorite_grey_600_24dp);
        }


        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(postion);
                VMSongKaraoke clickVmSongKaraoke = parseCursor(mCursor);
                if (mVmSongFavoriteScope.isAddedFavorite(clickVmSongKaraoke.getPk())) {
                    holder.imgFavorite.setImageResource(R.drawable
                            .ic_favorite_grey_600_24dp);
                    listener.onUnLikeSong(clickVmSongKaraoke.getPk());
                } else {
                    holder.imgFavorite.setImageResource(R.drawable
                            .ic_favorite_red_600_24dp);
                    listener.onLikeSong(clickVmSongKaraoke);
                }
            }
        });

    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mLayout, parent, false);
        return new MyHolder(v);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        findColumns(newCursor, mOriginalFrom);
        return super.swapCursor(newCursor);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }

    /**
     * Get the ID of the header associated with this item.  For example, if your headers group
     * items by their first letter, you could return the character representation of the first letter.
     * Return a value < 0 if the view should not have a header (like, a header view or footer view)
     *
     * @param position
     * @return
     *//*
    @Override
    public long getHeaderId(int position) {
      *//*  mCursor.moveToPosition(position);
        return parseCursor(mCursor).getsName().charAt(0);*//*
    }

    *//**
     * Creates a new ViewHolderRecord for a header.  This works the same way onCreateViewHolder in
     * Recycler.Adapter, ViewHolders can be reused for different views.  This is usually a good place
     * to inflate the layout for the header.
     *
     * @param parent
     * @return
     *//*
    @Override
    public RecyclerView.ViewHolderRecord onCreateHeaderViewHolder(ViewGroup parent) {
    *//*    View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolderRecord(view) {
        };*//*
    }

    */

    /**
     * Binds an existing ViewHolderRecord to the specified adapter position.
     *//*
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolderRecord holder, int position) {
        TextView textView = (TextView) holder.itemView;
        mCursor.moveToPosition(position);
        textView.setText(String.valueOf(parseCursor(mCursor).getsName().charAt(0)));
        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color
                .colorPrimary));
    }
*/


    public class MyHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public TextView tvIdSong;
        public TextView tvTitleSong;
        public TextView tvSongArtist;
        public ImageView imgFavorite;

        public MyHolder(View v) {
            super(v);
            tvIdSong = (TextView) v.findViewById(R.id.tv_song_id);
            tvTitleSong = (TextView) v.findViewById(R.id.tv_song_title);
            tvSongArtist = (TextView) v.findViewById(R.id.tv_song_artist);
            cardView = (CardView) v.findViewById(R.id.card_view);
            imgFavorite = (ImageView) v.findViewById(R.id.img_favorite);
        }
    }

    public VMSongKaraoke parseCursor(Cursor cursor) {
        VMSongKaraoke zs = new VMSongKaraoke();
        zs.setPk(cursor.getLong(cursor.getColumnIndex(VMSongArirangTable.PK_COLUMN)));
        zs.setSvol(cursor.getLong(cursor.getColumnIndex(VMSongArirangTable.SVOL_COLUMN)));
        zs.setEnt((byte) cursor.getInt(cursor.getColumnIndex(VMSongArirangTable.ENT_COLUMN)));
        zs.setOpt((byte) cursor.getInt(cursor.getColumnIndex(VMSongArirangTable
                .OPT_COLUMN)));
        zs.setRowid(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .ROWID_COLUMN)));
        zs.setSabbr(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SABBR_COLUMN)));
        zs.setsLanguage(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SLANGUAGE_COLUMN)));
        zs.setsLyric(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SLYRIC_COLUMN)));
        zs.setsLyricClean(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SLYRICCLEAN_COLUMN)));
        zs.setsManufacture(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SMANUFACTURE_COLUMN)));
        zs.setsMeta(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SMETA_COLUMN)));
        zs.setsMetaClean(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SMETACLEAN_COLUMN)));
        zs.setsName(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SNAME_COLUMN)));
        zs.setsNameClean(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SNAMECLEAN_COLUMN)));
/*        zs.setsFavorite(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .SFAVORITE_COLUMN)))*/
        zs.setsYoutube(cursor.getString(cursor.getColumnIndex(VMSongArirangTable
                .ZYOUTUBE_COLUMN)));
        return zs;
    }

    public interface OnClickItemSongListener {
        void onLikeSong(VMSongKaraoke vmSongKaraoke);

        void onUnLikeSong(long idSong);

        void onViewDetailSong(VMSongKaraoke vmSongKaraoke);
    }
}
