package com.phaxaude.medialibrary;

import java.util.ArrayList;
import java.util.List;

public class ImageFolder {
    private String id;
    private String name;
    private int imageCount;
    private List<String> previewImagePaths;

    public ImageFolder(String id, String name) {
        this.id = id;
        this.name = name;
        this.imageCount = 0;
        this.previewImagePaths = new ArrayList<>();
    }

    public void addImagePath(String path) {
        imageCount++;
        // We only need to save the first 4 images for your collage
        if (previewImagePaths.size() < 4) {
            previewImagePaths.add(path);
        }
    }

    public String getName() { return name; }
    public int getImageCount() { return imageCount; }
    public List<String> getPreviewImagePaths() { return previewImagePaths; }
    public String getId() { return id; }
}