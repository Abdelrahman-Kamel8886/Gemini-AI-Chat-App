package com.abdok.geminichat.Utils;

import com.abdok.geminichat.Models.ModelChat;

import java.util.ArrayList;

public class SharedModel {

    public static ArrayList<ModelChat> activeChat = new ArrayList<>();

    private static ArrayList<ModelChat> oldChat = new ArrayList<>();

    public static ArrayList<ModelChat> getActiveChat() {
        return activeChat;
    }

    public static void setActiveChat(ArrayList<ModelChat> activeChat) {
        SharedModel.activeChat = activeChat;
    }

    public static ArrayList<ModelChat> getOldChat() {
        return oldChat;
    }

    public static void setOldChat(ArrayList<ModelChat> oldChat) {
        SharedModel.oldChat = oldChat;
    }
}
