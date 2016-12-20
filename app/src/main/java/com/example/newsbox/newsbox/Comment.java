package com.example.newsbox.newsbox;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tester on 12/16/2016.
 */
public class Comment implements Parcelable {
    public String name;
    public String content;
    public String date;
    public String replyTo;
    public int isReplyTo;


    public Comment() {
    }

    public Comment(String name, String content, String date, String replyTo, int isReplyTo) {
        this.name = name;
        this.content = content;
        this.date = date;
        this.replyTo = replyTo;
        this.isReplyTo = isReplyTo;
    }

    private Comment(Parcel in) {
        name = in.readString();
        content = in.readString();
        date = in.readString();
        replyTo = in.readString();
        isReplyTo = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(content);
        out.writeString(date);
        out.writeString(replyTo);
        out.writeInt(isReplyTo);
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}