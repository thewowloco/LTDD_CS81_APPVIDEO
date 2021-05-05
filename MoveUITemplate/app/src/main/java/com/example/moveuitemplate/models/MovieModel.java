package com.example.moveuitemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieModel  implements Parcelable {
    //Model Class for our movies
    private String title;
    private String poster_path;
    private String release_date;
    private int movie_id;
    private float vote_average;
    private String movie_overview;
    private String backdrop_path;

    //Constructor

    public MovieModel(String title, String poster_path, String release_date, int movie_id,
                      float vote_average, String movie_overview,String backdrop_path) {
        this.title = title;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.movie_id = movie_id;
        this.vote_average = vote_average;
        this.movie_overview = movie_overview;
        this.backdrop_path = backdrop_path;
    }

    protected MovieModel(Parcel in) {
        title = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        movie_id = in.readInt();
        vote_average = in.readFloat();
        movie_overview = in.readString();
        backdrop_path = in.readString();
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    //Getter
    public String getTitle ()
    {
        return title;
    }
    public String getRelease_date ()
    {
        return release_date;
    }
    public String getPoster_path(){return poster_path;}
    public String getBackdrop_path(){return backdrop_path;}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(poster_path);
        parcel.writeString(release_date);
        parcel.writeInt(movie_id);
        parcel.writeFloat(vote_average);
        parcel.writeString(movie_overview);
        parcel.writeString(backdrop_path);
    }
}
