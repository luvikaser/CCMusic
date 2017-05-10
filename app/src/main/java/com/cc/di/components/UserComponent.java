package com.cc.di.components;

import com.cc.data.database.DatabaseBaseManager;
import com.cc.di.modules.MusicApiModule;
import com.cc.di.modules.UserModule;
import com.cc.di.scope.UserScope;
import com.cc.domain.model.User;
import com.cc.domain.repository.MusicApiRepository;
import com.cc.domain.repository.SongLocalFavoriteRepository;
import com.cc.domain.repository.SongLocalPlayedRepository;
import com.cc.service.MusicService;
import com.cc.ui.playback.PlaybackControlFullScreenFragment;
import com.cc.ui.search.BaseFragmentSearch;
import com.cc.ui.search.SearchOnlineManager;
import com.cc.ui.song.SongLocalFragment;

import dagger.Subcomponent;

/**
 * Author: NT
 * Since: 10/27/2016.
 */

@UserScope
@Subcomponent(
        modules = {
                UserModule.class,
                MusicApiModule.class
        }
)
public interface UserComponent {
    User getMyUser();

    DatabaseBaseManager getDatabaseBaseManager();

    SearchOnlineManager searchOnlineManager();

    MusicApiRepository musicApiRepository();

    SongLocalFavoriteRepository songFavoriteLocalRepository();

    SongLocalPlayedRepository songLocalPlayedRepository();

    void inject(PlaybackControlFullScreenFragment f);
    void inject(BaseFragmentSearch f);
}
