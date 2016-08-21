package com.example.dell.instantforecast;

/**
 * Created by USER on 8/13/2016.
 */
public class SingleItem {
    private String pubDate;
    private String title;
    private String description;
    private String link;

    public String getPubDate() {
        return pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public SingleItem(String _pubDate, String _title,
                      String _description, String _link) {
        pubDate = _pubDate;
        description = _description;
        title = _title;
        link = _link;
    }

    @Override
    public String toString() {
        return title;
    }
}
