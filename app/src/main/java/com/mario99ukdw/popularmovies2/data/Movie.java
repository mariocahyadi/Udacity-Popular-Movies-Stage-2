package com.mario99ukdw.popularmovies2.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mario99ukdw on 30.07.2017.
 */

public class Movie implements Parcelable {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    public static String INTENT_PARCEL_NAME = "MOVIE_PARCEL";

    private String id;
    private String title;
    private String posterPath; // Full URL
    private String overview;
    private double voteAverage;
    private String releaseDate;
    private String runTime;

    public Movie(String vId, String vTitle, String vPosterPath, String vOverview, double vVoteAverage, String vReleaseDate, String vRunTime)
    {
        this.id = vId;
        this.title = vTitle;
        this.posterPath = vPosterPath;
        this.overview = vOverview;
        this.voteAverage = vVoteAverage;
        this.releaseDate = vReleaseDate;
        this.runTime = vRunTime;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    /**
     * Storing the movie data to Parcel object
     **/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
        dest.writeString(runTime);
    }

    /**
     * Retrieving Movie data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private Movie(Parcel in){
        this.id = in.readString();
        this.title = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readDouble();
        this.releaseDate = in.readString();
        this.runTime = in.readString();
    }
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getDateFormat() {
        return DATE_FORMAT;
    }

    public String getId() {
        return this.id;
    }
    public String getTitle() {
        return this.title;
    }
    public String getPosterPath() {
        return this.posterPath;
    }
    public String getOverview() {
        return this.overview;
    }
    public String getRunTime() {
        return this.runTime;
    }
    public double getVoteAverage() { return this.voteAverage; }
    public String getReleaseDate() {
        return this.releaseDate;
    }
    public String getFormatedVoteAverage() {
        return String.valueOf(this.voteAverage);
    }
    public String getFormatedReleaseDate() {
        return this.releaseDate;
    }
}
