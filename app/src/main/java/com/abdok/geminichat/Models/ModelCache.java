package com.abdok.geminichat.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abdok.geminichat.Utils.ModelChatConverter;
import com.abdok.geminichat.Utils.UriTypeConverter;

import java.util.ArrayList;

@Entity(tableName = "cache_table")
@TypeConverters({ModelChatConverter.class})
public class ModelCache {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title ;

    private String content ;

    @TypeConverters(ModelChatConverter.class)
    private ArrayList<ModelChat> chat;

    public ModelCache(ArrayList<ModelChat> chat) {
        this.chat = chat;
        this.title = chat.get(0).getMessage();
        this.content = chat.get(1).getMessage();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<ModelChat> getChat() {
        return chat;
    }

    public void setChat(ArrayList<ModelChat> chat) {
        this.chat = chat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
