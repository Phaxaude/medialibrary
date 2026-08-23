package com.phaxaude.medialibrary;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import java.util.ArrayList;
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
}