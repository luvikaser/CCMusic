package com.cc.ui.karaoke.ui.adapter.record;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmobile.com.karaoke.R;

import com.cc.MusicApplication;
import com.cc.ui.karaoke.data.database.table.record.VMRecordTable;
import com.cc.ui.karaoke.data.model.record.VMRecordFile;
import com.cc.ui.karaoke.ui.activity.player.PlayBackRecordActivity;
import com.cc.ui.karaoke.ui.adapter.BaseCursorAdapter.VMCursorRecyclerAdapter;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.detail.VMSongKaraokeDetailFragment;
import com.cc.ui.karaoke.utils.SystemUtil;

/**
 * Author: NT
 * Since: 9/8/2016.
 */
public class VMRecordListAdapter extends VMCursorRecyclerAdapter<VMRecordListAdapter.ViewHolderRecord> {
    public String[] column = new String[]{
            VMRecordTable.COL_ID,
            VMRecordTable.COL_NAME,
            VMRecordTable.COL_FILE_PATH,
            VMRecordTable.COL_YOUTUBE_ID,
            VMRecordTable.COL_DATE_CREATE
    };

    private Context mContext;
    private int mLayout;
    private int[] mFrom;
    private int[] mTo;
    private String[] mOriginalFrom;

    public VMRecordListAdapter(Cursor c, Context context, int layout) {
        super(c, VMRecordTable.COL_ID);
        this.mContext = context;
        this.mLayout = layout;
        this.mOriginalFrom = column;
        this.mLayout = layout;
        findColumns(c, mOriginalFrom);
    }


    public long getDuration(File file) {
        MediaPlayer mp = MediaPlayer.create(mContext, Uri.fromFile(file));
        return (mp.getDuration());
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
    public Cursor swapCursor(Cursor newCursor) {
        findColumns(newCursor, mOriginalFrom);
        return super.swapCursor(newCursor);
    }

    @Override
    public void onBindViewHolder(ViewHolderRecord holder, Cursor cursor, int position) {
        final VMRecordFile recordFile = new VMRecordFile(cursor);
        File file = new File(recordFile.getFilePath());
        if (file.exists()) {
            holder.tvTitle.setText(recordFile.getName());
            holder.tvTime.setText(recordFile.getDate());
            holder.tvDuration.setText(MusicApplication.formatDuration(mContext, getDuration(file)));
            holder.tvSize.setText(MusicApplication.formatSize(mContext, file.length()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PlayBackRecordActivity.class);
                    intent.putExtra(VMSongKaraokeDetailFragment.KEY_BUNDLE_VIDEO_YOUTUB, recordFile);
                    SystemUtil.startActivity(mContext, intent, false);
                }
            });
        }

    }

    @Override
    public ViewHolderRecord onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        return new ViewHolderRecord(view);
    }

    public class ViewHolderRecord extends RecyclerView.ViewHolder {
        @BindView(R.id.recording_title)
        public TextView tvTitle;

        @BindView(R.id.recording_time)
        public TextView tvTime;

        @BindView(R.id.recording_duration)
        public TextView tvDuration;

        @BindView(R.id.recording_size)
        public TextView tvSize;

        public ViewHolderRecord(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}