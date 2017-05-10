package com.cc.domain.model;

/**
 * Created by AnhHieu on 3/25/16.
 */

public final class User {

    public String accesstoken;

    public long expirein;

    public String email;

    public User() {
    }

    public String getSession() {
        return accesstoken;
    }

}
