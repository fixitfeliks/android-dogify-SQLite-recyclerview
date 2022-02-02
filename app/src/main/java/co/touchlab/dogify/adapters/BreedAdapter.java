package co.touchlab.dogify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import co.touchlab.dogify.R;
import co.touchlab.dogify.data.models.BreedModel;

public class BreedAdapter extends RecyclerView.Adapter<BreedAdapter.ViewHolder>
{
    private final List<BreedModel> mBreedModels = new ArrayList<>();

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_breed, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int randomInt = ThreadLocalRandom.current().nextInt(400, 800);
        holder.imageLoading.setHeight(randomInt);
        holder.imageLoading.setVisibility(View.VISIBLE);
        holder.nameText.setVisibility(View.GONE);
        BreedModel breedModel = mBreedModels.get(position);
        holder.imageView.setContentDescription(breedModel.displayName);
        Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(true);
        picasso.load(breedModel.imageUrl).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.imageLoading.setVisibility(View.GONE);
                holder.nameText.setVisibility(View.VISIBLE);
                holder.nameText.setText(breedModel.displayName);
            }

            @Override
            public void onError(Exception e) {

            }
        });
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
        private final TextView imageLoading;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name);
            imageLoading = itemView.findViewById(R.id.image_loading);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
