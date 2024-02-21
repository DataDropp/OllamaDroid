package com.lvnvceo.ollamadroid.ollama;

import java.util.ArrayList;

public class Messages {
    public ArrayList<Message> messages = new ArrayList<>();
    public static class Message {
        public String role;
        public String content;
        public Message(String role, String string) {
            this.role = role;
            this.content = string;

        }
    }
}
