package com.mario99ukdw.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mario99ukdw.popularmovies2.adapter.MovieAdapter;
import com.mario99ukdw.popularmovies2.data.MovieContentProvider;
import com.mario99ukdw.popularmovies2.data.MovieContract;
import com.mario99ukdw.popularmovies2.ui.AutofitRecyclerView;
import com.mario99ukdw.popularmovies2.ui.RecyclerItemClickListener;
import com.mario99ukdw.popularmovies2.utilities.MovieDBResultParser;
import com.mario99ukdw.popularmovies2.utilities.NetworkUtils;
import com.mario99ukdw.popularmovies2.data.Movie;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    final static String SORT_METHOD_KEY = "SORT_METHOD";
    final static String VAR_NAME_MOVIE_ARRAY_LIST = "movieArrayList";
    final static String VAR_NAME_MOVIE_FIRST_VISIBLE_POSITION = "firstVisiblePosition";

    private ArrayList<Movie> mMovieArrayList = null;

    @BindView(R.id.rv_movie_list)
    AutofitRecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mRecyclerView.addOnItemTouchListener(
        new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override public void onItemClick(View view, int position) {
                    // do whatever
                    Movie movie = ((MovieAdapter) mRecyclerView.getAdapter()).getItem(position);

                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra(Movie.INTENT_PARCEL_NAME, movie);

                    startActivity(intent);
                }
            })
        );

        if (savedInstanceState == null) {
            loadMovieByDefault();
        } else {
            // Get data from local resources
            mMovieArrayList = savedInstanceState.getParcelableArrayList(VAR_NAME_MOVIE_ARRAY_LIST);

            if (mMovieArrayList != null) {
                loadMovieListToRecyclerView(mMovieArrayList);
                Log.d(LOG_TAG, "onCreate : movieArrayList is null");
            } else {
                loadMovieByDefault();
                Log.d(LOG_TAG, "onCreate : movieArrayList is loaded by default");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem sortPopularMenuItem = menu.findItem(R.id.action_sort_popular);
        MenuItem sortTopRatedMenuItem = menu.findItem(R.id.action_sort_top_rated);
        MenuItem sortFavoriteMenuItem = menu.findItem(R.id.action_sort_my_favorite);

        String sortMethod = getSortMethod();
        sortPopularMenuItem.setVisible(sortMethod.compareTo(NetworkUtils.PARAM_SORT_POPULAR) != 0);
        sortTopRatedMenuItem.setVisible(sortMethod.compareTo(NetworkUtils.PARAM_SORT_TOP_RATED) != 0);
        sortFavoriteMenuItem.setVisible(sortMethod.compareTo(NetworkUtils.PARAM_SORT_FAVORITE) != 0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular) {
            loadMovieFromServer(NetworkUtils.PARAM_SORT_POPULAR);
            return true;
        } else if (id == R.id.action_sort_top_rated) {
            loadMovieFromServer(NetworkUtils.PARAM_SORT_TOP_RATED);
            return true;
        } else if (id == R.id.action_sort_my_favorite) {
            loadFavoriteMovie();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        // https://stackoverflow.com/questions/3014089/maintain-save-restore-scroll-position-when-returning-to-a-listview
        // https://stackoverflow.com/questions/24989218/get-visible-items-in-recyclerview
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();

        state.putParcelableArrayList(VAR_NAME_MOVIE_ARRAY_LIST, mMovieArrayList);
        state.putInt(VAR_NAME_MOVIE_FIRST_VISIBLE_POSITION, firstVisiblePosition);

        Log.d(LOG_TAG, "onSaveInstanceState : movieArrayList is saved");
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Loading Saved data
        mMovieArrayList = savedInstanceState.getParcelableArrayList(VAR_NAME_MOVIE_ARRAY_LIST);
        loadMovieListToRecyclerView(mMovieArrayList);

        // https://stackoverflow.com/questions/3014089/maintain-save-restore-scroll-position-when-returning-to-a-listview
        int firstVisiblePosition = savedInstanceState.getInt(VAR_NAME_MOVIE_FIRST_VISIBLE_POSITION);

        // https://stackoverflow.com/questions/27377830/what-is-the-equivalent-listview-setselection-in-case-of-recycler-view
        GridLayoutManager layoutManager = ((GridLayoutManager)mRecyclerView.getLayoutManager());
        layoutManager.scrollToPosition(firstVisiblePosition);
        Log.d(LOG_TAG, "onRestoreInstanceState : movieArrayList is restored");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getSortMethod().compareTo(NetworkUtils.PARAM_SORT_FAVORITE) == 0) {
            loadFavoriteMovie();
        }
    }

    /**
     *  get sort method from local resources
     */
    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString(SORT_METHOD_KEY, NetworkUtils.PARAM_SORT_POPULAR);
    }

    /**
     *  get sort method selection into local resources
     */
    private void setSortMethod(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SORT_METHOD_KEY, sortMethod);
        editor.apply();

        invalidateOptionsMenu();
    }

    private void loadFavoriteMovie() {
        ArrayList<Movie> movies = new ArrayList<>();
        String[] projection = new String[]{
                MovieContract.MoviesColumn.COL_MOVIE_ID, // 0
                MovieContract.MoviesColumn.COL_MOVIE_TITLE, // 1
                MovieContract.MoviesColumn.COL_MOVIE_POSTER_PATH, // 2
                MovieContract.MoviesColumn.COL_MOVIE_OVERVIEW, // 3
                MovieContract.MoviesColumn.COL_MOVIE_VOTE_AVARAGE, // 4
                MovieContract.MoviesColumn.COL_MOVIE_RELEASE_DATE, // 5
                MovieContract.MoviesColumn.COL_MOVIE_RUNTIME, // 6
                MovieContract.MoviesColumn.COL_MOVIE_TRAILERS,
                MovieContract.MoviesColumn.COL_MOVIE_REVIEWS,
        };
        final Cursor cursor = getContentResolver().query(MovieContentProvider.CONTENT_URI,projection,null,null,null);
        Log.d(LOG_TAG, "loadFavoriteMovie count:" + cursor.getCount() );
        if (cursor.getCount()!=0) {
            while(cursor.moveToNext()) {
                Movie data = new Movie(String.valueOf(cursor.getInt(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6));
                movies.add(data);
            }
        }
        mMovieArrayList = movies;
        loadMovieListToRecyclerView(mMovieArrayList);

        setSortMethod(NetworkUtils.PARAM_SORT_FAVORITE);
    }

    /**
     *  parse JSON text from server
     */
    private void parseResults(String result) {
        mMovieArrayList = MovieDBResultParser.parseResult(result);
        loadMovieListToRecyclerView(mMovieArrayList);
    }

    /**
     *  load movie list from server with default sort method or based on SharedPreferences
     */
    private void loadMovieByDefault() {
        String sortMethod = getSortMethod();
        if (sortMethod.compareTo(NetworkUtils.PARAM_SORT_FAVORITE) == 0) {
            loadFavoriteMovie();
        } else {
            loadMovieFromServer(sortMethod);
        }
    }

    /**
     *  load movie list from server with selected sort method
     */
    private void loadMovieFromServer(String sortMethod) {
        if (isNetworkAvailable()) {
            setSortMethod(sortMethod);

            URL searchUrl = NetworkUtils.buildUrl(sortMethod);
            Log.d(LOG_TAG, searchUrl.toString());
            new MovieDBQueryTask().execute(searchUrl);
        } else {
            // show no internet connection
            Toast.makeText(getApplicationContext(), "No Internet Connection. Please check", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  load movie list into grid view
     */
    private void loadMovieListToRecyclerView(ArrayList<Movie> movieList) {
        MovieAdapter movieAdapter = new MovieAdapter(this, movieList);
        mRecyclerView.setAdapter(movieAdapter);
    }

    /**
     * check if internet connection is available
     * Based on a stackoverflow snippet
     * URL : https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * @return true if there is Internet. false if not.
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public class MovieDBQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {
                parseResults(searchResults);
                Log.d(LOG_TAG, "Search Result : " + searchResults);
            }
        }
    }
}
