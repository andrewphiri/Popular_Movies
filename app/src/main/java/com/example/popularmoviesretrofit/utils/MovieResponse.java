package com.example.popularmoviesretrofit.utils;

import com.example.popularmoviesretrofit.Movies;
import com.example.popularmoviesretrofit.Reviews;
import com.example.popularmoviesretrofit.Trailers;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<Movies> results;

    @SerializedName("total_pages")
    private int totalPages;

    public List<Movies> getResults() {
        return results;
    }

    public void setResults(List<Movies> results) {
        this.results = results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
