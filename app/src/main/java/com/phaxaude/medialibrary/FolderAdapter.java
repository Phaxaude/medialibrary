package com.phaxaude.medialibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private Context context;
    private List<ImageFolder> folders;

    public FolderAdapter(Context context, List<ImageFolder> folders) {
        this.context = context;
        this.folders = folders;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_folder, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        ImageFolder folder = folders.get(position);
        holder.tvFolderName.setText(folder.getName() + " (" + folder.getImageCount() + ")");
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, FolderViewActivity.class);
            intent.putExtra("BUCKET_ID", folder.getId());
            context.startActivity(intent);
        });

        List<String> paths = folder.getPreviewImagePaths();
        ImageView[] imageViews = {holder.img1, holder.img2, holder.img3, holder.img4};

        // Loop through the 4 image slots
        for (int i = 0; i < 4; i++) {
            if (i < paths.size()) {
                // We have an image for this slot, load it with Glide
                Glide.with(context)
                        .load(paths.get(i))
                        .centerCrop()
                        .into(imageViews[i]);
            } else {
                // Not enough images to fill all 4 slots, clear the remaining ones
                Glide.with(context).clear(imageViews[i]);
                imageViews[i].setImageDrawable(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView img1, img2, img3, img4;
        TextView tvFolderName;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);
            img4 = itemView.findViewById(R.id.img4);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);
        }
    }
}