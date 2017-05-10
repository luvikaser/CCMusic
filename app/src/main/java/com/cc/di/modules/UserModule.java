package com.cc.di.modules;

import com.cc.di.scope.UserScope;
import com.cc.domain.model.User;

import dagger.Module;
import dagger.Provides;

/**
 * Author: NT
 * Since: 10/27/2016.
 */
@Module
public class UserModule {
    private final User user;

    public UserModule(User user) {
        this.user = user;
    }

    @Provides
    @UserScope
    User providerUser() {
        return user;
    }
}