package com.mario99ukdw.popularmovies2.utilities;

/**
 * Created by mario99ukdw on 31.07.2017.
 */

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.mario99ukdw.popularmovies2.data.Movie;
import com.mario99ukdw.popularmovies2.data.MovieReview;
import com.mario99ukdw.popularmovies2.data.MovieTrailer;

/**
 * Created by mario99ukdw on 28.06.2017.
 */

public class MovieDBResultParser {
    private final static String MOVIEDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private final static String TAG_RESULTS = "results";
    private final static String TAG_ID = "id";
    private final static String TAG_ORIGINAL_TITLE = "original_title";
    private final static String TAG_POSTER_PATH = "poster_path";
    private final static String TAG_OVERVIEW = "overview";
    private final static String TAG_VOTE_AVERAGE = "vote_average";
    private final static String TAG_RELEASE_DATE = "release_date";
    private final static String TAG_RUNTIME = "runtime";
    private final static String TAG_YOUTUBE_KEY = "key";
    private final static String TAG_VIDEO_NAME = "name";
    private final static String TAG_REVIEW_AUTHOR = "author";
    private final static String TAG_REVIEW_CONTENT = "content";
    private final static String TAG_REVIEW_URL = "url";
    private final static String POSTER_SIZE = "w185";

    private static String constructPosterUrlString(String posterPath, String size) {
        String url = MOVIEDB_POSTER_BASE_URL;
        String posterSize = size;
        if (posterSize.isEmpty()) posterSize = POSTER_SIZE; // use default size if empty

        url = url + posterSize + posterPath;
        return url;
    }

    public static String constructYoutubeUrlString(String key) {
        return "http://www.youtube.com/watch?v=" + key;
    }

    private static Movie parseMovieFromJSONObject(JSONObject jsonObject) {
        String posterPath = null;
        String id = null;
        String originalTitle = null;
        String overview = null;
        double voteAverage = 0;
        String releaseDate = null;
        String runTime = null;

        try {
            if (jsonObject.has(TAG_POSTER_PATH)) posterPath = constructPosterUrlString(jsonObject.getString(TAG_POSTER_PATH), POSTER_SIZE);
            if (jsonObject.has(TAG_ID)) id = jsonObject.getString(TAG_ID);
            if (jsonObject.has(TAG_ORIGINAL_TITLE)) originalTitle = jsonObject.getString(TAG_ORIGINAL_TITLE);
            if (jsonObject.has(TAG_OVERVIEW)) overview = jsonObject.getString(TAG_OVERVIEW);
            if (jsonObject.has(TAG_VOTE_AVERAGE)) voteAverage = jsonObject.getDouble(TAG_VOTE_AVERAGE);
            if (jsonObject.has(TAG_RELEASE_DATE)) releaseDate = jsonObject.getString(TAG_RELEASE_DATE);
            if (jsonObject.has(TAG_RUNTIME)) runTime = jsonObject.getString(TAG_RUNTIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Movie(id, originalTitle, posterPath, overview, voteAverage, releaseDate, runTime);
    }
    public static ArrayList<Movie> parseResult(String datastring) {
        Log.e("TEST", datastring);
        ArrayList<Movie> result = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(datastring);
            if (data.has(TAG_RESULTS)) {
                JSONArray resultsJSONArray = data.getJSONArray(TAG_RESULTS);
                for (int i = 0; i < resultsJSONArray.length(); i++) {
                    JSONObject row = resultsJSONArray.getJSONObject(i);

                    result.add(parseMovieFromJSONObject(row));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
    public static Movie parseMovieDetailResult(String datastring) {
        Log.e("TEST DETAIL", datastring);
        try {
            JSONObject data = new JSONObject(datastring);
            return parseMovieFromJSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static ArrayList<MovieTrailer> parseTrailerResult(String datastring) {
        Log.e("TEST", datastring);
        ArrayList<MovieTrailer> result = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(datastring);
            if (data.has(TAG_RESULTS)) {
                JSONArray resultsJSONArray = data.getJSONArray(TAG_RESULTS);
                for (int i = 0; i < resultsJSONArray.length(); i++) {
                    JSONObject row = resultsJSONArray.getJSONObject(i);

                    String youtubeKey = null;
                    String name = null;

                    if (row.has(TAG_YOUTUBE_KEY)) youtubeKey = row.getString(TAG_YOUTUBE_KEY);
                    if (row.has(TAG_VIDEO_NAME)) name = row.getString(TAG_VIDEO_NAME);

                    result.add(new MovieTrailer(youtubeKey, name));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<MovieReview> parseReviewResult(String datastring) {
        Log.e("TEST", datastring);
        ArrayList<MovieReview> result = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(datastring);
            if (data.has(TAG_RESULTS)) {
                JSONArray resultsJSONArray = data.getJSONArray(TAG_RESULTS);
                for (int i = 0; i < resultsJSONArray.length(); i++) {
                    JSONObject row = resultsJSONArray.getJSONObject(i);

                    String author = null;
                    String content = null;
                    String url = null;

                    if (row.has(TAG_REVIEW_AUTHOR)) author = row.getString(TAG_REVIEW_AUTHOR);
                    if (row.has(TAG_REVIEW_CONTENT)) content = row.getString(TAG_REVIEW_CONTENT);
                    if (row.has(TAG_REVIEW_URL)) url = row.getString(TAG_REVIEW_URL);

                    result.add(new MovieReview(author, content, url));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}

