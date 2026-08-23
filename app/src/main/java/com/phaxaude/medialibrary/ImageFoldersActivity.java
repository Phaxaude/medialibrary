package com.phaxaude.medialibrary;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ImageFoldersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_folders);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFolders);

        // Set up the 5-column grid
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));

        // Fetch the folders and attach the adapter
        List<ImageFolder> folders = MediaFetcher.getImageFolders(this);
        FolderAdapter adapter = new FolderAdapter(this, folders);
        recyclerView.setAdapter(adapter);
    }
}