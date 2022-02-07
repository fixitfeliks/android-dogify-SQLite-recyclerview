package co.touchlab.dogify.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import co.touchlab.dogify.R;
import co.touchlab.dogify.data.models.BreedModel;

public class BreedAdapter extends RecyclerView.Adapter<BreedAdapter.ViewHolder>
{
    private final List<BreedModel> mBreedModels = new ArrayList<>();
    private final RequestManager mGlide;

    public BreedAdapter(RequestManager glide){
        mGlide = glide;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_breed, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BreedModel breedModel = mBreedModels.get(position);
        holder.nameText.setText(breedModel.displayName);
        holder.nameText.setVisibility(View.VISIBLE);
        holder.imageView.setContentDescription(breedModel.displayName);

        int randomInt = ThreadLocalRandom.current().nextInt(200, 400);
        holder.imageView.setContentDescription(breedModel.displayName);
        mGlide.load(breedModel.imageUrl)
            .fitCenter()
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.no_image_loaded)
            .transform(new RoundedCorners(25))
            .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mBreedModels.size();
    }

    public void addAll(List<BreedModel> breeds) {
        mBreedModels.addAll(breeds);
        notifyItemRangeInserted(mBreedModels.size() - 1, breeds.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
