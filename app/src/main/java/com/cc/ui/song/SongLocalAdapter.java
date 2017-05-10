package com.cc.ui.song;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.MusicApplication;
import com.cc.app.R;
import com.cc.helper.music.MediaBrowserProvider;
import com.cc.helper.music.MediaIDHelper;
import com.cc.lastfmapi.LastFmClient;
import com.cc.lastfmapi.callbacks.ArtistInfoListener;
import com.cc.lastfmapi.models.ArtistQuery;
import com.cc.lastfmapi.models.LastfmArtist;
import com.cc.provider.music.MusicProvider;
import com.cc.service.MusicService;
import com.cc.ui.image.CircleImageView;
import com.cc.ui.yourmusic.YourMusicActivity;
import com.cc.utils.AlbumArtCache;
import com.cc.utils.CCMusicUtils;
import com.cc.utils.SortedListAdapter;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Author  : duyng
 * since   : 10/24/2016
 */

public class SongLocalAdapter extends SortedListAdapter<MediaBrowserCompat.MediaItem> {
    private String TAG = this.getClass().getSimpleName();
    private MediaFragmentListener mMediaFragmentListener;
    private Context context;
    static final int STATE_INVALID = -1;
    static final int STATE_NONE = 0;
    static final int STATE_PLAYABLE = 1;
    static final int STATE_PAUSED = 2;
    static final int STATE_PLAYING = 3;

    public SongLocalAdapter(Context context, Comparator<MediaBrowserCompat.MediaItem>
            comparator, MediaFragmentListener mediaFragmentListener) {
        super(context, MediaBrowserCompat.MediaItem.class, comparator);
        this.mMediaFragmentListener = mediaFragmentListener;
        this.context = context;
    }


