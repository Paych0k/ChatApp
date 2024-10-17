package edu.school21.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class ChatRoom {
    private Long id;
    private String name;

    private LocalDateTime createdAt;
    private Long ownerId;


    public ChatRoom(){
        this.id = null;
        this.name = null;
        this.createdAt = null;
        this.ownerId = null;
    }

    public ChatRoom(Long id, String name, LocalDateTime creationDate, Long ownerId) {
        this.id = id;
        this.name = name;
        this.createdAt = creationDate;
        this.ownerId = ownerId;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) object;
        return Objects.equals(id, chatRoom.id) && Objects.equals(name, chatRoom.name) && Objects.equals(createdAt, chatRoom.createdAt) && Objects.equals(ownerId, chatRoom.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdAt, ownerId);
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creationDate=" + createdAt +
                ", ownerId=" + ownerId +
                '}';
    }
}
