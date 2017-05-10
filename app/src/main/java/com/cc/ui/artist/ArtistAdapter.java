package com.cc.ui.artist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.data.MusicEnumApp;
import com.cc.lastfmapi.LastFmClient;
import com.cc.lastfmapi.callbacks.ArtistInfoListener;
import com.cc.lastfmapi.models.ArtistQuery;
import com.cc.lastfmapi.models.LastfmArtist;
import com.cc.ui.album.AlbumsAdapter;
import com.cc.ui.song.SongLocalActivity;
import com.cc.ui.base.BaseMediaFragment;
import com.cc.utils.SortedListAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Author  : duyng
 * since   : 10/24/2016
 */

public class ArtistAdapter extends SortedListAdapter<MediaBrowserCompat.MediaItem> {
    private String TAG = "AlbumsAdapter";
    private List<MediaBrowserCompat.MediaItem> mListArtist;
    private Context context;

    public ArtistAdapter(Context context, Comparator<MediaBrowserCompat.MediaItem> comparator) {
        super(context, MediaBrowserCompat.MediaItem.class, comparator);
        this.context = context;
    }

    @Override
    protected ViewHolder<? extends MediaBrowserCompat.MediaItem> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        int layout = R.layout.row_artist;
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
        @BindView(R.id.tv_artist_names)
        TextView tvArtistName;

        @BindView(R.id.tv_song_count)
        TextView tvSongCount;

        @BindView(R.id.img_artist)
        ImageView ivArtist;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        protected void performBind(MediaBrowserCompat.MediaItem item) {
            final MediaDescriptionCompat mediaDescriptionCompat = item.getDescription();
            if (null == tvArtistName) return;
            tvArtistName.setText(mediaDescriptionCompat.getTitle().toString());
            if (musicType.type == MusicEnumApp.MusicType.ARTIST_ONLINE.type) {
                tvSongCount.setVisibility(View.GONE);
            }
            tvSongCount.setText(mediaDescriptionCompat.getDescription().toString());
            String nameArtist = mediaDescriptionCompat.getTitle().toString();
            while (nameArtist.contains("-")) {
                nameArtist = nameArtist.substring(0, nameArtist.indexOf("-"));
            }
            while (nameArtist.contains(",")) {
                nameArtist = nameArtist.substring(0, nameArtist.indexOf(","));
            }

            while (nameArtist.contains(".")) {
                nameArtist = nameArtist.substring(0, nameArtist.indexOf("."));
            }
            LastFmClient.getInstance(context).getArtistInfo(new ArtistQuery(nameArtist), new ArtistInfoListener() {
                @Override
                public void artistInfoSucess(LastfmArtist artist) {
                    if (artist != null && artist.mArtwork != null && !artist.mArtwork.get(2).mUrl.equals("")) {
                        ImageLoader.getInstance().displayImage(artist.mArtwork.get(2).mUrl, ivArtist,
                                new DisplayImageOptions.Builder().cacheInMemory(true)
                                        .cacheOnDisk(true)
                                        .showImageOnFail(R.drawable.ic_artist_default)
                                        .resetViewBeforeLoading(true)
                                        .displayer(new FadeInBitmapDisplayer(400))
                                        .build(), new SimpleImageLoadingListener(){
                                    @Override
                                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                        super.onLoadingFailed(imageUri, view, failReason);
                                        ivArtist.setImageResource(R.drawable.ic_artist_default);
                                    }
                                });
                }
                }

                @Override
                public void artistInfoFailed() {

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SongLocalActivity.class);
                    intent.putExtra(BaseMediaFragment.ARG_MEDIA_ID, mediaDescriptionCompat
                            .getMediaId());
                    intent.putExtra(MusicConstantsApp.BUNDLE_ARTIST_NAME, mediaDescriptionCompat
                            .getTitle());
                    intent.putExtra(MusicConstantsApp.BUNDLE_TITLE_SONG_PAGE, mediaDescriptionCompat
                            .getTitle());
                    SongLocalActivity.start(view.getContext(), intent);
                }
            });

        }
    }
}
