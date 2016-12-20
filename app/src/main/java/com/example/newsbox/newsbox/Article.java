package com.example.newsbox.newsbox;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by tester on 12/16/2016.
 */
public class Article implements Parcelable {
    String title;
    String img_link;
    String description;
    String date;
    String author;
    String link;
    String descriptionFull;
    String htmlContent;
    Bitmap image;
    ArrayList<Comment> coments = new ArrayList<Comment>();

    public Article() {
    }

    public Article(String title, String img_link, String description, String date, String author,
                   String link, String descriptionFull, String htmlContent, Bitmap image) {
        this.title = title;
        this.img_link = img_link;
        this.description = description;
        this.date = date;
        this.author = author;
        this.link = link;
        this.descriptionFull = descriptionFull;
        this.htmlContent = htmlContent;
        this.image = image;
    }

    private Article(Parcel in) {
        title = in.readString();
        img_link = in.readString();
        description = in.readString();
        date = in.readString();
        author = in.readString();
        link = in.readString();
        descriptionFull = in.readString();
        htmlContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(img_link);
        out.writeString(description);
        out.writeString(date);
        out.writeString(author);
        out.writeString(link);
        out.writeString(descriptionFull);
        out.writeString(htmlContent);
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}