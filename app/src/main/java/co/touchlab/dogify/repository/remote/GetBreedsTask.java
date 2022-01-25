package co.touchlab.dogify.repository.remote;

import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.touchlab.dogify.repository.remote.retrofit.NameResult;
import co.touchlab.dogify.ui.MainActivity;
import co.touchlab.dogify.repository.remote.retrofit.DogService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetBreedsTask extends AsyncTask<Void, Void, List<String>> {
    private WeakReference<MainActivity> activityRef;
    public GetBreedsTask(MainActivity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        MainActivity activity = activityRef.get();
        if (activity != null) {
            activity.initList();
        }
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://dog.ceo/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        DogService service = retrofit.create(DogService.class);

        if (isCancelled()) {
            return null;
        }

        return getBreedNames(service);
    }

    private List<String> getBreedNames(DogService service) {
        try {
            NameResult result = service.getBreeds().execute().body();
            if (result != null && result.message != null) {
                return new ArrayList<>(result.message.keySet());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    protected void onPostExecute(List<String> breeds) {
        MainActivity activity = this.activityRef.get();
        if (activity != null) {
            activity.showSpinner(false);
            activity.addAllBreedsToList(breeds);
        }
    }
}
