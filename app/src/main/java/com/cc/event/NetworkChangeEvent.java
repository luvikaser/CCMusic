package com.cc.event;

/**
 * Author: NT
 * Since: 10/26/2016.
 */
public class NetworkChangeEvent {
    public final boolean isOnline;

    public NetworkChangeEvent(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
