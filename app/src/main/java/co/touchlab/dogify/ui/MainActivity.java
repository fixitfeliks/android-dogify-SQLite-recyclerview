package co.touchlab.dogify.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;

import co.touchlab.dogify.R;
import co.touchlab.dogify.adapters.BreedAdapter;
import co.touchlab.dogify.ui.viewmodels.BreedsViewModel;

public class MainActivity extends AppCompatActivity
{
    private ProgressBar mSpinner;
    private ImageView mImageView;
    private BreedAdapter mBreedAdapter;
    private RecyclerView mBreedList;
    private BreedsViewModel mBreedsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinner = findViewById(R.id.intro_spinner);
        mImageView = findViewById(R.id.image);


        // RecyclerView
        mBreedAdapter = new BreedAdapter(Glide.with(getApplicationContext()));
        mBreedList = findViewById(R.id.breed_list);
        mBreedList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mBreedList.setAdapter(mBreedAdapter);

        // View Model
        mBreedsViewModel = new ViewModelProvider(this).get(BreedsViewModel.class);
        mBreedsViewModel.getBreedDataStream().observe(this, breedModels -> {
            mSpinner.setVisibility(View.GONE);
            mBreedAdapter.addAll(breedModels);
        });
        mBreedsViewModel.getErrorStream().observe(this, errorModel -> Toast.makeText(getApplicationContext(),
                String.format("Error\nstatus: %s\nmessage: %s", errorModel.status, errorModel.message), Toast.LENGTH_LONG)
        );
        mBreedsViewModel.fetchBreedData();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