    @Override
    protected ViewHolder<? extends MediaBrowserCompat.MediaItem> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song, parent, false);
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
        @BindView(R.id.img_avatar_song)
        CircleImageView imgAvatar;

        @BindView(R.id.img_animation_play)
        ImageView imgAnimPlay;

        @BindView(R.id.tv_song_title)
        TextView tvSongTitle;

        @BindView(R.id.tv_song_sub_title)
        TextView tvSongSubTitle;

        @BindView(R.id.img_more_option)
        ImageView imgMoreOption;


        AnimationDrawable animationPlaying;
        String imageArtistUrl = MusicApplication.getInstance().getString(R.string.preview_image_url);

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (sColorStateNotPlaying == null || sColorStatePlaying == null) {
                initializeColorStateLists(itemView.getContext());
            }

            animationPlaying = (AnimationDrawable)
                    ContextCompat.getDrawable(itemView.getContext(), R.drawable
                            .ic_equalizer_white_36dp);


        }

        @Override
        protected void performBind(final MediaBrowserCompat.MediaItem item) {
            Log.e("sss", "performBind" + item.getDescription().getTitle());
            int itemState = STATE_NONE;
            if (item.isPlayable()) {
                itemState = STATE_PLAYABLE;
                MediaControllerCompat controller = ((FragmentActivity) itemView.getContext())
                        .getSupportMediaController();
                if (controller != null && controller.getMetadata() != null) {
                    String currentPlaying = controller.getMetadata().getDescription().getMediaId();
                    String musicId = MediaIDHelper.extractMusicIDFromMediaID(
                            item.getDescription().getMediaId());
                    if (currentPlaying != null && currentPlaying.equals(musicId)) {
                        PlaybackStateCompat pbState = controller.getPlaybackState();
                        if (pbState == null ||
                                pbState.getState() == PlaybackStateCompat.STATE_ERROR) {
                            itemState = STATE_NONE;
                        } else if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                            itemState = STATE_PLAYING;
                        } else {
                            itemState = STATE_PAUSED;
                        }
                    }
                }
            }

            MediaDescriptionCompat mediaDescriptionCompat = item.getDescription();
            tvSongTitle.setText(mediaDescriptionCompat.getTitle().toString());
            if (!TextUtils.isEmpty(mediaDescriptionCompat.getSubtitle()))
                tvSongSubTitle.setText(mediaDescriptionCompat.getSubtitle().toString());


            CCMusicUtils.getImage(mediaDescriptionCompat.getTitle().toString(), imgAvatar);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.isPlayable()) {
                        Log.e(TAG, item.getDescription().getTitle().toString());
                        mMediaFragmentListener.onMediaItemSelected(item, getAdapterPosition());
                        notifyDataSetChanged();
                    }
                }
            });

            LastFmClient.getInstance(context).getArtistInfo(new ArtistQuery(tvSongSubTitle.getText().toString()), new ArtistInfoListener() {
                @Override
                public void artistInfoSucess(LastfmArtist artist) {
                    if (artist != null && artist.mArtwork != null && !artist.mArtwork.get(2).mUrl.equals("")) {
                        imageArtistUrl = artist.mArtwork.get(2).mUrl;
                    }
                }

                @Override
                public void artistInfoFailed() {

                }
            });

            imgMoreOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context wrapper = new ContextThemeWrapper(context, R.style.PopupMenu);
                    PopupMenu popup = new PopupMenu(wrapper, imgMoreOption);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu_option, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            ShareDialog shareDialog = new ShareDialog((Activity) context);
                            shareDialog.registerCallback(YourMusicActivity.callbackManager, new FacebookCallback<Sharer.Result>() {
                                @Override
                                public void onSuccess(Sharer.Result result) {

                                }

                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onError(FacebookException error) {

                                }
                            });
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(MusicApplication.getInstance().getString(R.string.link_play_store)))
                                        .setImageUrl(Uri.parse(imageArtistUrl))
                                        .setContentTitle(tvSongTitle.getText().toString())
                                        .setContentDescription(tvSongSubTitle.getText().toString())
                                        .build();

                                shareDialog.show(content);
                            }
                            return true;
                        }
                    });

                    popup.show();
                    if (popup.getDragToOpenListener() instanceof ListPopupWindow.ForwardingListener)
                    {
                        ListPopupWindow.ForwardingListener listener = (ListPopupWindow.ForwardingListener) popup.getDragToOpenListener();
                        listener.getPopup().setVerticalOffset(- 4 * imgMoreOption.getHeight());
                        listener.getPopup().show();
                    }
                }
            });

            switch (itemState) {
                case STATE_PLAYABLE:
                    Drawable pauseDrawable = ContextCompat.getDrawable(itemView.getContext(), R
                            .drawable
                            .ic_play_arrow_black_36dp);
                    DrawableCompat.setTintList(pauseDrawable, sColorStateNotPlaying);
                    imgAnimPlay.setImageDrawable(pauseDrawable);
                    imgAnimPlay.setVisibility(View.VISIBLE);
                    break;
                case STATE_PLAYING:
                    AnimationDrawable animation = (AnimationDrawable)
                            ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_equalizer_white_36dp);
                    DrawableCompat.setTintList(animation, sColorStatePlaying);
                    imgAnimPlay.setImageDrawable(animation);
                    imgAnimPlay.setVisibility(View.VISIBLE);
                    animation.start();
                    mMediaFragmentListener.scrollToItemPlay(getAdapterPosition());
                    break;
                case STATE_PAUSED:
                    Drawable playDrawable = ContextCompat.getDrawable(itemView.getContext(),
                            R.drawable.ic_equalizer1_white_36dp);
                    DrawableCompat.setTintList(playDrawable, sColorStatePlaying);
                    imgAnimPlay.setImageDrawable(playDrawable);
                    imgAnimPlay.setVisibility(View.VISIBLE);
                    mMediaFragmentListener.scrollToItemPlay(getAdapterPosition());
                    break;
                default:
                    imgAnimPlay.setVisibility(View.GONE);
            }
        }
    }

    public interface MediaFragmentListener extends MediaBrowserProvider {
        void onMediaItemSelected(MediaBrowserCompat.MediaItem item, int adapterPosition);

        void scrollToItemPlay(int position);
    }

    private static ColorStateList sColorStatePlaying;
    private static ColorStateList sColorStateNotPlaying;

    static private void initializeColorStateLists(Context ctx) {
        sColorStateNotPlaying = ColorStateList.valueOf(ctx.getResources().getColor(
                R.color.colorPrimary));
        sColorStatePlaying = ColorStateList.valueOf(ctx.getResources().getColor(
                R.color.colorPrimary));
    }


}
