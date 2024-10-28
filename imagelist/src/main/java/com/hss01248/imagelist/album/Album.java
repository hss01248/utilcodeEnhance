package com.hss01248.imagelist.album;

import android.net.Uri;

/**
 * Created by Darshan on 4/14/2015.
 */
public class Album {
    public String name;
    public long id;
    public String cover;
    public int count;
    public long fileSize;

    public Album setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public Uri uri;

    public Album setVideo(boolean video) {
        isVideo = video;
        return this;
    }

    boolean isVideo;

    public Album(String name, String cover, long id) {
        this.name = name;
        this.cover = cover;
        this.count = count;
        this.id = id;
    }
}
