package com.cc.ui.karaoke.data.model.services.response.base;

import com.google.gson.annotations.SerializedName;

/**
 * Author: NT
 * Since: 6/20/2016.
 */
public abstract class VMBaseResponse {
    @SerializedName("error")
    private int error;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

}
