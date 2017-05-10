package com.cc.di.modules;

import android.content.Context;

import com.cc.data.database.DatabaseBaseManager;
import com.cc.data.rest.MusicApiRepositoryImpl;
import com.cc.data.rest.MusicApiRest;
import com.cc.data.rest.SongLocalFavoriteRepositoryImpl;
import com.cc.data.rest.SongLocalPlayedRepositoryImpl;
import com.cc.di.scope.UserScope;
import com.cc.domain.repository.MusicApiRepository;
import com.cc.domain.repository.SongLocalFavoriteRepository;
import com.cc.domain.repository.SongLocalPlayedRepository;
import com.cc.ui.search.SearchOnlineManager;

import java.util.HashMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Author: NT
 * Since: 11/3/2016.
 */

@Module
public class MusicApiModule {

    @Provides
    @UserScope
    MusicApiRest providesMusicApiRest(@Named("BASE_REST_ADAPTER") Retrofit retrofit) {
        return retrofit.create(MusicApiRest.class);
    }

    @Provides
    @UserScope
    MusicApiRepository providesMusicApiRepository(MusicApiRest musicApiRest, @Named("paramDefault") HashMap<String, String> params) {
        return new MusicApiRepositoryImpl(musicApiRest, params);
    }

    @Provides
    @UserScope
    DatabaseBaseManager providesDatabaBaseManager(Context application) {
        return new DatabaseBaseManager(application);
    }

    @Provides
    @UserScope
    SongLocalFavoriteRepository providesSongFavoriteLocalRepository(DatabaseBaseManager databaseBaseManager) {
        return new SongLocalFavoriteRepositoryImpl(databaseBaseManager);
    }

    @Provides
    @UserScope
    SongLocalPlayedRepository provideSongLocalPlayedRepository(DatabaseBaseManager
                                                          databaseBaseManager) {
        return new SongLocalPlayedRepositoryImpl(databaseBaseManager);
    }

    @Provides
    @UserScope
    SearchOnlineManager provideSearchOnlineManager() {
        return new SearchOnlineManager();
    }

}