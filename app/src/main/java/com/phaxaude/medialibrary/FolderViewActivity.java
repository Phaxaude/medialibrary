package com.phaxaude.medialibrary;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.transition.TransitionManager;
import android.transition.AutoTransition;

public class FolderViewActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private RecyclerView recyclerThumbnails;
    private View divider;
    private ViewGroup rootContainer;
    private List<String> imagePaths;

    // Variables for the swipe-to-fullscreen feature
    private GestureDetector gestureDetector;
    private boolean isFullscreen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_view);

        viewPager = findViewById(R.id.viewPager);
        recyclerThumbnails = findViewById(R.id.recyclerThumbnails);
        divider = findViewById(R.id.divider);
        rootContainer = findViewById(R.id.rootContainer); // Add this

        String bucketId = getIntent().getStringExtra("BUCKET_ID");
        if (bucketId == null) return;

        imagePaths = MediaFetcher.getImagesForFolder(this, bucketId);

        // Initialize the custom Gesture Detector for Up/Down swipes
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        // Set up Top Half (Swiping)
        FullscreenAdapter fullscreenAdapter = new FullscreenAdapter();
        viewPager.setAdapter(fullscreenAdapter);

        // Set up Bottom Half (Grid)
        recyclerThumbnails.setLayoutManager(new GridLayoutManager(this, 5));
        ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter();
        recyclerThumbnails.setAdapter(thumbnailAdapter);

        // Keep the bottom grid synchronized with the top swipes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                recyclerThumbnails.scrollToPosition(position);
            }
        });
    }

    // Toggles the visibility of the bottom half of the screen smoothly
    private void toggleFullscreen(boolean goFullscreen) {
        if (goFullscreen && !isFullscreen) {
            // Tell Android to animate the upcoming layout changes
            TransitionManager.beginDelayedTransition(rootContainer, new AutoTransition());

            recyclerThumbnails.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            isFullscreen = true;
        } else if (!goFullscreen && isFullscreen) {
            // Tell Android to animate the upcoming layout changes
            TransitionManager.beginDelayedTransition(rootContainer, new AutoTransition());

            recyclerThumbnails.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            isFullscreen = false;
        }
    }

    // Listens specifically for directional flings (swipes)
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true; // We must consume the initial touch to register the fling
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;

            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();

            // Only trigger if the swipe is primarily vertical (ignores left/right swipes)
            if (Math.abs(diffY) > Math.abs(diffX)) {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        toggleFullscreen(true); // Swiped Down
                    } else {
                        toggleFullscreen(false); // Swiped Up
                    }
                    return true;
                }
            }
            return false;
        }
    }

    // --- ADAPTER FOR TOP HALF (VIEWPAGER2) ---
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
            com.github.chrisbanes.photoview.PhotoView photoView = (com.github.chrisbanes.photoview.PhotoView) holder.itemView;

            Glide.with(FolderViewActivity.this)
                    .load(imagePaths.get(position))
                    .into(photoView);

            // Feed the touches to our custom up/down detector, but return false
            // so PhotoView can still use those touches for pinch-to-zoom!
            photoView.setOnTouchListener((v, event) -> {
                gestureDetector.onTouchEvent(event);
                return false;
            });
        }

        @Override
        public int getItemCount() { return imagePaths.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(@NonNull View itemView) { super(itemView); }
        }
    }

    // --- ADAPTER FOR BOTTOM HALF (GRID) ---
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
                    .centerCrop()
                    .into(imgThumb);

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