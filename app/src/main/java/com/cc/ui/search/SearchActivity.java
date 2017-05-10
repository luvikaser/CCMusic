package com.cc.ui.search;

import android.os.Bundle;
import android.util.Log;

import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.data.MusicEnumApp;
import com.cc.ui.base.BaseFragment;
import com.cc.ui.base.BaseMediaActivity;
import com.cc.ui.base.BaseSearchToolbarActivity;

/**
 * Author: NT
 * Since: 11/26/2016.
 */
public class SearchActivity extends BaseSearchToolbarActivity {
    @Override
    public BaseFragment getFragmentToHost() {
        int typeMedia = getIntent().getExtras().getInt(BaseMediaActivity.BUNDLE_MEDIA_TYPE);
          if (typeMedia == MusicConstantsApp.MUSIC_TYPE_ALBUM) {
             return SearchAlbumFragment.newInstance(new SearchAlbumFragment(),
                    getIntent().getExtras());
        } else if (typeMedia == MusicConstantsApp.MUSIC_TYPE_ARTIST) {
             return SearchArtistFragment.newInstance(new SearchArtistFragment(),
                    getIntent().getExtras());
        } else if (typeMedia == MusicConstantsApp.MUSIC_TYPE_SONGS_KARA_ARIRANG) {
              return SearchSongFragment.newInstance(new SearchSongKaraArirangFragment(),
                      getIntent().getExtras());
          }
        return SearchSongFragment.newInstance(new SearchSongFragment(),
                getIntent().getExtras());
    }



}