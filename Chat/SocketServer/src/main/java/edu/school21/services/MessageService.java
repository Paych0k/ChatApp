package edu.school21.services;

import edu.school21.models.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    void saveMessage(String text, Long usernameId, Long chatRoomId, LocalDateTime localDateTime
    );
    List<Message> getRecentMessages(Long chatRoomId, int limit);
}
