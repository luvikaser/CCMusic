package com.cc.di.modules;

import android.content.Context;

import com.cc.MusicApplication;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Author: NT
 * Since: 10/27/2016.
 */
@Module
public class ApplicationModule {
    private final MusicApplication application;

    public ApplicationModule(MusicApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    EventBus providesEventBus() {
        return EventBus.getDefault();
    }
}