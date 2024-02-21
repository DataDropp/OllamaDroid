package com.lvnvceo.ollamadroid.ollama;

import java.util.ArrayList;

public class ChatRequest {
    public String model;
    public ArrayList<Messages.Message> messages;
    public boolean stream;
    public ChatRequest(String model, ArrayList<Messages.Message> message, boolean stream){
        this.model = model;
        this.messages = message;
        this.stream = stream;
    }
}
