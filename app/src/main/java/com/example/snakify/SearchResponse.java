package com.example.snakify;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {
    @SerializedName("items")
    public List<SearchItem> items;

    public static class SearchItem {
        @SerializedName("title")
        public String title;

        @SerializedName("link")
        public String link;

        @SerializedName("snippet")
        public String snippet;
    }
}
