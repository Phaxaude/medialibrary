package com.phaxaude.medialibrary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request storage permissions at runtime for modern Android
        checkAndRequestPermissions();

        CardView cardImages = findViewById(R.id.cardImages);
        CardView cardVideo = findViewById(R.id.cardVideo);

        cardImages.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImageFoldersActivity.class);
            startActivity(intent);
        });

        cardVideo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VideosActivity.class);
            startActivity(intent);
        });
    }

    private void checkAndRequestPermissions() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_VIDEO; // For Android 13+
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE; // For older versions
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission, Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        }
    }
}