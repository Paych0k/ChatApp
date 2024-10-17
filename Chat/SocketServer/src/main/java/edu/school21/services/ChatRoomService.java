package edu.school21.services;

import edu.school21.models.ChatRoom;
import edu.school21.models.User;

import java.util.List;

public interface ChatRoomService {
    void createChatRoom(String name, Long ownerId);
    List<ChatRoom> getAllChatRooms();
    void addParticipant(User user, ChatRoom chatRoom);
}
