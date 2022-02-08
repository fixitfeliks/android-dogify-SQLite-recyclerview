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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.util.concurrent.Executors;

import co.touchlab.dogify.R;
import co.touchlab.dogify.adapters.BreedAdapter;
import co.touchlab.dogify.ui.viewmodels.BreedsViewModel;

public class MainActivity extends AppCompatActivity
{
    private ProgressBar mSpinner;
    private ImageView mErrorView;
    private BreedAdapter mBreedAdapter;
    private RecyclerView mBreedList;
    private BreedsViewModel mBreedsViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinner = findViewById(R.id.intro_spinner);
        mErrorView = findViewById(R.id.error);

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
            mSwipeRefreshLayout.setRefreshing(false);
        });
        mBreedsViewModel.getErrorStream().observe(this, errorModel -> {
                    mErrorView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),
                            String.format("%s\nmessage: %s", errorModel.status, errorModel.message),
                            Toast.LENGTH_SHORT).show();
                }
        );
        mBreedsViewModel.fetchBreedData();

        // SwipeRefresher
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mBreedAdapter.clear();
            Executors.newSingleThreadExecutor().execute(() -> mBreedsViewModel.fetchBreedData());
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
