package com.lvnvceo.ollamadroid;

public class ChatMessage {
    private final int profileImage;
    private final String profileName;
    private final String messageContent;

    public ChatMessage(int profileImage, String profileName, String messageContent) {
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.messageContent = messageContent;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getMessageContent() {
        return messageContent;
    }
}
