package com.cc.ui.album;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.MusicApplication;
import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.data.MusicEnumApp;
import com.cc.ui.base.BaseMediaFragment;
import com.cc.ui.song.SongLocalActivity;
import com.cc.ui.yourmusic.YourMusicActivity;
import com.cc.utils.CCMusicUtils;
import com.cc.utils.SortedListAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Author  : duyng
 * since   : 10/24/2016
 */

public class AlbumsAdapter extends SortedListAdapter<MediaBrowserCompat.MediaItem> {
    private String TAG = "AlbumsAdapter";
    private List<MediaBrowserCompat.MediaItem> mListAlbum;
    private Context context;

    public AlbumsAdapter(Context context, Comparator<MediaBrowserCompat.MediaItem> comparator) {
        super(context, MediaBrowserCompat.MediaItem.class, comparator);
        this.context = context;
    }

    public void addList(List<MediaBrowserCompat.MediaItem> listAlbum) {
        this.mListAlbum = listAlbum;
    }


    @Override
    protected ViewHolder<? extends MediaBrowserCompat.MediaItem> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        int layout = R.layout.row_albums;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    protected boolean areItemsTheSame(MediaBrowserCompat.MediaItem item1, MediaBrowserCompat.MediaItem item2) {
        return item1.getDescription().getTitle() == item2.getDescription().getTitle();
    }

    @Override
    protected boolean areItemContentsTheSame(MediaBrowserCompat.MediaItem oldItem, MediaBrowserCompat.MediaItem newItem) {
        return oldItem.equals(newItem);
    }



    public class ItemViewHolder extends SortedListAdapter.ViewHolder<MediaBrowserCompat.MediaItem> {
        @BindView(R.id.tv_album_names)
        TextView tvAlbumName;

        @BindView(R.id.tv_song_count)
        TextView tvSongCount;

        @BindView(R.id.img_albums)
        ImageView ivAlbum;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        protected void performBind(MediaBrowserCompat.MediaItem item) {
            final MediaDescriptionCompat mediaDescriptionCompat = item.getDescription();
            if (null == tvAlbumName) return;
            tvAlbumName.setText(mediaDescriptionCompat.getTitle().toString());
            if (musicType.type == MusicEnumApp.MusicType.ALBUM_ONLINE.type) {
                tvSongCount.setVisibility(View.GONE);
            }
            tvSongCount.setText(mediaDescriptionCompat.getDescription().toString());
            Log.e(TAG, mediaDescriptionCompat.getTitle().toString());
            String[] columns = {android.provider.MediaStore.Audio.Albums._ID,
                    android.provider.MediaStore.Audio.Albums.ALBUM };

            String where = android.provider.MediaStore.Audio.Media.ALBUM + "=?";
            String whereVal[] = {mediaDescriptionCompat.getTitle().toString()};
            Cursor cursor = null;
            try{
                cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, columns, where, whereVal, null);

                if (cursor.moveToFirst()){
                    long id = cursor.getLong(cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums._ID));
                    Log.e(TAG, id+"");
                    ImageLoader.getInstance().displayImage(CCMusicUtils.getAlbumArtUri(id).toString(), ivAlbum,
                            new DisplayImageOptions.Builder().cacheInMemory(true)
                                    .showImageOnFail(R.drawable.ic_album_default)
                                    .resetViewBeforeLoading(true)
                                    .displayer(new FadeInBitmapDisplayer(400))
                                    .build(), new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {

                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    ivAlbum.setImageResource(R.drawable.ic_album_default);
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {

                                }
                            });
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SongLocalActivity.class);
                    intent.putExtra(BaseMediaFragment.ARG_MEDIA_ID, mediaDescriptionCompat
                            .getMediaId());
                    intent.putExtra(MusicConstantsApp.BUNDLE_TITLE_SONG_PAGE, mediaDescriptionCompat
                            .getTitle());
                    SongLocalActivity.start(view.getContext(), intent);
                }
            });

        }
    }
}
