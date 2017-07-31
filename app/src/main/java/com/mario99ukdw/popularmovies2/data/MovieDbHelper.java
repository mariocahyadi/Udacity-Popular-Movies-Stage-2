package com.mario99ukdw.popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mario99ukdw on 31.07.2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = MovieDbHelper.class.getName();

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "popularmovies2.db";

    private static final String CREATE_TABLE_FAVORITES = "create table " + MovieContract.MoviesColumn.TABLE_MOVIE
            + "(" + MovieContract.MoviesColumn.COL_MOVIE_ID + " integer primary key, "
            + MovieContract.MoviesColumn.COL_MOVIE_TITLE + " text not null,"
            + MovieContract.MoviesColumn.COL_MOVIE_POSTER_PATH + " text, "
            + MovieContract.MoviesColumn.COL_MOVIE_OVERVIEW + " text, "
            + MovieContract.MoviesColumn.COL_MOVIE_VOTE_AVARAGE + " double, "
            + MovieContract.MoviesColumn.COL_MOVIE_RELEASE_DATE + " text, "
            + MovieContract.MoviesColumn.COL_MOVIE_RUNTIME + " text, "
            + MovieContract.MoviesColumn.COL_MOVIE_TRAILERS + " text, "
            + MovieContract.MoviesColumn.COL_MOVIE_REVIEWS + " text );";

    private static final String DB_SCHEMA = CREATE_TABLE_FAVORITES;

    private SQLiteDatabase mDB;

    public MovieDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mDB = getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(LOG_TAG,"MovieDbHelper onCreate");
        db.execSQL(DB_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(LOG_TAG, "MovieDbHelper onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MoviesColumn.TABLE_MOVIE);
        onCreate(db);
    }
}
