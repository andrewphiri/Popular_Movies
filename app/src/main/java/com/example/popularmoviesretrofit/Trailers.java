package com.example.popularmoviesretrofit;

import com.google.gson.annotations.SerializedName;

public class Trailers {
    @SerializedName("key")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
