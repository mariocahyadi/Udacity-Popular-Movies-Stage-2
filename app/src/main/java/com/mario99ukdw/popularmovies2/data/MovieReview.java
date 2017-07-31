package com.mario99ukdw.popularmovies2.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mario99ukdw on 31.07.2017.
 */

public class MovieReview implements Parcelable {
    private String author;
    private String content;
    private String url;

    public MovieReview(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
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
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    /**
     * Retrieving Movie data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private MovieReview(Parcel in){
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }
    public static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {

        @Override
        public MovieReview createFromParcel(Parcel source) {
            return new MovieReview(source);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    public String getAuthor() {return this.author;}
    public String getContent() { return this.content;}
    public String getURl() { return this.url;}
}
