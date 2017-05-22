package com.example.joanabeleza.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joanabeleza on 25/02/2017.
 */

public class Movie implements Parcelable {

    private String id;
    private String title;
    private String imagePath;
    private String overview;
    private Double vote_average;
    private String release_date;

    public Movie(String id, String title, String imagePath, String overview, Double vote_average, String release_date) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    // Parcelling part
    public Movie(Parcel in){
        // the order needs to be the same as in writeToParcel() method
        this.id = in.readString();
        this.title = in.readString();
        this.imagePath = in.readString();
        this.overview = in.readString();
        this.vote_average = in.readDouble();
        this.release_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(imagePath);
        parcel.writeString(overview);
        parcel.writeDouble(vote_average);
        parcel.writeString(release_date);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
