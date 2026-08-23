package com.phaxaude.medialibrary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import java.util.List;

public class FolderViewActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private RecyclerView recyclerThumbnails;
    private List<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_view);

        viewPager = findViewById(R.id.viewPager);
        recyclerThumbnails = findViewById(R.id.recyclerThumbnails);

        // Get the ID of the folder you tapped on the previous screen
        String bucketId = getIntent().getStringExtra("BUCKET_ID");
        if (bucketId == null) return;

        // Fetch the naturally sorted list of image paths
        imagePaths = MediaFetcher.getImagesForFolder(this, bucketId);

        // Set up Top Half (Swiping)
        FullscreenAdapter fullscreenAdapter = new FullscreenAdapter();
        viewPager.setAdapter(fullscreenAdapter);

        // Set up Bottom Half (Grid) - 5 columns
        recyclerThumbnails.setLayoutManager(new GridLayoutManager(this, 5));
        ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter();
        recyclerThumbnails.setAdapter(thumbnailAdapter);

        // Link the ViewPager swipe event to scroll the bottom grid
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                recyclerThumbnails.scrollToPosition(position);
            }
        });
    }

    // --- ADAPTER FOR TOP HALF (VIEWPAGER2) ---
    private class FullscreenAdapter extends RecyclerView.Adapter<FullscreenAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fullscreen_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Glide.with(FolderViewActivity.this)
                    .load(imagePaths.get(position))
                    .fitCenter() // Scales perfectly without cropping
                    .into((ImageView) holder.itemView);
        }

        @Override
        public int getItemCount() { return imagePaths.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(@NonNull View itemView) { super(itemView); }
        }
    }

    // --- ADAPTER FOR BOTTOM HALF (RECYCLERVIEW GRID) ---
    private class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumbnail, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ImageView imgThumb = holder.itemView.findViewById(R.id.imgThumb);

            Glide.with(FolderViewActivity.this)
                    .load(imagePaths.get(position))
                    .centerCrop() // Perfect squares
                    .into(imgThumb);

            // When a thumbnail is clicked, jump the top ViewPager to that image
            holder.itemView.setOnClickListener(v -> {
                viewPager.setCurrentItem(position, true);
            });
        }

        @Override
        public int getItemCount() { return imagePaths.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(@NonNull View itemView) { super(itemView); }
        }
    }
}