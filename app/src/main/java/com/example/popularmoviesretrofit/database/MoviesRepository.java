package com.example.popularmoviesretrofit.database;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.popularmoviesretrofit.Movies;
import com.example.popularmoviesretrofit.R;
import com.example.popularmoviesretrofit.utils.AppExecutors;
import com.example.popularmoviesretrofit.utils.MovieResponse;
import com.example.popularmoviesretrofit.utils.MoviesApiService;
import com.example.popularmoviesretrofit.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesRepository {

    private static Retrofit retrofit = null;
    Context context;

    int pages;

    private static MoviesRepository instance;

    private MoviesDao moviesDao;

    public final LiveData<PagedList<Movies>> mAllMovies;
    PagedList.Config pagedList;
    private int databaseSize;

    public List<Movies> mostPopular;
    public List<Movies> topRated;
    private MutableLiveData<List<Movies>> dataPopular;
    private MutableLiveData<List<Movies>> dataTopRated;
    private MutableLiveData<Boolean> mIsFetchingData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsPopularMoviesDataAvailable = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsTopRatedMoviesDataAvailable = new MutableLiveData<>();


    /**
     * Singleton - Prevent multiple instances
     * @param application
     * @return
     */
    public static MoviesRepository getInstance(Application application) {
        if (instance == null){
            instance = new MoviesRepository(application);
        }
        return instance;
    }

    MoviesRepository(Application application) {
        MoviesDatabase db = MoviesDatabase.getInstance(application);
        moviesDao = db.moviesDao();
        context = application.getApplicationContext();

        pagedList = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(10)
                .setPageSize(20).build();

        mAllMovies = new LivePagedListBuilder<>(
                moviesDao.getAllPaged(), pagedList
        ).build();
        buildAndGetApiData();
    }

    /**
     * insert data into database
     * @param movies
     */
    public void insert(Movies movies) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                moviesDao.insertMovie(movies);
            }
        });
    }

    /**
     * Delete data from database
     * @param movies
     */
    public void delete(Movies movies) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                moviesDao.deleteMovie(movies);
            }
        });
    }

    public LiveData<PagedList<Movies>> allMovies() {
        return mAllMovies;
    }

    public int getAnyWord() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {

            private int size;

            @Override
           public void run() {
                size = moviesDao.getAnyWord().length;
                databaseSize = size;
           }
       });

        return databaseSize;
    }

    /**
     * getter Most popular movies
     * @return
     */
    public LiveData<List<Movies>> getMostPopularMovies() {
        setPopularMovies();
        return dataPopular;
    }

    /**
     * getter top rated movies
     * @return
     */
    public LiveData<List<Movies>> getTopRatedMovies() {
        setTopRatedMovies();
        return dataTopRated;
    }

    /**
     * Build URL using Retrofit Builder
     * And add a converter to process JSON data received
     */
    public void buildAndGetApiData() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(NetworkUtils.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }


    /**
     * Fetch popular movies
     */
    private void setPopularMovies(){
        dataPopular = new MutableLiveData<>();
        mIsFetchingData.postValue(true);

        MoviesApiService moviesApiService = retrofit.create(MoviesApiService.class);
        Call<MovieResponse> call = moviesApiService.getPopularMovies(NetworkUtils.API_KEY);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                pages = response.body().getTotalPages();
                mostPopular = response.body().getResults();
                mIsFetchingData.setValue(false);

                if (mostPopular != null) {
                    mIsPopularMoviesDataAvailable.setValue(true);

                    dataPopular.setValue(mostPopular);
                } else {
                    mIsPopularMoviesDataAvailable.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.failure_response),
                        Toast.LENGTH_LONG).show();
                mIsFetchingData.setValue(false);
                mIsPopularMoviesDataAvailable.setValue(false);
            }
        });

    }


    /**
     * fetch top rated movies
     */
    private void setTopRatedMovies(){
        dataTopRated = new MutableLiveData<>();
        mIsFetchingData.postValue(true);

        MoviesApiService moviesApiService = retrofit.create(MoviesApiService.class);
        Call<MovieResponse> call = moviesApiService.getTopRatedMovies(NetworkUtils.API_KEY);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                topRated = response.body().getResults();
                mIsFetchingData.setValue(false);

                if (topRated != null){
                    mIsTopRatedMoviesDataAvailable.setValue(true);

                    dataTopRated.setValue(topRated);
                } else {
                    mIsTopRatedMoviesDataAvailable.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.failure_response),
                        Toast.LENGTH_LONG).show();
                mIsFetchingData.setValue(false);
                mIsTopRatedMoviesDataAvailable.setValue(false);
            }
        });
    }

    public LiveData<Boolean> mIsFetching() {
        return mIsFetchingData;
    }
    public LiveData<Boolean> mIsMostPopularDataAvailable() {
        return mIsPopularMoviesDataAvailable;
    }
    public LiveData<Boolean> mIsTopRatedDataAvailable() {
        return mIsTopRatedMoviesDataAvailable;
    }
}
