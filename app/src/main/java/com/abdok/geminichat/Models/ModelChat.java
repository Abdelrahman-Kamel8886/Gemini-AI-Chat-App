package com.abdok.geminichat.Models;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abdok.geminichat.Utils.UriTypeConverter;

@Entity
public class ModelChat {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String Sender;
    private String Message;
    @ColumnInfo(name = "image_path")
    private String imagePath;;

    public ModelChat(String sender, String message, String uri) {
        Sender = sender;
        Message = message;
        this.imagePath = uri;
    }

    public ModelChat() {
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}

