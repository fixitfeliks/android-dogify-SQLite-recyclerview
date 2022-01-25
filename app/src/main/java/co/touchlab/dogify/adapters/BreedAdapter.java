package co.touchlab.dogify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.touchlab.dogify.R;

public class BreedAdapter extends RecyclerView.Adapter<BreedAdapter.ViewHolder> {

    private List<String> data = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_breed, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String breed = data.get(position);
        holder.nameText.setText(breed);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addAll(List<String> breeds) {
        data.addAll(breeds);
        notifyItemRangeInserted(data.size() - 1, breeds.size());
    }

    public void clear() {
        int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name);
        }
    }
}
