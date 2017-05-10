package com.cc.ui.karaoke.data.model.karaoke;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/19/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class VMLanguageKaraoke {
    private String id;
    private String name;
    private boolean isChecked;

    public VMLanguageKaraoke() {
    }

    public VMLanguageKaraoke(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
