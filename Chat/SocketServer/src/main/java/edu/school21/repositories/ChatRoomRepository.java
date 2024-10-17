package edu.school21.repositories;

import edu.school21.models.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    void create(String name, Long ownerId);

    Optional<ChatRoom> findById(Long id);

    List<ChatRoom> findAll();

    void addParticipant(Long chatRoomId, Long userId);

    void removeParticipant(Long chatRoomId, Long userId);
}

