package com.mario99ukdw.popularmovies2;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mario99ukdw.popularmovies2.data.Movie;
import com.mario99ukdw.popularmovies2.data.MovieContentProvider;
import com.mario99ukdw.popularmovies2.data.MovieContract;
import com.mario99ukdw.popularmovies2.data.MovieReview;
import com.mario99ukdw.popularmovies2.data.MovieTrailer;
import com.mario99ukdw.popularmovies2.utilities.MovieDBResultParser;
import com.mario99ukdw.popularmovies2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    final static String VAR_NAME_MOVIE = "movie";
    final static String VAR_NAME_MOVIE_TRAILERS = "movieTrailers";
    final static String VAR_NAME_MOVIE_REVIEWS = "movieReviews";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_title) TextView mTitleTextView;
    @BindView(R.id.iv_poster) ImageView mPosterImageView;
    @BindView(R.id.tv_overview) TextView mOverviewTextView;
    @BindView(R.id.tv_vote_average) TextView mVoteAverageTextView;
    @BindView(R.id.tv_release_date) TextView mReleaseDateTextView;
    @BindView(R.id.tv_runtime) TextView mRuntimeTextView;
    @BindView(R.id.ll_trailer_list) LinearLayout mTrailerListLinearLayout;
    @BindView(R.id.ll_movie_list) LinearLayout mMovieListLinearLayout;
    @BindView(R.id.btn_favorite) Button mFavoriteButton;

    private Movie mMovie = null;
    ArrayList<MovieTrailer> mMovieTrailers = null;
    ArrayList<MovieReview> mMovieReviews = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mMovie = intent.getParcelableExtra(Movie.INTENT_PARCEL_NAME);

            populateMovie(mMovie);
            loadMovieFromServer();
        } else {
            loadMovieFromInstanceState(savedInstanceState);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putParcelable(VAR_NAME_MOVIE, mMovie);
        state.putParcelableArrayList(VAR_NAME_MOVIE_REVIEWS, mMovieReviews);
        state.putParcelableArrayList(VAR_NAME_MOVIE_TRAILERS, mMovieTrailers);

        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        loadMovieFromInstanceState(savedInstanceState);
    }

    @OnClick(R.id.btn_favorite)
    public void OnClick(View v) {
        if (!isMarkedFavorite()) {
            // add favorite
            ContentValues values = new ContentValues();
            values.put(MovieContract.MoviesColumn.COL_MOVIE_ID, mMovie.getId());
            values.put(MovieContract.MoviesColumn.COL_MOVIE_TITLE, mMovie.getTitle());
            values.put(MovieContract.MoviesColumn.COL_MOVIE_OVERVIEW, mMovie.getOverview());
            values.put(MovieContract.MoviesColumn.COL_MOVIE_VOTE_AVARAGE, mMovie.getVoteAverage());
            values.put(MovieContract.MoviesColumn.COL_MOVIE_RUNTIME, mMovie.getRunTime());
            values.put(MovieContract.MoviesColumn.COL_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());
            values.put(MovieContract.MoviesColumn.COL_MOVIE_POSTER_PATH, mMovie.getPosterPath());
//            if (mMovieTrailers.length()>0)
//                values.put(MovieContract.MoviesColumn.COL_MOVIE_TRAILERS, mMovieTrailers.toString());
//            else
//                values.put(MovieContract.MoviesColumn.COL_MOVIE_TRAILERS, "");
//            if (mMovieReviews.length() > 0)
//                values.put(MovieContract.MoviesColumn.COL_MOVIE_REVIEWS, mMovieReviews.toString());
//            else
//                values.put(MovieContract.MoviesColumn.COL_MOVIE_REVIEWS, "");
            Uri uri = getContentResolver().insert(MovieContentProvider.CONTENT_URI, values);
            Log.d(LOG_TAG, "mark favorite " + uri);
        } else {
            // delete favorite
            Uri deleteUri = Uri.parse(MovieContentProvider.CONTENT_URI + "/" + mMovie.getId());
            int count = getContentResolver().delete(deleteUri, null, null);
            Log.d(LOG_TAG, "unmark favorite " + count);
        }

        updateFavoriteButtonText();
    }
    private boolean isMarkedFavorite() {
        Uri movieUri = Uri.parse(MovieContentProvider.CONTENT_URI + "/" + mMovie.getId());
        String[] projection = new String[]{MovieContract.MoviesColumn.COL_MOVIE_ID};
        Cursor cursor = getContentResolver().query(movieUri, projection, null, null, null);
        return cursor != null && cursor.getCount() > 0;
    }
    private void updateFavoriteButtonText() {
        if (isMarkedFavorite()) {
            mFavoriteButton.setText(getString(R.string.action_unmark_favorite));
        } else {
            mFavoriteButton.setText(getString(R.string.action_mark_favorite));
        }
    }

    private void loadMovieFromInstanceState(Bundle savedInstanceState) {
        // Loading Saved data
        mMovie = savedInstanceState.getParcelable(VAR_NAME_MOVIE);
        mMovieTrailers = savedInstanceState.getParcelableArrayList(VAR_NAME_MOVIE_TRAILERS);
        mMovieReviews = savedInstanceState.getParcelableArrayList(VAR_NAME_MOVIE_REVIEWS);

        if (mMovie != null) {
            populateMovie(mMovie);
            populateMovieReview(mMovieReviews);
            populateMovieTrailer(mMovieTrailers);
        }
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

    private String getFormatedDate(String date, String format) throws ParseException {
        Log.d(LOG_TAG, "parsing movie release date" + date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return android.text.format.DateFormat.format("yyyy", simpleDateFormat.parse(date)).toString();

//        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this.getApplicationContext());
//        return dateFormat.format("yyyy", simpleDateFormat.parse(date));
    }

    /**
     *  parse JSON text from server
     */
    private void parseMovie(String result) {
        Movie parsedMovie = MovieDBResultParser.parseMovieDetailResult(result);
        if (parsedMovie != null) {
            mMovie = parsedMovie;
        }
        populateMovie(mMovie);
        //loadMovieListToRecyclerView(mMovieArrayList);
    }

    private void populateMovie(Movie movie) {
        mTitleTextView.setText(movie.getTitle());

        Picasso.with(this)
                .load(movie.getPosterPath())
//                .resize(getResources().getInteger(R.integer.poster_w185_width),
//                        getResources().getInteger(R.integer.poster_w185_height))
                .into(mPosterImageView);

        String overview = movie.getOverview();
        if (overview == null || overview == "") {
            overview = getResources().getString(R.string.message_no_overview_found);
        }
        mOverviewTextView.setText(overview);

        mVoteAverageTextView.setText(movie.getFormatedVoteAverage() + "/10");

        String releaseDate = movie.getFormatedReleaseDate();
        if(releaseDate == null || releaseDate == "") {
            releaseDate = getResources().getString(R.string.message_no_release_date_found);
        } else {
            try {
                releaseDate = getFormatedDate(releaseDate, movie.getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
                releaseDate = "";
            }
        }
        mReleaseDateTextView.setText(releaseDate);

        String runTime = movie.getRunTime();
        if (runTime == null || runTime.isEmpty()) runTime = "";
        else runTime += "min";
        mRuntimeTextView.setText(runTime);

        updateFavoriteButtonText();
    }

    /**
     *  load movie list from server with selected sort method
     */
    private void loadMovieFromServer() {
        if (isNetworkAvailable()) {
            // load movie detail
            URL searchUrl = NetworkUtils.buildMovieDetailUrl(mMovie.getId());
            Log.d(LOG_TAG, "Load movie detail URL : " + searchUrl.toString());
            new DetailActivity.MovieDBQueryTask().execute(searchUrl);

            // load movie trailers
            searchUrl = NetworkUtils.buildMovieTrailerUrl(mMovie.getId());
            Log.d(LOG_TAG, "Load movie videos URL : " + searchUrl.toString());
            new DetailActivity.MovieTrailerQueryTask().execute(searchUrl);

            // load movie reviews
            searchUrl = NetworkUtils.buildMovieReviewUrl(mMovie.getId());
            Log.d(LOG_TAG, "Load movie review URL : " + searchUrl.toString());
            new DetailActivity.MovieReviewQueryTask().execute(searchUrl);
        } else {
            // show no internet connection
            Toast.makeText(getApplicationContext(), "No Internet Connection. Please check", Toast.LENGTH_SHORT).show();
        }
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
                parseMovie(searchResults);
                Log.d(LOG_TAG, "Search Result : " + searchResults);
            }
        }
    }

    /**
     *  parse JSON text from server
     */
    private void parseTrailer(String result) {
        ArrayList<MovieTrailer> parsedMovieTrailer = MovieDBResultParser.parseTrailerResult(result);
        if (parsedMovieTrailer != null) {
            mMovieTrailers = parsedMovieTrailer;
        }
        populateMovieTrailer(mMovieTrailers);
    }

    /**
     * https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
     * @param movieTrailer
     */
    public void watchTrailer(MovieTrailer movieTrailer){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + movieTrailer.getYoutubeKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, movieTrailer.getYoutubeUri());
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
    private void populateMovieTrailer(ArrayList<MovieTrailer> movieTrailerList) {
        mTrailerListLinearLayout.removeAllViews();

        for (final MovieTrailer movieTrailer : movieTrailerList) {
            View view = getLayoutInflater().inflate(R.layout.trailer_list_item,null);
            ImageButton playImageButton = (ImageButton) view.findViewById(R.id.ib_play);
            playImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(LOG_TAG, "Trailer URL : " + movieTrailer.getYoutubeUri().toString());
                    watchTrailer(movieTrailer);
                }
            });
            TextView nameTextView = (TextView) view.findViewById(R.id.tv_name);
            nameTextView.setText(movieTrailer.getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(LOG_TAG, "Trailer URL : " + movieTrailer.getYoutubeUri().toString());
                    watchTrailer(movieTrailer);
                }
            });
            mTrailerListLinearLayout.addView(view);
        }
    }

    public class MovieTrailerQueryTask extends AsyncTask<URL, Void, String> {

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
                parseTrailer(searchResults);
                Log.d(LOG_TAG, "Search Result : " + searchResults);
            }
        }
    }

    /**
     *  parse JSON text from server
     */
    private void parseReview(String result) {
        ArrayList<MovieReview> parsedMovieReview = MovieDBResultParser.parseReviewResult(result);
        if (parsedMovieReview != null) {
            mMovieReviews = parsedMovieReview;
        }
        populateMovieReview(mMovieReviews);
    }
    private void populateMovieReview(ArrayList<MovieReview> movieReviewList) {
        mMovieListLinearLayout.removeAllViews();

        for (final MovieReview movieReview : movieReviewList) {
            View view = getLayoutInflater().inflate(R.layout.review_list_item,null);
            TextView authorTextView = (TextView) view.findViewById(R.id.tv_author);
            authorTextView.setText(movieReview.getAuthor() + " wrote:");

            TextView contentTextView = (TextView) view.findViewById(R.id.tv_content);
            contentTextView.setText(movieReview.getContent());

            mMovieListLinearLayout.addView(view);
        }
    }
    public class MovieReviewQueryTask extends AsyncTask<URL, Void, String> {

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
                parseReview(searchResults);
                Log.d(LOG_TAG, "Search Result : " + searchResults);
            }
        }
    }
}
