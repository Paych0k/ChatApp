package edu.school21.repositories;

import edu.school21.models.Message;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message> {
    List<Message> findByUsernameId(Long usernameId);
    List<Message> findRecentMessagesByChatRoomId(Long chatRoomId, int limit);
}
