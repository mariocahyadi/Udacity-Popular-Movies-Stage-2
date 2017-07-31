package com.mario99ukdw.popularmovies2.data;

import android.provider.BaseColumns;

/**
 * Created by mario99ukdw on 31.07.2017.
 */

public class MovieContract {
    /* Inner class that defines table contents */
    public static abstract class MoviesColumn implements BaseColumns {

        public static final String TABLE_MOVIE = "movies_table";
        public static final String COL_MOVIE_ID = "id";
        public static final String COL_MOVIE_TITLE = "title";
        public static final String COL_MOVIE_OVERVIEW = "overview";
        public static final String COL_MOVIE_POSTER_PATH = "poster_path";
        public static final String COL_MOVIE_VOTE_AVARAGE = "vote_average";
        public static final String COL_MOVIE_RELEASE_DATE = "release_date";
        public static final String COL_MOVIE_RUNTIME = "runtime";
        public static final String COL_MOVIE_TRAILERS = "trailers";
        public static final String COL_MOVIE_REVIEWS = "reviews";
    }
}
