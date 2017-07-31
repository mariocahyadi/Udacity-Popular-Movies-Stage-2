package com.mario99ukdw.popularmovies2.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by mario99ukdw on 31.07.2017.
 */

public class NetworkUtils {

    final static String MOVIEDB_API_BASE_URL = "http://api.themoviedb.org/3/movie";
    final static String MOVIEDB_API_KEY = "";
    final static String PARAM_API_KEY = "api_key";
    public final static String PARAM_SORT_POPULAR = "popular";
    public final static String PARAM_SORT_TOP_RATED = "top_rated";
    public final static String PARAM_SORT_FAVORITE = "favorite";

    /**
     * Builds the URL used to query MovieDB API
     *
     * @param sortBy The sorting param that will be queried for.
     * @return The URL to use to query the MovieDB.
     */
    public static URL buildUrl(String sortBy) {
        String searchUrl = MOVIEDB_API_BASE_URL;
        if (sortBy.isEmpty()) searchUrl = searchUrl + "/" + PARAM_SORT_POPULAR; // default query
        else searchUrl = searchUrl + "/" + sortBy; // query with sorting

        Uri builtUri = Uri.parse(searchUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, MOVIEDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to query MovieDB API
     *
     * @param id is movie's id
     * @return The URL to use to query the MovieDB.
     */
    public static URL buildMovieDetailUrl(String id) {
        String searchUrl = MOVIEDB_API_BASE_URL + "/" + id;

        Uri builtUri = Uri.parse(searchUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, MOVIEDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to query Trailer for a movie
     *
     * @param id is movie's id
     * @return The URL to use to query the MovieDB.
     */
    public static URL buildMovieTrailerUrl(String id) {
        String searchUrl = MOVIEDB_API_BASE_URL + "/" + id + "/videos";

        Uri builtUri = Uri.parse(searchUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, MOVIEDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to query Trailer for a movie
     *
     * @param id is movie's id
     * @return The URL to use to query the MovieDB.
     */
    public static URL buildMovieReviewUrl(String id) {
        String searchUrl = MOVIEDB_API_BASE_URL + "/" + id + "/reviews";

        Uri builtUri = Uri.parse(searchUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, MOVIEDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}