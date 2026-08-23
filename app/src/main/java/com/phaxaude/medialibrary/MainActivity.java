package com.phaxaude.medialibrary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;
import androidx.cardview.widget.CardView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // A unique code to identify our permission request
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Loads the UI

        requestStoragePermissions();

        // 1. Find the cards in the XML by their IDs
        CardView cardAudio = findViewById(R.id.cardAudio);
        CardView cardVideo = findViewById(R.id.cardVideo);
        CardView cardImages = findViewById(R.id.cardImages);

        // 2. Set up click listeners for each
        cardAudio.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Audio Selected", Toast.LENGTH_SHORT).show();
        });

        cardVideo.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Videos Selected", Toast.LENGTH_SHORT).show();
        });

        cardImages.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Images Selected", Toast.LENGTH_SHORT).show();
        });
    }

    private void requestStoragePermissions() {
        String[] permissions;

        // Android 13 (API 33) introduced granular media permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            // Older Android versions just use one blanket storage permission
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }

        // Check if we already have the permission
        if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            fetchAndLogMedia(); // We have it, go ahead and fetch!
        } else {
            // We don't have it, trigger the system pop-up
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    // This method is called automatically after the user clicks "Allow" or "Deny" on the pop-up
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAndLogMedia(); // User clicked Allow!
            } else {
                Log.e("MediaLibrary", "Permission denied by user.");
            }
        }
    }

    private void fetchAndLogMedia() {
        // Run our custom fetcher
        List<MediaFile> audioFiles = MediaFetcher.getAudioFiles(this);

        // Log.d is Android's version of a print() statement
        Log.d("MediaLibrary", "Found " + audioFiles.size() + " audio files.");

        for (MediaFile file : audioFiles) {
            Log.d("MediaLibrary", "Title: " + file.getTitle() + " | Path: " + file.getPath());
        }
    }
}