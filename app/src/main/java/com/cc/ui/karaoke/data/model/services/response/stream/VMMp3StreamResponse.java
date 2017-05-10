package com.cc.ui.karaoke.data.model.services.response.stream;

import com.google.gson.annotations.SerializedName;

import com.cc.ui.karaoke.data.model.services.response.base.VMBaseResponse;

/**
 * Author: NT
 * Since: 10/14/2016.
 */
public class VMMp3StreamResponse extends VMBaseResponse {
    @SerializedName("msg")
    public String data;
}