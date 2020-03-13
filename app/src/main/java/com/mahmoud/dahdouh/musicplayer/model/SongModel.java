package com.mahmoud.dahdouh.musicplayer.model;

import android.graphics.Bitmap;

public class SongModel {

    private String name;
    private String length;
    private Bitmap image;

    public SongModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
