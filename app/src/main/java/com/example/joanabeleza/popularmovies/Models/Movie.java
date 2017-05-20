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
    //private String runtime;

    public Movie(String id, String title, String imagePath, String overview, Double vote_average, String release_date/*, String runtime*/) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
        //this.runtime = runtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    /*public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }*/

    // Parcelling part
    public Movie(Parcel in){
        // the order needs to be the same as in writeToParcel() method
        this.id = in.readString();
        this.title = in.readString();
        this.imagePath = in.readString();
        this.overview = in.readString();
        this.vote_average = in.readDouble();
        this.release_date = in.readString();
        //this.runtime = in.readString();
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
        //parcel.writeString(runtime);
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
