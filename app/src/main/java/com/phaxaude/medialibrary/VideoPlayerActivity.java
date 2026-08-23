package com.phaxaude.medialibrary;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class VideoPlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.playerView);

        // Get the file path passed from the grid
        String videoPath = getIntent().getStringExtra("VIDEO_PATH");
        if (videoPath == null) {
            finish();
            return;
        }

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.getPlayer();
        playerView.setPlayer(player);

        // Load the local file path into ExoPlayer
        MediaItem mediaItem = MediaItem.fromUri(videoPath);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause(); // Pause playback when you leave the screen
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release(); // Free up memory completely when closed
            player = null;
        }
    }
}