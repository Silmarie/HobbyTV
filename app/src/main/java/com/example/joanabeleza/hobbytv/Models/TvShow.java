package com.example.joanabeleza.hobbytv.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project PopularMovies refactored by joanabeleza on 25/02/2017.
 */

public class TvShow implements Parcelable {

    private String id;
    private String name;
    private String release_date;
    private Double vote_average;
    private String overview;
    private String genres;
    private String status;
    private int season_number;
    private String imagePath;

    public TvShow(String id, String name, String release_date, Double vote_average, String overview, String genres, String status, int season_number, String imagePath) {
        this.id = id;
        this.name = name;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.overview = overview;
        this.genres = genres;
        this.status = status;
        this.season_number = season_number;
        this.imagePath = imagePath;
    }

    // Parcelling part
    private TvShow(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.release_date = in.readString();
        this.vote_average = in.readDouble();
        this.overview = in.readString();
        this.genres = in.readString();
        this.status = in.readString();
        this.season_number = in.readInt();
        this.imagePath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(release_date);
        parcel.writeDouble(vote_average);
        parcel.writeString(overview);
        parcel.writeString(genres);
        parcel.writeString(status);
        parcel.writeInt(season_number);
        parcel.writeString(imagePath);
    }

    private static final Creator CREATOR = new Creator() {
        public TvShow createFromParcel(Parcel in) {
            return new TvShow(in);
        }

        public TvShow[] newArray(int size) {
            return new TvShow[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeason_number() {
        return season_number;
    }

    public void setSeason_number(int season_number) {
        this.season_number = season_number;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static Creator getCREATOR() {
        return CREATOR;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
