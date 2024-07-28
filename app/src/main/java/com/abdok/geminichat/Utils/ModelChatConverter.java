package com.abdok.geminichat.Utils;

import androidx.room.TypeConverter;

import com.abdok.geminichat.Models.ModelChat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ModelChatConverter {

    @TypeConverter
    public String fromModelChatList(ArrayList<ModelChat> modelChatList) {
        if (modelChatList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ModelChat>>() {}.getType();
        return gson.toJson(modelChatList, type);
    }

    @TypeConverter
    public ArrayList<ModelChat> toModelChatList(String modelChatString) {
        if (modelChatString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ModelChat>>() {}.getType();
        return gson.fromJson(modelChatString, type);
    }
}
