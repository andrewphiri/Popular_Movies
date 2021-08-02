package com.example.popularmoviesretrofit;

import com.google.gson.annotations.SerializedName;

public class Reviews {
    @SerializedName("content")
    private String content;

    @SerializedName("author")
    private String author;

    @SerializedName("username")
    private String userName;

    @SerializedName("created_at")
    private String date;


    public Reviews(String content, String author, String date) {
        this.content = content;
        this.author = author;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        if (author == null) {
            author = userName;
        }
        return author;
    }

    public String getDate() {
        return date;
    }
}
