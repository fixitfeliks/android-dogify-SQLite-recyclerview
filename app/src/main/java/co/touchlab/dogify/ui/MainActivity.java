package co.touchlab.dogify.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import co.touchlab.dogify.R;
import co.touchlab.dogify.adapters.BreedAdapter;
import co.touchlab.dogify.repository.remote.GetBreedsTask;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView breedList;
    private BreedAdapter adapter   = new BreedAdapter();
    private GetBreedsTask getBreeds = new GetBreedsTask(this);
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = findViewById(R.id.spinner);
        breedList = findViewById(R.id.breed_list);
        breedList.setLayoutManager(new GridLayoutManager(this, 2));
        breedList.setAdapter(adapter);
        getBreeds.execute();

    }

    @Override
    protected void onDestroy()
    {
        getBreeds.cancel(false);
        super.onDestroy();
    }

    public void initList() {
        showSpinner(true);
        adapter.clear();
    }

    public void addAllBreedsToList(List<String> breeds) {
        showSpinner(false);
        adapter.addAll(breeds);
    }

    public void showSpinner(Boolean show)
    {
        spinner.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
