package com.example.joanabeleza.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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
    private int duration;
    private String genres;
    private ArrayList<String> trailers;
    private ArrayList<Review> reviews;

    public Movie(String id, String title, String imagePath, String overview, Double vote_average, String release_date, int duration, String genres, ArrayList<String> trailers, ArrayList<Review> reviews) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.duration = duration;
        this.genres = genres;
        this.reviews = reviews;
        this.trailers = trailers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public ArrayList<String> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<String> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    // Parcelling part
    public Movie(Parcel in) {
        // the order needs to be the same as in writeToParcel() method
        this.id = in.readString();
        this.title = in.readString();
        this.imagePath = in.readString();
        this.overview = in.readString();
        this.vote_average = in.readDouble();
        this.release_date = in.readString();
        this.duration = in.readInt();
        this.genres = in.readString();
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
        parcel.writeInt(duration);
        parcel.writeString(genres);
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
