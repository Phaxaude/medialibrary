package com.phaxaude.medialibrary;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MediaFetcher {

    public static List<MediaFile> getAudioFiles(Context context) {
        List<MediaFile> audioFiles = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();

        // 1. Define the columns we want to extract
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA, // This is the actual file path
                MediaStore.Audio.Media.DURATION
        };

        // 2. Run the query
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, // "WHERE" clause (null means get everything)
                null, // "WHERE" arguments
                MediaStore.Audio.Media.TITLE + " ASC" // "ORDER BY"
        );

        // 3. Loop through the results
        if (cursor != null) {
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

            while (cursor.moveToNext()) {
                String title = cursor.getString(titleColumn);
                String path = cursor.getString(pathColumn);
                long duration = cursor.getLong(durationColumn);

                audioFiles.add(new MediaFile(title, path, duration));
            }
            cursor.close(); // Always close the cursor to prevent memory leaks!
        }

        return audioFiles;
    }

    public static List<ImageFolder> getImageFolders(Context context) {
        // A Map to quickly group images by their Folder ID
        HashMap<String, ImageFolder> folderMap = new HashMap<>();
        ContentResolver resolver = context.getContentResolver();

        String[] projection = {
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA // The actual image file path
        };

        // Sort by date added so the folder previews show the newest images
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                String bucketId = cursor.getString(bucketIdColumn);
                String bucketName = cursor.getString(bucketNameColumn);
                String path = cursor.getString(pathColumn);

                // If we haven't seen this folder yet, create it
                if (!folderMap.containsKey(bucketId)) {
                    // Fallback just in case Android returns a null folder name
                    if (bucketName == null) bucketName = "Unknown Folder";
                    folderMap.put(bucketId, new ImageFolder(bucketId, bucketName));
                }

                // Add the image to the folder (our class handles the 4-image limit)
                folderMap.get(bucketId).addImagePath(path);
            }
            cursor.close();
        }

        // Convert the map values back to a standard List for our UI to use
        return new ArrayList<>(folderMap.values());
    }
    public static List<String> getImagesForFolder(Context context, String bucketId) {
        List<String> imagePaths = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();

        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        String[] selectionArgs = { bucketId };

        // Fetch without SQL sorting so we can Natural Sort them in memory
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, null
        );

        if (cursor != null) {
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            while (cursor.moveToNext()) {
                imagePaths.add(cursor.getString(pathColumn));
            }
            cursor.close();
        }

        // --- NATURAL SORTING ALGORITHM ---
        // Fixes the "1, 10, 2" problem by separating numbers from text
        imagePaths.sort((s1, s2) -> {
            String[] parts1 = s1.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            String[] parts2 = s2.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

            for (int i = 0; i < Math.min(parts1.length, parts2.length); i++) {
                int cmp;
                // If both chunks are numbers, pad them with zeros to compare correctly
                if (parts1[i].matches("\\d+") && parts2[i].matches("\\d+")) {
                    int maxLen = Math.max(parts1[i].length(), parts2[i].length());
                    String padded1 = String.format("%" + maxLen + "s", parts1[i]).replace(' ', '0');
                    String padded2 = String.format("%" + maxLen + "s", parts2[i]).replace(' ', '0');
                    cmp = padded1.compareTo(padded2);
                } else {
                    cmp = parts1[i].compareToIgnoreCase(parts2[i]);
                }
                if (cmp != 0) return cmp;
            }
            return Integer.compare(parts1.length, parts2.length);
        });

        return imagePaths;
    }
    public static List<MediaFile> getVideoFiles(Context context) {
        List<MediaFile> videoFiles = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();

        String[] projection = {
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION
        };

        // Sort by newest videos first
        Cursor cursor = resolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            int titleCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
            int pathCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

            while (cursor.moveToNext()) {
                String title = cursor.getString(titleCol);
                String path = cursor.getString(pathCol);
                long duration = cursor.getLong(durationCol);

                videoFiles.add(new MediaFile(title, path, duration));
            }
            cursor.close();
        }
        return videoFiles;
    }
}