package edu.school21.models;

import java.time.LocalDateTime;


public class Message {
    private Long id;
    private Long usernameId;
    private String text;
    private LocalDateTime messageTime;
    private Long chatRoomId;

    public Message() {
        this.id = null;
        this.usernameId = null;
        this.text = null;
        this.messageTime = null;
        this.chatRoomId = null;
    }

    public Message(Long id, Long userNameId, String text, LocalDateTime messageTime, Long chatRoomId) {
        this.id = id;
        this.usernameId = userNameId;
        this.text = text;
        this.messageTime = messageTime;
        this.chatRoomId = chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public Long getId() {
        return id;
    }

    public Long getUsernameId() {
        return usernameId;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getMessageTime() {
        return messageTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsernameId(Long usernameId) {
        this.usernameId = usernameId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMessageTime(LocalDateTime messageTime) {
        this.messageTime = messageTime;
    }
}
