package com.cc.di.components;

import android.content.Context;

import com.cc.di.modules.ApplicationModule;
import com.cc.di.modules.NetworkModule;
import com.cc.di.modules.UserModule;
import com.cc.domain.executor.PostExecutionThread;
import com.cc.domain.executor.ThreadExecutor;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Author: NT
 * Since: 10/26/2016.
 */
@Singleton
@Component(modules = {
        NetworkModule.class,
        ApplicationModule.class
})
public interface ApplicationComponent {
    Context context();

    EventBus eventBus();

    UserComponent plus(UserModule userModule);


}
