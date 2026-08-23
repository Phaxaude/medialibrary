package com.phaxaude.medialibrary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;

public class VideosActivity extends AppCompatActivity {

    private static final String TAG = "VideosActivityDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Fetch the video list
        List<MediaFile> videos = MediaFetcher.getVideoFiles(this);
        Log.d(TAG, "Number of videos found: " + videos.size());

        VideoAdapter adapter = new VideoAdapter(videos);
        recyclerView.setAdapter(adapter);
    }

    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
        private List<MediaFile> videoFiles;

        public VideoAdapter(List<MediaFile> videoFiles) {
            this.videoFiles = videoFiles;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MediaFile video = videoFiles.get(position);
            holder.tvVideoTitle.setText(video.getTitle() != null ? video.getTitle() : "Untitled Video");

            // Optimized Glide call for local video frame extraction with caching
            Glide.with(VideosActivity.this)
                    .load(video.getPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.imgVideoThumb);

            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(VideosActivity.this, "Play: " + video.getTitle(), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return videoFiles != null ? videoFiles.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgVideoThumb;
            TextView tvVideoTitle;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgVideoThumb = itemView.findViewById(R.id.imgVideoThumb);
                tvVideoTitle = itemView.findViewById(R.id.tvVideoTitle);
            }
        }
    }
}