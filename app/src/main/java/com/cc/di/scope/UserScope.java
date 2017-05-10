package com.cc.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Author: NT
 * Since: 10/26/2016.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface UserScope {
}

