package com.mario99ukdw.popularmovies2.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mario99ukdw on 31.07.2017.
 */

public class MovieTrailer implements Parcelable {
    private String youtubeKey;
    private String name;

    public MovieTrailer(String vYoutubeKey, String vName) {
        this.youtubeKey = vYoutubeKey;
        this.name = vName;
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
        dest.writeString(youtubeKey);
        dest.writeString(name);
    }

    /**
     * Retrieving Movie data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private MovieTrailer(Parcel in){
        this.youtubeKey = in.readString();
        this.name = in.readString();
    }
    public static final Parcelable.Creator<MovieTrailer> CREATOR = new Parcelable.Creator<MovieTrailer>() {

        @Override
        public MovieTrailer createFromParcel(Parcel source) {
            return new MovieTrailer(source);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getYoutubeKey() { return youtubeKey;}
    public String getName() { return name;}
    public Uri getYoutubeUri() {
        return Uri.parse("http://www.youtube.com/watch?v=" + youtubeKey);
    }
}
