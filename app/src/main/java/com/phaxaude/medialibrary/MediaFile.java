package com.phaxaude.medialibrary;

public class MediaFile {
    private String title;
    private String path;
    private long duration;

    public MediaFile(String title, String path, long duration) {
        this.title = title;
        this.path = path;
        this.duration = duration;
    }

    public String getTitle() { return title; }
    public String getPath() { return path; }
    public long getDuration() { return duration; }
}