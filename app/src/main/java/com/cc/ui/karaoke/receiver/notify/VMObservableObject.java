package com.cc.ui.karaoke.receiver.notify;

import java.util.Observable;

/**
 * Author: NT
 * Since: 9/11/2016.
 */
public class VMObservableObject extends Observable {
    private static VMObservableObject ourInstance = new VMObservableObject();

    public static VMObservableObject getInstance() {
        return ourInstance;
    }

    private VMObservableObject() {
    }

    public void updateData(Object data) {
            synchronized (this) {
                setChanged();
                notifyObservers(data);
            }
    }
}
