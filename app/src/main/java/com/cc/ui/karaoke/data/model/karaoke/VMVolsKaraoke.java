package com.cc.ui.karaoke.data.model.karaoke;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/19/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class VMVolsKaraoke {
    private long id;
    private String name;
    private boolean isChecked;

    public VMVolsKaraoke() {
    }

    public VMVolsKaraoke(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
