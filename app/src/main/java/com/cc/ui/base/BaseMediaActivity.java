package com.cc.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.data.MusicEnumApp;
import com.cc.domain.utils.LogHelper;
import com.cc.service.MusicService;
import com.cc.ui.album.AlbumActivity;
import com.cc.ui.artist.ArtistActivity;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.VMSongArirangFragment;
import com.cc.ui.search.SearchActivity;
import com.cc.ui.song.SongLocalActivity;
import com.cc.ui.yourmusic.YourMusicActivity;
import com.cc.ui.yourmusic.YourMusicFragment;

import butterknife.BindView;


/**
 * Author: NT
 * Since: 11/1/2016.
 */
public abstract class BaseMediaActivity extends BaseActivity {
    private static final String TAG = LogHelper.makeLogTag(BaseMediaActivity.class);
    private static final String SAVED_MEDIA_ID = "vn.cccorp.music.android.uamp.MEDIA_ID";
    private static final String FRAGMENT_TAG = "ccmusic_list_container";

    public static final String EXTRA_START_FULLSCREEN = "vn.cccorp.music.EXTRA_START_FULLSCREEN";

    /**
     * Optionally used with {@link #EXTRA_START_FULLSCREEN} to carry a MediaDescription to
     * the {@link com.cc.ui.playback.PlaybackControlFullScreenFragment}, speeding up the screen rendering
     * while the {@link android.support.v4.media.session.MediaControllerCompat} is connecting.
     */
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION = "vn.cccorp.music.CURRENT_MEDIA_DESCRIPTION";

    public static final String BUNDLE_MEDIA_TYPE = "BUNDLE_MEDIA_TYPE";//ALBUM, ARTIST,

    // SONG


    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;

    @BindView(R.id.controls_container_player)
    protected View viewPlaybackBottom;

    @BindView(R.id.tv_title_name)
    protected TextView mTitle;

    public View getViewPlaybackBottom() {
        return viewPlaybackBottom;
    }

    private void openActivitySearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        if(this instanceof AlbumActivity)
            intent.putExtra(BUNDLE_MEDIA_TYPE, MusicConstantsApp.MUSIC_TYPE_ALBUM);
        else if(this instanceof ArtistActivity)
            intent.putExtra(BUNDLE_MEDIA_TYPE, MusicConstantsApp.MUSIC_TYPE_ARTIST);
        else if(this instanceof SongLocalActivity)
            intent.putExtra(BUNDLE_MEDIA_TYPE, MusicConstantsApp.MUSIC_TYPE_SONGS);
        else if(this instanceof YourMusicActivity){
            Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if ( f instanceof YourMusicFragment) {
                intent.putExtra(BUNDLE_MEDIA_TYPE, MusicConstantsApp.MUSIC_TYPE_SONGS);
            } else if (f instanceof VMSongArirangFragment){
                intent.putExtra(BUNDLE_MEDIA_TYPE, MusicConstantsApp.MUSIC_TYPE_SONGS_KARA_ARIRANG);
            }
        }
        Log.e(TAG, "this = ------" + this.getClass().getSimpleName());

        startActivity(intent);
    }


    @Override
    protected int getResLayoutId() {
        return R.layout.activity_common_player_control;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MusicEnumApp.MusicType musicType = MusicService.getMusicTypeCurrent();
        if (musicType.type == MusicEnumApp.MusicType.ARTIST_ONLINE.type || musicType
                .type == MusicEnumApp.MusicType.SONG_ONLINE.type || musicType.type ==
                MusicEnumApp.MusicType.ALBUM_ONLINE.type) {
            return false;
        }
        return true; // false
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                openActivitySearch();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}