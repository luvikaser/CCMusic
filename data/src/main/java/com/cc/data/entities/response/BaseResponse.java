package com.cc.data.entities.response;

import com.google.gson.annotations.SerializedName;

/**
 * Author: NT
 * Since: 11/3/2016.
 * Base response for all
 */
public class BaseResponse<T> {

    @SerializedName("error")
    public int errorCode;

    @SerializedName("msg")
    public T data;
}