package edu.school21.services;

import edu.school21.models.ChatRoom;
import edu.school21.models.User;
import edu.school21.repositories.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomServiceImpl implements ChatRoomService{
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public void createChatRoom(String name, Long ownerId) {
        chatRoomRepository.create(name, ownerId);
    }

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    @Override
    public void addParticipant(User user, ChatRoom chatRoom) {
        chatRoomRepository.addParticipant(chatRoom.getId(), user.getId());
    }

}
